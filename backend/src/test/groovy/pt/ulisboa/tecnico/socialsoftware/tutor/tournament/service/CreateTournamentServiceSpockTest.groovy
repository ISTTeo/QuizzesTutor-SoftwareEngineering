package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
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
class CreateTournamentServiceSpockTest extends Specification {

    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String TOPIC_NAME = "Testing"
    public static final String TOURNAMENT_NAME = "Tournament Name"
    public static final String USER_NAME= "Pedro Correia"
    public static final String USER_USERNAME = "stramer1"

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

    @Shared CREATED_DATE_STRING = DateHandler.toISOString(DateHandler.now())
    @Shared BEGIN_DATE_STRING = DateHandler.toISOString(DateHandler.now().plusDays(2))
    @Shared END_DATE_STRING= DateHandler.toISOString(DateHandler.now().plusDays(10))



    def topic
    def course
    def courseExecution
    def tournament
    def user
    def user2
    def user3

    def setup(){
        course  = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)

        user = new User(USER_NAME, USER_USERNAME, 1, User.Role.STUDENT)
        user.addCourse(courseExecution)
        courseExecution.addUser(user)
        userRepository.save(user)

        user2 = new User(USER_NAME + "2", USER_USERNAME + "2", 2, User.Role.TEACHER)
        user2.addCourse(courseExecution)
        courseExecution.addUser(user2)
        userRepository.save(user2)

        user3 = new User(USER_NAME + "3", USER_USERNAME + "3", 3, User.Role.STUDENT)
        userRepository.save(user3)

        courseExecutionRepository.save(courseExecution)

        topic = new Topic()
        topic.setName(TOPIC_NAME)
        topicRepository.save(topic)

        tournament = new TournamentDto()
    }

    def "correctly create a tournament"() {

        given: "a tournamentDto with dates and name"
        tournament.setCreationDate(CREATED_DATE_STRING)
        tournament.setBeginDate(BEGIN_DATE_STRING)
        tournament.setEndDate(END_DATE_STRING)
        tournament.setName(TOURNAMENT_NAME)
        and: 'a topic dto'
        def topics = new ArrayList<TopicDto>()
        topics.add(new TopicDto(topic))
        tournament.setTopics(topics)

        when:
        tournamentService.createTournament(courseExecution.getId(), tournament, user.getId())

        then: "the correct tournament is inside the repository"
        tournamentRepository.count() == 1L
        def result = tournamentRepository.findAll().get(0)
        result.getId() != null
        result.getTopics().size() == 1
        result.getName() == TOURNAMENT_NAME
        result.getCourseExecution().getId() == courseExecution.getId()
        DateHandler.toISOString(result.getCreationDate()) == CREATED_DATE_STRING
        DateHandler.toISOString(result.getBeginDate()) == BEGIN_DATE_STRING
        DateHandler.toISOString(result.getEndDate()) == END_DATE_STRING
        def userRes = userRepository.findByUsername(result.getCreatedBy().getUsername())
        userRes.getCreatedTournaments().size() == 1
        userRes.getCreatedTournaments().contains(result)
        def topicRes = topicRepository.findAll().get(0)
        topicRes.getTournaments().size() == 1
        topicRes.getTournaments().contains(result)

    }

    def "create tournament with no topics"(){

        given: "a tournamentDto with dates and name"
        tournament.setCreationDate(CREATED_DATE_STRING)
        tournament.setBeginDate(BEGIN_DATE_STRING)
        tournament.setEndDate(END_DATE_STRING)
        tournament.setName(TOURNAMENT_NAME)

        when:
        tournamentService.createTournament(courseExecution.getId(), tournament, user.getId())

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_NO_TOPICS
        tournamentRepository.count() == 0L
    }

    def "create tournament with no creator"(){
        given: "a tournamentDto with begin and end dates and name"
        tournament.setCreationDate(CREATED_DATE_STRING)
        tournament.setBeginDate(BEGIN_DATE_STRING)
        tournament.setEndDate(END_DATE_STRING)
        tournament.setName(TOURNAMENT_NAME)
        and: 'a topic dto'
        def topicDto = new TopicDto(topic)
        def topics = new ArrayList<TopicDto>()
        topics.add(topicDto)
        tournament.setTopics(topics)


        when:
        tournamentService.createTournament(courseExecution.getId(), tournament, -1)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_NO_CREATOR
        tournamentRepository.count() == 0L
    }

    def "create tournament with TEACHER user"(){
        given: "a tournamentDto with dates and name"
        tournament.setCreationDate(CREATED_DATE_STRING)
        tournament.setBeginDate(BEGIN_DATE_STRING)
        tournament.setEndDate(END_DATE_STRING)
        tournament.setName(TOURNAMENT_NAME)
        and: 'a topic dto'
        def topics = new ArrayList<TopicDto>()
        topics.add(new TopicDto(topic))
        tournament.setTopics(topics)

        when:
        tournamentService.createTournament(courseExecution.getId(), tournament, user2.getId())

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_WRONG_ROLE
        tournamentRepository.count() == 0L
    }

    def "create tournament with user not in courseExecution"(){
        given: "a tournamentDto with dates and name"
        tournament.setCreationDate(CREATED_DATE_STRING)
        tournament.setBeginDate(BEGIN_DATE_STRING)
        tournament.setEndDate(END_DATE_STRING)
        tournament.setName(TOURNAMENT_NAME)
        and: 'a topic dto'
        def topics = new ArrayList<TopicDto>()
        topics.add(new TopicDto(topic))
        tournament.setTopics(topics)

        when:
        tournamentService.createTournament(courseExecution.getId(), tournament, user3.getId())

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.ACCESS_DENIED
        tournamentRepository.count() == 0L
    }

    def "create tournament with no course execution"(){
        given: "a tournamentDto with dates and name"
        tournament.setCreationDate(CREATED_DATE_STRING)
        tournament.setBeginDate(BEGIN_DATE_STRING)
        tournament.setEndDate(END_DATE_STRING)
        tournament.setName(TOURNAMENT_NAME)
        and: 'a topic dto'
        def topicDto = new TopicDto(topic)
        def topics = new ArrayList<TopicDto>()
        topics.add(topicDto)
        tournament.setTopics(topics)

        when:
        tournamentService.createTournament(courseExecution.getId() +1, tournament, user.getId())

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.COURSE_EXECUTION_NOT_FOUND
        tournamentRepository.count() == 0L
    }

    @Unroll
    def "Invalid arguments: name=#name| begin=#begin | end=#end || errorMessage=#errorMessage"(){
        given: "a tournament dto"
        tournament.setBeginDate(begin)
        tournament.setEndDate(end)
        tournament.setName(name)
        and: 'a topic'
        def topics = new ArrayList<TopicDto>()
        topics.add(new TopicDto(topic))
        tournament.setTopics(topics)

        when:
        tournamentService.createTournament(courseExecution.getId(), tournament, , user.getId())

        then: 'A TutorException is thrown'
        def exception = thrown(TutorException)
        exception.getErrorMessage() == errorMessage

        where:
        name               |begin              |end                ||errorMessage
        null               |BEGIN_DATE_STRING  |END_DATE_STRING    ||ErrorMessage.TOURNAMENT_NO_NAME
        TOURNAMENT_NAME    |null               |END_DATE_STRING    ||ErrorMessage.TOURNAMENT_NO_BEGIN
        TOURNAMENT_NAME    |BEGIN_DATE_STRING  |null               ||ErrorMessage.TOURNAMENT_NO_END
        TOURNAMENT_NAME    |END_DATE_STRING    |BEGIN_DATE_STRING  ||ErrorMessage.TOURNAMENT_DATE_CONFLICT
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
