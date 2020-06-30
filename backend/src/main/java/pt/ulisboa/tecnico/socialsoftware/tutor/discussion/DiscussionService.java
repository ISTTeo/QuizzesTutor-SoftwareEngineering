package pt.ulisboa.tecnico.socialsoftware.tutor.discussion;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.QuestionAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

@Service
public class DiscussionService {
    @Autowired
    private DiscussionRepository discussionRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionAnswerRepository questionAnswerRepository;

    @Autowired
    private QuizAnswerRepository quizAnswerRepository;

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Boolean changeDiscussionPublicStatus(Integer discussionId, Boolean isPublic) {
        Discussion discussion = findDiscussion(discussionId);

        discussion.setIsPublic(isPublic);
        return discussion.getIsPublic();
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<DiscussionDto> checkUserDiscussionsStatus(int userId) {
        return discussionRepository.findAll().stream().filter(d -> d.getUser().getId() == userId)
                .map(DiscussionDto::new).collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<DiscussionDto> getQuestionsDiscussions(List<Integer> intQuestionList) {
        return discussionRepository.findAll().stream()
                .filter(d -> intQuestionList.contains(d.getQuestion().getId()))
                .map(DiscussionDto::new).collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<DiscussionDto> getQuestionPublicDiscussions(Integer questionId) {
        if (questionId == null)
            throw new TutorException(QUESTION_DOES_NOT_EXIST);
        return discussionRepository.findAll().stream()
                .filter(d -> questionId.equals(d.getQuestion().getId()) && d.getIsPublic())
                .map(DiscussionDto::new).collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public QuestionDto getDiscussionQuestion(Integer discussionId) {
        Discussion discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new TutorException(DISCUSSION_NOT_FOUND, discussionId));
        return new QuestionDto(discussion.getQuestion());
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public CourseDto findDiscussionCourse(Integer discussionId) {
        Discussion discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new TutorException(DISCUSSION_NOT_FOUND, discussionId));
        Integer questionId = discussion.getQuestion().getId();
        return questionRepository.findById(questionId).map(Question::getCourse).map(CourseDto::new)
                .orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, questionId));
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public DiscussionDto createDiscussion(Integer questionId, Integer userId, DiscussionDto discussionDto) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, questionId));

        Integer questionAnswerId = discussionDto.getQuestionAnswerId();
        // UserDto userDto = discussionDto.getUserDto();

        SetDiscussionDtoKey(discussionDto);

        // Create discussion
        Discussion discussion = new Discussion(discussionDto);

        // Set question
        discussion.setQuestion(question);

        // // Check if question answer exists
        QuestionAnswer questionAnswer = CheckIfQuestionAnswerExists(questionAnswerId, discussion);

        // Check if user exists
        User user = CheckIfUserExists(userId, discussion, discussionDto);

        // Check if user is a student
        CheckIfUserIsStudent(user);

        // Check if user answered the question
        CheckIfStudentAnsweredQuestion(user, questionAnswerId);

        // Check if title is valid
        CheckTitle(discussionDto, discussion);

        // Check if content is valid
        CheckContent(discussionDto, discussion);

        // Check if number of replies is valid
        checkNumberOfReplies(discussionDto, discussion);

        discussion.setIsPublic(false);

        entityManager.persist(discussion);

        return new DiscussionDto(discussion);
    }

    private void SetDiscussionDtoKey(DiscussionDto discussionDto) {
        if (discussionDto.getKey() == null) {
            if (discussionRepository.count() == 0)
                discussionDto.setKey(1);
            else
                discussionDto.setKey(discussionRepository.getMaxDiscussionKey() + 1);
        }
    }

    private QuestionAnswer CheckIfQuestionAnswerExists(Integer questionAnswerId, Discussion discussion)
            throws TutorException {
        QuestionAnswer questionAnswer = questionAnswerRepository.findById(questionAnswerId)
                .orElseThrow(() -> new TutorException(QUESTION_ANSWER_NOT_FOUND, questionAnswerId));
        discussion.setQuestionAnswerId(questionAnswerId);
        return questionAnswer;
    }

    private User CheckIfUserExists(Integer userId, Discussion discussion, DiscussionDto discussionDto)
            throws TutorException {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));
        discussion.setUser(user);

        return user;
    }

    private void CheckIfUserIsStudent(User user) throws TutorException {
        User.Role role = user.getRole();
        if (!role.equals(User.Role.STUDENT))
            throw new TutorException(USER_NOT_A_STUDENT);
    }

    private void CheckIfStudentAnsweredQuestion(User user, Integer questionAnswerId) throws TutorException {
        QuestionAnswer questionAnswer = questionAnswerRepository.findById(questionAnswerId)
                .orElseThrow(() -> new TutorException(QUESTION_ANSWER_NOT_FOUND, questionAnswerId));
        Integer quizAnswerId = questionAnswer.getQuizAnswer().getId();
        QuizAnswer quizAnswer = quizAnswerRepository.findById(quizAnswerId)
                .orElseThrow(() -> new TutorException(QUIZ_ANSWER_NOT_FOUND, quizAnswerId));
        if (quizAnswer.getUser().getUsername() != user.getUsername())
            throw new TutorException(USER_DIDNT_ANSWER_QUESTION);
    }

    private void CheckTitle(DiscussionDto discussionDto, Discussion discussion) throws TutorException {
        if (discussionDto.getTitle() == "")
            throw new TutorException(EMPTY_TITLE);
        else if (discussionDto.getTitle() == " ")
            throw new TutorException(BLANK_TITLE);
        discussion.setTitle(discussionDto.getTitle());
    }

    private void CheckContent(DiscussionDto discussionDto, Discussion discussion) throws TutorException {
        if (discussionDto.getContent() == "")
            throw new TutorException(EMPTY_CONTENT);
        else if (discussionDto.getContent() == " ")
            throw new TutorException(BLANK_CONTENT);
        discussion.setContent(discussionDto.getContent());
    }

    private void checkNumberOfReplies(DiscussionDto discussionDto, Discussion discussion) throws TutorException {
        if (discussionDto.getNumberOfReplies() != 0)
            throw new TutorException(WRONG_NUMBER_REPLIES);
        discussion.setNumberOfReplies(discussionDto.getNumberOfReplies());
    }

    private Discussion findDiscussion(Integer discussionId) throws TutorException {
        if (discussionId == null)
            throw new TutorException((DISCUSSION_DOES_NOT_EXIST));

        Discussion discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new TutorException(DISCUSSION_NOT_FOUND, discussionId));
            
        return discussion;
    }
}