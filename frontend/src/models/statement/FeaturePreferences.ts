export default class FeaturePreferences {
    
    answerStatsVisibility!: boolean;
    tournamentStatsVisibility!:boolean;
    discussionStatsVisibility!: boolean;
    proposalVisibility!: boolean;

    constructor(jsonObj?: FeaturePreferences) {
      if (jsonObj) {
        this.answerStatsVisibility = jsonObj.answerStatsVisibility;
        this.tournamentStatsVisibility = jsonObj.tournamentStatsVisibility;
        this.discussionStatsVisibility = jsonObj.discussionStatsVisibility;
        this.proposalVisibility = jsonObj.proposalVisibility;
        
      }
    }
  }
  