package pt.ulisboa.tecnico.socialsoftware.tutor.discussion.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean


import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.Discussion
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.DiscussionService
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.DiscussionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.reply.Reply
import pt.ulisboa.tecnico.socialsoftware.tutor.reply.ReplyService
import pt.ulisboa.tecnico.socialsoftware.tutor.reply.ReplyRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException

import spock.lang.Specification
import spock.lang.Unroll

@DataJpaTest
class CheckUserDiscussionsStatusServiceSpockPerformanceTest extends Specification {
    static final String USER_NAME = "student"
    static final String USER_USERNAME = "studentuser"
    static final String USER_NAME_2 = "teacher"
    static final String USER_USERNAME_2 = "teacheruser"
    static final String DISCUSSION_TITLE = "Title"
    static final String DISCUSSION_CONTENT = "Content"
    static final Integer DISCUSSION_NUM_REPLIES = 2
    static final String REPLY_CONTENT = "replycontent"
    static final Integer NON_ZERO_NUMBER = 1

    @Autowired
    DiscussionService discussionService

    @Autowired
    DiscussionRepository discussionRepository

    @Autowired
    DiscussionRepository replyRepository

    @Autowired
    UserRepository userRepository

    def "performance testing to check a user's discussion status with 500 discussions each with 2 replies"() {
        given: "a student"
        def student = new User(USER_NAME, USER_USERNAME, 1, User.Role.STUDENT)
        userRepository.save(student)
        def teacher = new User(USER_NAME_2, USER_USERNAME_2, 2, User.Role.TEACHER)
        userRepository.save(teacher)
        and: "2 iterators"
        Integer d = 1
        Integer r = 1
        and: "500 discussions each with 2 replies"
        1.upto(1, {
            def discussion = new Discussion()
            discussion.setKey(d)
            discussion.setUser(student)
            discussion.setTitle(DISCUSSION_TITLE + d)
            discussion.setContent(DISCUSSION_CONTENT + d)
            discussion.setNumberOfReplies(DISCUSSION_NUM_REPLIES)
            discussionRepository.save(discussion)
            d = d + 1
            1.upto(1,{
                def reply = new Reply()
                reply.setKey(r)
                reply.setDiscussionId(discussion.getId())
                reply.setUser(teacher)
                reply.setReplyContent(REPLY_CONTENT + r)
                replyRepository.save(reply)
                r = r + 1

            })

        })

        when: "we check the user status"
        discussionService.checkUserDiscussionsStatus(student.getId())

        then:
        true
    }

    @TestConfiguration
    static class DiscussionServiceImplementTextConfiguration {

        @Bean
        DiscussionService discussionService() {
            return new DiscussionService()
        }
    }
}