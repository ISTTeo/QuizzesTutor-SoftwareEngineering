describe('Resubmit Proposal After Being Rejected Walkthrough', () => {
    
    before(() => {
        cy.demoStudentLogin();
        cy.clickStudentCreateProposalTopbar();
        cy.submitQuestionProposalNoNeedToClick('Random Title', 'Description');
        cy.addOptionsWithCorrect('optContent');
        cy.addImage('url', 5);
        cy.clickSubmitProposalBtn();

        cy.submitQuestionProposalClickBtn(
            'Worst Question',
            'Ill Formed No Diagnostic Required'
          );
          cy.addOptionsWithCorrect('optContent');
          cy.addImage('url', 5);
          cy.clickSubmitProposalBtn();

        cy.contains('Logout').click();
        cy.demoTeacherLogin();
        cy.contains('Proposals').click();
        cy.get('[data-cy="searchProposal"]').type('Random Title');
        cy.log('navigate to proposal');
        cy.get('[data-cy="reviewProposalButton"]')
        .first()
        .click();
        cy.get('[data-cy="reasonBar"]').type(
            'Oh my god, this means there can be false positives to -- Is this a program ? -- ???'
          );
        cy.log('Reject review');
        cy.contains('Reject').click();
        cy.contains('Logout').click();
        cy.demoTeacherLogin();
        cy.contains('Proposals').click();
        cy.get('[data-cy="searchProposal"]').type('Worst Question');
        cy.log('navigate to proposal');
        cy.get('[data-cy="reviewProposalButton"]')
        .first()
        .click();
        cy.get('[data-cy="reasonBar"]').type(
            'Oh my god, this means there can be false positives to -- Is this a program ? -- ???'
          );
        cy.log('Reject review');
        cy.contains('Reject').click();

        cy.contains('Logout').click();


    });
    beforeEach(() => {
      cy.demoStudentLogin();
    });
  
    it('Resubmits and checks if passes to pending', () => {
        cy.clickStudentViewProposalTopbar();
        cy.log('search for  Questions');
        cy.get('[data-cy="searchProposal"]').type('Random Title');
        cy.log('navigate to proposal');
        cy.get('[data-cy="resubmitDialogBtn"]')
          .first()
          .click();
          cy.get('[data-cy="resubmitBtn"]')
          .click();
        cy.contains("PENDING");

    });

    it('Resubmits and checks if teacher got the new one', () => {
        cy.clickStudentViewProposalTopbar();
        cy.log('search for  Questions');
        cy.get('[data-cy="searchProposal"]').type('Worst Question');
        cy.log('navigate to proposal');
        cy.get('[data-cy="resubmitDialogBtn"]')
          .first()
          .click();
          cy.get('[data-cy="resubmitBtn"]')
          .click();
        
          cy.contains('Logout').click();
          cy.demoTeacherLogin();
          cy.contains('Proposals').click();
            cy.get('[data-cy="searchProposal"]').type('Worst Question');
            cy.log('navigate to proposal');
          cy.contains("PENDING");
    });
  
    
  });
  