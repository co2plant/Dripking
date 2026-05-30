package kr.co.inntavern.dripking.dto.response;

import kr.co.inntavern.dripking.model.enumType.ItemType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class WishlistItemResponseDTO {
    private Long wishlistItemId;
    private Long id;
    private ItemType itemType;
    private Long targetId;
    private String name;
    private String description;
    private String imgUrl;
    private String address;
    private Double latitude;
    private Double longitude;
    private LocalDateTime createdAt;
}
