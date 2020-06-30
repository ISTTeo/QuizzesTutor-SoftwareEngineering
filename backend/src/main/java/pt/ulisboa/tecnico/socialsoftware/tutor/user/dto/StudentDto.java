package pt.ulisboa.tecnico.socialsoftware.tutor.user.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import java.io.Serializable;

public class StudentDto implements Serializable {
    private String username;
    private String name;
    private UserStatsDto userStats;
    private int percentageOfCorrectAnswers = 0;
    private int percentageOfCorrectTeacherAnswers = 0;
    private int percentageOfCorrectInClassAnswers = 0;
    private int percentageOfCorrectStudentAnswers = 0;
    private int numberOfAnswers;
    private String creationDate;
    private String lastAccess;

    public StudentDto(User user) {
        this.username = user.getUsername();
        this.name = user.getName();
        this.lastAccess = DateHandler.toISOString(user.getLastAccess());
        this.creationDate = DateHandler.toISOString(user.getCreationDate());
        this.userStats = new UserStatsDto(user.getUserStats(), user.getId(), user.getId());
        this.numberOfAnswers = this.userStats.getNumberOfTeacherAnswers() + this.userStats.getNumberOfInClassAnswers() + this.userStats.getNumberOfStudentAnswers();

        if (this.userStats.getNumberOfTeacherAnswers() != 0)
            this.percentageOfCorrectTeacherAnswers = this.userStats.getNumberOfCorrectTeacherAnswers() * 100 / this.userStats.getNumberOfTeacherAnswers();
        if (this.userStats.getNumberOfInClassAnswers() != 0)
            this.percentageOfCorrectInClassAnswers = this.userStats.getNumberOfCorrectInClassAnswers() * 100 / this.userStats.getNumberOfInClassAnswers();
        if (this.userStats.getNumberOfStudentAnswers() != 0)
            this.percentageOfCorrectStudentAnswers = this.userStats.getNumberOfCorrectStudentAnswers() * 100 / this.userStats.getNumberOfStudentAnswers();
        if (this.numberOfAnswers != 0)
            this.percentageOfCorrectAnswers = (this.userStats.getNumberOfCorrectTeacherAnswers() + this.userStats.getNumberOfCorrectInClassAnswers() + this.userStats.getNumberOfCorrectStudentAnswers())  * 100 / this.numberOfAnswers;

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUserStats(UserStatsDto stats) {
        this.userStats = stats;
    }

    public UserStatsDto getUserStats() {
        return this.userStats;
    }

    public Integer getNumberOfAnswers() {
        return numberOfAnswers;
    }

    public void setNumberOfAnswers(Integer numberOfAnswers) {
        this.numberOfAnswers = numberOfAnswers;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(String lastAccess) {
        this.lastAccess = lastAccess;
    }

    public int getPercentageOfCorrectInClassAnswers() {
        return percentageOfCorrectInClassAnswers;
    }

    public void setPercentageOfCorrectInClassAnswers(int percentageOfCorrectInClassAnswers) {
        this.percentageOfCorrectInClassAnswers = percentageOfCorrectInClassAnswers;
    }

    public int getPercentageOfCorrectStudentAnswers() {
        return percentageOfCorrectStudentAnswers;
    }

    public void setPercentageOfCorrectStudentAnswers(int percentageOfCorrectStudentAnswers) {
        this.percentageOfCorrectStudentAnswers = percentageOfCorrectStudentAnswers;
    }

    @Override
    public String toString() {
        return "StudentDto{" +
                "username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", numberOfTeacherQuizzes=" + this.userStats.getNumberOfTeacherQuizzes() +
                ", numberOfStudentQuizzes=" + this.userStats.getNumberOfStudentQuizzes() +
                ", numberOfAnswers=" + numberOfAnswers +
                ", numberOfTeacherAnswers=" + this.userStats.getNumberOfTeacherAnswers() +
                ", percentageOfCorrectAnswers=" + percentageOfCorrectAnswers +
                ", percentageOfCorrectTeacherAnswers=" + percentageOfCorrectTeacherAnswers +
                ", creationDate='" + creationDate + '\'' +
                ", lastAccess='" + lastAccess + '\'' +
                '}';
    }
}
