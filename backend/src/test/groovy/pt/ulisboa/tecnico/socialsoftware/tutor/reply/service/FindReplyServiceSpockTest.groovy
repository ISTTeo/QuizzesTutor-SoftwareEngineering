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
import pt.ulisboa.tecnico.socialsoftware.tutor.reply.Reply
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException

import spock.lang.Specification
import spock.lang.Unroll


@DataJpaTest
class FindReplyServiceSpockTest extends Specification {
    public static final String REPLY_CONTENT = "Reply Content"
    public static final String USER_NAME = "name"
    public static final String USER_USERNAME = "username"
    public static final String STUDENT_NAME = "student_name"
    public static final String STUDENT_USERNAME = "student_username"

    @Autowired
    ReplyService replyService

    @Autowired
    ReplyRepository replyRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    DiscussionRepository discussionRepository


    def reply
    def discussion
    def teacher


    def setup() {
        discussion = new Discussion()
        discussion.setKey(1)
        discussionRepository.save(discussion)

        teacher = new User(USER_NAME, USER_USERNAME, 1, User.Role.TEACHER)
        userRepository.save(teacher)

        reply = new Reply()
        reply.setKey(1)
    }

    def "create a reply and fetch it"() {

        given: "a reply"
        reply.setUser(teacher)
        reply.setDiscussionId(discussion.getId())
        reply.setReplyContent(REPLY_CONTENT)
        replyRepository.save(reply)

        when: "we search for the reply"
        def result = replyService.findReply(discussion.getId())

        then: "the correct reply is fetched"
        result.get(0).getId() == reply.getId()
        result.get(0).getKey() == 1
        result.get(0).getUserDto().getUsername() == USER_USERNAME
        result.get(0).getDiscussionId() == discussion.getId()
        result.get(0).getReplyContent() == REPLY_CONTENT
    }

    def "fetch from discussion with no replies"() {
        when: "we search for the reply"
        def result = replyService.findReply(discussion.getId())

        then: "the correct reply is fetched"
        result == []
    }

    @TestConfiguration
    static class ReplyServiceImplTestContextConfiguration {

        @Bean
        ReplyService replyService(){
            return new ReplyService()
        }
    }
}