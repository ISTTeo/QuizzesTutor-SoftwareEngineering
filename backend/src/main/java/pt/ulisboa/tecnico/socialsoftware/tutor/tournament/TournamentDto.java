package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

public class TournamentDto implements Serializable {
  private Integer id;
  private Integer key;
  private String name;
  private UserDto createdBy;
  private QuizDto quiz = null;
  private List<TopicDto> topics = new ArrayList<>();
  private Integer numberOfQuestions;
  private List<UserDto> enrolled = new ArrayList<>();
  private String creationDate = null;
  private String beginDate = null;
  private String endDate = null;
  private Boolean isCancelled;

  public TournamentDto() {
  }

  public TournamentDto(Tournament tournament, boolean deepCopy) {
    this.id = tournament.getId();
    this.name = tournament.getName();
    this.numberOfQuestions = tournament.getNumberOfQuestions();
    this.isCancelled = tournament.isCancelled();
    if(tournament.getQuiz() !=null)
      this.quiz = new QuizDto(tournament.getQuiz(), deepCopy);
    if (tournament.getCreationDate() != null)
      this.creationDate = DateHandler.toISOString(tournament.getCreationDate());
    if (tournament.getBeginDate() != null)
      this.beginDate = DateHandler.toISOString(tournament.getBeginDate());
    if (tournament.getEndDate() != null)
      this.endDate = DateHandler.toISOString(tournament.getEndDate());
    

    if (deepCopy) {
      this.topics = tournament.getTopics().stream().map(topic -> new TopicDto(topic)).collect(Collectors.toList());
      this.enrolled = tournament.getEnrolled().stream().map(user -> new UserDto(user)).collect(Collectors.toList());

      if (tournament.getCreatedBy() != null)
        this.createdBy = new UserDto(tournament.getCreatedBy());
    }
  }

  public Integer getId() {
    return this.id;
  }

  public Integer getKey() {
    return this.key;
  }

  public String getName() {
    return this.name;
  }

  public UserDto getCreatedBy() {
    return this.createdBy;
  }

  public Boolean isCancelled(){
    return this.isCancelled;
  }

  public List<TopicDto> getTopics() {
    return this.topics;
  }

  public Integer getNumberOfQuestions() {
    return this.numberOfQuestions;
  }

  public List<UserDto> getEnrolled() {
    return this.enrolled;
  }

  public String getCreationDate() {
    return this.creationDate;
  }

  public String getBeginDate() {
    return this.beginDate;
  }

  public String getEndDate() {
    return this.endDate;
  }

  public QuizDto getQuiz() {
    return this.quiz;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public void setKey(Integer key) {
    this.key = key;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setCreatedBy(UserDto u) {
    this.createdBy = u;
  }

  public void setTopics(List<TopicDto> t) {
    this.topics = t;
  }

  public void addTopic(TopicDto t) {
    this.topics.add(t);
  }

  public void setNumberOfQuestions(Integer n) {
    this.numberOfQuestions = n;
  }

  public void setEnrolled(List<UserDto> u) {
    this.enrolled = u;
  }

  public void setCreationDate(String d) {
    this.creationDate = d;
  }

  public void setBeginDate(String d) {
    this.beginDate = d;
  }

  public void setEndDate(String d) {
    this.endDate = d;
  }

  public void setQuiz(QuizDto q){
    this.quiz = q;
  }

  @Override
  public String toString() {
    return "TournamentDto{" + "id=" + id + ", key=" + key + ", name=" + name + ", createdBy='" + createdBy + '\''
        + ", creationDate='" + creationDate + '\'' + ", beginDate='" + beginDate + '\'' + ", endDate='" + endDate + '\''
        + ", topics=" + topics + ", enrolled=" + enrolled + '\'' + ", numberOfQuestions=" + numberOfQuestions + '}';
  }

}