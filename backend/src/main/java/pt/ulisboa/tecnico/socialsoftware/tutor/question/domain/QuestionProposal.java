package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import javax.persistence.*;
import java.util.List;
import java.util.Set;
import java.util.Comparator;
import java.util.HashSet;
import java.util.stream.Collectors;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionProposalDto;

@Entity
@Table (name = "questionproposals",
        indexes = {
            @Index(name = "questionproposal_indx_0", columnList = "key")
        })
public class QuestionProposal {
    public enum Status {
        PENDING, REJECTED, ACCEPTED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    private Integer key;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String title;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "questionProposal")
    private Image image;

    @OneToMany(mappedBy = "questionProposal", fetch = FetchType.EAGER, orphanRemoval=true)
    private Set<Option> options = new HashSet<>();

    private String reason = "";

    @OneToMany(mappedBy = "questionProposals", fetch = FetchType.EAGER, orphanRemoval=true)
    private Set<Topic> topics = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User author;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    public QuestionProposal() {
    }

    public QuestionProposal(User student, Course course, QuestionProposalDto questionProposalDto) {
        setCourse(course);
        setAuthor(student);
        this.update(questionProposalDto);
        setKey(questionProposalDto.getKey());
    }

    public void update(QuestionProposalDto questionProposalDto) {
        checkConsistentQuestionProposal(questionProposalDto);

        setTitle(questionProposalDto.getTitle());
        setContent(questionProposalDto.getContent());

        if (getOptions().size() == 0 || questionProposalDto.getOptions().size() > 0)
            setOptions(questionProposalDto.getOptions());

        if (questionProposalDto.getImage() != null)
            setImage(new Image(questionProposalDto.getImage()));

        if (questionProposalDto.getStatus() != null)
            this.status = Status.valueOf(questionProposalDto.getStatus());
    }

    public void reSubmit(QuestionProposalDto questionProposalDto) {
        this.update(questionProposalDto);
        this.status = Status.PENDING;
    }

    public Integer getId() {
        return id;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public Integer getKey() {
        if (this.key == null) {
            generateKeys();
        }
        return key;
    }

    private void generateKeys(){
        Integer max = this.course.getQuestionProposals().stream()
            .filter(proposal -> proposal.key != null)
            .map(QuestionProposal::getKey)
            .max(Comparator.comparing(Integer::valueOf))
            .orElse(0);
        List<QuestionProposal> nullKeyProposals = this.course.getQuestionProposals().stream()
            .filter(proposal -> proposal.key == null).collect(Collectors.toList());

        for (QuestionProposal prop: nullKeyProposals) {
            max = max + 1;
            prop.key = max;
        }
    }

    public Status getStatus() {
        return this.status;
    }

    public boolean wasAccepted() {
        return this.status == Status.ACCEPTED;
    }

    public void setAuthor(User author) {
        this.author = author;
        this.author.addProposal(this);
    }

    public User getAuthor() {
        assert this.author != null;
        return this.author;
    }

    private boolean openForReview() {
        return this.status == Status.PENDING;
    }

    public void accept(String reason) throws TutorException {
        if (!openForReview()) {
            throw new TutorException(QUESTION_PROPOSAL_NOT_OPEN_FOR_REVIEW);
        }
        this.reason = reason;
        this.status = Status.ACCEPTED;
    }

    public void reject(String reason) throws TutorException {
        if (!openForReview()) {
            throw new TutorException(QUESTION_PROPOSAL_NOT_OPEN_FOR_REVIEW);
        }

        if (reason == null || reason.length() == 0) {
            throw new TutorException(EMPTY_JUSTIFICATION);
        }
        this.reason = reason;
        this.status = Status.REJECTED;
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

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
        course.addQuestionProposal(this);
    }

    public void addOption(Option option) {
        options.add(option);
    }

    public void addTopic(Topic topic) {
        topics.add(topic);
    }

    public Set<Option> getOptions() {
        return options;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
        image.setQuestionProposal(this);
    }

    public Set<Topic> getTopics() {
        return topics;
    }

    public void remove() {
        getCourse().getQuestionProposals().remove(this);
        course = null;
        getTopics().forEach(topic -> topic.getQuestionProposals().remove(this));
        getTopics().clear();
    }

    @Override
    public String toString() {
        return "QuestionProposal{" +
                "id=" + id +
                ", key=" + key +
                ", content='" + content + '\'' +
                ", title='" + title + '\'' +
                ", status=" + status  +
                ", image=" + image +
                /* ", options=" + options + */
                ", topics=" + topics +
                '}';
    }

    private void checkConsistentQuestionProposal(QuestionProposalDto questionProposalDto) {
        if (    questionProposalDto.getTitle() == null ||
                questionProposalDto.getContent() == null ||
                questionProposalDto.getTitle().trim().length() == 0 ||
                questionProposalDto.getContent().trim().length() == 0
                ) {
            throw new TutorException(QUESTION_MISSING_DATA);
        }

        if (questionProposalDto.getOptions().size() > 0) {
            //If there are options added one must be correct
            if (questionProposalDto.getOptions().stream().noneMatch(OptionDto::getCorrect))
                throw new TutorException(NO_CORRECT_OPTION);

            //If there are options added only one can be correct
            if (questionProposalDto.getOptions().stream().filter(OptionDto::getCorrect).count() > 1)
                throw new TutorException(QUESTION_MULTIPLE_CORRECT_OPTIONS);

            questionProposalDto.getOptions().stream().forEach(this::checkOptionDto);
        }
    }

    private void checkOptionDto(OptionDto optionDto) {
        if (optionDto.getContent() == null || optionDto.getContent().trim() == "")
            throw new TutorException(INVALID_CONTENT_FOR_OPTION);
        if (optionDto.getSequence() == null || optionDto.getSequence().intValue() < 0)
            throw new TutorException(INVALID_SEQUENCE_FOR_OPTION);
    }

    public void updateTopics(Set<Topic> newTopics) {
        Set<Topic> toRemove = this.topics.stream().filter(topic -> !newTopics.contains(topic)).collect(Collectors.toSet());

        toRemove.forEach(topic -> {
            this.topics.remove(topic);
            topic.getQuestionProposals().remove(this);
        });

        newTopics.stream().filter(topic -> !this.topics.contains(topic)).forEach(topic -> {
            this.topics.add(topic);
            topic.getQuestionProposals().add(this);
        });
    }

    public void setOptions(List<OptionDto> options) {
        if (options.stream().filter(OptionDto::getCorrect).count() != 1) {
            throw new TutorException(ONE_CORRECT_OPTION_NEEDED);
        }

        int index = 0;
        for (OptionDto optionDto : options) {
            if (optionDto.getId() == null) {
                optionDto.setSequence(index++);
                Option opt = new Option(optionDto);
                opt.setQuestionProposal(this);
            } else {
                Option option = getOptions()
                        .stream()
                        .filter(op -> op.getId().equals(optionDto.getId()))
                        .findFirst()
                        .orElseThrow(() -> new TutorException(OPTION_NOT_FOUND, optionDto.getId()));

                option.setContent(optionDto.getContent());
                option.setCorrect(optionDto.getCorrect());
            }
        }
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

}
