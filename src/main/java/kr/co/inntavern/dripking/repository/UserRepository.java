package kr.co.inntavern.dripking.repository;

import kr.co.inntavern.dripking.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Long countByIsEmailVerified(boolean isEmailVerified);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);

    @Query("""
            SELECT u FROM User u
            WHERE :search IS NULL
               OR LOWER(COALESCE(u.nickname, '')) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(COALESCE(u.email, '')) LIKE LOWER(CONCAT('%', :search, '%'))
            """)
    Page<User> searchUsers(@Param("search") String search, Pageable pageable);

    @Query("""
            SELECT u FROM User u
            WHERE (:search IS NULL
               OR LOWER(COALESCE(u.nickname, '')) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(COALESCE(u.email, '')) LIKE LOWER(CONCAT('%', :search, '%')))
              AND (:locked IS NULL OR u.isLocked = :locked)
            """)
    Page<User> searchUsers(@Param("search") String search, @Param("locked") Boolean locked, Pageable pageable);
}
