package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.Discussion;
import pt.ulisboa.tecnico.socialsoftware.tutor.reply.Reply;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserStats;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserPreferences;

public interface Visitor {
    default void visitQuestion(Question question) {}

    default void visitImage(Image image) {}

    default void visitOption(Option option) {}

    default void visitQuiz(Quiz quiz) {}

    default void visitQuizQuestion(QuizQuestion quizQuestion) {}

    default void visitUser(User user) {}

    default void visitUserStats(UserStats userStats) {}

    default void visitUserPreferences(UserPreferences userPreferences) {}

    default void visitQuizAnswer(QuizAnswer quizAnswer) {}

    default void visitQuestionAnswer(QuestionAnswer questionAnswer) {}

    default void visitTopic(Topic topic) {}

    default void visitCourse(Course course) {}

    default void visitAssessment(Assessment assessment) {}

    default void visitCourseExecution(CourseExecution courseExecution) {}

    default void visitTournament(Tournament tournament) {}

    default void visitDiscussion(Discussion discussion) {}

    default void visitReply(Reply reply) {}

    default String convertSequenceToLetter(Integer value) {
        switch (value) {
            case 0:
                return "A";
            case 1:
                return "B";
            case 2:
                return "C";
            case 3:
                return "D";
            default:
                return "X";
        }
    }
}
