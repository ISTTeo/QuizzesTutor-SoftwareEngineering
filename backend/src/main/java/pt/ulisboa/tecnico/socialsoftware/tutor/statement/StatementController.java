package pt.ulisboa.tecnico.socialsoftware.tutor.statement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CorrectAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.SolvedQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementCreationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AUTHENTICATION_ERROR;

@RestController
public class StatementController {
    @Autowired
    private StatementService statementService;

    @GetMapping("/executions/{executionId}/quizzes/available")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<StatementQuizDto> getAvailableQuizzes(Principal principal, @PathVariable int executionId) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null) {
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return statementService.getAvailableQuizzes(user.getId(), executionId);
    }

    @PostMapping("/executions/{executionId}/quizzes/generate")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public StatementQuizDto getNewQuiz(Principal principal, @PathVariable int executionId, @RequestBody StatementCreationDto quizDetails) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null) {
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return statementService.generateStudentQuiz(user.getId(), executionId, quizDetails);
    }

    @GetMapping("/executions/{executionId}/quizzes/solved")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<SolvedQuizDto> getSolvedQuizzes(Principal principal, @PathVariable int executionId) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null) {
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return statementService.getSolvedQuizzes(user.getId(), executionId);
    }

    @GetMapping("/quizzes/{quizId}/byqrcode")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#quizId, 'QUIZ.ACCESS')")
    public StatementQuizDto getQuizByQRCode(Principal principal, @PathVariable int quizId) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null) {
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return statementService.getQuizByQRCode(user.getId(), quizId);
    }

    @PostMapping("/quizzes/{quizId}/submit")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#quizId, 'QUIZ.ACCESS')")
    public void submitAnswer(Principal principal, @PathVariable int quizId, @Valid @RequestBody StatementAnswerDto answer) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null) {
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        statementService.submitAnswer(user.getId(), quizId, answer);
    }

    @GetMapping("/quizzes/{quizId}/start")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#quizId, 'QUIZ.ACCESS')")
    public void startQuiz(Principal principal, @PathVariable int quizId) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null) {
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        statementService.startQuiz(user.getId(), quizId);
    }

    @GetMapping("/quizzes/{quizId}/conclude")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#quizId, 'QUIZ.ACCESS')")
    public List<CorrectAnswerDto> concludeQuiz(Principal principal, @PathVariable int quizId) {

        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null) {
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return statementService.concludeQuiz(user.getId(), quizId);
    }

    @GetMapping("/executions/{executionId}/tournaments/ongoing")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<StatementQuizDto> getOngoingTournaments(Principal principal, @PathVariable int executionId) {

        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null) {
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return statementService.getOngoingTournaments(user.getId(), executionId);
    }

    @GetMapping("/executions/{executionId}/tournaments/solved")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<SolvedQuizDto> getSolvedTournaments(Principal principal, @PathVariable int executionId) {

        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null) {
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return statementService.getSolvedTournaments(user.getId(), executionId);
    }


    @GetMapping("/executions/{executionId}/tournaments/ongoing/solved")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<SolvedQuizDto> getSolvedOngoingTournaments(Principal principal, @PathVariable int executionId) {

        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null) {
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return statementService.getSolvedOngoingTournaments(user.getId(), executionId);
    }
}