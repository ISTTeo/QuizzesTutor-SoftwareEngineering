package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

@Service
public class TournamentService {

  @Autowired
  private CourseExecutionRepository courseExecutionRepository;

  @Autowired
  private TopicRepository topicRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private TournamentRepository tournamentRepository;

  @Autowired
  private QuizRepository quizRepository;


  @PersistenceContext
  EntityManager entityManager;

  @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
  @Transactional(isolation = Isolation.REPEATABLE_READ)
  public TournamentDto createTournament(Integer courseExecutionId, TournamentDto tournamentDto, Integer userId) {
    CourseExecution courseExecution = courseExecutionRepository.findById(courseExecutionId)
        .orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, courseExecutionId));

    User creator = userRepository.findById(userId).orElseThrow(() -> new TutorException(TOURNAMENT_NO_CREATOR, userId));
    if (creator.getRole() != User.Role.STUDENT && creator.getRole() != User.Role.ADMIN
        && creator.getRole() != User.Role.DEMO_ADMIN) {
      throw new TutorException(TOURNAMENT_WRONG_ROLE, creator.getUsername());
    }

    if (!userHasThisExecution(creator, courseExecutionId)) {
      throw new TutorException(ACCESS_DENIED);
    }
    
    Set<Topic> topics = getTopics(tournamentDto);

    Set<User> enrolled = getEnrolled(tournamentDto);


    if (!enrolled.contains(creator)) {
      enrolled.add(creator);
    }

    Tournament tournament = buildTournament(tournamentDto, courseExecution, creator, topics, enrolled);
    tournamentRepository.save(tournament);

    if(tournamentDto.getQuiz() != null){
      Quiz quiz = new Quiz(tournamentDto.getQuiz());
      Set<Question> availableQuestions = new HashSet<>();

      for(Topic t : topics){
          availableQuestions.addAll(t.getQuestions());
      }
      
      if (availableQuestions.size() < tournamentDto.getQuiz().getNumberOfQuestions()) {
          throw new TutorException(NOT_ENOUGH_QUESTIONS);
      }
      for (Question q : creator.filterQuestionsByStudentModel(tournamentDto.getQuiz().getNumberOfQuestions(), availableQuestions.stream().collect(Collectors.toList()))){
          new QuizQuestion(quiz, q, quiz.getQuizQuestions().size()+1);
      }
      quiz.setType(Quiz.QuizType.TOURNAMENT.toString());
      quiz.setCourseExecution(courseExecution);
      tournament.setQuiz(quiz);
      quizRepository.save(quiz);
      
    }

    return new TournamentDto(tournament, true);
  }

  @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
  @Transactional(isolation = Isolation.REPEATABLE_READ)
  public TournamentDto enrollInTournament(Integer studentId, Integer tournamentId) {

    if (studentId == null) {
      throw new TutorException(USER_NOT_SPECIFIED);
    }

    if (tournamentId == null) {
      throw new TutorException(TOURNAMENT_NOT_SPECIFIED);
    }

    Tournament tournament = getTournamentById(tournamentId);

    User user = getUserById(studentId);
    
    checkUserRole(user);

    checkIsTournamentOpen(tournament);

    checkIsTournamentCancelled(tournament);

    checkStudentCourseExecution(tournament, user);

    checkDoubleEnrollment(tournament, user);
    
    tournament.addEnrolled(user);
    
    user.addEnrolledIn(tournament);

    return new TournamentDto(tournament, true);
  }

  @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
  @Transactional(isolation = Isolation.REPEATABLE_READ)
  public TournamentDto unenrollFromTournament(Integer studentId, Integer tournamentId) {

    if (studentId == null) {
      throw new TutorException(USER_NOT_SPECIFIED);
    }

    if (tournamentId == null) {
      throw new TutorException(TOURNAMENT_NOT_SPECIFIED);
    }

    Tournament tournament = getTournamentById(tournamentId);
    User user = getUserById(studentId);
    
    checkIsTournamentOpen(tournament);

    unenroll(tournament, user);

    return new TournamentDto(tournament, true);
  }

  private void unenroll(Tournament t, User u){
    if(t.getEnrolled().contains(u) && u.getEnrolledIn().contains(t)){
      t.getEnrolled().remove(u);
      u.getEnrolledIn().remove(t);
    }else{
      throw new TutorException(USER_NOT_ENROLLED_TOURNAMENT, u.getUsername());
    }
  }

  @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
  @Transactional(isolation = Isolation.REPEATABLE_READ)
  public CourseDto findTournamentCourseExecution(int tournamentId) {
    return this.tournamentRepository.findById(tournamentId).map(Tournament::getCourseExecution).map(CourseDto::new)
        .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));
  }

  @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
  @Transactional(isolation = Isolation.REPEATABLE_READ)
  public List<TournamentDto> findOpenTournaments(int executionId) {
    LocalDateTime now = DateHandler.now();

    Comparator<Tournament> comparator = Comparator
        .comparing(Tournament::getCreationDate, Comparator.nullsFirst(Comparator.reverseOrder()))
        .thenComparing(Tournament::getBeginDate, Comparator.nullsFirst(Comparator.reverseOrder()))
        .thenComparing(Tournament::getEndDate, Comparator.nullsFirst(Comparator.reverseOrder()));

    return tournamentRepository.findTournaments(executionId).stream()
        .filter(t -> t.getBeginDate().isAfter(now))
        .sorted(comparator)
        .map(t -> new TournamentDto(t, true)).collect(Collectors.toList());
  }

  @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
  @Transactional(isolation = Isolation.REPEATABLE_READ)
  public TournamentDto cancelTournament(Integer tournamentId, Integer userId){
    if (userId == null) {
      throw new TutorException(USER_NOT_SPECIFIED);
    }

    if (tournamentId == null) {
      throw new TutorException(TOURNAMENT_NOT_SPECIFIED);
    }
    Tournament tournament = getTournamentById(tournamentId);
    User user = getUserById(userId);

    checkUserRole(user);

    checkIsCreator(user, tournament);

    checkIsTournamentOpen(tournament);

    tournament.setIsCancelled(true);

    entityManager.persist(tournament);

    return new TournamentDto(getTournamentById(tournamentId), true);
  }

  @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
  @Transactional(isolation = Isolation.REPEATABLE_READ)
  public List<TournamentDto> findEnrolledCancelledTournaments(Integer studentId, Integer courseExecutionId){
    if (studentId == null) {
      throw new TutorException(USER_NOT_SPECIFIED);
    }
    User user = getUserById(studentId);

    return user.getEnrolledIn().stream()
                .filter(Tournament::isCancelled)
                .filter(t -> t.getCourseExecution().getId() == courseExecutionId)
                .map(t -> new TournamentDto(t, true))
                .collect(Collectors.toList());
  }

  @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
  @Transactional(isolation = Isolation.REPEATABLE_READ)
  public List<TournamentDto> findSolvedTournaments(Integer studentId, Integer courseExecutionId){
    if (studentId == null) {
      throw new TutorException(USER_NOT_SPECIFIED);
    }
    User user = getUserById(studentId);

    return user.getEnrolledIn().stream()
                .filter(t -> !t.isCancelled())
                .filter(t-> DateHandler.now().isBefore(t.getEndDate()))
                .filter(t -> t.getCourseExecution().getId().equals(courseExecutionId))
                .filter(t -> t.getQuiz() != null)
                .filter(t -> t.getQuiz().getQuizAnswers().stream().anyMatch(a -> a.getUser().getId().equals(studentId)))
                .map(t -> new TournamentDto(t, true))
                .collect(Collectors.toList());
  }

  private Tournament buildTournament(TournamentDto tournamentDto, CourseExecution courseExecution, User creator,
      Set<Topic> topics, Set<User> enrolled) {
    Tournament tournament = new Tournament(tournamentDto);
    tournament.setCourseExecution(courseExecution);
    tournament.setCreatedBy(creator);
    for (Topic t : topics) {
      tournament.addTopic(t);
    }
    for (User u : enrolled) {
      tournament.addEnrolled(u);
    }
    if (tournamentDto.getCreationDate() == null) {
      tournament.setCreationDate(DateHandler.now());
    }
    
    return tournament;
  }


  private Tournament getTournamentById(Integer tournamentId) {
    return tournamentRepository.findById(tournamentId)
        .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));
  }

  private User getUserById(Integer studentId) {
    return userRepository.findById(studentId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, studentId));
  }

  private void checkIsTournamentOpen(Tournament tournament) {
    if (!tournament.isOpen()) {
      throw new TutorException(CLOSED_TOURNAMENT);
    }
  }

  private void checkDoubleEnrollment(Tournament tournament, User user) {
    if (tournament.getEnrolled().contains(user)) {
      throw new TutorException(DOUBLE_ENROLLMENT);
    }
  }

  private void checkStudentCourseExecution(Tournament tournament, User user) {
    if (user.getCourseExecutions() == null || !user.getCourseExecutions().contains(tournament.getCourseExecution())) {
      throw new TutorException(DIFF_COURSE_EXECS);
    }
  }

  private void checkUserRole(User user) throws TutorException {
    if (!(user.getRole().equals(User.Role.STUDENT) || user.getRole().equals(User.Role.ADMIN)
        || user.getRole().equals(User.Role.DEMO_ADMIN))) {
      throw new TutorException(NOT_A_STUDENT);
    }
  }

  private void checkIsTournamentCancelled(Tournament tournament){
    if(tournament.isCancelled()){
      throw new TutorException(CANCELLED_TOURNAMENT);
    }
  }

  private void checkIsCreator(User user, Tournament tournament){
    if (!user.equals(tournament.getCreatedBy())){
      throw new TutorException(NOT_THE_CREATOR);
    }
  }

  private Set<Topic> getTopics(TournamentDto tournamentDto) {
    Set<Topic> topics = new HashSet<>();
    if (tournamentDto.getTopics() != null && !tournamentDto.getTopics().isEmpty()) {
      for (TopicDto topicDto : tournamentDto.getTopics()) {
        Topic topic = topicRepository.findById(topicDto.getId())
            .orElseThrow(() -> new TutorException(TOPIC_NOT_FOUND, topicDto.getId()));
        topics.add(topic);
      }
    } else {
      throw new TutorException(TOURNAMENT_NO_TOPICS);
    }
    return topics;
  }

  private Set<User> getEnrolled(TournamentDto tournamentDto) {
    Set<User> enrolled = new HashSet<>();

    if (tournamentDto.getEnrolled() != null && !tournamentDto.getEnrolled().isEmpty()) { 
      for (UserDto userDto : tournamentDto.getEnrolled()) {
        User user = userRepository.findById(userDto.getId())
            .orElseThrow(() -> new TutorException(USER_NOT_FOUND, userDto.getId()));
        if (user.getRole() == User.Role.TEACHER) {
          throw new TutorException(TOURNAMENT_WRONG_ROLE, user.getUsername());
        }
        enrolled.add(user);
      }
    }
    return enrolled;
  }

  private boolean userHasThisExecution(User user, int id) {
    return user.getCourseExecutions().stream().anyMatch(courseExecution -> courseExecution.getId() == id);
  }
}
