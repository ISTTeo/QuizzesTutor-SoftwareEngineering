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
class FindTournamentsServiceSpockTest extends Specification {

    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String TOPIC_NAME = "Testing"
    public static final String TOURNAMENT_NAME = "Tournament Name"
    public static final String USER_NAME= "Pedro Correia"
    public static final String USER_USERNAME = "stramer1"

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
    def user

    def setup(){
        course  = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        topic = new Topic()
        topic.setName(TOPIC_NAME)
        topicRepository.save(topic)

        tournamentDto = new TournamentDto()
        tournamentDto.setKey(1)

        user = new User(USER_NAME, USER_USERNAME, 1, User.Role.STUDENT)
        userRepository.save(user)

    }

    def "create an open tournament and fetch it"() {

        given: "a tournament"
        tournamentDto.setCreationDate(CREATED_DATE_STRING)
        tournamentDto.setBeginDate(BEGIN_DATE_STRING)
        tournamentDto.setEndDate(END_DATE_STRING)
        tournamentDto.setName(TOURNAMENT_NAME)
        def tournament = new Tournament(tournamentDto)
        tournament.setCourseExecution(courseExecution)
        tournamentRepository.save(tournament)

        when:
        def resultService = tournamentService.findOpenTournaments(courseExecution.getId())

        then: "the correct tournament is fetched"
        resultService.size() == 1L
        def result = resultService.get(0)
        result.getId() != null
        result.getName() == TOURNAMENT_NAME
        result.getCreationDate() == CREATED_DATE_STRING
        result.getBeginDate() == BEGIN_DATE_STRING
        result.getEndDate() == END_DATE_STRING

    }

    def "fetch with no tournaments"(){
        when:
        def result = tournamentService.findOpenTournaments(courseExecution.getId())

        then: "An empty array is returned"
        result.size() == 0L
    }

    def "fetch with wrong courseExecutionId"(){
        given: "a tournament in some courseExecution"
        tournamentDto.setCreationDate(CREATED_DATE_STRING)
        tournamentDto.setBeginDate(BEGIN_DATE_STRING)
        tournamentDto.setEndDate(END_DATE_STRING)
        tournamentDto.setName(TOURNAMENT_NAME)
        def tournament = new Tournament(tournamentDto)
        tournament.setCourseExecution(courseExecution)
        tournamentRepository.save(tournament)

        when:
        def result = tournamentService.findOpenTournaments(-1)

        then: "An empty array is returned"
        result.size() == 0L
    }

    def "create a closed tournament and fail to fetch it"() {

        given: "a tournament"
        tournamentDto.setCreationDate(CREATED_DATE_STRING)
        tournamentDto.setBeginDate(CREATED_DATE_STRING)
        tournamentDto.setEndDate(END_DATE_STRING)
        tournamentDto.setName(TOURNAMENT_NAME)
        def tournament = new Tournament(tournamentDto)
        tournament.setCourseExecution(courseExecution)
        tournamentRepository.save(tournament)

        when:
        def result = tournamentService.findOpenTournaments(courseExecution.getId())

        then: "An empty array is returned"
        result.size() == 0L

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
