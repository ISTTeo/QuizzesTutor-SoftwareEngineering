package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import javax.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.*;
import java.util.Set;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;

@Entity
@Table(name = "tournaments", indexes = { @Index(name = "tounament_indx_0", columnList = "key") })
public class Tournament implements DomainEntity{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column
  private Integer key;

  private String name;

  @Column
  private Boolean isCancelled;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User createdBy;

  @ManyToOne
  @JoinColumn(name = "course_execution_id")
  private CourseExecution courseExecution;

  @ManyToMany(mappedBy = "tournaments")
  private Set<Topic> topics = new HashSet<>();

  @Column
  private Integer numberOfQuestions;

  @ManyToMany(mappedBy = "enrolledIn")
  private Set<User> enrolled = new HashSet<>();

  @OneToOne
  @JoinColumn(name="quiz_id")
  private Quiz quiz;

  @Column
  private LocalDateTime creationDate;

  @Column
  private LocalDateTime beginDate;

  @Column
  private LocalDateTime endDate;

  public Tournament() {
  }

  public Tournament(TournamentDto tournamentDto) {
    this.id = tournamentDto.getId();
    this.key = tournamentDto.getKey();
    setName(tournamentDto.getName());
    this.numberOfQuestions = tournamentDto.getNumberOfQuestions();
    setCreationDate(DateHandler.toLocalDateTime(tournamentDto.getCreationDate()));
    setBeginDate(DateHandler.toLocalDateTime(tournamentDto.getBeginDate()));
    setEndDate(DateHandler.toLocalDateTime(tournamentDto.getEndDate()));
    this.isCancelled = false;
  }

  @Override
    public void accept(Visitor visitor) {
        visitor.visitTournament(this);
    }

  public Integer getId() {
    return this.id;
  }

  public Integer getKey() {
    if (this.key == null) {
      generateKeys();
    }

    return this.key;
  }

  public String getName() {
    return this.name;
  }

  public User getCreatedBy() {
    return this.createdBy;
  }

  public Boolean isCancelled(){
    return this.isCancelled;
  }

  public Set<Topic> getTopics() {
    return this.topics;
  }

  public Integer getNumberOfQuestions() {
    return this.numberOfQuestions;
  }

  public Set<User> getEnrolled() {
    return this.enrolled;
  }

  public LocalDateTime getCreationDate() {
    return this.creationDate;
  }

  public LocalDateTime getBeginDate() {
    return this.beginDate;
  }

  public LocalDateTime getEndDate() {
    return this.endDate;
  }

  public CourseExecution getCourseExecution() {
    return this.courseExecution;
  }

  public Quiz getQuiz(){
    if(!this.enrolled.isEmpty()){
      return this.quiz;
    }
    return null;
  }

  public void setKey(Integer key) {
    this.key = key;
  }

  public void setName(String n) {
    checkName(n);
    this.name = n;
  }

  public void setCreatedBy(User u) {
    this.createdBy = u;
    u.addCreatedTournament(this);
  }

  public void setIsCancelled(Boolean c){
    this.isCancelled = c;
  }


  public void addTopic(Topic t) {
    this.topics.add(t);
    t.addTournament(this);
  }

  public void setNQuestions(Integer n) {
    this.numberOfQuestions = n;
  }

  public void addEnrolled(User u) {
    this.enrolled.add(u);
    u.addEnrolledIn(this);
  }

  public void setCreationDate(LocalDateTime d) {
    this.creationDate = d;
  }


  public void setBeginDate(LocalDateTime d) {
    checkBeginDate(d);
    this.beginDate = d;
  }


  public void setEndDate(LocalDateTime d) {
    checkEndDate(d);
    this.endDate = d;
  }

  public void setCourseExecution(CourseExecution c) {
    this.courseExecution = c;
    c.addTournament(this);
  }

  public void setQuiz(Quiz q){
    this.quiz = q;
    q.setTournament(this);
  }

  public void checkName(String n) {
    if (n == null || n.trim().length() == 0) {
      throw new TutorException(TOURNAMENT_NO_NAME);
    }
  }

  public void checkBeginDate(LocalDateTime d) {
    if (d == null) {
      throw new TutorException(TOURNAMENT_NO_BEGIN);
    } else if (this.endDate != null && this.endDate.isBefore(d)) {
      throw new TutorException(TOURNAMENT_DATE_CONFLICT);
    }
  }

  public void checkEndDate(LocalDateTime d) {
    if (d == null) {
      throw new TutorException(TOURNAMENT_NO_END);
    } else if (this.beginDate != null && this.beginDate.isAfter(d)) {
      throw new TutorException(TOURNAMENT_DATE_CONFLICT);
    }
  }

  public boolean isOpen() {
    return this.beginDate.isAfter(LocalDateTime.now());
  }

  private void generateKeys() {
    Integer max = this.courseExecution.getTournaments().stream().filter(t -> t.key != null).map(Tournament::getKey)
        .max(Comparator.comparing(Integer::valueOf)).orElse(0);

    List<Tournament> nullKeyTournaments = this.courseExecution.getTournaments().stream().filter(t -> t.key == null)
        .collect(Collectors.toList());

    for (Tournament t : nullKeyTournaments) {
      max++;
      t.setKey(max);
    }
  }

}
