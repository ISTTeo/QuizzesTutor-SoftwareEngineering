package pt.ulisboa.tecnico.socialsoftware.tutor.discussion;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;

import javax.persistence.*;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@Table(name = "discussions", indexes = { @Index(name = "discussion_indx_0", columnList = "key") })
public class Discussion implements DomainEntity {

    @SuppressWarnings("unused")

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private Integer key;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @Column(name = "question_answer_id")
    private Integer questionAnswerId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "discussion_title", columnDefinition = "TEXT")
    private String title;

    @Column(name = "discussion_content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "number_of_replies", columnDefinition = "integer default 0")
    private Integer numberOfReplies = 0;

    @Column(name = "is_public")
    private Boolean isPublic = false;

    public Discussion() {
    }

    public Discussion(DiscussionDto discussionDto) {
        this.id = discussionDto.getId();
        this.key = discussionDto.getKey();
        this.title = discussionDto.getTitle();
        this.content = discussionDto.getContent();
        this.numberOfReplies = discussionDto.getNumberOfReplies();
        this.isPublic = discussionDto.getIsPublic();
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitDiscussion(this);
    }

    public Integer getId() {
        return id;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Integer getQuestionAnswerId() {
        return questionAnswerId;
    }

    public void setQuestionAnswerId(Integer questionAnswerId) {
        this.questionAnswerId = questionAnswerId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        this.user.addDiscussion(this);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getNumberOfReplies() {
        return numberOfReplies;
    }

    public void setNumberOfReplies(Integer numberOfReplies) {
        this.numberOfReplies = numberOfReplies;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    @Override
    public String toString() {
        return "DiscussionDto{" + ", id=" + id + ", user=" + user + ", title='" + title + '\'' + ", content='" + content
                + '\'' + ", numberOfReplies=" + numberOfReplies + ", isPublic=" + isPublic + '}';
    }
}
