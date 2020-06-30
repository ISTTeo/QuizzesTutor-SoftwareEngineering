describe('Submit Question Proposal Student Walkthrough', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
  });

  it('login submits a proposal', () => {
    cy.clickStudentCreateProposalTopbar();
    cy.submitQuestionProposalNoNeedToClick('Random Title', 'Description');
    cy.clickSubmitProposalBtn();
  });

  it('login submits proposal with options without a correct one', () => {
    cy.clickStudentCreateProposalTopbar();
    cy.submitQuestionProposalNoNeedToClick('Random Title', 'Description');
    cy.addOptionsWithoutCorrect('optContent');
    cy.clickSubmitProposalBtn();
    cy.checkError();
  });

  it('login submits proposal with options with a correct one', () => {
    cy.clickStudentCreateProposalTopbar();
    cy.submitQuestionProposalNoNeedToClick('Random Title', 'Description');
    cy.addOptionsWithCorrect('optContent');
    cy.clickSubmitProposalBtn();
  });

  it('login submits proposal with image', () => {
    cy.clickStudentCreateProposalTopbar();
    cy.submitQuestionProposalNoNeedToClick('Random Title', 'Description');
    cy.addImage('url', 5);
    cy.clickSubmitProposalBtn();
  });

  it('login views proposals and search for a proposal', () => {
    cy.clickStudentCreateProposalTopbar();
    cy.submitQuestionProposalNoNeedToClick('Random Title', 'Description');
    cy.addOptionsWithCorrect('optContent');
    cy.addImage('url', 5);
    cy.clickSubmitProposalBtn();
    cy.checkProposals();
    cy.searchProposal('Random Title', 'Description');
  });
});
