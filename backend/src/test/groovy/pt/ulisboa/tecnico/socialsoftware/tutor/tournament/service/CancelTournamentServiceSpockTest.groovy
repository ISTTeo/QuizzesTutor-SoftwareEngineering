package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import java.util.*

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

import spock.lang.Specification
import spock.lang.Unroll
import spock.lang.Shared

import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService

@DataJpaTest
class CancelTournamentServiceSpockTest extends Specification{

    public static final String      TOURNAMENT_NAME = 'Tournament_test'
    public static final String      USER_NAME = 'Name'
    public static final String      USER_USERNAME = 'username'
    public static final String      USER1_NAME = 'Name1'
    public static final String      USER1_USERNAME = 'username1'
    public static final String      USER2_NAME = 'Name2'
    public static final String      USER2_USERNAME = 'username2'
    public static final String      COURSE_NAME = "Software Engineering"
    public static final String      ACRONYM = "ES1"
    public static final String      ACADEMIC_TERM = "1 SEM"
    public static final String      TOPIC_NAME = "Testing"

    @Autowired
    TournamentService tournamentService

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired 
    TopicRepository topicRepository


    def tournament
    def course
    def courseExecution
    def student
    def user
    def dateBefore
    def dateAfter
    def topic

    def setup(){
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        dateBefore = DateHandler.now().plusDays(-1)
        dateAfter = DateHandler.now().plusDays(1)

        topic = new Topic()
        topic.setName(TOPIC_NAME)
        topicRepository.save(topic)

        user = new User(USER_NAME, USER_USERNAME, 1, User.Role.STUDENT)
        userRepository.save(user)

        student = new User(USER1_NAME, USER1_USERNAME, 2, User.Role.STUDENT)
        student.addCourse(courseExecution)
        userRepository.save(student)

        tournament = new Tournament()
        tournament.setKey(1)
        tournament.setCourseExecution(courseExecution)
        tournament.addTopic(topic)
        tournament.setCreatedBy(student)
        tournament.setIsCancelled(false)

    }

    def "successfully cancels tournament"(){
        given: "an open tournament with 2 students enrolled"
        tournament.setBeginDate(dateAfter)
        tournament.addEnrolled(user)
        tournamentRepository.save(tournament)
        def tournamentId = tournamentRepository.findAll().get(0).getId()

        and: "its creator"
        def creatorId = userRepository.findByUsername(student.getUsername()).getId()

        when: "creator cancels tournament"
        def canceledTournament = tournamentService.cancelTournament(tournamentId, creatorId)

        then: 
        canceledTournament.isCancelled() == true
    }

    def "cancel tournament after begin date"(){
        given: "an already started tournament"
        tournament.setCreationDate( DateHandler.now().plusDays(-2))
        tournament.setBeginDate(dateBefore)
        tournament.addEnrolled(user)
        tournamentRepository.save(tournament)
        def tournamentId = tournamentRepository.findAll().get(0).getId()

        and: "its creator"
        def creatorId = userRepository.findByUsername(student.getUsername()).getId()

        when: "creator tries to cancel tournament"
        tournamentService.cancelTournament(tournamentId, creatorId)

        then: "tournament is not canceled"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.CLOSED_TOURNAMENT
        def t = tournamentRepository.findAll().get(0)
        t.isCancelled() == false
    }

    def "non creator tries to cancel tournament"(){
        given: "an open tournament"
        tournament.setCreationDate( DateHandler.now().plusDays(-2))
        tournament.setBeginDate(dateBefore)
        tournament.addEnrolled(user)
        tournamentRepository.save(tournament)
        def tournamentId = tournamentRepository.findAll().get(0).getId()

        and: "a user that is not its creator"
        def userId = userRepository.findByUsername(user.getUsername()).getId()

        when: "that user tries to cancel"
        tournamentService.cancelTournament(tournamentId, userId)

        then: "tournament is not canceled"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.NOT_THE_CREATOR
        def t = tournamentRepository.findAll().get(0)
        t.isCancelled() == false
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