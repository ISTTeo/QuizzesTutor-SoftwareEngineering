package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentDto
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
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService

import java.util.*
import java.time.LocalDateTime
import spock.lang.Specification

@DataJpaTest
class EnrollInTournamentServiceSpockTest extends Specification{
    public static final String      USER_NAME       = 'Name'
    public static final String      USER_USERNAME   = 'username'
    public static final String      USER1_NAME      = 'Name1'
    public static final String      USER1_USERNAME  = 'username1'
    public static final String      USER2_NAME      = 'Name2'
    public static final String      USER2_USERNAME  = 'username2'
    public static final String      USER3_NAME      = 'Name3'
    public static final String      USER3_USERNAME  = 'username3'
    public static final String      COURSE_NAME     = "Software Engineering"
    public static final String      ACRONYM         = "ES1"
    public static final String      ACADEMIC_TERM   = "1 SEM"
    public static final String      TOPIC_NAME      = "Testing"
    public static final String      TODAY           = DateHandler.toISOString(DateHandler.now())
    public static final String      TOMORROW        = DateHandler.toISOString(DateHandler.now().plusDays(1))
    public static final String      LATER           = DateHandler.toISOString(DateHandler.now().plusDays(2))
    public static final String      VERSION         = 'B'
    public static final String      QUIZ_TITLE      = 'quiz title'
    public static final String      TOURNAMENT_NAME = "Tournament Name"

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
    QuizRepository quizRepository

    @Autowired TopicRepository topicRepository



    def tournament
    def course
    def courseExecution
    def student
    def student2
    def user
    def dateBefore
    def dateAfter
    def topic
    def quiz
    def tournamentDto

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

        student2 = new User(USER2_NAME, USER2_USERNAME, 3, User.Role.STUDENT)
        student2.addCourse(courseExecution)
        userRepository.save(student2)

        tournament = new Tournament()
        tournament.setKey(1)
        tournament.setCourseExecution(courseExecution)
        tournament.addTopic(topic)
        tournament.setCreatedBy(user)
        tournament.setIsCancelled(false)

        quiz = new Quiz()
        quiz.setKey(1)
        quiz.setScramble(true)
        quiz.setOneWay(true)
        quiz.setCreationDate(DateHandler.now())
        quiz.setAvailableDate(DateHandler.now().plusDays(1))
        quiz.setConclusionDate(DateHandler.now().plusDays(2))
        quiz.setTitle(QUIZ_TITLE)
        quiz.setType(Quiz.QuizType.TOURNAMENT.toString())
        quizRepository.save(quiz)

    }

    def "enroll in tournament is successful"(){
        // user is a student, tournament is open, the student isn't signed up yet and the student is enrolled in
        // the Course Execution associated with the tournament

        given: "a student id"
        def studentId = userRepository.findByUsername(USER1_USERNAME).getId()

        and: "a open tournament"
        tournament.setCreationDate(DateHandler.now())
        tournament.setBeginDate(dateAfter)
        tournamentRepository.save(tournament)
        def tournamentId = tournamentRepository.findAll().get(0).getId()

        when:
        tournamentService.enrollInTournament(studentId, tournamentId)

        then:
        def newTournament = tournamentRepository.findAll().get(0)
        def newUser = userRepository.findByUsername(USER1_USERNAME)
        !newTournament.getEnrolled().isEmpty()
        newTournament.getEnrolled().size() == 1
        newTournament.getEnrolled().contains(newUser)
        newUser.getEnrolledIn().contains(newTournament)
        newTournament.getQuiz() == null;

    }

    def "enroll in tournament without student specified"(){
        // a enroll in a tournament is attempted without being associated with a student
        given: "a tournament"
        tournamentRepository.save(tournament)
        def tournamentId = tournamentRepository.findAll().get(0).getId()

        when:
        tournamentService.enrollInTournament(null, tournamentId)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.USER_NOT_SPECIFIED
        def newTournament = tournamentRepository.findAll().get(0)
        newTournament.getEnrolled().isEmpty()
    }

    def "enroll in no tournament"(){
        // a enroll in an unspecified tournament is attempted
        given: "a student"
        def studentId = userRepository.findByUsername(USER1_USERNAME).getId()

        when:
        tournamentService.enrollInTournament(studentId, null)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_NOT_SPECIFIED
    }

    def "enroll in a closed tournament"(){
        // an enroll in an existing but closed tournament is being attempted
        given: "a closed tournament"
        tournament.setCreationDate(dateBefore)
        tournament.setBeginDate(DateHandler.now())
        tournamentRepository.save(tournament)
        def tournamentId = tournamentRepository.findAll().get(0).getId()

        and: "a student"
        def studentId = userRepository.findByUsername(USER1_USERNAME).getId()

        when:
        tournamentService.enrollInTournament(studentId, tournamentId)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.CLOSED_TOURNAMENT
        def newTournament = tournamentRepository.findAll().get(0)
        newTournament.getEnrolled().isEmpty()
        def newUser = userRepository.findByUsername(USER1_USERNAME)
        newUser.getEnrolledIn().isEmpty()
    }

    def "enroll in tournament, user is not a STUDENT, an ADMIN or an DEMO_ADMIN"(){
        // the user trying to enroll in the tournament is not a student, an admin or a demo_admin
        given: "a tournament"
        tournament.setCreationDate(DateHandler.now())
        tournament.setBeginDate(dateAfter)
        tournamentRepository.save(tournament)
        def tournamentId = tournamentRepository.findAll().get(0).getId()

        and: "a user that is not a student"
        def user = new User(USER3_NAME, USER3_USERNAME, 4, User.Role.TEACHER)
        userRepository.save(user)
        def userId = userRepository.findByUsername(USER3_USERNAME).getId()

        when:
        tournamentService.enrollInTournament(userId, tournamentId)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.NOT_A_STUDENT
        def newTournament = tournamentRepository.findAll().get(0)
        newTournament.getEnrolled().isEmpty()
        def newUser = userRepository.findByUsername(USER2_USERNAME)
        newUser.getEnrolledIn().isEmpty()
    }

    def "student enrolls to a tournament again"(){
        // a student tries to enroll in a tournament it has already signed up to
        given: "a student id"
        def studentId = userRepository.findByUsername(USER1_USERNAME).getId()

        and: "a open tournament"
        tournament.setCreationDate(DateHandler.now())
        tournament.setBeginDate(dateAfter)
        tournamentRepository.save(tournament)
        def tournamentId = tournamentRepository.findAll().get(0).getId()

        when:
        tournamentService.enrollInTournament(studentId, tournamentId)
        tournamentService.enrollInTournament(studentId, tournamentId)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.DOUBLE_ENROLLMENT
        def newTournament = tournamentRepository.findAll().get(0)
        def newUser = userRepository.findByUsername(USER1_USERNAME)
        !newTournament.getEnrolled().isEmpty()
        newTournament.getEnrolled().size() == 1
        newTournament.getEnrolled().contains(newUser)
        newUser.getEnrolledIn().size()==1
        newUser.getEnrolledIn().contains(newTournament)
    }

    def "student enrolls to a tournament, not enrolled in the Course Execution associated with tournament"(){
        // a student tries to enroll in a tournament but is not enrolled in the Course Execution
        // associated with the tournament

        given: "a student enrolled in no course"
        student.setCourseExecutions(null)
        def userId = userRepository.findByUsername(USER1_USERNAME).getId()

        and: "a open tournament"
        tournament.setCreationDate(DateHandler.now())
        tournament.setBeginDate(dateAfter)
        tournamentRepository.save(tournament)
        def tournamentId = tournamentRepository.findAll().get(0).getId()

        when:
        tournamentService.enrollInTournament(userId, tournamentId)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.DIFF_COURSE_EXECS
        def result = tournamentRepository.findAll().get(0)
        result.getEnrolled().isEmpty()
        def newUser = userRepository.findByUsername(USER1_USERNAME)
        newUser.getEnrolledIn().isEmpty()
    }

    def "Second enroll in tournament generates quizz successfully"(){

        given: "a student id "
        def studentId2 = userRepository.findByUsername(USER2_USERNAME).getId()

        and: "a open tournament"
        tournament.setCreationDate(DateHandler.now())
        tournament.setBeginDate(dateAfter)
        tournament.setQuiz(quiz)
        tournament.addEnrolled(user)
        tournamentRepository.save(tournament)
        def tournamentId = tournamentRepository.findAll().get(0).getId()

        when:
        tournamentService.enrollInTournament(studentId2, tournamentId)


        then:
        def newTournament = tournamentRepository.findAll().get(0)
        !newTournament.getEnrolled().isEmpty()
        newTournament.getEnrolled().size() == 2
        newTournament.getQuiz() != null

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
