package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.dto.request.UserTasteProfileRequestDTO;
import kr.co.inntavern.dripking.dto.response.UserTasteProfileResponseDTO;
import kr.co.inntavern.dripking.model.User;
import kr.co.inntavern.dripking.model.UserTasteProfile;
import kr.co.inntavern.dripking.repository.CategoryRepository;
import kr.co.inntavern.dripking.repository.UserRepository;
import kr.co.inntavern.dripking.repository.UserTasteProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class UserTasteProfileService {
    private final UserTasteProfileRepository userTasteProfileRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public UserTasteProfileService(UserTasteProfileRepository userTasteProfileRepository,
                                   UserRepository userRepository,
                                   CategoryRepository categoryRepository) {
        this.userTasteProfileRepository = userTasteProfileRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public UserTasteProfileResponseDTO getTasteProfile(Long userId) {
        requireUser(userId);
        return userTasteProfileRepository.findByUserId(userId)
                .map(this::mapToResponseDTO)
                .orElseGet(() -> emptyResponse(userId));
    }

    @Transactional
    public UserTasteProfileResponseDTO saveTasteProfile(Long userId, UserTasteProfileRequestDTO requestDTO) {
        User user = requireUser(userId);
        List<Long> categories = normalizeCategories(requestDTO);
        List<String> flavorTags = normalizeFlavorTags(requestDTO);

        if (categories.isEmpty() && flavorTags.isEmpty()) {
            throw new IllegalArgumentException("카테고리 또는 맛 태그를 하나 이상 선택해주세요.");
        }

        validateCategories(categories);

        UserTasteProfile tasteProfile = userTasteProfileRepository.findByUserId(userId)
                .orElseGet(UserTasteProfile::new);
        tasteProfile.setUser(user);
        tasteProfile.setCategories(new ArrayList<>(categories));
        tasteProfile.setFlavorTags(new ArrayList<>(flavorTags));
        tasteProfile.setUpdatedAt(LocalDateTime.now());

        return mapToResponseDTO(userTasteProfileRepository.save(tasteProfile));
    }

    private User requireUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    private List<Long> normalizeCategories(UserTasteProfileRequestDTO requestDTO) {
        if (requestDTO == null || requestDTO.getCategories() == null) {
            return List.of();
        }

        Set<Long> normalized = new LinkedHashSet<>();
        for (Long categoryId : requestDTO.getCategories()) {
            if (categoryId != null) {
                normalized.add(categoryId);
            }
        }
        return List.copyOf(normalized);
    }

    private List<String> normalizeFlavorTags(UserTasteProfileRequestDTO requestDTO) {
        if (requestDTO == null || requestDTO.getFlavorTags() == null) {
            return List.of();
        }

        Set<String> normalized = new LinkedHashSet<>();
        for (String flavorTag : requestDTO.getFlavorTags()) {
            if (flavorTag == null) {
                continue;
            }
            String trimmed = flavorTag.trim();
            if (!trimmed.isBlank()) {
                normalized.add(trimmed.toLowerCase(Locale.ROOT));
            }
        }
        return List.copyOf(normalized);
    }

    private void validateCategories(List<Long> categories) {
        long existingCount = categoryRepository.findAllById(categories).size();
        if (existingCount != categories.size()) {
            throw new IllegalArgumentException("존재하지 않는 카테고리가 포함되어 있습니다.");
        }
    }

    private UserTasteProfileResponseDTO emptyResponse(Long userId) {
        UserTasteProfileResponseDTO responseDTO = new UserTasteProfileResponseDTO();
        responseDTO.setUserId(userId);
        responseDTO.setCategories(List.of());
        responseDTO.setFlavorTags(List.of());
        responseDTO.setUpdatedAt(null);
        return responseDTO;
    }

    private UserTasteProfileResponseDTO mapToResponseDTO(UserTasteProfile tasteProfile) {
        UserTasteProfileResponseDTO responseDTO = new UserTasteProfileResponseDTO();
        responseDTO.setUserId(tasteProfile.getUserId());
        responseDTO.setCategories(List.copyOf(tasteProfile.getCategories()));
        responseDTO.setFlavorTags(List.copyOf(tasteProfile.getFlavorTags()));
        responseDTO.setUpdatedAt(tasteProfile.getUpdatedAt());
        return responseDTO;
    }
}
