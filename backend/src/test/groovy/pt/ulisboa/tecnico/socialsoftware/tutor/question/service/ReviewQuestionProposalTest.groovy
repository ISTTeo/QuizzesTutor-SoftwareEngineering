package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionProposalDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.QuestionProposal
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionProposalService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionProposalRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Bean
import org.springframework.boot.test.context.TestConfiguration

import spock.lang.Specification
import spock.lang.Unroll

@DataJpaTest
class ReviewQuestionProposalTest extends Specification {
    public static final String REASON = "reason"
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String OPTION_CONTENT = "optionId content"
    public static final String QUESTION_TITLE = "question title"
    public static final String QUESTION_CONTENT = "question content"
    public static final int PROPOSAL_KEY = 42

    @Autowired
    QuestionService questionService

    @Autowired
    QuestionProposalRepository questionProposalRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    QuestionProposalService questionProposalService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    def course
    def proposalId

    def setup() {
        course = new Course(COURSE_NAME, Course.Type.TECNICO)

        def courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        course.addCourseExecution(courseExecution)
        courseExecution.setCourse(course)

        courseExecutionRepository.save(courseExecution)
        courseRepository.save(course)

        def student = new User("PEDRO", "PC", 2, User.Role.STUDENT)
        student.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(student)
        userRepository.save(student)

        // add a proposal
        def proposalDto = new QuestionProposalDto()
        proposalDto.setKey(PROPOSAL_KEY)
        proposalDto.setTitle(QUESTION_TITLE)
        proposalDto.setContent(QUESTION_CONTENT)

        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setCorrect(true)
        optionDto.setSequence(0)

        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        proposalDto.setOptions(options)

        questionProposalService.submitQuestionProposal(course.getId(), student.getId(), proposalDto)
        def result = questionProposalRepository.findAll().get(0);
        proposalId = result.getId()
    }

    @Unroll
    def "invalid parameters: id=#id |decision=#decision |reason=#reason || error=#errorMessage"() {
        when: "a review with invalid parameters is made"
        questionProposalService.reviewQuestionProposal(proposalId + id, decision, reason)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == errorMessage

        where:
        id |decision                                |reason ||errorMessage
        1  |QuestionProposalService.Decision.ACCEPT |REASON ||ErrorMessage.QUESTION_PROPOSAL_NOT_FOUND
        1  |QuestionProposalService.Decision.REJECT |REASON ||ErrorMessage.QUESTION_PROPOSAL_NOT_FOUND
        0  |QuestionProposalService.Decision.REJECT |""     ||ErrorMessage.EMPTY_JUSTIFICATION
        0  |QuestionProposalService.Decision.REJECT |null   ||ErrorMessage.EMPTY_JUSTIFICATION
    }

    @Unroll
    def "giving duplicate review #review on an already #first_review proposal"() {
        given: "an already reviewed (and accepted) proposal"
        questionProposalService.reviewQuestionProposal(proposalId, first_review, REASON)

        when: "duplicate review is given"
        questionProposalService.reviewQuestionProposal(proposalId, review, REASON);

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == errorMessage

        where:
        first_review                            |review                                  ||errorMessage
        QuestionProposalService.Decision.ACCEPT |QuestionProposalService.Decision.ACCEPT ||ErrorMessage.QUESTION_PROPOSAL_NOT_OPEN_FOR_REVIEW
        QuestionProposalService.Decision.ACCEPT |QuestionProposalService.Decision.REJECT ||ErrorMessage.QUESTION_PROPOSAL_NOT_OPEN_FOR_REVIEW
        QuestionProposalService.Decision.REJECT |QuestionProposalService.Decision.REJECT ||ErrorMessage.QUESTION_PROPOSAL_NOT_OPEN_FOR_REVIEW
        QuestionProposalService.Decision.REJECT |QuestionProposalService.Decision.ACCEPT ||ErrorMessage.QUESTION_PROPOSAL_NOT_OPEN_FOR_REVIEW
    }

    @Unroll
    def "valid review: decision=#decision || delta=#delta | status=#status"() {
        given: "number of approved question proposals"
        int previousApprovedProposalCount = questionProposalRepository
            .findAll() // JPA Repository
            .findAll{it.getCourse().getId().equals(course.getId()) && it.getStatus() == QuestionProposal.Status.ACCEPTED}
            .size();

        and: "the number of questions"
        int previousQuestionCount = questionRepository.count()

        when: "a question proposal is accepted"
        questionProposalService.reviewQuestionProposal(proposalId, decision, REASON)

        then:
        def proposal = questionProposalRepository.findById(proposalId).get();
        proposal.getStatus() == status
        proposal.getId() == proposalId
        proposal.getKey() == PROPOSAL_KEY
        int currentApprovedProposalCount = questionProposalRepository
            .findAll() // JPA Repository
            .findAll{it.getCourse().getId().equals(course.getId()) && it.getStatus() == QuestionProposal.Status.ACCEPTED}
            .size();
        currentApprovedProposalCount - previousApprovedProposalCount == delta
        questionRepository.count() - previousQuestionCount == 0 // questions are not automatically created

        where:
        decision                                ||status                           |delta
        QuestionProposalService.Decision.ACCEPT ||QuestionProposal.Status.ACCEPTED |1
        QuestionProposalService.Decision.REJECT ||QuestionProposal.Status.REJECTED |0
    }

    @TestConfiguration
    static class QuestionProposalServiceImplementTextConfiguration {
        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }

        @Bean
        QuestionProposalService questionProposalService() {
            return new QuestionProposalService()
        }
    }
}

