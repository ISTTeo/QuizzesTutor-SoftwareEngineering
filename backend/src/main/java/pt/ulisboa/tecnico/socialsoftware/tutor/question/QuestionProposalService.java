package pt.ulisboa.tecnico.socialsoftware.tutor.question;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.QuestionProposal;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionProposalDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionProposalRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuestionProposalService {
    public enum Decision { ACCEPT, REJECT }

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuestionProposalRepository questionProposalRepository;

    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public QuestionProposalDto submitQuestionProposal(int courseId, int authorId, QuestionProposalDto questionProposalDto) throws TutorException {
        Course course = courseRepository.findById(courseId).orElseThrow(
                () -> new TutorException(COURSE_NOT_FOUND, courseId)
                );

        User author = userRepository.findById(authorId).orElseThrow(
                () -> new TutorException(USER_NOT_FOUND, authorId)
                );

        QuestionProposal questionProposal = new QuestionProposal(author, course, questionProposalDto);
        questionProposalRepository.save(questionProposal);
        questionProposal.getOptions().stream().forEach(opt -> optionRepository.save(opt));

        return new QuestionProposalDto(questionProposal);
    }

    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public QuestionProposalDto reSubmitQuestionProposal(int proposalId, int editorId, QuestionProposalDto editedQuestionProposalDto) throws TutorException {
        QuestionProposal questionProposal = questionProposalRepository
            .findById(proposalId)
            .orElseThrow(() -> new TutorException(QUESTION_PROPOSAL_NOT_FOUND, proposalId));

        if (editorId != questionProposal.getAuthor().getId().intValue()) {
            throw new TutorException(QUESTION_PROPOSAL_EDITOR_IS_NOT_THE_AUTHOR);
        } else if (questionProposal.getStatus() != QuestionProposal.Status.REJECTED) {
            throw new TutorException(QUESTION_PROPOSAL_NOT_REJECTED, proposalId);
        }

        removeMissingOptions(questionProposal, editedQuestionProposalDto);
        questionProposal.reSubmit(editedQuestionProposalDto);
        questionProposalRepository.save(questionProposal);
        questionProposal.getOptions().stream().forEach(opt -> optionRepository.save(opt));
        return new QuestionProposalDto(questionProposal);
    }

    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public QuestionProposalDto editQuestionProposal(int proposalId, int editorId, QuestionProposalDto editedQuestionProposalDto) throws TutorException {
        QuestionProposal questionProposal = questionProposalRepository
            .findById(proposalId)
            .orElseThrow(() -> new TutorException(QUESTION_PROPOSAL_NOT_FOUND, proposalId));

        User editor = userRepository.findById(editorId).orElseThrow(
                () -> new TutorException(USER_NOT_FOUND, editorId)
                );

        switch (editor.getRole()) {
            case STUDENT:
                if (editorId != questionProposal.getAuthor().getId().intValue()) {
                    throw new TutorException(QUESTION_PROPOSAL_EDITOR_IS_NOT_THE_AUTHOR);
                } else if (questionProposal.getStatus() != QuestionProposal.Status.PENDING) {
                    throw new TutorException(QUESTION_PROPOSAL_NOT_PENDING, proposalId);
                }
                break;
            case TEACHER:
                if (questionProposal.getStatus() != QuestionProposal.Status.ACCEPTED) {
                    throw new TutorException(QUESTION_PROPOSAL_NOT_ACCEPTED, proposalId);
                }
                break;
            default:
                throw new UnsupportedOperationException("Only Students and teacher can edit a proposal");
        }

        removeMissingOptions(questionProposal, editedQuestionProposalDto);
        questionProposal.update(editedQuestionProposalDto);
        questionProposalRepository.save(questionProposal);
        questionProposal.getOptions().stream().forEach(opt -> optionRepository.save(opt));
        return new QuestionProposalDto(questionProposal);
    }


    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public QuestionProposalDto reviewQuestionProposal(Integer proposalId, Decision decision, String reason) throws TutorException {
        QuestionProposal questionProposal = questionProposalRepository
            .findById(proposalId)
            .orElseThrow(() -> new TutorException(QUESTION_PROPOSAL_NOT_FOUND, proposalId));

        switch (decision) {
            case ACCEPT:
                questionProposal.accept(reason);
                questionProposalRepository.save(questionProposal);
                break;
            case REJECT:
                questionProposal.reject(reason);
                questionProposalRepository.save(questionProposal);
                break;
            default:
                throw new UnsupportedOperationException("Nothing to do with decision '" + decision + "'");
        }

        return new QuestionProposalDto(questionProposal);
    }

    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<QuestionProposal> listAuthorQuestionProposals(int studentId) {
        final User student = userRepository.findById(studentId).orElseThrow(
                () -> new TutorException(USER_NOT_FOUND, studentId)
                );

        return questionProposalRepository.findAll().stream()
            .filter(prop -> prop.getAuthor().getId().equals(student.getId()))
            .collect(Collectors.toList());
    }

    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<QuestionProposal> listTeacherQuestionProposals(int teacherId) {
        final User teacher = userRepository.findById(teacherId).orElseThrow(
                () -> new TutorException(USER_NOT_FOUND, teacherId)
                );
        final Set<Integer> userCourseIds = teacher.getCourseExecutions().stream()
            .map(CourseExecution::getCourse)
            .map(Course::getId)
            .collect(Collectors.toSet());

        return questionProposalRepository.findAll().stream()
            .filter(proposal -> userCourseIds.contains(proposal.getCourse().getId()))
            .collect(Collectors.toList());
    }

    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public QuestionDto convertQuestionProposal(int proposalId) {
        final QuestionProposal questionProposal = questionProposalRepository
            .findById(proposalId)
            .orElseThrow(() -> new TutorException(QUESTION_PROPOSAL_NOT_FOUND, proposalId));

        if (questionProposal.getStatus() != QuestionProposal.Status.ACCEPTED) {
            throw new TutorException(QUESTION_PROPOSAL_NOT_ACCEPTED, proposalId);
        }

        return questionService.createQuestionFromProposal(questionProposal);
    }

    private void removeMissingOptions(QuestionProposal orig, QuestionProposalDto replacement) {
        if (replacement.getOptions().size() > 0) {
            // remove elements which are not in the new array
            orig.getOptions().stream()
                .filter(opt -> replacement.getOptions().stream()
                                .map(OptionDto::getId)
                                .noneMatch(id -> id == opt.getId().intValue())
                        )
                .forEach(opt -> { opt.remove(); optionRepository.delete(opt); });

            optionRepository.flush();
        }
    }
}
