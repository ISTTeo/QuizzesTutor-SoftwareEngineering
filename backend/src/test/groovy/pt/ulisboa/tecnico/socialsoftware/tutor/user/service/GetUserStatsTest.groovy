package pt.ulisboa.tecnico.socialsoftware.tutor.user.service

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserStatsDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserPreferencesDto

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

import spock.lang.Specification

@DataJpaTest
class GetUserStatsTest extends Specification {
    @Autowired
    UserRepository userRepository

    @Autowired
    UserService userService

    def 'the user does not exist' () {
        given: 'a querier student'
        def student = userService.createUser('Pedro', 'pc', User.Role.STUDENT)
        when: 'we query for a non-existant student'
        userService.getUserStats(student.getId(), 42, true);

        then: 'an exception is thrown'
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.USER_NOT_FOUND
    }

    def 'valid query, without visibility changes' () {
        given: 'a querier'
        def querier = userService.createUser('Baltasar', 'bsd', User.Role.STUDENT)
        and: 'a student'
        def student = userService.createUser('Pedro', 'pc', User.Role.STUDENT)
        when: 'we query for the student'
        def stats = userService.getUserStats(querier.getId(), student.getId(), true);
        then: 'we can check the statistics of the user'
        stats.getUser().getId().intValue() == student.getId().intValue()
        stats.getNumberOfTeacherQuizzes() == 0
        stats.getNumberOfInClassQuizzes() == 0
        stats.getNumberOfStudentQuizzes() == 0
        stats.getNumberOfTeacherAnswers() == 0
        stats.getNumberOfInClassAnswers() == 0
        stats.getNumberOfStudentAnswers() == 0
        stats.getNumberOfCorrectTeacherAnswers() == 0
        stats.getNumberOfCorrectInClassAnswers() == 0
        stats.getNumberOfCorrectStudentAnswers() == 0
        stats.getNumberOfSolvedTournaments() == 0
        stats.getAverageTournamentScore() == 0.0
        stats.getNumberOfDiscussions() == 0
        stats.getNumberOfPublicDiscussions() == 0
        stats.getNumberOfAcceptedProposals() == 0
        stats.getNumberOfSubmitedProposals() == 0
    }

    def 'valid query, with visibility changes and different users' () {
        given: 'a querier'
        def querier = userService.createUser('Baltasar', 'bsd', User.Role.STUDENT)
        and: 'a student'
        def student = userService.createUser('Pedro', 'pc', User.Role.STUDENT)
        and: 'custom preferences: share nothing '
        def preferences = new UserPreferencesDto();
        preferences.setAnswerStatsVisibility(false);
        preferences.setTournamentStatsVisibility(false);
        preferences.setDiscussionStatsVisibility(false);
        preferences.setProposalVisibility(false);
        userService.updateUserPreferences(student.getId(), preferences);

        when: 'we query for the student\'s stats'
        def stats = userService.getUserStats(querier.getId(), student.getId(), true);
        then: 'we can check the statistics of the user'
        stats.getUser().getId().intValue() == student.getId().intValue()
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
        stats.getHasNumberOfDiscussions() == false
        stats.getHasNumberOfPublicDiscussions() == false
        stats.getHasNumberOfAcceptedProposals() == false
        stats.getHasNumberOfSubmitedProposals() == false
    }

    def 'valid query, with visibility changes and the same' () {
        given: 'a student'
        def student = userService.createUser('Pedro', 'pc', User.Role.STUDENT)
        and: 'custom preferences: share nothing '
        def preferences = new UserPreferencesDto();
        preferences.setAnswerStatsVisibility(false);
        preferences.setTournamentStatsVisibility(false);
        preferences.setDiscussionStatsVisibility(false);
        preferences.setProposalVisibility(false);
        userService.updateUserPreferences(student.getId(), preferences);

        when: 'we query for the our stats'
        def stats = userService.getUserStats(student.getId(), student.getId(), true);
        then: 'we can check our statistics'
        stats.getUser().getId().intValue() == student.getId().intValue()
        stats.getHasNumberOfTeacherQuizzes() == true
        stats.getHasNumberOfInClassQuizzes() == true
        stats.getHasNumberOfStudentQuizzes() == true
        stats.getHasNumberOfTeacherAnswers() == true
        stats.getHasNumberOfInClassAnswers() == true
        stats.getHasNumberOfStudentAnswers() == true
        stats.getHasNumberOfCorrectTeacherAnswers() == true
        stats.getHasNumberOfCorrectInClassAnswers() == true
        stats.getHasNumberOfCorrectStudentAnswers() == true
        stats.getHasNumberOfSolvedTournaments() == true
        stats.getHasAverageTournamentScore() == true
        stats.getHasNumberOfDiscussions() == true
        stats.getHasNumberOfPublicDiscussions() == true
        stats.getHasNumberOfAcceptedProposals() == true
        stats.getHasNumberOfSubmitedProposals() == true
    }

    @TestConfiguration
    static class TextContextConfiguration {
        @Bean
        UserService userService() {
            return new UserService()
        }
    }
}
