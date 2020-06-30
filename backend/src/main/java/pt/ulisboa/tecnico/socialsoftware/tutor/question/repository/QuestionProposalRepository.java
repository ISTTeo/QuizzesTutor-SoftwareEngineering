package pt.ulisboa.tecnico.socialsoftware.tutor.question.repository;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.QuestionProposal;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface QuestionProposalRepository extends JpaRepository<QuestionProposal, Integer> {
    @Query(value = "SELECT MAX(key) FROM questionProposals", nativeQuery = true)
    Integer getMaxQuestionNumber();

    @Query(value = "SELECT * FROM questionProposals q WHERE q.course_id = :courseId", nativeQuery = true)
    List<QuestionProposal> getCourseProposals(Integer courseId);
}
