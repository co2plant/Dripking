package kr.co.inntavern.dripking.repository;

import kr.co.inntavern.dripking.model.Tag;
import kr.co.inntavern.dripking.model.enumType.TagGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findAllByActiveTrueOrderByTagGroupAscSortOrderAscNameAsc();

    List<Tag> findAllByTagGroupAndActiveTrueOrderBySortOrderAscNameAsc(TagGroup tagGroup);
}
