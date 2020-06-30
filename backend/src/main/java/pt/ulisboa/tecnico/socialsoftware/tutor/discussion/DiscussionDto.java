package pt.ulisboa.tecnico.socialsoftware.tutor.discussion;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.QuestionAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.Discussion;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import java.io.Serializable;

public class DiscussionDto implements Serializable {
    private Integer id;
    private Integer key;
    private QuestionDto questionDto;
    private Integer questionAnswerId;
    private UserDto userDto;
    private String title;
    private String content;
    private Integer numberOfReplies;
    private Boolean isPublic = false;

    public DiscussionDto() {
    }

    public DiscussionDto(Discussion discussion) {
        this.id = discussion.getId();
        this.key = discussion.getKey();
        this.questionDto = new QuestionDto();
        this.questionAnswerId = discussion.getQuestionAnswerId();
        this.userDto = new UserDto(discussion.getUser());
        this.title = discussion.getTitle();
        this.content = discussion.getContent();
        this.numberOfReplies = discussion.getNumberOfReplies();
        this.isPublic = discussion.getIsPublic();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public Integer getQuestionAnswerId() {
        return questionAnswerId;
    }

    public void setQuestionAnswerId(Integer questionAnswerId) {
        this.questionAnswerId = questionAnswerId;
    }

    public UserDto getUserDto() {
        return userDto;
    }

    public void setUserDto(UserDto userDto) {
        this.userDto = userDto;
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
        return "DiscussionDto{" + ", id=" + id + ", user=" + userDto + ", title='" + title + '\'' + ", content='"
                + content + '\'' + ", numberOfReplies=" + numberOfReplies + ", isPublic=" + isPublic + '}';
    }
}