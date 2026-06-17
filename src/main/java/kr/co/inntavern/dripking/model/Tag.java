package kr.co.inntavern.dripking.model;

import jakarta.persistence.*;
import kr.co.inntavern.dripking.model.enumType.TagGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Table(indexes = {
        @Index(name = "idx_tag_group_sort", columnList = "tag_group, sort_order")
})
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id", unique = true, nullable=false)
    private Long id;

    private String name;

    @Column(columnDefinition = "TEXT", length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "tag_group")
    private TagGroup tagGroup;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Builder.Default
    private Boolean active = true;
}
