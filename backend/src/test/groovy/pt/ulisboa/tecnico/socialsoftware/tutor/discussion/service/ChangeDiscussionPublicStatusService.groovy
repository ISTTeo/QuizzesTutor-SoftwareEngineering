package pt.ulisboa.tecnico.socialsoftware.tutor.discussion.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.Discussion
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.DiscussionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.DiscussionService
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.DiscussionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException

import spock.lang.Specification
import spock.lang.Unroll


@DataJpaTest
class ChangeDiscussionPublicStatusService extends Specification {
    public static final String USER_NAME = "name"
    public static final String USER_USERNAME = "username"
    public static final String STUDENT_NAME = "student_name"
    public static final String STUDENT_USERNAME = "student_username"
    public static final String EMPTY_STRING = ""
    public static final String BLANK_STRING = " "

    @Autowired
    DiscussionService discussionService

    @Autowired
    UserRepository userRepository

    @Autowired
    DiscussionRepository discussionRepository


    def userDto
    def discussion
    def discussionDto
    def teacher


    def setup() {
        discussion = new Discussion()
        discussion.setKey(1)
        discussionRepository.save(discussion)

        discussionDto = new DiscussionDto()
        discussionDto.setId(discussion.getId())
        discussionDto.setKey(discussion.getKey())

        teacher = new User(USER_NAME, USER_USERNAME, 1, User.Role.TEACHER)
        userRepository.save(teacher)

        userDto = new UserDto(teacher)
    }

    def "make discussion public"() {
        // the discution is made public
        when: "we change the discussion status"
        def result = discussionService.changeDiscussionPublicStatus(discussion.getId(), true)

        then: "the returned data are correct"
        result == true
    }

    def "make discussion not public"() {
        // the discution is made not public
        when: "we change the discussion status"
        def result = discussionService.changeDiscussionPublicStatus(discussion.getId(), false)

        then: "the returned data are correct"
        result == false
    }

    def "the discussion does not exist"() {
        // an exception is thrown
        given: "a flag"
        def isPublic = false
        and: "a discussionId"
        def discussionId = null

        when: "we change the discussion status"
        def result = discussionService.changeDiscussionPublicStatus(discussionId, isPublic)

        then: "the returned data are correct"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.DISCUSSION_DOES_NOT_EXIST
    }

    def "the discussion was not created"() {
        // an exception is thrown
        given: "a flag"
        def isPublic = false
        and: "a discussionId"
        def discussionId = 0

        when: "we change the discussion status"
        def result = discussionService.changeDiscussionPublicStatus(discussionId, isPublic)

        then: "the returned data are correct"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.DISCUSSION_NOT_FOUND
    }

    @TestConfiguration
    static class DiscussionServiceImplementTextConfiguration {

        @Bean
        DiscussionService discussionService() {
            return new DiscussionService()
        }
    }
}
