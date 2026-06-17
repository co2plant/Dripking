package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.dto.request.TagRequestDTO;
import kr.co.inntavern.dripking.dto.response.TagResponseDTO;
import kr.co.inntavern.dripking.model.Tag;
import kr.co.inntavern.dripking.model.enumType.TagGroup;
import kr.co.inntavern.dripking.repository.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TagService {
    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Transactional(readOnly = true)
    public List<TagResponseDTO> getAllTags(TagGroup group) {
        List<Tag> tags = group == null
                ? tagRepository.findAllByActiveTrueOrderByTagGroupAscSortOrderAscNameAsc()
                : tagRepository.findAllByTagGroupAndActiveTrueOrderBySortOrderAscNameAsc(group);

        return tags.stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Transactional
    public TagResponseDTO createTag(TagRequestDTO requestDTO) {
        validateRequest(requestDTO);

        Tag tag = Tag.builder()
                .name(requestDTO.getName().trim())
                .description(requestDTO.getDescription())
                .tagGroup(requestDTO.getGroup())
                .sortOrder(resolveSortOrder(requestDTO.getSortOrder()))
                .active(resolveActive(requestDTO.getActive()))
                .build();

        return mapToResponseDTO(tagRepository.save(tag));
    }

    @Transactional
    public TagResponseDTO updateTag(Long tagId, TagRequestDTO requestDTO) {
        validateRequest(requestDTO);
        Tag existingTag = tagRepository.findById(tagId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 태그가 존재하지 않습니다."));

        Tag tag = Tag.builder()
                .id(existingTag.getId())
                .name(requestDTO.getName().trim())
                .description(requestDTO.getDescription())
                .tagGroup(requestDTO.getGroup())
                .sortOrder(resolveSortOrder(requestDTO.getSortOrder()))
                .active(resolveActive(requestDTO.getActive()))
                .build();

        return mapToResponseDTO(tagRepository.save(tag));
    }

    @Transactional
    public void deleteTagById(Long tagId) {
        tagRepository.findById(tagId)
                .orElseThrow(() -> new IllegalArgumentException("이미 삭제되거나 없는 태그입니다."));
        tagRepository.deleteById(tagId);
    }

    private void validateRequest(TagRequestDTO requestDTO) {
        if (requestDTO == null || requestDTO.getName() == null || requestDTO.getName().isBlank()) {
            throw new IllegalArgumentException("태그 이름이 필요합니다.");
        }

        if (requestDTO.getGroup() == null) {
            throw new IllegalArgumentException("태그 그룹이 필요합니다.");
        }
    }

    private int resolveSortOrder(Integer sortOrder) {
        return sortOrder == null ? 0 : sortOrder;
    }

    private boolean resolveActive(Boolean active) {
        return active == null || active;
    }

    private TagResponseDTO mapToResponseDTO(Tag tag) {
        TagResponseDTO responseDTO = new TagResponseDTO();
        responseDTO.setId(tag.getId());
        responseDTO.setName(tag.getName());
        responseDTO.setDescription(tag.getDescription());
        responseDTO.setGroup(tag.getTagGroup());
        responseDTO.setGroupLabel(tag.getTagGroup() == null ? null : tag.getTagGroup().getLabel());
        responseDTO.setSortOrder(tag.getSortOrder());
        responseDTO.setActive(tag.getActive());
        return responseDTO;
    }
}
