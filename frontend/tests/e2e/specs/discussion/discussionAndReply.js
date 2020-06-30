describe('Discussion and Reply walkthrough', () => {
  beforeEach(() => {});

  afterEach(() => {
          cy.get('[data-cy=logoutButton]').click()
  })

  it('login creates a discussion', () => {
    cy.demoStudentLogin();

    cy.solveQuiz();

    cy.createDiscussionWithError('Title', 'Content');

    cy.get('[data-cy=logoutButton]').click();

    cy.demoTeacherLogin();

    cy.createReply('Resposta a pergunta feita pelo aluno.');

    cy.get('[data-cy=logoutButton]').click();

    cy.demoStudentLogin();

    cy.seeReplies();
  });

  it('create a discussion and create 2 replies', () => {
    cy.demoStudentLogin();

    cy.solveQuiz();

    cy.createDiscussion('Title', 'Content');

    cy.get('[data-cy=logoutButton]').click();

    cy.demoTeacherLogin();

    cy.createReply('Resposta a pergunta feita pelo aluno.');

    cy.get('[data-cy=logoutButton]').click();

    cy.demoStudentLogin();

    cy.writeReply('Pedido de esclarecimento adicional.');

    cy.seeReplies();
  });

  it('create a discussion, make it public and create 2 replies', () => {
    cy.demoStudentLogin();

    cy.solveQuiz();

    cy.createDiscussion('Title', 'Content');

    cy.get('[data-cy=logoutButton]').click();

    cy.demoTeacherLogin();

    cy.createReplyAndMakePublic('Resposta a pergunta feita pelo aluno.');

    cy.get('[data-cy=logoutButton]').click();

    cy.demoStudentLogin();

    cy.writeReplyForPublic('Pedido de esclarecimento numa discussão pública.');

    cy.seeReplies();
  });

});
