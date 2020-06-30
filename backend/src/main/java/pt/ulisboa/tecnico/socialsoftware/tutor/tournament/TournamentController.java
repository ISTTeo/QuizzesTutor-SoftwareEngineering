package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService;

@RestController
public class TournamentController {

  @Autowired
  private TournamentService tournamentService;


  @PostMapping("/executions/{executionId}/tournaments")
  @PreAuthorize("hasRole('ROLE_STUDENT')  and hasPermission(#executionId, 'EXECUTION.ACCESS')")
  public TournamentDto createTournament(Principal principal, @PathVariable int executionId,
      @Valid @RequestBody TournamentDto tournament) {
    User user = (User) ((Authentication) principal).getPrincipal();

    if (user == null) {
      throw new TutorException(ErrorMessage.AUTHENTICATION_ERROR);
    }

    return this.tournamentService.createTournament(executionId, tournament, user.getId());
  }

  @PutMapping("/tournaments/{tournamentId}/enroll")
  @PreAuthorize("hasRole('ROLE_STUDENT') and (hasPermission(#tournamentId, 'TOURNAMENT.ACCESS'))")
  public TournamentDto enrollInTournament(@PathVariable int tournamentId, Principal principal){
    User user = (User) ((Authentication) principal).getPrincipal();

    if (user == null) {
      throw new TutorException(ErrorMessage.AUTHENTICATION_ERROR);
    }
    return this.tournamentService.enrollInTournament(user.getId(), tournamentId);
  }

  @GetMapping("/executions/{executionId}/tournaments")
  @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
  public List<TournamentDto> findTournaments(@PathVariable int executionId) {
    return tournamentService.findOpenTournaments(executionId);
  }

  @GetMapping("/executions/{executionId}/cancelled")
  @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
  public List<TournamentDto> findCancelledTournaments(@PathVariable int executionId, Principal principal) {
    User user = (User) ((Authentication) principal).getPrincipal();

    if (user == null) {
      throw new TutorException(ErrorMessage.AUTHENTICATION_ERROR);
    }

    return tournamentService.findEnrolledCancelledTournaments(user.getId(), executionId);
  }

  @GetMapping("/executions/{executionId}/solved")
  @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
  public List<TournamentDto> findSolvedTournaments(@PathVariable int executionId, Principal principal) {
    User user = (User) ((Authentication) principal).getPrincipal();

    if (user == null) {
      throw new TutorException(ErrorMessage.AUTHENTICATION_ERROR);
    }

    return tournamentService.findSolvedTournaments(user.getId(), executionId);
  }


  @PutMapping("tournaments/{tournamentId}/cancel")
  @PreAuthorize("hasRole('ROLE_STUDENT') and (hasPermission(#tournamentId, 'TOURNAMENT.ACCESS'))")
  public TournamentDto cancelTournament(@PathVariable int tournamentId, Principal principal){
    User user = (User) ((Authentication) principal).getPrincipal();

    if (user == null) {
      throw new TutorException(ErrorMessage.AUTHENTICATION_ERROR);
    }
    return this.tournamentService.cancelTournament(tournamentId, user.getId());
  
  } 

  @PutMapping("/tournaments/{tournamentId}/unenroll")
  @PreAuthorize("hasRole('ROLE_STUDENT') and (hasPermission(#tournamentId, 'TOURNAMENT.ACCESS'))")
  public TournamentDto unenrollFromTournament(@PathVariable int tournamentId, Principal principal){
    User user = (User) ((Authentication) principal).getPrincipal();

    if (user == null) {
      throw new TutorException(ErrorMessage.AUTHENTICATION_ERROR);
    }
    return this.tournamentService.unenrollFromTournament(user.getId(), tournamentId);
  }

}