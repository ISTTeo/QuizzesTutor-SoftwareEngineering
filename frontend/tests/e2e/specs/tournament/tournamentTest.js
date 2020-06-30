describe('Student walkthrough', () => {
  const TOUR_NAME = 'T1';

  beforeEach(() => {
    cy.demoStudentLogin();
  });

  afterEach(() => {
    cy.deleteTournament(TOUR_NAME);
    cy.get('[data-cy=logoutButton]').click();
  });

  it('Create a tournament and solve it, check dashboard is updated', () => {
    cy.navCreateTournament();
    cy.createOngoingTournament(TOUR_NAME); /////mudar para criar quase já a começar
    cy.navOngoingTournaments(true);
    cy.solveTournamentQuiz(TOUR_NAME);
    cy.navOngoingTournaments(false);
    cy.contains(TOUR_NAME);
    cy.navStudentDashboard();
    cy.wait(1000);
    cy.changeDashboardVisibility();
  });

  it('Create, enroll and unenroll in a tournament', () => {
    cy.navCreateTournament();
    cy.createTournament(TOUR_NAME);
    cy.get('[data-cy=logoutButton]').click();
    cy.demoStudentLogin();
    cy.navOpenTournament(true);
    cy.enrollInTournament(TOUR_NAME);
    cy.wait(200);
    cy.unenrollInTournament(TOUR_NAME);
  });

  it('Create a tournament and cancel it', () => {
    cy.navCreateTournament();
    cy.createTournament(TOUR_NAME);
    cy.cancelTournament();
    cy.navFinishedTournaments(true);
    cy.contains(TOUR_NAME);
  });

});
