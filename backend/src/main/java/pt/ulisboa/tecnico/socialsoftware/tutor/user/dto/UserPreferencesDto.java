package pt.ulisboa.tecnico.socialsoftware.tutor.user.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserPreferences;

import java.io.Serializable;

public class UserPreferencesDto implements Serializable {
    private boolean answerStatsVisibility = true;
    private boolean tournamentStatsVisibility = true;
    private boolean discussionStatsVisibility = true;
    private boolean proposalVisibility = true;

    public UserPreferencesDto(){}

    public UserPreferencesDto(UserPreferences preferences) {
        this.answerStatsVisibility = preferences.getAnswerStatsVisibility();
        this.tournamentStatsVisibility = preferences.getTournamentStatsVisibility();
        this.discussionStatsVisibility = preferences.getDiscussionStatsVisibility();
        this.proposalVisibility = preferences.getProposalVisibility();
    }

    public boolean getAnswerStatsVisibility() {
        return this.answerStatsVisibility;
    }

    public void setAnswerStatsVisibility(boolean visibility) {
        this.answerStatsVisibility = visibility;
    }

    public boolean getTournamentStatsVisibility() {
        return this.tournamentStatsVisibility;
    }

    public void setTournamentStatsVisibility(boolean visibility) {
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
}
