package pt.ulisboa.tecnico.socialsoftware.tutor.user.service

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserPreferencesDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

import spock.lang.Specification

@DataJpaTest
class UpdateUserPreferencesTest extends Specification {
    @Autowired
    UserRepository userRepository

    @Autowired
    UserService userService

    def 'the user does not exist' () {
        given: 'user preferences'
        def preferences = new UserPreferencesDto();
        preferences.setAnswerStatsVisibility(false);
        preferences.setTournamentStatsVisibility(false);

        when: 'we update the preferences for a non-existant student'
        userService.updateUserPreferences(42, preferences);

        then: 'an exception is thrown'
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.USER_NOT_FOUND
    }

    def 'the user updates the preferences with null (yelding the default preferences)' () {
        given: 'a student'
        def student = userService.createUser('Pedro', 'pc', User.Role.STUDENT)

        when: 'we update with null'
        def preferences = userService.updateUserPreferences(student.getId(), null);

        then: 'the user has the default preferences'
        preferences.getAnswerStatsVisibility() == true
        preferences.getTournamentStatsVisibility() == true
    }

    def 'the user updates the preferences' () {
        given: 'a student'
        def student = userService.createUser('Pedro', 'pc', User.Role.STUDENT)
        and: 'user preferences'
        def preferences = new UserPreferencesDto();
        preferences.setAnswerStatsVisibility(false);
        preferences.setTournamentStatsVisibility(false);

        when: 'we update with custom preferences'
        def newPreferences = userService.updateUserPreferences(student.getId(), preferences)

        then: 'answers are not visible'
        preferences.getAnswerStatsVisibility() == false
        preferences.getTournamentStatsVisibility() == false
    }

    @TestConfiguration
    static class TextContextConfiguration {
        @Bean
        UserService userService() {
            return new UserService()
        }
    }
}
