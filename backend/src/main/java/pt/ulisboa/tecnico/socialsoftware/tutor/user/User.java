package pt.ulisboa.tecnico.socialsoftware.tutor.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.QuestionProposal;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.Discussion;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.Tournament;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
public class User implements UserDetails {
    public enum Role {
        STUDENT, TEACHER, ADMIN, DEMO_ADMIN
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private Integer key;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(unique = true)
    private String username;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "createdBy", fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Tournament> createdTournaments = new HashSet<>();

    @ManyToMany
    private Set<Tournament> enrolledIn = new HashSet<>();

    private String name;
    private String enrolledCoursesAcronyms;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @Column(name = "last_access")
    private LocalDateTime lastAccess;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<QuizAnswer> quizAnswers = new HashSet<>();

    @ManyToMany( cascade=CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<CourseExecution> courseExecutions = new HashSet<>();

    @Embedded
    private UserStats stats = new UserStats(this);

    @Embedded
    private UserPreferences preferences = new UserPreferences(this);

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "author", fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<QuestionProposal> proposals = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Discussion> discussions = new HashSet<>();

    public User() {
    }

    public User(String name, String username, Integer key, User.Role role) {
        this.name = name;
        setUsername(username);
        this.key = key;
        this.role = role;
        this.creationDate = DateHandler.now();
    }

    public void accept(Visitor visitor) {
        visitor.visitUser(this);
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

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<Tournament> getCreatedTournaments() {
        return this.createdTournaments;
    }

    public void addCreatedTournament(Tournament t) {
        this.createdTournaments.add(t);
    }

    public void removeCreatedTournament(Tournament t){
        this.createdTournaments.remove(t);
    }

    public Set<Tournament> getEnrolledIn() {
        return this.enrolledIn;
    }

    public void addEnrolledIn(Tournament t) {
        this.enrolledIn.add(t);
    }

    public void removeEnrolledIn(Tournament t){
        this.enrolledIn.remove(t);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnrolledCoursesAcronyms() {
        return enrolledCoursesAcronyms;
    }

    public void setEnrolledCoursesAcronyms(String enrolledCoursesAcronyms) {
        this.enrolledCoursesAcronyms = enrolledCoursesAcronyms;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(LocalDateTime lastAccess) {
        this.lastAccess = lastAccess;
    }

    public Set<QuizAnswer> getQuizAnswers() {
        return quizAnswers;
    }

    public Set<CourseExecution> getCourseExecutions() {
        return courseExecutions;
    }

    public void setCourseExecutions(Set<CourseExecution> courseExecutions) {
        this.courseExecutions = courseExecutions;
    }

    public UserPreferences getUserPreferences() {
        return this.preferences;
    }

    public void setUserPreferences(UserPreferences preferences) {
        this.preferences = preferences;
        this.preferences.setUser(this);
    }

    public UserStats getUserStats() {
        return this.stats;
    }

    public void setUserStats(UserStats stats) {
        this.stats = stats;
        this.stats.setUser(this);
    }

    public void increaseNumberOfQuizzes(Quiz.QuizType type) {
        switch (type) {
            case PROPOSED:
                this.stats.setNumberOfTeacherQuizzes(this.stats.getNumberOfTeacherQuizzes() + 1);
                break;
            case IN_CLASS:
                this.stats.setNumberOfInClassQuizzes(this.stats.getNumberOfInClassQuizzes() + 1);
                break;
            case GENERATED:
                this.stats.setNumberOfStudentQuizzes(this.stats.getNumberOfStudentQuizzes() + 1);
                break;
            default:
                break;
        }
    }

    public void increaseNumberOfAnswers(Quiz.QuizType type) {
        switch (type) {
            case PROPOSED:
                this.stats.setNumberOfTeacherAnswers(this.stats.getNumberOfTeacherAnswers() + 1);
                break;
            case IN_CLASS:
                this.stats.setNumberOfInClassAnswers(this.stats.getNumberOfInClassAnswers() + 1);
                break;
            case GENERATED:
                this.stats.setNumberOfStudentAnswers(this.stats.getNumberOfStudentAnswers() + 1);
                break;
            default:
                break;
        }
    }

    public void increaseNumberOfCorrectAnswers(Quiz.QuizType type) {
        switch (type) {
            case PROPOSED:
                this.stats.setNumberOfCorrectTeacherAnswers(this.stats.getNumberOfCorrectTeacherAnswers() + 1);
                break;
            case IN_CLASS:
                this.stats.setNumberOfCorrectInClassAnswers(this.stats.getNumberOfCorrectInClassAnswers() + 1);
                break;
            case GENERATED:
                this.stats.setNumberOfCorrectStudentAnswers(this.stats.getNumberOfCorrectStudentAnswers() + 1);
                break;
            default:
                break;
        }
    }

    public void addQuizAnswer(QuizAnswer quizAnswer) {
        this.quizAnswers.add(quizAnswer);
    }

    public void addCourse(CourseExecution course) {
        this.courseExecutions.add(course);
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", role=" + role + ", id=" + id + ", username='" + username + '\'' + ", name='"
                + name + '\'' + ", courseAcronyms='" + enrolledCoursesAcronyms + '\''
                + ", numberOfTeacherQuizzes=" + this.stats.getNumberOfTeacherQuizzes()
                + ", numberOfInClassQuizzes=" + this.stats.getNumberOfInClassQuizzes()
                + ", numberOfStudentQuizzes=" + this.stats.getNumberOfStudentQuizzes()
                + ", numberOfTeacherAnswers=" + this.stats.getNumberOfTeacherAnswers()
                + ", numberOfCorrectTeacherAnswers=" + this.stats.getNumberOfCorrectTeacherAnswers()
                + ", numberOfInClassAnswers=" + this.stats.getNumberOfInClassAnswers()
                + ", numberOfCorrectInClassAnswers=" + this.stats.getNumberOfCorrectInClassAnswers()
                + ", numberOfStudentAnswers=" + this.stats.getNumberOfStudentAnswers()
                + ", numberOfCorrectStudentAnswers=" + this.stats.getNumberOfCorrectStudentAnswers()
                + ", creationDate=" + creationDate
                + ", courseExecutions=" + courseExecutions + '}';
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> list = new ArrayList<>();

        list.add(new SimpleGrantedAuthority("ROLE_" + role));

        return list;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public List<Question> filterQuestionsByStudentModel(Integer numberOfQuestions, List<Question> availableQuestions) {
        List<Question> studentAnsweredQuestions = getQuizAnswers().stream()
                .flatMap(quizAnswer -> quizAnswer.getQuestionAnswers().stream())
                .filter(questionAnswer -> availableQuestions.contains(questionAnswer.getQuizQuestion().getQuestion()))
                .filter(questionAnswer -> questionAnswer.getTimeTaken() != null && questionAnswer.getTimeTaken() != 0)
                .map(questionAnswer -> questionAnswer.getQuizQuestion().getQuestion()).collect(Collectors.toList());

        List<Question> notAnsweredQuestions = availableQuestions.stream()
                .filter(question -> !studentAnsweredQuestions.contains(question)).collect(Collectors.toList());

        List<Question> result = new ArrayList<>();

        // add 80% of notanswered questions
        // may add less if not enough notanswered
        int numberOfAddedQuestions = 0;
        while (numberOfAddedQuestions < numberOfQuestions * 0.8
                && notAnsweredQuestions.size() >= numberOfAddedQuestions + 1) {
            result.add(notAnsweredQuestions.get(numberOfAddedQuestions++));
        }

        // add notanswered questions if there is not enough answered questions
        // it is ok because the total id of available questions > numberOfQuestions
        while (studentAnsweredQuestions.size() + numberOfAddedQuestions < numberOfQuestions) {
            result.add(notAnsweredQuestions.get(numberOfAddedQuestions++));
        }

        // add answered questions
        Random rand = new Random(System.currentTimeMillis());
        while (numberOfAddedQuestions < numberOfQuestions) {
            int next = rand.nextInt(studentAnsweredQuestions.size());
            if (!result.contains(studentAnsweredQuestions.get(next))) {
                result.add(studentAnsweredQuestions.get(next));
                numberOfAddedQuestions++;
            }
        }

        return result;
    }

    public void addProposal(QuestionProposal prop) {
        this.proposals.add(prop);
    }

    public Set<QuestionProposal> getProposals() {
        return this.proposals;
    }

    public void addDiscussion(Discussion discussion) {
        this.discussions.add(discussion);
    }

    public Set<Discussion> getDiscussions() {
        return this.discussions;
    }
}
