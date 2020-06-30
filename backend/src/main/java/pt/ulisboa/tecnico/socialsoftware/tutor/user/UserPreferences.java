package pt.ulisboa.tecnico.socialsoftware.tutor.user;

import org.hibernate.annotations.Parent;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserPreferencesDto;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.io.Serializable;

@Embeddable @Access(AccessType.FIELD)
public class UserPreferences implements Serializable {
    @Parent
    private User user;

    private Boolean answerStatsVisibility = true;
    private Boolean tournamentStatsVisibility = true;
    private Boolean discussionStatsVisibility = true;
    private Boolean proposalVisibility = true;

    public UserPreferences() { }

    public UserPreferences(User user) {
        setUser(user);
    }

    public User getUser() {
        return this.user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public boolean getAnswerStatsVisibility() {
        return this.answerStatsVisibility;
    }

    public void setAnswerStatsVisibility(Boolean visibility) {
        this.answerStatsVisibility = visibility;
    }

    public boolean getTournamentStatsVisibility(){
        return this.tournamentStatsVisibility;
    }

    public void setTournamentStatsVisibility(Boolean visibility){
        this.tournamentStatsVisibility = visibility;
    }

    public boolean getDiscussionStatsVisibility() {
        return this.discussionStatsVisibility;
    }

    public void setDiscussionStatsVisibility(boolean visibility) {
        this.discussionStatsVisibility = visibility;
    }

    public boolean getProposalVisibility() {
        return this.proposalVisibility;
    }

    public void setProposalVisibility(boolean visibility) {
        this.proposalVisibility = visibility;
    }

    public void updateUserPreferences(UserPreferencesDto preferencesDto) {
        if (preferencesDto == null) {
            this.setAnswerStatsVisibility(true);
            this.setDiscussionStatsVisibility(true);
            this.setTournamentStatsVisibility(true);
            this.setProposalVisibility(true);
        } else {
            this.setAnswerStatsVisibility(preferencesDto.getAnswerStatsVisibility());
            this.setDiscussionStatsVisibility(preferencesDto.getDiscussionStatsVisibility());
            this.setTournamentStatsVisibility(preferencesDto.getTournamentStatsVisibility());
            this.setProposalVisibility(preferencesDto.getProposalVisibility());
        }
    }
}
