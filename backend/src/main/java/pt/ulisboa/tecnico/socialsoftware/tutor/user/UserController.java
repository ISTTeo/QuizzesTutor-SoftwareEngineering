package pt.ulisboa.tecnico.socialsoftware.tutor.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserStatsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserPreferencesDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService;

import java.security.Principal;
import javax.validation.Valid;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AUTHENTICATION_ERROR;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/user/stats")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public UserStatsDto getStats(Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null) {
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return userService.getUserStats(user.getId(), user.getId(), false);
    }

    @GetMapping("/user/updated_stats")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public UserStatsDto getUpdatedStats(Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null) {
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return userService.getUserStats(user.getId(), user.getId(), true);
    }

    @GetMapping("/user/{targetId}/stats")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public UserStatsDto getTargetStats(Principal principal, @PathVariable int targetId) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null) {
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return userService.getUserStats(user.getId(), targetId, false);
    }

    @GetMapping("/user/{targetId}/updated_stats")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public UserStatsDto getUpdatedTargetStats(Principal principal, @PathVariable int targetId) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null) {
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return userService.getUserStats(user.getId(), targetId, true);
    }

    @GetMapping("/user/stats_preferences")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public UserPreferencesDto getPreferences(Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();
        return userService.getUserPreferences(user.getId());
    }

    @PutMapping("/user/stats_preferences")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public UserPreferencesDto updatePreferences(Principal principal, @Valid @RequestBody UserPreferencesDto preferences) {
        User user = (User) ((Authentication) principal).getPrincipal();
        return userService.updateUserPreferences(user.getId(), preferences);
    }
}
