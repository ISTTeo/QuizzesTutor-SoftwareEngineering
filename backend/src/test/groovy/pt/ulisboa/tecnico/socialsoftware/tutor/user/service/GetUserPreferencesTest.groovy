package pt.ulisboa.tecnico.socialsoftware.tutor.user.service

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserStats
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserPreferences
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserPreferencesDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserStatsDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

import spock.lang.Specification

@DataJpaTest
class GetUserPreferencesTest extends Specification {
    @Autowired
    UserRepository userRepository

    @Autowired
    UserService userService

    def 'the user does not exist' () {
        when: 'we query for a non-existant student'
        userService.getUserPreferences(42);

        then: 'an exception is thrown'
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.USER_NOT_FOUND
    }

    def 'the user exists and has the default preferences' () {
        given: 'a student'
        def student = userService.createUser('Pedro', 'pc', User.Role.STUDENT)

        when: 'we retrieve the preferences'
        def preferences = userService.getUserPreferences(student.getId());

        then: 'the user has the default preferences'
        preferences.getAnswerStatsVisibility() == new UserPreferences().getAnswerStatsVisibility();
        preferences.getTournamentStatsVisibility() == new UserPreferences().getTournamentStatsVisibility();
    }

    def 'the user exists and has specific preferences' () {
        given: 'a querier student'
        def querier = userService.createUser('Baltasar', 'bsd', User.Role.STUDENT)
        and: 'a target student'
        def target = userService.createUser('Pedro', 'pc', User.Role.STUDENT)
        and: 'custom preferences'
        def customPreferences = new UserPreferencesDto()
        customPreferences.setAnswerStatsVisibility(false)
        customPreferences.setTournamentStatsVisibility(false)
        customPreferences.setProposalVisibility(false)
        userService.updateUserPreferences(target.getId(), customPreferences)

        when: 'we retrieve the preferences'
        def preferences = userService.getUserPreferences(target.getId())

        then: 'the user has the custom preferences'
        preferences.getAnswerStatsVisibility() == false
        preferences.getProposalVisibility() == false
        def stats = userService.getUserStats(querier.getId(), target.getId(), true)
        stats.getHasNumberOfTeacherQuizzes() == false
        stats.getHasNumberOfInClassQuizzes() == false
        stats.getHasNumberOfStudentQuizzes() == false
        stats.getHasNumberOfTeacherAnswers() == false
        stats.getHasNumberOfInClassAnswers() == false
        stats.getHasNumberOfStudentAnswers() == false
        stats.getHasNumberOfCorrectTeacherAnswers() == false
        stats.getHasNumberOfCorrectInClassAnswers() == false
        stats.getHasNumberOfCorrectStudentAnswers() == false
        stats.getHasNumberOfSolvedTournaments() == false
        stats.getHasAverageTournamentScore() == false
        stats.getHasNumberOfAcceptedProposals() == false
        stats.getHasNumberOfSubmitedProposals() == false
    }

    @TestConfiguration
    static class TextContextConfiguration {
        @Bean
        UserService userService() {
            return new UserService()
        }
    }
}
