package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.QuestionProposal
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionProposalDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionProposalService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionProposalRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

import spock.lang.Specification
import spock.lang.Unroll

@DataJpaTest
class SubmitQuestionProposalTest extends Specification {
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String QUESTION_TITLE = "question title"
    public static final String QUESTION_CONTENT = "question content"
    public static final String OPTION_CONTENT = "optionId content"
    public static final String URL = "URL"

    @Autowired
    QuestionService questionService

    @Autowired
    QuestionProposalRepository questionProposalRepository

    @Autowired
    QuestionProposalService questionProposalService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    UserRepository userRepository

    def course
    def courseExecution

    def setup() {
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        course.addCourseExecution(courseExecution)
        courseExecution.setCourse(course)

        courseExecutionRepository.save(courseExecution)
        courseRepository.save(course)
    }

    @Unroll
    def "invalid parameters: userValid=#userValid |title=#title |content=#content || error=#errorMessage"() {
        given: "an (possibly) invalid questionProposalDto"
        def questionProposalDto = new QuestionProposalDto()
        questionProposalDto.setTitle(title)
        questionProposalDto.setContent(content)

        and: "a (possibly) invalid user"
        def student = null;
        if (userValid == true) {
            student = new User("PEDROfine", "PC", 2, User.Role.STUDENT)
            student.getCourseExecutions().add(courseExecution)
            courseExecution.getUsers().add(student)
            userRepository.save(student)
        }

        when: "it is submited"
        questionProposalService.submitQuestionProposal(course.getId(), student == null ? -1 : student.getId(), questionProposalDto)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == errorMessage

        where:
        userValid |title          |content          ||errorMessage
        false     |QUESTION_TITLE |QUESTION_CONTENT ||ErrorMessage.USER_NOT_FOUND
        true      |""             |QUESTION_CONTENT ||ErrorMessage.QUESTION_MISSING_DATA
        true      |null           |QUESTION_CONTENT ||ErrorMessage.QUESTION_MISSING_DATA
        true      |QUESTION_TITLE |""               ||ErrorMessage.QUESTION_MISSING_DATA
        true      |QUESTION_TITLE |null             ||ErrorMessage.QUESTION_MISSING_DATA
    }

    //The following two tests check for valid question proposal submissions
    def "submit a question proposal with no image and one option"() {
        // Verify that the proposal is correctly added to the repository
        given: "a questionProposalDto"
        def questionProposalDto = new QuestionProposalDto()
        questionProposalDto.setKey(1)
        questionProposalDto.setTitle(QUESTION_TITLE)
        questionProposalDto.setContent(QUESTION_CONTENT)
        and: "an optionID"
        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setCorrect(true)
        optionDto.setSequence(0)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        questionProposalDto.setOptions(options)
        and: "a student"
        def student = new User("PEDROfine", "PC", 2, User.Role.STUDENT)
        student.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(student)
        userRepository.save(student)

        when:
        questionProposalService.submitQuestionProposal(course.getId(), student.getId(), questionProposalDto)

        then: "the correct question proposal is inside the repository"
        questionProposalRepository.count() == 1L
        def result = questionProposalRepository.findAll().get(0)
        result.getId() != null
        result.getKey() == 1
        result.getStatus() == QuestionProposal.Status.PENDING
        result.getTitle() == QUESTION_TITLE
        result.getContent() == QUESTION_CONTENT
        result.getImage() == null
        result.getOptions().size() == 1
        result.getCourse().getName() == COURSE_NAME
        course.getQuestionProposals().contains(result)
        def resOption = result.getOptions().iterator().next();
        resOption.getContent() == OPTION_CONTENT
        resOption.getCorrect()

    }

    def "submit two question proposals"() {
        // Verify that question proposals are created with correct numbers
        given: "a questionDto"
        def questionProposalDto = new QuestionProposalDto()
        questionProposalDto.setTitle(QUESTION_TITLE)
        questionProposalDto.setContent(QUESTION_CONTENT)
        questionProposalDto.setStatus(QuestionProposal.Status.PENDING.name())
        and: 'a optionId'
        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setCorrect(true)
        optionDto.setSequence(0)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        questionProposalDto.setOptions(options)
        and: "a student"
        def student = new User("PEDROfine2", "PC", 2, User.Role.STUDENT)
        student.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(student)
        userRepository.save(student)

        when: 'are created two questions'
        questionProposalService.submitQuestionProposal(course.getId(), student.getId(), questionProposalDto)
        questionProposalDto.setKey(null)
        questionProposalService.submitQuestionProposal(course.getId(), student.getId(), questionProposalDto)

        then: "the two questions are created with the correct numbers"
        questionProposalRepository.count() == 2L
        def resultOne = questionProposalRepository.findAll().get(0)
        def resultTwo = questionProposalRepository.findAll().get(1)
        resultOne.getKey() + resultTwo.getKey() == 3
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
