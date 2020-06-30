package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

@DataJpaTest
class AuthorListQuestionProposalsTest extends Specification {
    public static final String REASON = "reason"
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String OPTION_CONTENT = "optionId content"
    public static final String QUESTION_TITLE = "question title"
    public static final String QUESTION_CONTENT = "question content"
    public static final int PROPOSAL_KEY = 42

    @Autowired
    QuestionProposalRepository questionProposalRepository

    @Autowired
    QuestionService questionService

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

    def student
    def course
    def proposalDto
    def setup() {
        course = new Course(COURSE_NAME, Course.Type.TECNICO)

        def courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        course.addCourseExecution(courseExecution)
        courseExecution.setCourse(course)

        courseExecutionRepository.save(courseExecution)
        courseRepository.save(course)

        student = new User("PEDRO", "PC", 2, User.Role.STUDENT)
        student.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(student)
        userRepository.save(student)

        // add a proposal
        proposalDto = new QuestionProposalDto()
        proposalDto.setKey(PROPOSAL_KEY)
        proposalDto.setTitle(QUESTION_TITLE)
        proposalDto.setContent(QUESTION_CONTENT)
        proposalDto.setStatus(QuestionProposal.Status.PENDING.name())

        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setCorrect(true)
        optionDto.setSequence(0)

        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        proposalDto.setOptions(options)
    }

    def "user doesn't have any question proposals"() {
        when: "the user requests his question proposals"
        def proposals = questionProposalService.listAuthorQuestionProposals(student.getId());

        then:
            proposals.size() == 0
    }

    def "user has a question proposal"() {
        given: "the user submits a question proposal"
        questionProposalService.submitQuestionProposal(course, student, proposalDto)

        when: "the user requests his question proposals"
        def proposals = questionProposalService.listAuthorQuestionProposals(student.getId());

        then:
            proposals.size() == 1
            proposals.get(0).getId().equals(questionProposalRepository.findAll().get(0).getId());
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

