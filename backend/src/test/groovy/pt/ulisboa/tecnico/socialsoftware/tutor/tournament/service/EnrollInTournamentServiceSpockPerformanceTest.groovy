package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.Tournament
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
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService

import java.util.*
import java.time.LocalDateTime
import spock.lang.Specification

@DataJpaTest
class EnrollInTournamentServiceSpockPerformanceTest extends Specification{
    public static final String      USER_NAME = 'Name'
    public static final String      USER_USERNAME = 'username'
    public static final String      CREATOR_NAME = 'CName'
    public static final String      CREATOR_USERNAME = 'c_username'
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

    @Autowired TopicRepository topicRepository

    def tournament
    def tournamentId
    def course
    def courseExecution
    def user
    def topic

    def setup(){
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        topic = new Topic()
        topic.setName(TOPIC_NAME)
        topicRepository.save(topic);

        user = new User(CREATOR_NAME, CREATOR_USERNAME, 1001, User.Role.STUDENT)
        userRepository.save(user)

        tournament = new Tournament()
        tournament.setKey(1)
        tournament.setCourseExecution(courseExecution)
        tournament.addTopic(topic)
        tournament.setCreatedBy(user)
        tournament.setCreationDate(DateHandler.now())
        tournament.setBeginDate(DateHandler.now().plusDays(1))
        tournament.setIsCancelled(false)
        tournamentRepository.save(tournament)
    }

    def "performance testing to enroll 1000 students in one tournament"(){

        given: "a open tournament"
        tournamentId = tournamentRepository.findAll().get(0).getId()

        and: "1000 a student ids"
        1.upto(1,{
            user = new User(USER_NAME, USER_USERNAME + it, it, User.Role.STUDENT)
            user.addCourse(courseExecution)
            userRepository.save(user)
        })


        when:
        1.upto(1,{
            tournamentService.enrollInTournament(userRepository.findByUsername(USER_USERNAME + it).getId(), tournamentId)
        })

        then:
        true
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
