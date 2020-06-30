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
class TeacherListQuestionProposalsTest extends Specification {
    public static final String REASON = "reason"
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String OPTION_CONTENT = "optionId content"
    public static final String QUESTION_TITLE = "question title"
    public static final String QUESTION_CONTENT = "question content"
    public static final int PROPOSAL_KEY = 42
    public static final int PROPOSAL_KEY2 = 43

    @Autowired
    QuestionProposalRepository questionProposalRepository

    @Autowired
    QuestionProposalService questionProposalService

    @Autowired
    QuestionService questionService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    UserRepository userRepository

    def setup() { }

    def "teacher has no course executions"() {
        given: "a teacher without course executions"
        def teacher = new User("BALTASAR", "BD", 3, User.Role.TEACHER)
        userRepository.save(teacher)

        when: "we query for the proposals"
        def proposals = questionProposalService.listTeacherQuestionProposals(teacher.getId());

        then: "the list is empty"
        proposals.size() == 0
    }

    def "teacher has a course execution but no questions"() {
        given: "a course execution without questions"
        def courseExecution = generateCourseExecution();
        and: "a teacher in that course execution"
        def teacher = new User("BALTASAR", "BD", 3, User.Role.TEACHER)
        teacher.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(teacher)
        userRepository.save(teacher)

        when: "we query for the proposals"
        def proposals = questionProposalService.listTeacherQuestionProposals(teacher.getId());

        then: "the list is empty"
        proposals.size() == 0
    }

    def "teacher's course execution has a question proposal"() {
        given: "a course execution"
        def courseExecution = generateCourseExecution();
        and: "a submited proposal for that course execution"
        submitProposalsToExecution(courseExecution, 1);
        and: "a teacher in that course execution"
        def teacher = new User("BALTASAR", "BD", 3, User.Role.TEACHER)
        teacher.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(teacher)
        userRepository.save(teacher)

        when: "we query for the proposals"
        def proposals = questionProposalService.listTeacherQuestionProposals(teacher.getId());

        then: "the list is has that proposal"
        proposals.size() == 1
        proposals.get(0).getId() == questionProposalRepository.findAll().get(0).getId()
        proposals.get(0).getKey() == PROPOSAL_KEY
        proposals.get(0).getTitle() == QUESTION_TITLE
        proposals.get(0).getContent() == QUESTION_CONTENT
        proposals.get(0).getStatus() == QuestionProposal.Status.PENDING
    }

    def "teacher's course execution has multiple question proposals"() {
        given: "a course execution"
        def courseExecution = generateCourseExecution();
        and: "multiple submited proposals for that course execution"
        submitProposalsToExecution(courseExecution, 5);
        and: "a teacher in that course execution"
        def teacher = new User("BALTASAR", "BD", 3, User.Role.TEACHER)
        teacher.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(teacher)
        userRepository.save(teacher)

        when: "we query for the proposals"
        def proposals = questionProposalService.listTeacherQuestionProposals(teacher.getId());

        then: "the list is has the same number of proposals"
        proposals.size() == 5
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

    private CourseExecution generateCourseExecution() {
        def course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        def courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        course.addCourseExecution(courseExecution)
        courseExecution.setCourse(course)
        courseExecutionRepository.save(courseExecution)

        return courseExecution;
    }

    private void submitProposalsToExecution(CourseExecution courseExecution, int n) {
        def student = new User("PEDRO", "PC", 2, User.Role.STUDENT)
        student.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(student)
        userRepository.save(student)

        def proposalDto = new QuestionProposalDto()
        proposalDto.setKey(PROPOSAL_KEY)
        proposalDto.setTitle(QUESTION_TITLE)
        proposalDto.setContent(QUESTION_CONTENT)
        proposalDto.setStatus(QuestionProposal.Status.PENDING.name())

        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setSequence(0)
        optionDto.setCorrect(true)

        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        proposalDto.setOptions(options)

        for (int i = 0; i < n; i++) {
            questionProposalService.submitQuestionProposal(courseExecution.getCourse().getId(),
                student.getId(),
                proposalDto)
        }
    }
}

