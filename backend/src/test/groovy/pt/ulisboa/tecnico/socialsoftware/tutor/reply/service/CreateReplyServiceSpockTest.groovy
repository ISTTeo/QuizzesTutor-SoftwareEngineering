package pt.ulisboa.tecnico.socialsoftware.tutor.discussion.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.Discussion
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.DiscussionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.DiscussionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.reply.ReplyRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.reply.ReplyService
import pt.ulisboa.tecnico.socialsoftware.tutor.reply.ReplyDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException

import spock.lang.Specification
import spock.lang.Unroll


@DataJpaTest
class CreateReplyServiceSpockTest extends Specification {
    public static final String REPLY_CONTENT = "Reply Content"
    public static final String USER_NAME = "name"
    public static final String USER_USERNAME = "username"
    public static final String ADMIN_NAME = "admin_name"
    public static final String ADMIN_USERNAME = "admin_username"
    public static final String STUDENT_NAME = "student_name"
    public static final String STUDENT_USERNAME = "student_username"
    public static final String EMPTY_STRING = ""
    public static final String BLANK_STRING = " "

    @Autowired
    ReplyService replyService

    @Autowired
    ReplyRepository replyRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    DiscussionRepository discussionRepository


    def reply
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

        reply = new ReplyDto()
        reply.setKey(1)
    }

    def "the reply does not exist and teacher creates reply"() {
        // the reply is created in reference to a discussion

        given: "a reply"
        reply.setUserDto(userDto)
        reply.setReplyContent(REPLY_CONTENT)
        and: "a discussionId"
        def discussionId = discussion.getId()
 
        when: "we create a new reply"
        def newReply = replyService.createReply(discussionId, reply, userDto.getId())

        then: "the returned data are correct"
        replyRepository.count() == 1L
        def result = replyRepository.findAll().get(0)
        result.getId() != null
        result.getKey() != null
        discussionDto.getId() == result.getDiscussionId()
        userDto.getId() == result.getUser().getId()
        result.getReplyContent() == REPLY_CONTENT
    }

    
    def "the reply does not exist and student creates reply"() {
        // an exception is thrown
        given: "a user that is a student"
        def student = new User(STUDENT_NAME, STUDENT_NAME, 2, User.Role.STUDENT)
        userRepository.save(student)
        userDto = new UserDto(student)

        and: "a reply"
        reply.setUserDto(userDto)
        reply.setReplyContent(REPLY_CONTENT)
        and: "a discussionId"
        def discussionId = discussion.getId()

        when:
        def newReply = replyService.createReply(discussionId, reply, userDto.getId())
        
        then: "the returned data are correct"
        replyRepository.count() == 1L
        def result = replyRepository.findAll().get(0)
        result.getId() != null
        result.getKey() != null
        discussionDto.getId() == result.getDiscussionId()
        userDto.getId() == result.getUser().getId()
        result.getReplyContent() == REPLY_CONTENT
    }


    def "the discussion does not exist"() {
        // an exception is thrown
        given: "a reply"
        reply.setUserDto(userDto)
        reply.setReplyContent(REPLY_CONTENT)
        and: "a discussionId"
        def discussionId = null

        when: "we create a new reply"
        def newReply = replyService.createReply(discussionId, reply, userDto.getId())

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.DISCUSSION_DOES_NOT_EXIST
        replyRepository.count() == 0L
    }

    def "the user does not exist"(){
        //an exception is thrown
        given: "a reply"
        reply.setUserDto(null)
        reply.setReplyContent(REPLY_CONTENT)
        and: "a discussionId"
        def discussionId = discussion.getId()

        when: "we create a new reply"
        def newReply = replyService.createReply(discussionId, reply, -1)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.USER_NOT_FOUND
        replyRepository.count() == 0L
    }

    def "the user is not a teacher or a student"() {
        // an exception is thrown
        given: "a user that is not a teacher or a student"
        def admin = new User(ADMIN_NAME, ADMIN_USERNAME, 3, User.Role.ADMIN)
        userRepository.save(admin)
        userDto = new UserDto(admin)

        and: "a reply"
        reply.setUserDto(userDto)
        reply.setReplyContent(REPLY_CONTENT)
        and: "a discussionId"
        def discussionId = discussion.getId()

        when: "we create a new reply"
        def newReply = replyService.createReply(discussionId, reply, userDto.getId())
        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.USER_NOT_A_TEACHER_OR_A_STUDENT
        replyRepository.count() == 0L
    }

    @Unroll
    def "invalid arguments: replyContent=#replyContent || errorMessage=#errorMessage"() {
        given: "a discussion"
        reply.setUserDto(userDto)
        reply.setReplyContent(replyContent)
        and: "a discussionId"
        def discussionId = discussion.getId()

        when: "we create a new reply"
        def newReply = replyService.createReply(discussionId, reply, userDto.getId())

        then:
        def error = thrown(TutorException)
        error.errorMessage == errorMessage

        where:
        replyContent            || errorMessage
        EMPTY_STRING            || ErrorMessage.EMPTY_REPLY_CONTENT
        BLANK_STRING            || ErrorMessage.BLANK_REPLY_CONTENT

    }

    @TestConfiguration
    static class ReplyServiceImplTestContextConfiguration {

        @Bean
        ReplyService replyService(){
            return new ReplyService()
        }
    }
}
