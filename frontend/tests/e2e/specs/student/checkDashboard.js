describe('Student Checks Dashboard Walkthrough', () => {

    it('Check Dashboard', () => {
        cy.demoStudentLogin();
        cy.contains('Dashboard').click();
        cy.contains('0');
    });

    it('Add proposal and check for change', () => {
        cy.demoStudentLogin();
        cy.clickStudentCreateProposalTopbar();
        cy.submitQuestionProposalNoNeedToClick(
          'Good Question',
          'Life, the Universe and Everything (hint: 42)'
        );
        cy.addOptionsWithCorrect('optContent');
        cy.addImage('url', 5);
        cy.clickSubmitProposalBtn();
  
        
        cy.contains('Dashboard').click();
        cy.contains('1');
        cy.contains('0');

        cy.contains('Logout').click();

    });

    it('Accept proposal, add extra proposal and check for change', () => {
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

        cy.demoStudentLogin();
        cy.clickStudentCreateProposalTopbar();
        cy.submitQuestionProposalNoNeedToClick(
          'Bad Question',
          'Life, the Universe and Everything (hint: 42)'
        );
        cy.addOptionsWithCorrect('optContent');
        cy.addImage('url', 5);
        cy.clickSubmitProposalBtn();

        cy.contains('Dashboard').click();
        cy.contains('1');
        cy.contains('2');
        cy.contains('Logout').click();
    });

    it('Check dashboard and toggle visibility', () => {
        cy.demoStudentLogin();
        

        cy.contains('Dashboard').click();
        cy.get('[data-cy="visibilitySwitch"]')
          .first()
          .click({force:true});
          cy.get('[data-cy="visibilitySwitch"]')
          .first()
          .click({force:true});
          cy.get('[data-cy="visibilitySwitch"]')
          .first()
          .click({force:true});
        cy.contains('Logout').click();
    });
});
  