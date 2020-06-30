package pt.ulisboa.tecnico.socialsoftware.tutor.reply;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.sql.SQLException;

import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.Discussion;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.DiscussionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.DiscussionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReplyService {
    //Id; DiscussionId; UserId; ReplyContent
    @Autowired
    private ReplyRepository replyRepository;

    @Autowired
    private DiscussionRepository discussionRepository;

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<ReplyDto> findReply(int discussionId) {
        return this.replyRepository.findByDiscussionId(discussionId).stream().map(reply -> new ReplyDto(reply)).collect(Collectors.toList());
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ReplyDto createReply(Integer discussionId, ReplyDto replyDto, Integer userId){
        // DiscussionDto discussionDto = replyDto.getDiscussionDto();CreateReplyServiceSpockTest

        setCourseKey(replyDto);

        // Create Reply
        Reply reply = new Reply(replyDto);

        // Check if discussion exists
        checkIfDiscussionExists(discussionId, reply);

        // Check if user exists
        User user = checkIfUserExists(userId, reply);

        // Check if user is a teacher or a student
        checkIfUserIsTeacherOrStudent(user);

        // check if reply content is valid
        checkReplyContent(replyDto, reply);

        entityManager.persist(reply);

        return new ReplyDto(reply);
    }

    private void checkReplyContent(ReplyDto replyDto, Reply reply) {
        if(replyDto.getReplyContent() == "")
            throw new TutorException(EMPTY_REPLY_CONTENT);
        else if (replyDto.getReplyContent() == " ")
            throw new TutorException(BLANK_REPLY_CONTENT);
        reply.setReplyContent(replyDto.getReplyContent());
    }

    private void checkIfUserIsTeacherOrStudent(User user) {
        User.Role role = user.getRole();
        if(!role.equals(User.Role.TEACHER) && !role.equals(User.Role.STUDENT))
            throw new TutorException(USER_NOT_A_TEACHER_OR_A_STUDENT);
    }

    private User checkIfUserExists(Integer userId, Reply reply) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));
        reply.setUser(user);

        return user;
    }

    private void checkIfDiscussionExists(Integer discussionId, Reply reply) {
        if(discussionId == null)
            throw new TutorException((DISCUSSION_DOES_NOT_EXIST));

        Discussion discussion = discussionRepository.findById(discussionId).orElseThrow(() -> new TutorException(DISCUSSION_NOT_FOUND, discussionId));
        discussion.setNumberOfReplies(discussion.getNumberOfReplies() + 1);
        reply.setDiscussionId(discussionId);
    }

    private void setCourseKey(ReplyDto replyDto) {
        if(replyDto.getKey() == null){
            if (replyRepository.count() == 0)
                replyDto.setKey(1);
            else
                replyDto.setKey(replyRepository.getMaxReplyKey() + 1);
        }
            
    }
}
