package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface TournamentRepository extends JpaRepository<Tournament, Integer> {
    @Query(value = "SELECT * FROM tournaments WHERE course_execution_id = :executionId AND is_cancelled = false", nativeQuery = true)
    List<Tournament> findTournaments(int executionId);

    @Query(value = "SELECT MAX(key) FROM tournaments", nativeQuery = true)
    Integer getMaxTournamentKey();

    @Query(value = "SELECT * FROM tournaments t WHERE t.key = :key", nativeQuery = true)
    Optional<Tournament> findByKey(Integer key);

}