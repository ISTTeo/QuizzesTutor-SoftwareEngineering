package pt.ulisboa.tecnico.socialsoftware.tutor.reply;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.List;

@Repository
@Transactional
public interface ReplyRepository extends JpaRepository<Reply, Integer> {
    @Query(value = "select * from replies r where r.key = :key", nativeQuery = true)
    Optional<Reply> findByKey(Integer key);

    @Query(value = "select * from replies r where r.discussion_id = :discussionId", nativeQuery = true)
    List<Reply> findByDiscussionId(Integer discussionId);

    @Query(value = "SELECT MAX(key) FROM replies", nativeQuery = true)
    Integer getMaxReplyKey();
}

