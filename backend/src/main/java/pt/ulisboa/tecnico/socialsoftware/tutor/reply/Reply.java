package pt.ulisboa.tecnico.socialsoftware.tutor.reply;

import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.Discussion;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;

import javax.persistence.*;

@Entity
@Table(
        name = "replies",
        indexes = {
                @Index(name = "reply_indx_0", columnList = "key")
        })
public class Reply implements DomainEntity {
    // Id; DiscussionId; UserId; ReplyContent

    @SuppressWarnings("unused")

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique=true, nullable = false)
    private Integer key;

    @Column(name = "discussion_id")
    private Integer discussionId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "reply_content", columnDefinition = "TEXT")
    private String replyContent;


    public Reply() {}

    public Reply(ReplyDto replyDto) {
        this.id = replyDto.getId();
        this.key = replyDto.getKey();
        this.replyContent = replyDto.getReplyContent();
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitReply(this);
    }

    public Integer getId() {
        return id;
    }

    public Integer getKey() { return key; }

    public void setKey(Integer key) { this.key = key; }

    public Integer getDiscussionId() { return discussionId; }

    public void setDiscussionId(Integer discussionId) { this.discussionId = discussionId; }

    public User getUser() { return user; }

    public void setUser(User user) {
        this.user = user;
    }

    public String getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }

    @Override
    public String toString() {
        return "ReplyDto{" +
                ", id=" + id +
                ", user=" + user +
                ", replyContent='" + replyContent +
                '}';
    }
}
