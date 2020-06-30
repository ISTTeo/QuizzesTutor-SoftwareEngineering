/**
 * important note
 *
 * the tests do not cleanup, since there is no cleanup UI
 * this also affects the tests themselves (eg: we test for *at least* two rows, instead of two rows)
 *
 * this should be fixed when this is implemented
 */

describe('Teacher Reviews Question Proposals', () => {
  before(() => {
    cy.demoStudentLogin();
    cy.clickStudentCreateProposalTopbar();
    cy.submitQuestionProposalNoNeedToClick(
      'Good Question',
      'Life, the Universe and Everything (hint: 42)'
    );
    cy.addOptionsWithCorrect('optContent');
    cy.addImage('url', 5);
    cy.clickSubmitProposalBtn();
    cy.submitQuestionProposalClickBtn(
      'Bad Question',
      'Ill Formed No Diagnostic Required'
    );
    cy.addOptionsWithCorrect('optContent');
    cy.addImage('url', 5);
    cy.clickSubmitProposalBtn();
    cy.contains('Logout').click();
  });

  beforeEach(() => {
    cy.demoTeacherLogin();
  });

  it('login cancels a review', () => {
    cy.contains('Proposals').click();
    cy.log('navigate to proposal');
    cy.get('[data-cy="reviewProposalButton"')
      .first()
      .click();
    cy.log('cancel review');
    cy.contains('Cancel').click();
  });

  it('login accepts a proposal', () => {
    cy.contains('Proposals').click();

    cy.log('search for Good Question');
    cy.get('[data-cy="searchProposal"]').type('Good Question');
    cy.log('navigate to proposal');
    cy.get('[data-cy="reviewProposalButton"]')
      .first()
      .click();
    cy.log('accept review');
    cy.contains('Accept').click();
  });

  it('login rejects a proposal', () => {
    cy.contains('Proposals').click();
    cy.log('search for Bad Question');
    cy.get('[data-cy="searchProposal"]').type('Bad Question');
    cy.log('navigate to proposal');
    cy.get('[data-cy="reviewProposalButton"]')
      .first()
      .click();
    cy.log('justify');
    cy.get('[data-cy="reasonBar"]').type(
      'Oh my god, this means there can be false positives to -- Is this a program ? -- ???'
    );
    cy.log('reject review');
    cy.contains('Reject').click();
  });
});
