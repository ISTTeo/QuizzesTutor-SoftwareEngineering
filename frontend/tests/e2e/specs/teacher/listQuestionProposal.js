/**
 * important note
 *
 * the tests do not cleanup, since there is no cleanup UI
 * this also affects the tests themselves (eg: we test for *at least* two rows, instead of two rows)
 *
 * this should be fixed when this is implemented
 */

describe('Teacher Lists Question Proposals', () => {
  before(() => {
    cy.demoStudentLogin();
    cy.clickStudentCreateProposalTopbar();
    cy.submitQuestionProposalNoNeedToClick(
      'Random Title',
      'Proposterous Description'
    );
    cy.addOptionsWithCorrect('optContent');
    cy.addImage('url', 5);
    cy.clickSubmitProposalBtn();
    cy.submitQuestionProposalClickBtn('Another Title', 'Simple Description');
    cy.addOptionsWithCorrect('optContent');
    cy.addImage('url', 5);
    cy.clickSubmitProposalBtn();
    cy.contains('Logout').click();
  });

  beforeEach(() => {
    cy.demoTeacherLogin();
  });

  it('login lists questions', () => {
    cy.contains('Proposals').click();
    cy.log('check how many proposals there are');
    cy.getProposalCount().should('length.be.at.least', 2);
  });

  it('login searches for questions', () => {
    cy.contains('Proposals').click();
    cy.log('search for Random Title');
    cy.get('[data-cy="searchProposal"]').type('Random');

    cy.log('verify that searched proposal exists');
    cy.contains('Random Title');
    cy.contains('Proposterous Description');

    cy.log('check how many proposals there are');
    cy.getProposalCount().should('length.be.at.least', 1);
  });
});
