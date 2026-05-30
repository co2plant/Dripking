package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.dto.request.WishlistItemRequestDTO;
import kr.co.inntavern.dripking.dto.response.WishlistItemResponseDTO;
import kr.co.inntavern.dripking.model.*;
import kr.co.inntavern.dripking.model.enumType.ItemType;
import kr.co.inntavern.dripking.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WishlistService {
    private final WishlistItemRepository wishlistItemRepository;
    private final UserRepository userRepository;
    private final AlcoholRepository alcoholRepository;
    private final DistilleryRepository distilleryRepository;
    private final DestinationRepository destinationRepository;

    public WishlistService(WishlistItemRepository wishlistItemRepository,
                           UserRepository userRepository,
                           AlcoholRepository alcoholRepository,
                           DistilleryRepository distilleryRepository,
                           DestinationRepository destinationRepository) {
        this.wishlistItemRepository = wishlistItemRepository;
        this.userRepository = userRepository;
        this.alcoholRepository = alcoholRepository;
        this.distilleryRepository = distilleryRepository;
        this.destinationRepository = destinationRepository;
    }

    @Transactional(readOnly = true)
    public List<WishlistItemResponseDTO> getWishlist(Long userId) {
        return wishlistItemRepository.findAllByUserIdOrderByCreatedAtAscIdAsc(userId)
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Transactional
    public WishlistItemResponseDTO addWishlistItem(Long userId, WishlistItemRequestDTO requestDTO) {
        validateRequest(requestDTO);
        validateTargetExists(requestDTO.getItemType(), requestDTO.getTargetId());

        WishlistItem wishlistItem = wishlistItemRepository
                .findByUserIdAndItemTypeAndTargetId(userId, requestDTO.getItemType(), requestDTO.getTargetId())
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new IllegalArgumentException("해당 ID의 유저가 존재하지 않습니다."));
                    WishlistItem newItem = new WishlistItem();
                    newItem.setUser(user);
                    newItem.setItemType(requestDTO.getItemType());
                    newItem.setTargetId(requestDTO.getTargetId());
                    return wishlistItemRepository.save(newItem);
                });

        return mapToResponseDTO(wishlistItem);
    }

    @Transactional
    public void deleteWishlistItem(Long userId, ItemType itemType, Long targetId) {
        validateItemType(itemType);
        if (targetId == null) {
            throw new IllegalArgumentException("targetId가 필요합니다.");
        }
        wishlistItemRepository.deleteByUserIdAndItemTypeAndTargetId(userId, itemType, targetId);
    }

    private void validateRequest(WishlistItemRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new IllegalArgumentException("위시리스트 항목 정보가 필요합니다.");
        }
        validateItemType(requestDTO.getItemType());
        if (requestDTO.getTargetId() == null) {
            throw new IllegalArgumentException("targetId가 필요합니다.");
        }
    }

    private void validateItemType(ItemType itemType) {
        if (itemType != ItemType.ALCOHOL
                && itemType != ItemType.DISTILLERY
                && itemType != ItemType.DESTINATION) {
            throw new IllegalArgumentException("위시리스트에 추가할 수 없는 itemType입니다.");
        }
    }

    private void validateTargetExists(ItemType itemType, Long targetId) {
        switch (itemType) {
            case ALCOHOL -> alcoholRepository.findById(targetId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 ID의 술이 존재하지 않습니다."));
            case DISTILLERY -> distilleryRepository.findById(targetId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 ID의 양조장이 존재하지 않습니다."));
            case DESTINATION -> destinationRepository.findById(targetId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 ID의 여행지가 존재하지 않습니다."));
            default -> throw new IllegalArgumentException("위시리스트에 추가할 수 없는 itemType입니다.");
        }
    }

    private WishlistItemResponseDTO mapToResponseDTO(WishlistItem wishlistItem) {
        WishlistItemResponseDTO responseDTO = new WishlistItemResponseDTO();
        responseDTO.setWishlistItemId(wishlistItem.getId());
        responseDTO.setId(wishlistItem.getTargetId());
        responseDTO.setTargetId(wishlistItem.getTargetId());
        responseDTO.setItemType(wishlistItem.getItemType());
        responseDTO.setCreatedAt(wishlistItem.getCreatedAt());
        applyTargetSnapshot(responseDTO);
        return responseDTO;
    }

    private void applyTargetSnapshot(WishlistItemResponseDTO responseDTO) {
        switch (responseDTO.getItemType()) {
            case ALCOHOL -> applyAlcoholSnapshot(responseDTO);
            case DISTILLERY -> applyDistillerySnapshot(responseDTO);
            case DESTINATION -> applyDestinationSnapshot(responseDTO);
            default -> throw new IllegalArgumentException("위시리스트에 추가할 수 없는 itemType입니다.");
        }
    }

    private void applyAlcoholSnapshot(WishlistItemResponseDTO responseDTO) {
        Alcohol alcohol = alcoholRepository.findById(responseDTO.getTargetId())
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 술이 존재하지 않습니다."));
        responseDTO.setName(alcohol.getName());
        responseDTO.setDescription(alcohol.getDescription());
        responseDTO.setImgUrl(alcohol.getImgUrl());

        Distillery distillery = alcohol.getDistillery();
        if (distillery != null) {
            responseDTO.setAddress(distillery.getAddress());
            responseDTO.setLatitude(distillery.getLatitude());
            responseDTO.setLongitude(distillery.getLongitude());
        }
    }

    private void applyDistillerySnapshot(WishlistItemResponseDTO responseDTO) {
        Distillery distillery = distilleryRepository.findById(responseDTO.getTargetId())
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 양조장이 존재하지 않습니다."));
        responseDTO.setName(distillery.getName());
        responseDTO.setDescription(distillery.getDescription());
        responseDTO.setImgUrl(distillery.getImgUrl());
        responseDTO.setAddress(distillery.getAddress());
        responseDTO.setLatitude(distillery.getLatitude());
        responseDTO.setLongitude(distillery.getLongitude());
    }

    private void applyDestinationSnapshot(WishlistItemResponseDTO responseDTO) {
        Destination destination = destinationRepository.findById(responseDTO.getTargetId())
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 여행지가 존재하지 않습니다."));
        responseDTO.setName(destination.getName());
        responseDTO.setDescription(destination.getDescription());
        responseDTO.setImgUrl(destination.getImgUrl());
        responseDTO.setAddress(buildDestinationAddress(destination));
        responseDTO.setLatitude(destination.getLatitude());
        responseDTO.setLongitude(destination.getLongitude());
    }

    private String buildDestinationAddress(Destination destination) {
        if (destination.getCity() == null) {
            return null;
        }
        if (destination.getCity().getCountry() == null) {
            return destination.getCity().getName();
        }
        return destination.getCity().getName() + ", " + destination.getCity().getCountry().getName();
    }
}
