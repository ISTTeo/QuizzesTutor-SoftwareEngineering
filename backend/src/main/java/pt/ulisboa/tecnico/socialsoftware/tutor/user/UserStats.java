package pt.ulisboa.tecnico.socialsoftware.tutor.user;

import org.hibernate.annotations.Parent;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.SolvedQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.Discussion;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserStatsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.io.Serializable;

@Embeddable
@Access(AccessType.FIELD)
public class UserStats implements Serializable {
    @Parent
    private User user;

    private Integer numberOfTeacherQuizzes = null;
    private Integer numberOfInClassQuizzes = null;
    private Integer numberOfStudentQuizzes = null;
    private Integer numberOfTeacherAnswers = null;
    private Integer numberOfInClassAnswers = null;
    private Integer numberOfStudentAnswers = null;
    private Integer numberOfCorrectTeacherAnswers = null;
    private Integer numberOfCorrectInClassAnswers = null;
    private Integer numberOfCorrectStudentAnswers = null;
    private Integer numberOfSolvedTournaments = null;
    private Double averageTournamentScore = null;
    private Integer numberOfDiscussions = null;
    private Integer numberOfPublicDiscussions = null;
    private Integer numberOfSubmitedProposals = 0;
    private Integer numberOfAcceptedProposals = 0;

    public UserStats() {
    }

    public UserStats(User user) {
        setUser(user);
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getNumberOfTeacherQuizzes() {
        if (this.numberOfTeacherQuizzes == null)
            this.numberOfTeacherQuizzes = (int) this.user.getQuizAnswers().stream().filter(QuizAnswer::isCompleted)
                    .filter(quizAnswer -> quizAnswer.getQuiz().getType().equals(Quiz.QuizType.PROPOSED)).count();

        return numberOfTeacherQuizzes;
    }

    public void setNumberOfTeacherQuizzes(Integer numberOfTeacherQuizzes) {
        this.numberOfTeacherQuizzes = numberOfTeacherQuizzes;
    }

    public boolean hasNumberOfTeacherQuizzes() {
        return this.user.getUserPreferences().getAnswerStatsVisibility();
    }

    public Integer getNumberOfInClassQuizzes() {
        if (this.numberOfInClassQuizzes == null)
            this.numberOfInClassQuizzes = (int) this.user.getQuizAnswers().stream().filter(QuizAnswer::isCompleted)
                    .filter(quizAnswer -> quizAnswer.getQuiz().getType().equals(Quiz.QuizType.IN_CLASS)).count();

        return numberOfInClassQuizzes;
    }

    public void setNumberOfInClassQuizzes(Integer numberOfInClassQuizzes) {
        this.numberOfInClassQuizzes = numberOfInClassQuizzes;
    }

    public boolean hasNumberOfInClassQuizzes() {
        return this.user.getUserPreferences().getAnswerStatsVisibility();
    }

    public Integer getNumberOfStudentQuizzes() {
        if (this.numberOfStudentQuizzes == null)
            this.numberOfStudentQuizzes = (int) this.user.getQuizAnswers().stream().filter(QuizAnswer::isCompleted)
                    .filter(quizAnswer -> quizAnswer.getQuiz().getType().equals(Quiz.QuizType.GENERATED)).count();

        return numberOfStudentQuizzes;
    }

    public void setNumberOfStudentQuizzes(Integer numberOfStudentQuizzes) {
        this.numberOfStudentQuizzes = numberOfStudentQuizzes;
    }

    public boolean hasNumberOfStudentQuizzes() {
        return this.user.getUserPreferences().getAnswerStatsVisibility();
    }

    public Integer getNumberOfTeacherAnswers() {
        if (this.numberOfTeacherAnswers == null)
            this.numberOfTeacherAnswers = (int) this.user.getQuizAnswers().stream().filter(QuizAnswer::isCompleted)
                    .filter(quizAnswer -> quizAnswer.getQuiz().getType().equals(Quiz.QuizType.PROPOSED))
                    .mapToInt(quizAnswer -> quizAnswer.getQuiz().getQuizQuestions().size()).sum();

        return numberOfTeacherAnswers;
    }

    public void setNumberOfTeacherAnswers(Integer numberOfTeacherAnswers) {
        this.numberOfTeacherAnswers = numberOfTeacherAnswers;
    }

    public boolean hasNumberOfTeacherAnswers() {
        return this.user.getUserPreferences().getAnswerStatsVisibility();
    }

    public Integer getNumberOfInClassAnswers() {
        if (this.numberOfInClassAnswers == null)
            this.numberOfInClassAnswers = (int) this.user.getQuizAnswers().stream().filter(QuizAnswer::isCompleted)
                    .filter(quizAnswer -> quizAnswer.getQuiz().getType().equals(Quiz.QuizType.IN_CLASS))
                    .mapToInt(quizAnswer -> quizAnswer.getQuiz().getQuizQuestions().size()).sum();
        return numberOfInClassAnswers;
    }

    public void setNumberOfInClassAnswers(Integer numberOfInClassAnswers) {
        this.numberOfInClassAnswers = numberOfInClassAnswers;
    }

    public boolean hasNumberOfInClassAnswers() {
        return this.user.getUserPreferences().getAnswerStatsVisibility();
    }

    public Integer getNumberOfStudentAnswers() {
        if (this.numberOfStudentAnswers == null) {
            this.numberOfStudentAnswers = (int) this.user.getQuizAnswers().stream().filter(QuizAnswer::isCompleted)
                    .filter(quizAnswer -> quizAnswer.getQuiz().getType().equals(Quiz.QuizType.GENERATED))
                    .mapToInt(quizAnswer -> quizAnswer.getQuiz().getQuizQuestions().size()).sum();
        }

        return numberOfStudentAnswers;
    }

    public void setNumberOfStudentAnswers(Integer numberOfStudentAnswers) {
        this.numberOfStudentAnswers = numberOfStudentAnswers;
    }

    public boolean hasNumberOfStudentAnswers() {
        return this.user.getUserPreferences().getAnswerStatsVisibility();
    }

    public Integer getNumberOfCorrectTeacherAnswers() {
        if (this.numberOfCorrectTeacherAnswers == null)
            this.numberOfCorrectTeacherAnswers = (int) this.user.getQuizAnswers().stream()
                    .filter(QuizAnswer::isCompleted)
                    .filter(quizAnswer -> quizAnswer.getQuiz().getType().equals(Quiz.QuizType.PROPOSED))
                    .flatMap(quizAnswer -> quizAnswer.getQuestionAnswers().stream())
                    .filter(questionAnswer -> questionAnswer.getOption() != null
                            && questionAnswer.getOption().getCorrect())
                    .count();

        return numberOfCorrectTeacherAnswers;
    }

    public void setNumberOfCorrectTeacherAnswers(Integer numberOfCorrectTeacherAnswers) {
        this.numberOfCorrectTeacherAnswers = numberOfCorrectTeacherAnswers;
    }

    public boolean hasNumberOfCorrectTeacherAnswers() {
        return this.user.getUserPreferences().getAnswerStatsVisibility();
    }

    public Integer getNumberOfCorrectInClassAnswers() {
        if (this.numberOfCorrectInClassAnswers == null)
            this.numberOfCorrectInClassAnswers = (int) this.user.getQuizAnswers().stream()
                    .filter(QuizAnswer::isCompleted)
                    .filter(quizAnswer -> quizAnswer.getQuiz().getType().equals(Quiz.QuizType.IN_CLASS))
                    .flatMap(quizAnswer -> quizAnswer.getQuestionAnswers().stream())
                    .filter(questionAnswer -> questionAnswer.getOption() != null
                            && questionAnswer.getOption().getCorrect())
                    .count();

        return numberOfCorrectInClassAnswers;
    }

    public void setNumberOfCorrectInClassAnswers(Integer numberOfCorrectInClassAnswers) {
        this.numberOfCorrectInClassAnswers = numberOfCorrectInClassAnswers;
    }

    public boolean hasNumberOfCorrectInClassAnswers() {
        return this.user.getUserPreferences().getAnswerStatsVisibility();
    }

    public Integer getNumberOfCorrectStudentAnswers() {
        if (this.numberOfCorrectStudentAnswers == null)
            this.numberOfCorrectStudentAnswers = (int) this.user.getQuizAnswers().stream()
                    .filter(QuizAnswer::isCompleted)
                    .filter(quizAnswer -> quizAnswer.getQuiz().getType().equals(Quiz.QuizType.GENERATED))
                    .flatMap(quizAnswer -> quizAnswer.getQuestionAnswers().stream())
                    .filter(questionAnswer -> questionAnswer.getOption() != null
                            && questionAnswer.getOption().getCorrect())
                    .count();

        return numberOfCorrectStudentAnswers;
    }

    public void setNumberOfCorrectStudentAnswers(Integer numberOfCorrectStudentAnswers) {
        this.numberOfCorrectStudentAnswers = numberOfCorrectStudentAnswers;
    }

    public boolean hasNumberOfCorrectStudentAnswers() {
        return this.user.getUserPreferences().getAnswerStatsVisibility();
    }

    public Integer getNumberOfSolvedTournaments() {
        if (this.numberOfSolvedTournaments == null)
            this.numberOfSolvedTournaments = (int) this.user.getQuizAnswers().stream()
                    .filter(quizAnswer -> quizAnswer.getQuiz().getTournament() != null)
                    .filter(quizAnswer -> !quizAnswer.getQuiz().getTournament().isCancelled())
                    .filter(quizAnswer -> quizAnswer.getQuiz().getTournament().getEnrolled().contains(user))
                    .filter(quizAnswer -> quizAnswer.isCompleted())
                    .filter(quizAnswer -> quizAnswer.getAnswerDate() == null
                            || DateHandler.now().isAfter(quizAnswer.getAnswerDate()))
                    .count();
        return numberOfSolvedTournaments;
    }

    public void setNumberOfSolvedTournaments(Integer numberOfSolvedTournaments) {
        this.numberOfSolvedTournaments = numberOfSolvedTournaments;
    }

    public boolean hasNumberOfSolvedTournaments() {
        return this.user.getUserPreferences().getTournamentStatsVisibility();
    }

    public boolean hasAverageTournamentScore(){
        return this.user.getUserPreferences().getTournamentStatsVisibility();
    }

    public void setAverageTournamentScore(Double avg){
        this.averageTournamentScore = avg;
    }

    public Double getAverageTournamentScore() {
        if (this.averageTournamentScore == null) {
            List<SolvedQuizDto> quizzes = this.user.getQuizAnswers().stream()
                    .filter(quizAnswer -> quizAnswer.getQuiz().getTournament() != null)
                    .filter(quizAnswer -> !quizAnswer.getQuiz().getTournament().isCancelled())
                    .filter(quizAnswer -> quizAnswer.getQuiz().getTournament().getEnrolled().contains(user))
                    .filter(QuizAnswer::isCompleted)
                    .filter(quizAnswer -> quizAnswer.getQuiz().getConclusionDate() == null
                            || DateHandler.now().isAfter(quizAnswer.getAnswerDate()))
                    .map(SolvedQuizDto::new)
                    .sorted(Comparator.comparing(SolvedQuizDto::getAnswerDate))
                    .collect(Collectors.toList());

            OptionalDouble res = quizzes.stream().filter(q -> !q.getCorrectAnswers().isEmpty()).mapToDouble(q -> quizAverage(q)).average();
            this.averageTournamentScore = res.orElse(0.0);
        }
        return averageTournamentScore;
    }

    private double quizAverage(SolvedQuizDto q){
        int correct = 0;
        for (int i = 0; i < q.getStatementQuiz().getQuestions().size(); i++) {
            if ((q.getStatementQuiz().getAnswers().get(i)!=null) && q.getCorrectAnswers().get(i).getCorrectOptionId()
                    .equals(q.getStatementQuiz().getAnswers().get(i).getOptionId())) {
                correct += 1;
            }
        }

        return ((double)correct)/q.getCorrectAnswers().size();
    }

    public Integer getNumberOfDiscussions() {
        if (this.numberOfDiscussions == null)
            this.numberOfDiscussions = (int) this.user.getDiscussions().stream().count();
        return this.numberOfDiscussions;
    }

    public void setNumberOfDiscussions(Integer numberOfDiscussions) {
        this.numberOfDiscussions = numberOfDiscussions;
    }

    public boolean hasNumberOfDiscussions() {
        return this.user.getUserPreferences().getDiscussionStatsVisibility();
    }

    public Integer getNumberOfPublicDiscussions() {
        if (this.numberOfPublicDiscussions == null)
            this.numberOfPublicDiscussions = (int) this.user.getDiscussions().stream()
                    .filter(discussion -> discussion.getIsPublic())
                    .count();
        return this.numberOfPublicDiscussions;
    }

    public void setNumberOfPublicDiscussions(Integer numberOfPublicDiscussions) {
        this.numberOfPublicDiscussions = numberOfPublicDiscussions;
    }

    public boolean hasNumberOfPublicDiscussions() {
        return this.user.getUserPreferences().getDiscussionStatsVisibility();
    }
    public int getNumberOfSubmitedProposals() {
        if (this.numberOfSubmitedProposals == null)
            this.numberOfSubmitedProposals = (int) this.user.getProposals().stream().count();

        return numberOfSubmitedProposals;
	}
	public void setNumberOfSubmitedProposals(int numberOfSubmitedProposals) {
		this.numberOfSubmitedProposals = numberOfSubmitedProposals;
	}

	public int getNumberOfAcceptedProposals() {
		if (this.numberOfAcceptedProposals == null)
        this.numberOfAcceptedProposals = (int) this.user.getProposals().stream()
        .filter(p -> p.wasAccepted()).count();

        return numberOfAcceptedProposals;
    }

	public void setNumberOfAcceptedProposals(int numberOfAcceptedProposals) {
		this.numberOfAcceptedProposals = numberOfAcceptedProposals;
    }

	public boolean hasNumberOfSubmitedProposals() {
        return this.user.getUserPreferences().getProposalVisibility();
    }

    public boolean hasNumberOfAcceptedProposals() {
        return this.user.getUserPreferences().getProposalVisibility();
    }



    public void clear() {
        this.numberOfAcceptedProposals = null;
        this.numberOfSubmitedProposals = null;
        // fill with updatable stats
        // just null them out
        this.averageTournamentScore = null;
        this.numberOfSolvedTournaments = null;
        this.numberOfDiscussions = null;
        this.numberOfPublicDiscussions = null;
    }

    public void updateUserStats(UserStatsDto statsDto) {
        setNumberOfTeacherQuizzes(statsDto.getNumberOfTeacherQuizzes());
        setNumberOfInClassQuizzes(statsDto.getNumberOfInClassQuizzes());
        setNumberOfStudentQuizzes(statsDto.getNumberOfStudentQuizzes());
        setNumberOfTeacherAnswers(statsDto.getNumberOfTeacherAnswers());
        setNumberOfInClassAnswers(statsDto.getNumberOfInClassAnswers());
        setNumberOfStudentAnswers(statsDto.getNumberOfStudentAnswers());
        setNumberOfCorrectTeacherAnswers(statsDto.getNumberOfCorrectTeacherAnswers());
        setNumberOfCorrectInClassAnswers(statsDto.getNumberOfCorrectInClassAnswers());
        setNumberOfCorrectStudentAnswers(statsDto.getNumberOfCorrectStudentAnswers());
        setNumberOfDiscussions(statsDto.getNumberOfDiscussions()) ;
        setNumberOfPublicDiscussions(statsDto.getNumberOfPublicDiscussions()) ;
        setNumberOfSolvedTournaments(statsDto.getNumberOfSolvedTournaments());
        setAverageTournamentScore(statsDto.getAverageTournamentScore());
        setNumberOfSubmitedProposals(statsDto.getNumberOfSubmitedProposals());
        setNumberOfAcceptedProposals(statsDto.getNumberOfAcceptedProposals());
    }
    
}
