export default class FeatureStats {
    
    numberOfSolvedTournaments!:number;
    averageTournamentScore!:number;

    numberOfDiscussions !: number;
    numberOfPublicDiscussions !: number;

    numberOfSubmitedProposals!: number;
    numberOfAcceptedProposals!: number;
  
    constructor(jsonObj?: FeatureStats) {
      if (jsonObj) {
        
        this.numberOfSolvedTournaments = jsonObj.numberOfSolvedTournaments;
        this.averageTournamentScore = jsonObj.averageTournamentScore;

        this.numberOfDiscussions = jsonObj.numberOfDiscussions;
        this.numberOfPublicDiscussions = jsonObj.numberOfPublicDiscussions;

        this.numberOfAcceptedProposals = jsonObj.numberOfAcceptedProposals;
        this.numberOfSubmitedProposals = jsonObj.numberOfSubmitedProposals;
      }
    }
}
  