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
class ConvertQuestionProposalTest extends Specification {
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
        optionDto.setSequence(1)

        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        proposalDto.setOptions(options)

        questionProposalService.submitQuestionProposal(course.getId(), student.getId(), proposalDto)
        def result = questionProposalRepository.findAll().get(0);
        proposalId = result.getId()
    }

    @Unroll
    def "invalid parameters: id=#id | decision=#decision || error=#errorMessage"() {
        given: "a question proposal"
        if (decision != null) {
            questionProposalService.reviewQuestionProposal(proposalId, decision, REASON);
        }
        when: "an attempt to make a conversion is made"
        questionProposalService.convertQuestionProposal(proposalId + id)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == errorMessage

        where:
        id |decision                                ||errorMessage
        1  |QuestionProposalService.Decision.ACCEPT ||ErrorMessage.QUESTION_PROPOSAL_NOT_FOUND
        1  |QuestionProposalService.Decision.REJECT ||ErrorMessage.QUESTION_PROPOSAL_NOT_FOUND
        1  |null                                    ||ErrorMessage.QUESTION_PROPOSAL_NOT_FOUND
        0  |QuestionProposalService.Decision.REJECT ||ErrorMessage.QUESTION_PROPOSAL_NOT_ACCEPTED
        0  |null                                    ||ErrorMessage.QUESTION_PROPOSAL_NOT_ACCEPTED
    }

    def "valid conversion"() {
        given: "number of approved question proposals"
        int previousApprovedProposalCount = questionProposalRepository
            .findAll() // JPA Repository
            .findAll{it.getCourse().getId().equals(course.getId()) && it.getStatus() == QuestionProposal.Status.ACCEPTED}
            .size();

        and: "the number of questions"
        int previousQuestionCount = questionRepository.count()
        and: "an approved question"
        questionProposalService.reviewQuestionProposal(proposalId, QuestionProposalService.Decision.ACCEPT, REASON)

        when: "the proposal is converted"
        questionProposalService.convertQuestionProposal(proposalId)

        then:
        def proposal = questionProposalRepository.findById(proposalId).get();
        proposal.getStatus() == QuestionProposal.Status.ACCEPTED
        proposal.getId() == proposalId
        proposal.getKey() == PROPOSAL_KEY
        int currentApprovedProposalCount = questionProposalRepository
            .findAll() // JPA Repository
            .findAll{it.getCourse().getId().equals(course.getId()) && it.getStatus() == QuestionProposal.Status.ACCEPTED}
            .size();
        currentApprovedProposalCount - previousApprovedProposalCount == 1
        questionRepository.count() - previousQuestionCount == 1
        def question = questionRepository.findAll().get(questionRepository.findAll().size() - 1);
        question.getContent().equals(proposal.getContent())
        question.getOptions().size() == 1
        proposal.getOptions().size() == 1
        question.getOptions().toArray()[0].getContent().equals(proposal.getOptions().toArray()[0].getContent())
        question.getOptions().toArray()[0].getCorrect() == proposal.getOptions().toArray()[0].getCorrect()
        question.getOptions().toArray()[0].getSequence().equals(proposal.getOptions().toArray()[0].getSequence())
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

