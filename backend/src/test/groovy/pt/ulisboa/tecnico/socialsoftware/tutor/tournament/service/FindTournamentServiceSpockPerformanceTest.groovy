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
class FindTournamentsServiceSpockPerformanceTest extends Specification {

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

    @Shared CREATED_DATE_STRING = DateHandler.toISOString(DateHandler.now())
    @Shared BEGIN_DATE_STRING = DateHandler.toISOString(DateHandler.now().plusDays(2))
    @Shared END_DATE_STRING= DateHandler.toISOString(DateHandler.now().plusDays(10))


    def "performance testing to get 1000 tournaments"() {
        
        given: "a course"
        def course  = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)
        and: 'a course execution'
        def courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)
        and: 'a tournamentDto'
        def tournamentDto = new TournamentDto()
        tournamentDto.setCreationDate(CREATED_DATE_STRING)
        tournamentDto.setBeginDate(BEGIN_DATE_STRING)
        tournamentDto.setEndDate(END_DATE_STRING)
        tournamentDto.setName(TOURNAMENT_NAME)
        and: '1000 tournaments'
         1.upto(1, {
             def tournament = new Tournament(tournamentDto)
             tournament.setCourseExecution(courseExecution)
             tournament.setName(COURSE_NAME + it)
             tournamentRepository.save(tournament)
        })

        when:
        1.upto(1, { tournamentService.findOpenTournaments(courseExecution.getId())})

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