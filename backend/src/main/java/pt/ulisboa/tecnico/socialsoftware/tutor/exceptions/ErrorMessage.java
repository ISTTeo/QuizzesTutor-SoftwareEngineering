package pt.ulisboa.tecnico.socialsoftware.tutor.exceptions;

public enum ErrorMessage {

    INVALID_ACADEMIC_TERM_FOR_COURSE_EXECUTION("Invalid academic term for course execution"),
    INVALID_ACRONYM_FOR_COURSE_EXECUTION("Invalid acronym for course execution"),
    INVALID_CONTENT_FOR_OPTION("Invalid content for option"),
    INVALID_CONTENT_FOR_QUESTION("Invalid content for question"),
    INVALID_NAME_FOR_COURSE("Invalid name for course"),
    INVALID_NAME_FOR_TOPIC("Invalid name for topic"),
    INVALID_SEQUENCE_FOR_OPTION("Invalid sequence for option"),
    INVALID_SEQUENCE_FOR_QUESTION_ANSWER("Invalid sequence for question answer"),
    INVALID_TITLE_FOR_ASSESSMENT("Invalid title for assessment"),
    INVALID_TITLE_FOR_QUESTION("Invalid title for question"),
    INVALID_URL_FOR_IMAGE("Invalid url for image"),
    INVALID_TYPE_FOR_COURSE("Invalid type for course"),
    INVALID_TYPE_FOR_COURSE_EXECUTION("Invalid type for course execution"),
    INVALID_AVAILABLE_DATE_FOR_QUIZ("Invalid available date for quiz"),
    INVALID_CONCLUSION_DATE_FOR_QUIZ("Invalid conclusion date for quiz"),
    INVALID_RESULTS_DATE_FOR_QUIZ("Invalid results date for quiz"),
    INVALID_TITLE_FOR_QUIZ("Invalid title for quiz"),
    INVALID_TYPE_FOR_QUIZ("Invalid type for quiz"),
    INVALID_QUESTION_SEQUENCE_FOR_QUIZ("Invalid question sequence for quiz"),

    ASSESSMENT_NOT_FOUND("Assessment not found with id %d"),
    COURSE_EXECUTION_NOT_FOUND("Course execution not found with id %d"),
    OPTION_NOT_FOUND("Option not found with id %d"),
    QUESTION_ANSWER_NOT_FOUND("Question answer not found with id %d"),
    QUESTION_NOT_FOUND("Question not found with id %d"),
    QUIZ_ANSWER_NOT_FOUND("Quiz answer not found with id %d"),
    QUIZ_NOT_FOUND("Quiz not found with id %d"),
    QUIZ_QUESTION_NOT_FOUND("Quiz question not found with id %d"),
    TOPIC_CONJUNCTION_NOT_FOUND("Topic Conjunction not found with id %d"),

    COURSE_NOT_FOUND("Course not found with name %s"),
    COURSE_NAME_IS_EMPTY("The course name is empty"),
    COURSE_TYPE_NOT_DEFINED("The course type is not defined"),
    COURSE_EXECUTION_ACRONYM_IS_EMPTY("The course execution acronym is empty"),
    COURSE_EXECUTION_ACADEMIC_TERM_IS_EMPTY("The course execution academic term is empty"),

    TOPIC_NOT_FOUND("Topic not found with id %d"),
    USER_NOT_FOUND("User not found with id %d"),

    CANNOT_DELETE_COURSE_EXECUTION("The course execution cannot be deleted %s"),
    USERNAME_NOT_FOUND("Username %d not found"),

    QUIZ_USER_MISMATCH("Quiz %s is not assigned to student %s"),
    QUIZ_MISMATCH("Quiz Answer Quiz %d does not match Quiz Question Quiz %d"),
    QUESTION_OPTION_MISMATCH("Question %d does not have option %d"),
    COURSE_EXECUTION_MISMATCH("Course Execution %d does not have quiz %d"),

    TOURNAMENT_NO_CREATOR("Tournament has no creator assigned"),
    TOURNAMENT_NO_TOPICS("Tournament has no topics assigned"),
    TOURNAMENT_NO_NAME("Tournament has no name assigned, or name is empty"),
    TOURNAMENT_NO_BEGIN("Tournament has no begin date assigned"),
    TOURNAMENT_NO_END("Tournament has no end date assigned"),
    TOURNAMENT_DATE_CONFLICT("Tournament's end date is prior to begin date"),
    TOURNAMENT_WRONG_ROLE("User %s's role is not permitted"),

    DUPLICATE_TOPIC("Duplicate topic: %s"),
    DUPLICATE_USER("Duplicate user: %s"),
    DUPLICATE_COURSE_EXECUTION("Duplicate course execution: %s"),

    USERS_IMPORT_ERROR("Error importing users: %s"),
    QUESTIONS_IMPORT_ERROR("Error importing questions: %s"),
    TOPICS_IMPORT_ERROR("Error importing topics: %s"),
    ANSWERS_IMPORT_ERROR("Error importing answers: %s"),
    QUIZZES_IMPORT_ERROR("Error importing quizzes: %s"),

    QUESTION_IS_USED_IN_QUIZ("Question is used in quiz %s"),
    USER_NOT_ENROLLED("%s - Not enrolled in any available course"),
    QUIZ_NO_LONGER_AVAILABLE("This quiz is no longer available"),
    QUIZ_NOT_YET_AVAILABLE("This quiz is not yet available"),

    DISCUSSION_DOES_NOT_EXIST("Reply must be created in reference to a discussion"),
    DISCUSSION_NOT_FOUND("Discussion not found with id %d"),
    REPLY_NOT_FOUND("Reply not found with id %d"),
    USER_DOES_NOT_EXIST_REPLY("No teacher defined when creating reply"),
    USER_NOT_A_TEACHER_OR_A_STUDENT("Must be a teacher or a student to create a reply"), EMPTY_REPLY_CONTENT("A reply's content cannot be empty"),
    BLANK_REPLY_CONTENT("A reply's content cannot be blank"),

    NO_CORRECT_OPTION("Question does not have a correct option"),
    NOT_ENOUGH_QUESTIONS("Not enough questions to create a quiz"),

    QUESTION_DOES_NOT_EXIST("Question does not exist"),
    QUESTION_MISSING_DATA("Missing information for quiz"),
    QUESTION_MULTIPLE_CORRECT_OPTIONS("Questions can only have 1 correct option"),
    QUESTION_CHANGE_CORRECT_OPTION_HAS_ANSWERS("Can not change correct option of answered question"),

    ONE_CORRECT_OPTION_NEEDED("Questions need to have 1 and only 1 correct option"),
    CANNOT_CHANGE_ANSWERED_QUESTION("Can not change answered question"),
    QUIZ_HAS_ANSWERS("Quiz already has answers"),
    QUIZ_ALREADY_COMPLETED("Quiz already completed"),
    QUIZ_ALREADY_STARTED("Quiz was already started"),
    QUIZ_QUESTION_HAS_ANSWERS("Quiz question has answers"),

    AUTHENTICATION_ERROR("Authentication Error"),
    FENIX_CONFIGURATION_ERROR("Incorrect server configuration files for fenix"),
    FENIX_ERROR("Fenix Error"),

    INVALID_QUESTION_PROPOSAL("Question Proposal is invalid: %s"),
    EMPTY_JUSTIFICATION("A justification for rejection is required"),
    USER_IS_NULL("User is null"),
    USER_NOT_STUDENT("User is not a student"),
    STUDENT_NOT_ENROLLED("Student is not enrolled in course"),
    QUESTION_PROPOSAL_NOT_FOUND("Question Proposal with id %d not found"),
    QUESTION_PROPOSAL_NOT_OPEN_FOR_REVIEW("Question Proposal is not open for review"),
    QUESTION_PROPOSAL_NOT_ACCEPTED("Question Proposal with id %d hasn't been accepted"),
    QUESTION_PROPOSAL_NOT_REJECTED("Question Proposal with id %d hasn't been rejected"),
    QUESTION_PROPOSAL_NOT_PENDING("Question Proposal with id %d is no longer pending"),
    QUESTION_PROPOSAL_EDITOR_IS_NOT_THE_AUTHOR("The editor of question proposal with id %d is not the author"),

    USER_DOESNT_EXIST("No student defined when creating discussion"),
    QUESTION_DOESNT_EXIST("No question defined when creating discussion"),
    QUESTION_ANSWER_DOESNT_EXIST("No question answer defined when creating discussion"),
    USER_NOT_A_STUDENT("Only students can create discussions"),
    USER_DIDNT_ANSWER_QUESTION("Student must answer question before creating discussion"),
    EMPTY_TITLE("Title of discussion is empty"), BLANK_TITLE("Title of discussion is blank"),
    EMPTY_CONTENT("Content of discussion is empty"), BLANK_CONTENT("Content of discussion is blank"),
    WRONG_NUMBER_REPLIES("Discussion must be created with no replies"),

    TOURNAMENT_NOT_FOUND("Tournament not found with id %s"),
    USER_NOT_SPECIFIED("No student specified when enrolling in tournament"),
    TOURNAMENT_NOT_SPECIFIED("No tournament specified when enrolling to a tournament"),
    CLOSED_TOURNAMENT("This tournament is closed."),
    NOT_A_STUDENT("Only students can sign up to tournaments"),
    DOUBLE_ENROLLMENT("Student is already enrolled in this tournament"),
    DIFF_COURSE_EXECS("Student must be enrolled in the same Course Execution as the tournament"),
    NOT_THE_CREATOR("User isn't the tournaments creator"),
    CANCELLED_TOURNAMENT("Tournament was cancelled"),
    USER_NOT_ENROLLED_TOURNAMENT("User %s is not enrolled in this tournament"),

    ACCESS_DENIED("You do not have permission to view this resource"), CANNOT_OPEN_FILE("Cannot open file");

    public final String label;

    ErrorMessage(String label) {
        this.label = label;
    }
}
