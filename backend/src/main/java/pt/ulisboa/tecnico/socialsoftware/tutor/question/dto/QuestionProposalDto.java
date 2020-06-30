package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.QuestionProposal;

import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import java.util.stream.Collectors;


import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class QuestionProposalDto implements Serializable {
    private Integer id;
    private Integer key;
    private String title;
    private String content;
    private String status;
    private UserDto author;
    private Integer courseId;
    private List<OptionDto> options = new ArrayList<>();
    private List<TopicDto> topics = new ArrayList<>();
    private ImageDto image;
    private String reason;

    public QuestionProposalDto() {
    }

    public QuestionProposalDto(QuestionProposal questionProposal) {
        this.id = questionProposal.getId();
        this.title = questionProposal.getTitle();
        this.content = questionProposal.getContent();
        this.status = questionProposal.getStatus().name();
        this.options = questionProposal.getOptions().stream().map(OptionDto::new).collect(Collectors.toList());
        this.topics = questionProposal.getTopics().stream().map(TopicDto::new).collect(Collectors.toList());
        this.author = new UserDto(questionProposal.getAuthor());
        this.reason = questionProposal.getReason();
        this.courseId = questionProposal.getCourse().getId();

        if (questionProposal.getImage() != null)
            this.image = new ImageDto(questionProposal.getImage());
    }

    public Integer getId() {
        return id;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public Integer getKey() {
        return key;
    }

    public void setAuthor(UserDto user) {
        this.author = user;
    }

    public UserDto getAuthor() {
        return this.author;
    }

    public void setCourseId(Integer id) {
        this.courseId = id;
    }

    public Integer getCourseId() {
        return this.courseId;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ImageDto getImage() {
        return image;
    }

    public void setImage(ImageDto image) {
        this.image = image;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public String getContent() {
        return content;
    }

    public List<OptionDto> getOptions() {
        return options;
    }

    public void setOptions(List<OptionDto> options) {
        this.options = options;
    }

    public List<TopicDto> getTopics() {
        return topics;
    }

    public void setTopics(List<TopicDto> options) {
        this.topics = topics;
    }


    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

}
