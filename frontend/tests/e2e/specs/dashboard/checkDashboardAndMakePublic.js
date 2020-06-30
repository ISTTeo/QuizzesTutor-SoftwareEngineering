describe('Dashboard Walkthrough', () => {
  beforeEach(() => {});

  afterEach(() => {
          cy.get('[data-cy=logoutButton]').click()
  })

  it('create a discussion and make it public, then check the student dashboard', () => {
    cy.demoStudentLogin();
  
    cy.solveQuiz();
  
    cy.createDiscussion('Title', 'Content');
  
    cy.get('[data-cy=logoutButton]').click();
  
    cy.demoTeacherLogin();
  
    cy.createReplyAndMakePublic('Resposta a pergunta feita pelo aluno.');
  
    cy.get('[data-cy=logoutButton]').click();
  
    cy.demoStudentLogin();
  
    cy.checkDashboard();
  
  });

});