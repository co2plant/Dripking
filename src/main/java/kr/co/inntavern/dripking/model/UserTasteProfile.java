package kr.co.inntavern.dripking.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "user_taste_profile")
public class UserTasteProfile {
    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @ElementCollection
    @CollectionTable(name = "user_taste_profile_category", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "category_id")
    private List<Long> categories = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "user_taste_profile_flavor_tag", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "flavor_tag")
    private List<String> flavorTags = new ArrayList<>();

    private LocalDateTime updatedAt;
}
