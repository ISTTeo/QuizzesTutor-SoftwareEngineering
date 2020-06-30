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
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Bean
import org.springframework.boot.test.context.TestConfiguration

import spock.lang.Specification
import spock.lang.Unroll

@DataJpaTest
class EditQuestionProposalTest extends Specification {
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
    UserService userService

    @Autowired
    UserRepository userRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    def course
    def proposalId
    def author

    def setup() {
        course = new Course(COURSE_NAME, Course.Type.TECNICO)

        def courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        course.addCourseExecution(courseExecution)
        courseExecution.setCourse(course)

        courseExecutionRepository.save(courseExecution)
        courseRepository.save(course)

        author = userService.createUser("PEDRO", "PC", User.Role.STUDENT)
        author.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(author)
        userRepository.save(author)

        // add a proposal
        def proposalDto = generateProposalDto();

        questionProposalService.submitQuestionProposal(course.getId(), author.getId(), proposalDto)
        def result = questionProposalRepository.findAll().get(0);
        proposalId = result.getId()
    }

    @Unroll
    def "invalid parameters: | role=#role | userId=#userId | id=#id | decision=#decision || error=#errorMessage"() {
        given: 'a question proposal'
        if (decision != null) {
            questionProposalService.reviewQuestionProposal(proposalId, decision, REASON);
        }
        and: 'a proposalDto'
        def editedProposalDto = generateProposalDto()
        editedProposalDto.setTitle(QUESTION_TITLE + "edited")
        and: 'an editor'
        def editor = role == null ? author : userService.createUser("Baltasar", "bsd", role);
        if (role != null) userRepository.save(editor)

        when: 'an edit is attempted'
        questionProposalService.editQuestionProposal(proposalId + id, editor.getId() + userId, editedProposalDto)

        then: 'an exception is thrown'
        def exception = thrown(TutorException)
        exception.getErrorMessage() == errorMessage

        where:
        role                | userId | id | decision                                ||errorMessage
        User.Role.STUDENT   | 1      | 0  | QuestionProposalService.Decision.ACCEPT ||ErrorMessage.USER_NOT_FOUND
        User.Role.STUDENT   | 1      | 0  | QuestionProposalService.Decision.REJECT ||ErrorMessage.USER_NOT_FOUND
        User.Role.STUDENT   | 1      | 0  | null                                    ||ErrorMessage.USER_NOT_FOUND
        User.Role.TEACHER   | 1      | 0  | QuestionProposalService.Decision.ACCEPT ||ErrorMessage.USER_NOT_FOUND
        User.Role.TEACHER   | 1      | 0  | QuestionProposalService.Decision.REJECT ||ErrorMessage.USER_NOT_FOUND
        User.Role.TEACHER   | 1      | 0  | null                                    ||ErrorMessage.USER_NOT_FOUND
        User.Role.STUDENT   | 0      | 1  | QuestionProposalService.Decision.ACCEPT ||ErrorMessage.QUESTION_PROPOSAL_NOT_FOUND
        User.Role.STUDENT   | 0      | 1  | QuestionProposalService.Decision.REJECT ||ErrorMessage.QUESTION_PROPOSAL_NOT_FOUND
        User.Role.STUDENT   | 0      | 1  | null                                    ||ErrorMessage.QUESTION_PROPOSAL_NOT_FOUND
        User.Role.TEACHER   | 0      | 1  | QuestionProposalService.Decision.ACCEPT ||ErrorMessage.QUESTION_PROPOSAL_NOT_FOUND
        User.Role.TEACHER   | 0      | 1  | QuestionProposalService.Decision.REJECT ||ErrorMessage.QUESTION_PROPOSAL_NOT_FOUND
        User.Role.TEACHER   | 0      | 1  | null                                    ||ErrorMessage.QUESTION_PROPOSAL_NOT_FOUND
        User.Role.TEACHER   | 0      | 0  | QuestionProposalService.Decision.REJECT ||ErrorMessage.QUESTION_PROPOSAL_NOT_ACCEPTED
        User.Role.TEACHER   | 0      | 0  | null                                    ||ErrorMessage.QUESTION_PROPOSAL_NOT_ACCEPTED
        User.Role.STUDENT   | 0      | 0  | QuestionProposalService.Decision.ACCEPT ||ErrorMessage.QUESTION_PROPOSAL_EDITOR_IS_NOT_THE_AUTHOR
        User.Role.STUDENT   | 0      | 0  | QuestionProposalService.Decision.REJECT ||ErrorMessage.QUESTION_PROPOSAL_EDITOR_IS_NOT_THE_AUTHOR
        User.Role.STUDENT   | 0      | 0  | null                                    ||ErrorMessage.QUESTION_PROPOSAL_EDITOR_IS_NOT_THE_AUTHOR
        null                | 0      | 0  | QuestionProposalService.Decision.ACCEPT ||ErrorMessage.QUESTION_PROPOSAL_NOT_PENDING
        null                | 0      | 0  | QuestionProposalService.Decision.REJECT ||ErrorMessage.QUESTION_PROPOSAL_NOT_PENDING
    }

    def "valid edition for a teacher"() {
        given: 'an accepted question proposal'
        questionProposalService.reviewQuestionProposal(proposalId, QuestionProposalService.Decision.ACCEPT, REASON);
        and: 'a proposalDto'
        def editedProposalDto = new QuestionProposalDto()
        editedProposalDto.setKey(PROPOSAL_KEY)
        editedProposalDto.setTitle(QUESTION_TITLE + "edited")
        editedProposalDto.setContent(QUESTION_CONTENT)
        and: 'a teacher'
        def editor = userService.createUser("Baltasar", "bsd", User.Role.TEACHER);
        userRepository.save(author)

        when: 'an edit is attempted'
        questionProposalService.editQuestionProposal(proposalId, editor.getId(), editedProposalDto)

        then: 'the proposal has been updated'
        def newProposal = questionProposalRepository.findById(proposalId).get();
        newProposal.getId() == proposalId
        newProposal.getTitle() == QUESTION_TITLE + "edited"
        newProposal.getKey() == PROPOSAL_KEY
        newProposal.getContent() ==  QUESTION_CONTENT
        newProposal.getOptions().size() ==  1
    }

    def "valid edition for a student (the author)"() {
        given: 'a proposalDto'
        def editedProposalDto = new QuestionProposalDto()
        editedProposalDto.setKey(PROPOSAL_KEY)
        editedProposalDto.setTitle(QUESTION_TITLE + "edited")
        editedProposalDto.setContent(QUESTION_CONTENT)

        when: 'an edit is attempted'
        questionProposalService.editQuestionProposal(proposalId, author.getId(), editedProposalDto)

        then: 'the proposal has been updated'
        def newProposal = questionProposalRepository.findById(proposalId).get();
        newProposal.getId() == proposalId
        newProposal.getTitle() == QUESTION_TITLE + "edited"
        newProposal.getKey() == PROPOSAL_KEY
        newProposal.getContent() ==  QUESTION_CONTENT
        newProposal.getOptions().size() ==  1
    }

    private QuestionProposalDto generateProposalDto() {
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

        return proposalDto;
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

        @Bean
        UserService userService() {
            return new UserService()
        }
    }
}

