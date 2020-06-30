package pt.ulisboa.tecnico.socialsoftware.tutor.reply;

import java.io.Serializable;

import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.Discussion;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.DiscussionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

public class ReplyDto implements Serializable {
    // Id; Discussion; User; ReplyContent
    private Integer id;
    private Integer key;
    private Integer discussionId;
    private UserDto userDto;
    private String replyContent;

    public ReplyDto() {}

    public ReplyDto(Reply reply){
        this.id = reply.getId();
        this.key = reply.getKey();
        this.discussionId = reply.getDiscussionId();
        this.userDto = new UserDto(reply.getUser());
        this.replyContent = reply.getReplyContent();
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public Integer getKey() { return key; }

    public void setKey(Integer key) {this.key = key;}

    public void setDiscussionId(Integer discussionId) {this.discussionId = discussionId;}

    public Integer getDiscussionId() {return this.discussionId;}

    public UserDto getUserDto() { return userDto; }

    public void setUserDto(UserDto userDto) { this.userDto = userDto; }

    public String getReplyContent() { return replyContent; }

    public void setReplyContent(String replyContent) { this.replyContent = replyContent; }

    @Override
    public String toString() {
        return "ReplyDto{" +
                ", id=" + id +
                ", key=" + key +
                ", discussionId = " + discussionId +
                ", user=" + userDto +
                ", replyContent='" + replyContent +
                '}';
    }
}