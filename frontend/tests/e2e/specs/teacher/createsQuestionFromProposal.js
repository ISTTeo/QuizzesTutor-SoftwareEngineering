/**
 * important note
 *
 * the tests do not cleanup, since there is no cleanup UI
 * this also affects the tests themselves (eg: we test for *at least* two rows, instead of two rows)
 *
 * this should be fixed when this is implemented
 */

describe('Teacher Creates Question from proposal', () => {
    before(() => {
      cy.log('Create 2 proposals');
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

      cy.log('Accept both');

      cy.demoTeacherLogin();
      cy.contains('Proposals').click();

      cy.log('search for  Questions');
      cy.get('[data-cy="searchProposal"]').type('Good Question');
      cy.log('navigate to proposal');
      cy.get('[data-cy="reviewProposalButton"]')
        .first()
        .click();
        cy.get('[data-cy="reasonBar"]').type(
            'Oh my god, this means there can be false positives to -- Is this a program ? -- ???'
          );
      cy.log('accept review');
      cy.contains('Accept').click();
      cy.contains('Logout').click();

    
      cy.demoTeacherLogin();
      cy.contains('Proposals').click();
      cy.get('[data-cy="searchProposal"]').type('Bad Question');
      cy.log('navigate to proposal');
      cy.get('[data-cy="reviewProposalButton"]')
        .first()
        .click();
        cy.get('[data-cy="reasonBar"]').type(
            'Oh my god, this means there can be false positives to -- Is this a program ? -- ???'
          );
      cy.log('accept review');
      cy.contains('Accept').click();

      cy.contains('Logout').click();

    });
  
    beforeEach(() => {
      cy.demoTeacherLogin();
    });
  
    
    it('Creates question from non altered proposal', () => {

      cy.contains('Proposals').click();
      cy.log('search for Question');
      cy.get('[data-cy="searchProposal"]').type('Good Question');
      cy.log('navigate to proposal');
      
      cy.log('Create question from proposal');
      cy.get('[data-cy="openQstFromPropDialogBtn"]')
        .first()
        .click();
      cy.get('[data-cy="qstFromPropSaveBtn"]')
        .click();
        cy.contains('Logout').click();
        cy.demoTeacherLogin();

      cy.log('Check questions');
      cy.contains('Questions').click();
      cy.contains('Good Question')
    });

    it('Creates question from altered proposal', () => {
      cy.contains('Proposals').click();
      cy.log('search for Question');
      cy.get('[data-cy="searchProposal"]').type('Bad Question');
      cy.log('navigate to proposal');
      
      cy.log('Create Question from altered proposal');
      cy.get('[data-cy="openQstFromPropDialogBtn"]')
        .first()
        .click();
      cy.log('change stuff');
      cy.get('[data-cy="qstFromPropTitleBar"]').type(
        '!',
        {force:true}
      );
      cy.get('[data-cy="qstFromPropSaveBtn"]')
        .click();
        cy.contains('Logout').click();
        cy.demoTeacherLogin();

        cy.log('Check questions');
      cy.contains('Questions').click();
      cy.contains('Bad Question!');
    });

  });
  
