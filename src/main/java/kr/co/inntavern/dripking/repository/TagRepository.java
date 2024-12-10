package kr.co.inntavern.dripking.repository;

import kr.co.inntavern.dripking.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Tag save(Tag tag);

}
