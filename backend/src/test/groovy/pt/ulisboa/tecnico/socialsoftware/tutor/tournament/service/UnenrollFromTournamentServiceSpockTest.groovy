package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentRepository
import spock.lang.Specification
import spock.lang.Unroll
import spock.lang.Shared
import pt.ulisboa.tecnico.socialsoftware.tutor.course.*
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService

import java.util.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@DataJpaTest
class UnenrollFromTournamentServiceSpockTest extends Specification {

    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String TOPIC_NAME = "Testing"
    public static final String TOURNAMENT_NAME = "Tournament Name"
    public static final String USER_NAME= "Pedro Correia"
    public static final String USER_USERNAME = "stramer1"
    public static final String USER2_NAME= "Pedro Correia2"
    public static final String USER2_USERNAME = "stramer12"

    @Autowired
    TournamentService tournamentService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    UserRepository  userRepository

    @Autowired
    TopicRepository topicRepository

    @Shared CREATED_DATE  = DateHandler.now()
    @Shared BEGIN_DATE    = DateHandler.now().plusDays(2)
    @Shared END_DATE      = DateHandler.now().plusDays(10)



    def topic
    def course
    def courseExecution
    def tournament
    def user
    def user2

    def setup(){
        course  = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        user = new User(USER_NAME, USER_USERNAME, 1, User.Role.STUDENT)
        user.addCourse(courseExecution)
        courseExecution.addUser(user)
        userRepository.save(user)

        user2 = new User(USER2_NAME, USER2_USERNAME, 2, User.Role.TEACHER)
        user2.addCourse(courseExecution)
        courseExecution.addUser(user2)
        userRepository.save(user2)

        topic = new Topic()
        topic.setName(TOPIC_NAME)
        topicRepository.save(topic)

        tournament = new Tournament()
        tournament.setKey(1)
        tournament.setCourseExecution(courseExecution)
        tournament.addTopic(topic)
        tournament.setCreatedBy(user)
        tournament.setCreationDate(CREATED_DATE)
        tournament.setBeginDate(BEGIN_DATE)
        tournament.setEndDate(END_DATE)
        tournament.setName(TOURNAMENT_NAME)
    }

    def "Sucessfully unenroll from a tournament"() {

        given: "a student id"
        def studentId = userRepository.findByUsername(USER_USERNAME).getId()

        and: "a open tournament"
        tournament.addEnrolled(user)
        tournament.addEnrolled(user2)
        tournamentRepository.save(tournament)
        def tournamentId = tournamentRepository.findAll().get(0).getId()

        when:
        def result = tournamentService.unenrollFromTournament(user.getId(), tournamentId)

        then: "the tournament no longer has the student enrolled"
        def t = tournamentRepository.findAll().get(0)
        def u = userRepository.findByUsername(USER_USERNAME)
        t.getEnrolled().size() == 1
        t.getEnrolled().contains(user) == false
        u.getEnrolledIn().contains(t) == false;

    }
    
    def "Unsucessfully unenroll from a tournament"() {

        given: "a student id"
        def studentId = userRepository.findByUsername(USER_USERNAME).getId()

        and: "a open tournament"
        tournament.addEnrolled(user2)
        tournamentRepository.save(tournament)
        def tournamentId = tournamentRepository.findAll().get(0).getId()

        when:
        tournamentService.unenrollFromTournament(user.getId(), tournamentId)

        then: "the tournament no longer has the student enrolled"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.USER_NOT_ENROLLED_TOURNAMENT
    }

    @TestConfiguration
    static class TournamentServiceImplTestContextConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }

        @Bean
        QuizService quizService() {
            return new QuizService()
        }

        @Bean
        AnswerService answerService() {
            return new AnswerService()
        }

        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }

        @Bean
        AnswersXmlImport xmlImporter() {
            return new AnswersXmlImport()
        }
    }
}
