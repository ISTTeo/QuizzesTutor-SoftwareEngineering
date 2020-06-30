package pt.ulisboa.tecnico.socialsoftware.tutor.discussion;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface DiscussionRepository extends JpaRepository<Discussion, Integer> {
    @Query(value = "select * from discussions d where d.key = :key", nativeQuery = true)
    Optional<Discussion> findByKey(Integer key);

    @Query(value = "SELECT MAX(key) FROM discussions", nativeQuery = true)
    Integer getMaxDiscussionKey();
}