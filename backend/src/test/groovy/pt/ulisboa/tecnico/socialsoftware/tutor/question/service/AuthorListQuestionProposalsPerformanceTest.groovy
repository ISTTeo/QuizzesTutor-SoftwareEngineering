package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
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
class AuthorListQuestionProposalsPerformanceTest extends Specification {
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
    QuestionProposalService questionProposalService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    def course
    def courseExecution
    def options

    def setup() {
        course = new Course(COURSE_NAME, Course.Type.TECNICO)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        course.addCourseExecution(courseExecution)
        courseExecution.setCourse(course)

        courseExecutionRepository.save(courseExecution)
        courseRepository.save(course)


        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setCorrect(true)
        optionDto.setSequence(0)
        options = new ArrayList<OptionDto>()
        options.add(optionDto)
    }

    def "performance test listing 1000 proposals 1000 times"() {
        given: "a student"
        def student = new User("PEDRO", "PC", 2, User.Role.STUDENT)
        student.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(student)
        userRepository.save(student)
        and: "1000 submited proposals by the student"
        1.upto(1, {
            def proposalDto = new QuestionProposalDto()
            proposalDto.setKey(PROPOSAL_KEY)
            proposalDto.setTitle(QUESTION_TITLE + it)
            proposalDto.setContent(QUESTION_CONTENT + it)
            proposalDto.setOptions(options)
            questionProposalService.submitQuestionProposal(course, student, proposalDto)
        })
        when: "they are listed 1000 times"
        1.upto(1, {
            questionProposalService.listAuthorQuestionProposals(student.getId())
        })


        then:
        true
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

