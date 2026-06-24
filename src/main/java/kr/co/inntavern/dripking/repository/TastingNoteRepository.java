package kr.co.inntavern.dripking.repository;

import kr.co.inntavern.dripking.model.TastingNote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TastingNoteRepository extends JpaRepository<TastingNote, Long> {
    Optional<TastingNote> findByIdAndUserId(Long id, Long userId);

    Page<TastingNote> findAllByUserId(Long userId, Pageable pageable);

    Page<TastingNote> findAllByUserIdAndAlcoholId(Long userId, Long alcoholId, Pageable pageable);

    @Query("""
            SELECT note FROM TastingNote note
            WHERE note.user.id = :userId
            AND (LOWER(note.alcoholName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(COALESCE(note.placeName, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(COALESCE(note.pairing, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(COALESCE(note.memo, '')) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<TastingNote> searchByUserKeyword(@Param("userId") Long userId,
                                          @Param("keyword") String keyword,
                                          Pageable pageable);

    @Query("""
            SELECT note FROM TastingNote note
            WHERE note.user.id = :userId
            AND note.alcohol.id = :alcoholId
            AND (LOWER(note.alcoholName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(COALESCE(note.placeName, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(COALESCE(note.pairing, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(COALESCE(note.memo, '')) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<TastingNote> searchByUserAndAlcoholKeyword(@Param("userId") Long userId,
                                                    @Param("alcoholId") Long alcoholId,
                                                    @Param("keyword") String keyword,
                                                    Pageable pageable);
}
