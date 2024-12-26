package kr.co.inntavern.dripking.repository;

import kr.co.inntavern.dripking.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
    Users save(Users users);
}
