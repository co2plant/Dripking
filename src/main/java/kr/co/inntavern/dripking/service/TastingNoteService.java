package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.dto.request.TastingNoteRequestDTO;
import kr.co.inntavern.dripking.dto.response.TastingNoteResponseDTO;
import kr.co.inntavern.dripking.model.Alcohol;
import kr.co.inntavern.dripking.model.TastingNote;
import kr.co.inntavern.dripking.model.User;
import kr.co.inntavern.dripking.repository.AlcoholRepository;
import kr.co.inntavern.dripking.repository.TastingNoteRepository;
import kr.co.inntavern.dripking.repository.UserRepository;
import kr.co.inntavern.dripking.util.PlainTextSecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class TastingNoteService {
    private static final int MAX_TAGS_PER_GROUP = 20;

    private final TastingNoteRepository tastingNoteRepository;
    private final UserRepository userRepository;
    private final AlcoholRepository alcoholRepository;

    public TastingNoteService(TastingNoteRepository tastingNoteRepository,
                              UserRepository userRepository,
                              AlcoholRepository alcoholRepository) {
        this.tastingNoteRepository = tastingNoteRepository;
        this.userRepository = userRepository;
        this.alcoholRepository = alcoholRepository;
    }

    @Transactional(readOnly = true)
    public Page<TastingNoteResponseDTO> getTastingNotes(Long userId,
                                                        Long alcoholId,
                                                        String keyword,
                                                        int page,
                                                        int size,
                                                        String sort) {
        requireUser(userId);
        Pageable pageable = PageRequest.of(Math.max(page, 0), normalizePageSize(size), resolveSort(sort));
        String normalizedKeyword = normalizeKeyword(keyword);
        return searchTastingNotes(userId, alcoholId, normalizedKeyword, pageable)
                .map(this::mapToResponseDTO);
    }

    private Page<TastingNote> searchTastingNotes(Long userId,
                                                 Long alcoholId,
                                                 String keyword,
                                                 Pageable pageable) {
        if (keyword == null) {
            if (alcoholId == null) {
                return tastingNoteRepository.findAllByUserId(userId, pageable);
            }
            return tastingNoteRepository.findAllByUserIdAndAlcoholId(userId, alcoholId, pageable);
        }

        if (alcoholId == null) {
            return tastingNoteRepository.searchByUserKeyword(userId, keyword, pageable);
        }
        return tastingNoteRepository.searchByUserAndAlcoholKeyword(userId, alcoholId, keyword, pageable);
    }

    @Transactional(readOnly = true)
    public TastingNoteResponseDTO getTastingNote(Long userId, Long noteId) {
        return mapToResponseDTO(requireOwnedNote(userId, noteId));
    }

    @Transactional
    public TastingNoteResponseDTO createTastingNote(Long userId, TastingNoteRequestDTO requestDTO) {
        User user = requireUser(userId);
        if (requestDTO == null) {
            throw new IllegalArgumentException("테이스팅 기록 정보가 필요합니다.");
        }

        TastingNote tastingNote = new TastingNote();
        tastingNote.setUser(user);
        applyRequest(tastingNote, requestDTO, true);
        return mapToResponseDTO(tastingNoteRepository.save(tastingNote));
    }

    @Transactional
    public TastingNoteResponseDTO updateTastingNote(Long userId, Long noteId, TastingNoteRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new IllegalArgumentException("테이스팅 기록 정보가 필요합니다.");
        }

        TastingNote tastingNote = requireOwnedNote(userId, noteId);
        applyRequest(tastingNote, requestDTO, false);
        return mapToResponseDTO(tastingNoteRepository.save(tastingNote));
    }

    @Transactional
    public void deleteTastingNote(Long userId, Long noteId) {
        TastingNote tastingNote = requireOwnedNote(userId, noteId);
        tastingNoteRepository.delete(tastingNote);
    }

    private void applyRequest(TastingNote tastingNote, TastingNoteRequestDTO requestDTO, boolean requireFullFields) {
        Alcohol alcohol = resolveAlcohol(requestDTO.getAlcoholId());
        if (requestDTO.getAlcoholId() != null || requireFullFields) {
            tastingNote.setAlcohol(alcohol);
        }

        String alcoholName = resolveAlcoholName(requestDTO.getAlcoholName(), alcohol, requireFullFields);
        if (alcoholName != null) {
            tastingNote.setAlcoholName(alcoholName);
        }

        LocalDate tastedAt = requestDTO.getTastedAt();
        if (tastedAt != null) {
            tastingNote.setTastedAt(tastedAt);
        } else if (requireFullFields) {
            tastingNote.setTastedAt(LocalDate.now());
        }

        if (requestDTO.getPlace() != null) {
            tastingNote.setPlaceName(PlainTextSecurityUtils.validateAndNormalizeOptional(
                    requestDTO.getPlace().getName(),
                    PlainTextSecurityUtils.TASTING_NOTE_PLACE
            ));
            tastingNote.setPlaceLat(validateLatitude(requestDTO.getPlace().getLat()));
            tastingNote.setPlaceLng(validateLongitude(requestDTO.getPlace().getLng()));
        }

        applyRatings(tastingNote, requestDTO.getRatings(), requireFullFields);

        if (requestDTO.getTags() != null) {
            tastingNote.setAromaTags(normalizeTags(requestDTO.getTags().getAroma()));
            tastingNote.setPalateTags(normalizeTags(requestDTO.getTags().getPalate()));
            tastingNote.setFinishTags(normalizeTags(requestDTO.getTags().getFinish()));
        } else if (requireFullFields) {
            tastingNote.setAromaTags(new ArrayList<>());
            tastingNote.setPalateTags(new ArrayList<>());
            tastingNote.setFinishTags(new ArrayList<>());
        }

        if (requestDTO.getPairing() != null || requireFullFields) {
            tastingNote.setPairing(PlainTextSecurityUtils.validateAndNormalizeOptional(
                    requestDTO.getPairing(),
                    PlainTextSecurityUtils.TASTING_NOTE_PAIRING
            ));
        }
        if (requestDTO.getMemo() != null || requireFullFields) {
            tastingNote.setMemo(PlainTextSecurityUtils.validateAndNormalizeOptional(
                    requestDTO.getMemo(),
                    PlainTextSecurityUtils.TASTING_NOTE_MEMO
            ));
        }
    }

    private void applyRatings(TastingNote tastingNote,
                              TastingNoteRequestDTO.RatingsDTO ratings,
                              boolean requireFullFields) {
        if (ratings == null) {
            if (requireFullFields) {
                throw new IllegalArgumentException("테이스팅 평점 정보가 필요합니다.");
            }
            return;
        }

        tastingNote.setAppearance(resolveRating("색/질감", ratings.getAppearance(), tastingNote.getAppearance(), requireFullFields));
        tastingNote.setAroma(resolveRating("향", ratings.getAroma(), tastingNote.getAroma(), requireFullFields));
        tastingNote.setPalate(resolveRating("맛", ratings.getPalate(), tastingNote.getPalate(), requireFullFields));
        tastingNote.setFinish(resolveRating("여운", ratings.getFinish(), tastingNote.getFinish(), requireFullFields));
        tastingNote.setOverall(resolveRating("총점", ratings.getOverall(), tastingNote.getOverall(), requireFullFields));
    }

    private Byte resolveRating(String label, Integer nextRating, Byte currentRating, boolean requireFullFields) {
        if (nextRating == null) {
            if (requireFullFields) {
                throw new IllegalArgumentException(label + " 평점이 필요합니다.");
            }
            return currentRating;
        }

        if (nextRating < 1 || nextRating > 5) {
            throw new IllegalArgumentException(label + " 평점은 1점에서 5점 사이여야 합니다.");
        }
        return nextRating.byteValue();
    }

    private Alcohol resolveAlcohol(Long alcoholId) {
        if (alcoholId == null) {
            return null;
        }
        return alcoholRepository.findById(alcoholId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 술이 존재하지 않습니다."));
    }

    private String resolveAlcoholName(String requestedName, Alcohol alcohol, boolean requireFullFields) {
        String sourceName = requestedName;
        if ((sourceName == null || sourceName.isBlank()) && alcohol != null) {
            sourceName = alcohol.getName();
        }

        if (sourceName == null || sourceName.isBlank()) {
            if (requireFullFields) {
                throw new IllegalArgumentException("술 이름이 필요합니다.");
            }
            return null;
        }

        return PlainTextSecurityUtils.validateAndNormalize(
                sourceName,
                PlainTextSecurityUtils.TASTING_NOTE_ALCOHOL_NAME
        );
    }

    private List<String> normalizeTags(List<String> tags) {
        if (tags == null) {
            return List.of();
        }

        Set<String> normalized = new LinkedHashSet<>();
        for (String tag : tags) {
            String normalizedTag = PlainTextSecurityUtils.validateAndNormalizeOptional(
                    tag,
                    PlainTextSecurityUtils.TASTING_NOTE_TAG
            );
            if (normalizedTag != null) {
                normalized.add(normalizedTag);
            }
            if (normalized.size() > MAX_TAGS_PER_GROUP) {
                throw new IllegalArgumentException("테이스팅 태그는 그룹당 " + MAX_TAGS_PER_GROUP + "개 이하로 선택해주세요.");
            }
        }
        return List.copyOf(normalized);
    }

    private BigDecimal validateLatitude(BigDecimal latitude) {
        if (latitude == null) {
            return null;
        }
        if (latitude.compareTo(BigDecimal.valueOf(-90)) < 0 || latitude.compareTo(BigDecimal.valueOf(90)) > 0) {
            throw new IllegalArgumentException("위도는 -90에서 90 사이여야 합니다.");
        }
        return latitude;
    }

    private BigDecimal validateLongitude(BigDecimal longitude) {
        if (longitude == null) {
            return null;
        }
        if (longitude.compareTo(BigDecimal.valueOf(-180)) < 0 || longitude.compareTo(BigDecimal.valueOf(180)) > 0) {
            throw new IllegalArgumentException("경도는 -180에서 180 사이여야 합니다.");
        }
        return longitude;
    }

    private User requireUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    private TastingNote requireOwnedNote(Long userId, Long noteId) {
        return tastingNoteRepository.findByIdAndUserId(noteId, userId)
                .orElseThrow(() -> new IllegalArgumentException("테이스팅 기록을 찾을 수 없습니다."));
    }

    private int normalizePageSize(int size) {
        if (size <= 0) {
            return 20;
        }
        return Math.min(size, 100);
    }

    private Sort resolveSort(String sort) {
        String normalizedSort = sort == null ? "recent" : sort.trim().toLowerCase(Locale.ROOT);
        return switch (normalizedSort) {
            case "score" -> Sort.by(Sort.Direction.DESC, "overall").and(Sort.by(Sort.Direction.DESC, "tastedAt"));
            case "name" -> Sort.by(Sort.Direction.ASC, "alcoholName").and(Sort.by(Sort.Direction.DESC, "tastedAt"));
            default -> Sort.by(Sort.Direction.DESC, "tastedAt").and(Sort.by(Sort.Direction.DESC, "id"));
        };
    }

    private String normalizeKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        return keyword.trim();
    }

    private TastingNoteResponseDTO mapToResponseDTO(TastingNote tastingNote) {
        TastingNoteResponseDTO responseDTO = new TastingNoteResponseDTO();
        responseDTO.setId(tastingNote.getId());
        responseDTO.setUserId(tastingNote.getUser().getId());
        responseDTO.setAlcoholId(tastingNote.getAlcohol() == null ? null : tastingNote.getAlcohol().getId());
        responseDTO.setAlcoholName(tastingNote.getAlcoholName());
        responseDTO.setTastedAt(tastingNote.getTastedAt());
        responseDTO.setPlace(mapPlace(tastingNote));
        responseDTO.setAppearance(toInteger(tastingNote.getAppearance()));
        responseDTO.setAroma(toInteger(tastingNote.getAroma()));
        responseDTO.setPalate(toInteger(tastingNote.getPalate()));
        responseDTO.setFinish(toInteger(tastingNote.getFinish()));
        responseDTO.setOverall(toInteger(tastingNote.getOverall()));
        responseDTO.setAromaTags(List.copyOf(tastingNote.getAromaTags()));
        responseDTO.setPalateTags(List.copyOf(tastingNote.getPalateTags()));
        responseDTO.setFinishTags(List.copyOf(tastingNote.getFinishTags()));
        responseDTO.setPairing(tastingNote.getPairing());
        responseDTO.setMemo(tastingNote.getMemo());
        responseDTO.setPhotoCount(0);
        responseDTO.setPrimaryPhoto(null);
        responseDTO.setCreatedAt(tastingNote.getCreatedAt());
        responseDTO.setUpdatedAt(tastingNote.getUpdatedAt());
        return responseDTO;
    }

    private Integer toInteger(Byte value) {
        return value == null ? null : value.intValue();
    }

    private TastingNoteResponseDTO.PlaceDTO mapPlace(TastingNote tastingNote) {
        if (tastingNote.getPlaceName() == null
                && tastingNote.getPlaceLat() == null
                && tastingNote.getPlaceLng() == null) {
            return null;
        }

        TastingNoteResponseDTO.PlaceDTO placeDTO = new TastingNoteResponseDTO.PlaceDTO();
        placeDTO.setName(tastingNote.getPlaceName());
        placeDTO.setLat(tastingNote.getPlaceLat());
        placeDTO.setLng(tastingNote.getPlaceLng());
        return placeDTO;
    }
}
