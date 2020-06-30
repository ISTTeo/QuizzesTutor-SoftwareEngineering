package pt.ulisboa.tecnico.socialsoftware.tutor.user.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserStats;

import java.io.Serializable;

public class UserStatsDto implements Serializable {
    private UserDto user;

    private int numberOfTeacherQuizzes = 0;
	private boolean hasNumberOfTeacherQuizzes = false;

    private int numberOfInClassQuizzes = 0;
	private boolean hasNumberOfInClassQuizzes = false;

    private int numberOfStudentQuizzes = 0;
	private boolean hasNumberOfStudentQuizzes = false;

    private int numberOfTeacherAnswers = 0;
	private boolean hasNumberOfTeacherAnswers = false;

    private int numberOfInClassAnswers = 0;
	private boolean hasNumberOfInClassAnswers = false;

    private int numberOfStudentAnswers = 0;
	private boolean hasNumberOfStudentAnswers = false;

    private int numberOfCorrectTeacherAnswers = 0;
	private boolean hasNumberOfCorrectTeacherAnswers = false;

    private int numberOfCorrectInClassAnswers = 0;
	private boolean hasNumberOfCorrectInClassAnswers = false;

    private int numberOfCorrectStudentAnswers = 0;
	private boolean hasNumberOfCorrectStudentAnswers = false;

	private int numberOfSolvedTournaments = 0;
	private boolean hasNumberOfSolvedTournaments = false;

	private double averageTournamentScore = 0.0;
	private boolean hasAverageTournamentScore = false;
	
	private int numberOfDiscussions = 0;
	private boolean hasNumberOfDiscussions = false;

	private int numberOfPublicDiscussions = 0;
	private boolean hasNumberOfPublicDiscussions = false;

	private int numberOfSubmitedProposals = 0;
	private boolean hasNumberOfSubmitedProposals = false;

	private int numberOfAcceptedProposals = 0;
	private boolean hasNumberOfAcceptedProposals = false;


    public UserStatsDto(){}

    public UserStatsDto(UserStats stats, int userId, int targetId) {
        boolean sameUser = userId == targetId;

        this.hasNumberOfTeacherQuizzes = sameUser || stats.hasNumberOfTeacherQuizzes();
		if (this.hasNumberOfTeacherQuizzes)
			this.numberOfTeacherQuizzes = stats.getNumberOfTeacherQuizzes();

        this.hasNumberOfInClassQuizzes = sameUser || stats.hasNumberOfInClassQuizzes();
		if (this.hasNumberOfInClassQuizzes)
			this.numberOfInClassQuizzes = stats.getNumberOfInClassQuizzes();

        this.hasNumberOfStudentQuizzes = sameUser || stats.hasNumberOfStudentQuizzes();
		if (this.hasNumberOfStudentQuizzes)
			this.numberOfStudentQuizzes = stats.getNumberOfStudentQuizzes();

        this.hasNumberOfTeacherAnswers = sameUser || stats.hasNumberOfTeacherAnswers();
		if (this.hasNumberOfTeacherAnswers)
			this.numberOfTeacherAnswers = stats.getNumberOfTeacherAnswers();

        this.hasNumberOfInClassAnswers = sameUser || stats.hasNumberOfInClassAnswers();
		if (this.hasNumberOfInClassAnswers)
			this.numberOfInClassAnswers = stats.getNumberOfInClassAnswers();

        this.hasNumberOfStudentAnswers = sameUser || stats.hasNumberOfStudentAnswers();
		if (this.hasNumberOfStudentAnswers)
			this.numberOfStudentAnswers = stats.getNumberOfStudentAnswers();

        this.hasNumberOfCorrectTeacherAnswers = sameUser || stats.hasNumberOfCorrectTeacherAnswers();
		if (this.hasNumberOfCorrectTeacherAnswers)
			this.numberOfCorrectTeacherAnswers = stats.getNumberOfCorrectTeacherAnswers();

        this.hasNumberOfCorrectInClassAnswers = sameUser || stats.hasNumberOfCorrectInClassAnswers();
		if (this.hasNumberOfCorrectInClassAnswers)
			this.numberOfCorrectInClassAnswers = stats.getNumberOfCorrectInClassAnswers();

        this.hasNumberOfCorrectStudentAnswers = sameUser || stats.hasNumberOfCorrectStudentAnswers();
		if (this.hasNumberOfCorrectStudentAnswers)
			this.numberOfCorrectStudentAnswers = stats.getNumberOfCorrectStudentAnswers();

		this.hasNumberOfSolvedTournaments = sameUser || stats.hasNumberOfSolvedTournaments();
		if (this.hasNumberOfSolvedTournaments)
			this.numberOfSolvedTournaments = stats.getNumberOfSolvedTournaments();

		this.hasAverageTournamentScore = sameUser || stats.hasAverageTournamentScore();
		if (this.hasAverageTournamentScore)
			this.averageTournamentScore = stats.getAverageTournamentScore();
			
		this.hasNumberOfDiscussions = sameUser || stats.hasNumberOfDiscussions();
		if (this.hasNumberOfDiscussions)
			this.numberOfDiscussions = stats.getNumberOfDiscussions();

		this.hasNumberOfPublicDiscussions = sameUser || stats.hasNumberOfPublicDiscussions();
		if (this.hasNumberOfPublicDiscussions)
			this.numberOfPublicDiscussions = stats.getNumberOfPublicDiscussions();

		this.hasNumberOfSubmitedProposals = sameUser || stats.hasNumberOfSubmitedProposals();
		if (this.hasNumberOfSubmitedProposals) {
			this.numberOfSubmitedProposals = stats.getNumberOfSubmitedProposals();
		}

		this.hasNumberOfAcceptedProposals = sameUser || stats.hasNumberOfAcceptedProposals();
		if (this.hasNumberOfAcceptedProposals) {
			this.numberOfAcceptedProposals = stats.getNumberOfAcceptedProposals();
		}

        this.user = new UserDto(stats.getUser());
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public UserDto getUser() {
        return this.user;
    }

    public int getNumberOfTeacherQuizzes() {
		return this.numberOfTeacherQuizzes;
	}
	public void setNumberOfTeacherQuizzes(int numberOfTeacherQuizzes) {
		this.numberOfTeacherQuizzes = numberOfTeacherQuizzes;
	}
	public boolean getHasNumberOfTeacherQuizzes() {
        return this.hasNumberOfTeacherQuizzes;
	}
	public void setHasNumberOfTeacherQuizzes(boolean hasNumberOfTeacherQuizzes) {
		this.hasNumberOfTeacherQuizzes = hasNumberOfTeacherQuizzes;
	}

    public int getNumberOfInClassQuizzes() {
		return this.numberOfInClassQuizzes;
	}
	public void setNumberOfInClassQuizzes(int numberOfInClassQuizzes) {
		this.numberOfInClassQuizzes = numberOfInClassQuizzes;
	}
	public boolean getHasNumberOfInClassQuizzes() {
        return this.hasNumberOfInClassQuizzes;
	}
	public void setHasNumberOfInClassQuizzes(boolean hasNumberOfInClassQuizzes) {
		this.hasNumberOfInClassQuizzes = hasNumberOfInClassQuizzes;
	}

    public int getNumberOfStudentQuizzes() {
		return this.numberOfStudentQuizzes;
    }
	public void setNumberOfStudentQuizzes(int numberOfStudentQuizzes) {
		this.numberOfStudentQuizzes = numberOfStudentQuizzes;
	}
	public boolean getHasNumberOfStudentQuizzes() {
        return this.hasNumberOfStudentQuizzes;
	}
	public void setHasNumberOfStudentQuizzes(boolean hasNumberOfStudentQuizzes) {
		this.hasNumberOfStudentQuizzes = hasNumberOfStudentQuizzes;
	}

    public int getNumberOfTeacherAnswers() {
		return this.numberOfTeacherAnswers;
	}
	public void setNumberOfTeacherAnswers(int numberOfTeacherAnswers) {
		this.numberOfTeacherAnswers = numberOfTeacherAnswers;
	}
	public boolean getHasNumberOfTeacherAnswers() {
        return this.hasNumberOfTeacherAnswers;
	}
	public void setHasNumberOfTeacherAnswers(boolean hasNumberOfTeacherAnswers) {
		this.hasNumberOfTeacherAnswers = hasNumberOfTeacherAnswers;
	}

    public int getNumberOfInClassAnswers() {
		return this.numberOfInClassAnswers;
	}
	public void setNumberOfInClassAnswers(int numberOfInClassAnswers) {
		this.numberOfInClassAnswers = numberOfInClassAnswers;
	}
	public boolean getHasNumberOfInClassAnswers() {
        return this.hasNumberOfInClassAnswers;
	}
	public void setHasNumberOfInClassAnswers(boolean hasNumberOfInClassAnswers) {
		this.hasNumberOfInClassAnswers = hasNumberOfInClassAnswers;
	}

    public int getNumberOfStudentAnswers() {
		return this.numberOfStudentAnswers;
	}
	public void setNumberOfStudentAnswers(int numberOfStudentAnswers) {
		this.numberOfStudentAnswers = numberOfStudentAnswers;
	}
	public boolean getHasNumberOfStudentAnswers() {
        return this.hasNumberOfStudentAnswers;
	}
	public void setHasNumberOfStudentAnswers(boolean hasNumberOfStudentAnswers) {
		this.hasNumberOfStudentAnswers = hasNumberOfStudentAnswers;
	}

    public int getNumberOfCorrectTeacherAnswers() {
		return this.numberOfCorrectTeacherAnswers;
	}
	public void setNumberOfCorrectTeacherAnswers(int numberOfCorrectTeacherAnswers) {
		this.numberOfCorrectTeacherAnswers = numberOfCorrectTeacherAnswers;
	}
	public boolean getHasNumberOfCorrectTeacherAnswers() {
        return this.hasNumberOfCorrectTeacherAnswers;
	}
	public void setHasNumberOfCorrectTeacherAnswers(boolean hasNumberOfCorrectTeacherAnswers) {
		this.hasNumberOfCorrectTeacherAnswers = hasNumberOfCorrectTeacherAnswers;
	}

    public int getNumberOfCorrectInClassAnswers() {
		return this.numberOfCorrectInClassAnswers;
	}
	public void setNumberOfCorrectInClassAnswers(int numberOfCorrectInClassAnswers) {
		this.numberOfCorrectInClassAnswers = numberOfCorrectInClassAnswers;
	}
	public boolean getHasNumberOfCorrectInClassAnswers() {
        return this.hasNumberOfCorrectInClassAnswers;
	}
	public void setHasNumberOfCorrectInClassAnswers(boolean hasNumberOfCorrectInClassAnswers) {
		this.hasNumberOfCorrectInClassAnswers = hasNumberOfCorrectInClassAnswers;
	}

    public int getNumberOfCorrectStudentAnswers() {
		return this.numberOfCorrectStudentAnswers;
	}
	public void setNumberOfCorrectStudentAnswers(int numberOfCorrectStudentAnswers) {
		this.numberOfCorrectStudentAnswers = numberOfCorrectStudentAnswers;
	}
	public boolean getHasNumberOfCorrectStudentAnswers() {
        return this.hasNumberOfCorrectStudentAnswers;
	}

	public void setHasNumberOfCorrectStudentAnswers(boolean hasNumberOfCorrectStudentAnswers) {
		this.hasNumberOfCorrectStudentAnswers = hasNumberOfCorrectStudentAnswers;
	}

	public int getNumberOfSolvedTournaments() {
		return this.numberOfSolvedTournaments;
	}
	public void setNumberOfSolvedTournaments(int numberOfSolvedTournaments) {
		this.numberOfSolvedTournaments = numberOfSolvedTournaments;
	}
	public boolean getHasNumberOfSolvedTournaments() {
        return this.hasNumberOfSolvedTournaments;
	}
	public void setHasNumberOfSolvedTournaments(boolean hasNumberOfSolvedTournaments) {
		this.hasNumberOfSolvedTournaments = hasNumberOfSolvedTournaments;
	}

	public double getAverageTournamentScore() {
		return this.averageTournamentScore;
	}
	public void setAverageTournamentScore(double averageTournamentScore) {
		this.averageTournamentScore = averageTournamentScore;
	}
	public boolean getHasAverageTournamentScore() {
        return this.hasAverageTournamentScore;
	}
	public void setHasAverageTournamentScore(boolean hasAverageTournamentScore) {
		this.hasAverageTournamentScore = hasAverageTournamentScore;
	}
	public int getNumberOfDiscussions() {
		return this.numberOfDiscussions;
	}
	public void setNumberOfDiscussions(int numberOfDiscussions) {
		this.numberOfDiscussions = numberOfDiscussions;
	}
	public boolean getHasNumberOfDiscussions() {
        return this.hasNumberOfDiscussions;
	}
	public void setHasNumberOfDiscussions(boolean hasNumberOfDiscussions) {
        this.hasNumberOfDiscussions = hasNumberOfDiscussions;
	}

	public int getNumberOfPublicDiscussions() {
		return this.numberOfPublicDiscussions;
	}
	public void setNumberOfPublicDiscussions(int numberOfPublicDiscussions) {
		this.numberOfPublicDiscussions = numberOfPublicDiscussions;
	}
	public boolean getHasNumberOfPublicDiscussions() {
        return this.hasNumberOfPublicDiscussions;
	}
	public void setHasNumberOfPublicDiscussions(boolean hasNumberOfPublicDiscussions) {
		this.hasNumberOfPublicDiscussions = hasNumberOfPublicDiscussions;
	}	

	public int getNumberOfSubmitedProposals() {
		return this.numberOfSubmitedProposals;
	}
	public void setNumberOfSubmitedProposals(int numberOfSubmitedProposals) {
		this.numberOfSubmitedProposals = numberOfSubmitedProposals;
	}

	public int getNumberOfAcceptedProposals() {
		return this.numberOfAcceptedProposals;
	}
	public void setNumberOfAcceptedProposals(int numberOfAcceptedProposals) {
		this.numberOfAcceptedProposals = numberOfAcceptedProposals;
	}
	
	
	public boolean getHasNumberOfAcceptedProposals() {
		return this.hasNumberOfAcceptedProposals;
	} 

	public void setHasNumberOfAcceptedProposals(boolean hasNumberOfProposals) {
		this.hasNumberOfAcceptedProposals = hasNumberOfProposals;
	}

	public boolean getHasNumberOfSubmitedProposals() {
		return this.hasNumberOfSubmitedProposals;
	} 

	public void setHasNumberOfSubmitedProposals(boolean hasNumberOfProposals) {
		this.hasNumberOfSubmitedProposals = hasNumberOfProposals;
	}
}
