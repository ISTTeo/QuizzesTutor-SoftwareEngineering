package pt.ulisboa.tecnico.socialsoftware.tutor.discussion.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean


import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.Discussion
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.DiscussionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.DiscussionService
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.DiscussionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException

import spock.lang.Specification
import spock.lang.Unroll

@DataJpaTest
class GetQuestionPublicDiscussionsServiceSpockTest extends Specification {
    static final String USER_NAME = "student"
    static final String USER_USERNAME = "studentuser"
    static final String USER_NAME_2 = "teacher"
    static final String USER_USERNAME_2 = "teacheruser"
    static final String DISCUSSION_TITLE = "Title"
    static final String DISCUSSION_CONTENT = "Content"
    public static final String OPTION_CONTENT = "optioncontent"
    static final Integer DISCUSSION_NUM_REPLIES = 2
    static final String QUESTION_CONTENT = "questioncontent"
    static final String QUESTION_TITLE = "questiontitle"
    static final Integer NON_ZERO_NUMBER = 1

    @Autowired
    DiscussionService discussionService

    @Autowired
    DiscussionRepository discussionRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    UserRepository userRepository

    def "create a question and a public discussion associated with it and check"() {
        given: "a student"
        def student = new User(USER_NAME, USER_USERNAME, 1, User.Role.STUDENT)
        userRepository.save(student)
        and: "a question"       
        def question = new Question()
        question.setKey(1)
        question.setTitle(QUESTION_TITLE)
        question.setContent(QUESTION_CONTENT)
        questionRepository.save(question)
        and:"a discussion associated with that question"
        def discussion = new Discussion()
        discussion.setKey(1)
        discussion.setUser(student)
        discussion.setIsPublic(true)
        discussion.setQuestion(question)
        discussion.setTitle(DISCUSSION_TITLE)
        discussion.setContent(DISCUSSION_CONTENT)
        discussion.setNumberOfReplies(DISCUSSION_NUM_REPLIES)
        discussionRepository.save(discussion)


        when: "we get the discussions"
        def result = discussionService.getQuestionPublicDiscussions(question.getId())

        then:"the returned data are correct"
        result.get(0).getId() == discussion.getId()
        result.get(0).getIsPublic() == true
    }

    def "create a question and a private discussion associated with it and check"() {
        given: "a student"
        def student = new User(USER_NAME, USER_USERNAME, 1, User.Role.STUDENT)
        userRepository.save(student)
        and: "a question"       
        def question = new Question()
        question.setKey(1)
        question.setTitle(QUESTION_TITLE)
        question.setContent(QUESTION_CONTENT)
        questionRepository.save(question)
        and:"a discussion associated with that question"
        def discussion = new Discussion()
        discussion.setKey(1)
        discussion.setUser(student)
        discussion.setQuestion(question)
        discussion.setTitle(DISCUSSION_TITLE)
        discussion.setContent(DISCUSSION_CONTENT)
        discussion.setNumberOfReplies(DISCUSSION_NUM_REPLIES)
        discussionRepository.save(discussion)

        when: "we get the discussions"
        def result = discussionService.getQuestionPublicDiscussions(question.getId())

        then:"the returned data are correct"
        result.size == 0
    }

    def "null question test"() {
        given: "a student"
        def student = new User(USER_NAME, USER_USERNAME, 1, User.Role.STUDENT)
        userRepository.save(student)
        and:"a discussion associated with that question"
        def discussion = new Discussion()
        discussion.setKey(1)
        discussion.setUser(student)
        discussion.setTitle(DISCUSSION_TITLE)
        discussion.setContent(DISCUSSION_CONTENT)
        discussion.setNumberOfReplies(DISCUSSION_NUM_REPLIES)
        discussionRepository.save(discussion)

        when: "we get the discussions"
        def result = discussionService.getQuestionPublicDiscussions(null)

        then:"the returned data are correct"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.QUESTION_DOES_NOT_EXIST
    }

    @TestConfiguration
    static class DiscussionServiceImplementTextConfiguration {

        @Bean
        DiscussionService discussionService() {
            return new DiscussionService()
        }
    }
}