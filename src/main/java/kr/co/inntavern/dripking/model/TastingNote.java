package kr.co.inntavern.dripking.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "tasting_note")
@EntityListeners(AuditingEntityListener.class)
public class TastingNote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tasting_note_id", unique = true, nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alcohol_id")
    private Alcohol alcohol;

    @Column(nullable = false)
    private String alcoholName;

    @Column(nullable = false)
    private LocalDate tastedAt;

    private String placeName;

    @Column(precision = 10, scale = 7)
    private BigDecimal placeLat;

    @Column(precision = 10, scale = 7)
    private BigDecimal placeLng;

    @Column(nullable = false)
    private Byte appearance;

    @Column(nullable = false)
    private Byte aroma;

    @Column(nullable = false)
    private Byte palate;

    @Column(nullable = false)
    private Byte finish;

    @Column(nullable = false)
    private Byte overall;

    @ElementCollection
    @CollectionTable(name = "tasting_note_aroma_tag", joinColumns = @JoinColumn(name = "tasting_note_id"))
    @OrderColumn(name = "sort_order")
    @Column(name = "tag")
    private List<String> aromaTags = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "tasting_note_palate_tag", joinColumns = @JoinColumn(name = "tasting_note_id"))
    @OrderColumn(name = "sort_order")
    @Column(name = "tag")
    private List<String> palateTags = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "tasting_note_finish_tag", joinColumns = @JoinColumn(name = "tasting_note_id"))
    @OrderColumn(name = "sort_order")
    @Column(name = "tag")
    private List<String> finishTags = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String pairing;

    @Column(columnDefinition = "TEXT")
    private String memo;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
