package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.Tournament
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
class FindEnrolledCancelledTournamentsServiceSpockTest extends Specification {

    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String TOPIC_NAME = "Testing"
    public static final String TOURNAMENT_NAME = "Tournament Name"
    public static final String USER_NAME= "Pedro Correia"
    public static final String USER_USERNAME = "stramer1"
    public static final String      USER1_NAME      = 'Name1'
    public static final String      USER1_USERNAME  = 'username1'

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    TournamentService tournamentService

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    UserRepository  userRepository

    @Autowired
    TopicRepository topicRepository
    
    @Shared CREATED_DATE_STRING = DateHandler.toISOString(DateHandler.now())
    @Shared BEGIN_DATE_STRING = DateHandler.toISOString(DateHandler.now().plusDays(2))
    @Shared END_DATE_STRING= DateHandler.toISOString(DateHandler.now().plusDays(10))



    def topic
    def course
    def courseExecution
    def tournamentDto
    def tournament
    def user
    def student

    def setup(){
        course  = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        topic = new Topic()
        topic.setName(TOPIC_NAME)
        topicRepository.save(topic)

        user = new User(USER_NAME, USER_USERNAME, 1, User.Role.STUDENT)
        
        tournament = new Tournament()
        tournament.setKey(1)
        tournament.setCourseExecution(courseExecution)
        tournament.addTopic(topic)
        tournament.setCreatedBy(user)
        tournament.addEnrolled(user)
        tournament.setIsCancelled(true)
        tournamentRepository.save(tournament)

        user.addEnrolledIn(tournament)
        userRepository.save(user)
    }

    def "create an cancelled tournament and fetch it"() {

        given: "a user"
        def userId = userRepository.findByUsername(USER_USERNAME).getId()

        when:
        def tours = tournamentService.findEnrolledCancelledTournaments(userId, courseExecution.getId())

        then: "the canceled tournament is fetched"
        tours.size() == 1L
        def tour = tours.get(0)
        tour.isCancelled() == true
    }

    def "cancelled tournament, find cancelled tournaments in other courseExecution"() {

        given: "a user"
        def userId = userRepository.findByUsername(USER_USERNAME).getId()

        when:
        def tours = tournamentService.findEnrolledCancelledTournaments(userId, courseExecution.getId() + 1)

        then: "user is not enrolled in any cancelled tournament of that course execution"
        tours.isEmpty()
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
