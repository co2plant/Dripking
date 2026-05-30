package kr.co.inntavern.dripking.controller;

import kr.co.inntavern.dripking.dto.request.WishlistItemRequestDTO;
import kr.co.inntavern.dripking.dto.response.WishlistItemResponseDTO;
import kr.co.inntavern.dripking.model.enumType.ItemType;
import kr.co.inntavern.dripking.security.CustomUserDetails;
import kr.co.inntavern.dripking.service.WishlistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {
    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping
    public ResponseEntity<List<WishlistItemResponseDTO>> getWishlist(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok(wishlistService.getWishlist(customUserDetails.getId()));
    }

    @PostMapping("/items")
    public ResponseEntity<WishlistItemResponseDTO> addWishlistItem(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                   @RequestBody WishlistItemRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(wishlistService.addWishlistItem(customUserDetails.getId(), requestDTO));
    }

    @DeleteMapping("/items")
    public ResponseEntity<Void> deleteWishlistItem(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                   @RequestParam ItemType itemType,
                                                   @RequestParam Long targetId) {
        wishlistService.deleteWishlistItem(customUserDetails.getId(), itemType, targetId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
