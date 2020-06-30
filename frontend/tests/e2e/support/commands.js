// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//
// -- This is a parent command --
// Cypress.Commands.add("login", (email, password) => { ... })
//
//
// -- This is a child command --
// Cypress.Commands.add("drag", { prevSubject: 'element'}, (subject, options)
// => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add("dismiss", { prevSubject: 'optional'}, (subject,
// options) => { ... })
//
//
// -- This will overwrite an existing command --
// Cypress.Commands.overwrite("visit", (originalFn, url, options) => { ... })
/// <reference types="Cypress" />

Cypress.Commands.add('createCourseExecution', (name, acronym, academicTerm) => {
  cy.get('[data-cy="createButton"]').click();
  cy.get('[data-cy="courseExecutionNameInput"]').type(name);
  cy.get('[data-cy="courseExecutionAcronymInput"]').type(acronym);
  cy.get('[data-cy="courseExecutionAcademicTermInput"]').type(academicTerm);
  cy.get('[data-cy="saveButton"]').click();
});

Cypress.Commands.add('closeErrorMessage', (name, acronym, academicTerm) => {
  cy.contains('Error').parent().find('button').click();
});

Cypress.Commands.add('deleteCourseExecution', acronym => {
  cy.contains(acronym)
      .parent()
      .should('have.length', 1)
      .children()
      .should('have.length', 7)
      .find('[data-cy="deleteCourse"]')
      .click();
});

Cypress.Commands.add(
    'createFromCourseExecution', (name, acronym, academicTerm) => {
      cy.contains(name)
          .parent()
          .should('have.length', 1)
          .children()
          .should('have.length', 7)
          .find('[data-cy="createFromCourse"]')
          .click();
      cy.get('[data-cy="courseExecutionAcronymInput"]').type(acronym);
      cy.get('[data-cy="courseExecutionAcademicTermInput"]').type(academicTerm);
      cy.get('[data-cy="saveButton"]').click();
    });

Cypress.Commands.add('solveQuiz', () => {
  cy.get('[data-cy="quizzesButton"]').click();
  cy.contains('Available').click();
  cy.get(':nth-child(2) > .last-col > [data-cy=quizCircle]').click();
  cy.get(':nth-child(1) > [data-cy=optionButton]').click();
  cy.get('[data-cy=nextQuestionButton]').click();
  cy.get(':nth-child(1) > [data-cy=optionButton]').click();
  cy.get('[data-cy=nextQuestionButton]').click();
  cy.get(':nth-child(1) > [data-cy=optionButton]').click();
  cy.get('[data-cy=nextQuestionButton]').click();
  cy.get(':nth-child(1) > [data-cy=optionButton]').click();
  cy.get('[data-cy=nextQuestionButton]').click();
  cy.get(':nth-child(1) > [data-cy=optionButton]').click();
  cy.get('[data-cy=endQuizButton]').click();
  cy.get('.primary--text').click();
});

Cypress.Commands.add('createDiscussion', (title, content) => {
  cy.get('[data-cy="createDiscussionButton"]').click();
  cy.get('[data-cy="Title"]').type(title);
  cy.get('[data-cy="Content"]').type(content);
  cy.get('[data-cy="saveButton"]').click();
});

Cypress.Commands.add('createDiscussionWithError', (title, content) => {
  cy.get('[data-cy="createDiscussionButton"]').click();
  cy.get('[data-cy="saveButton"]').click();
  cy.get('.v-alert__dismissible > .v-btn__content > .v-icon').click();
  cy.get('[data-cy="Title"]').type(title);
  cy.get('[data-cy="Content"]').type(content);
  cy.get('[data-cy="saveButton"]').click(); 
});

Cypress.Commands.add('createReply', content => {
  cy.get('[data-cy="managementButton"]').click();
  cy.contains('Discussions').click();
  
  cy.wait(1000);
  cy.get(':nth-child(2) > .last-col > [data-cy=createReplyButton]').click({
    force: true
  });
  cy.get('[data-cy="Reply"]').type(content);
  cy.get('[data-cy="saveButton"]').click();
});

Cypress.Commands.add('createReplyAndMakePublic', content => {
  cy.get('[data-cy="managementButton"]').click();
  cy.contains('Discussions').click();
  
  cy.wait(1000);
  cy.get(':nth-child(2) > .last-col > [data-cy=createReplyButton]').click({
    force: true
  });
  cy.get('[data-cy="Reply"]').type(content);
  cy.get('[data-cy="MakePublicToggle"]').click({
    force: true
  });
  cy.get('[data-cy="saveButton"]').click();
});

Cypress.Commands.add('writeReply', content => {
  cy.get('[data-cy="quizzesButton"]').click();
  cy.contains('Discussions').click();
  cy.get(':nth-child(2) > [data-cy=showReplyButton]').click();
  cy.get('[data-cy="Reply"]').type(content);
  cy.get('[data-cy="saveButton"]').click();
});

Cypress.Commands.add('writeReplyForPublic', content => {
  cy.get('[data-cy="quizzesButton"]').click();
  cy.contains('Solved').click();
  cy.get(':nth-child(2) > .last-col > [data-cy=SolvedQuizButton]').click();
  cy.get('[data-cy=showReplyButton]').click()
  cy.get('[data-cy="Reply"]').type(content);
  cy.get('[data-cy="saveButton"]').click();
});

Cypress.Commands.add('seeReplies', () => {
  cy.get('[data-cy="quizzesButton"]').click();
  cy.contains('Discussions').click();
  cy.get(':nth-child(2) > [data-cy=showReplyButton]').click();
  cy.wait(1000);
  cy.get('[data-cy="closeReplyButton"]').click();
});

Cypress.Commands.add('checkDashboard', () => {
  cy.get('[data-cy="dashboardButton"]').click();
  cy.get('[data-cy="visibilitySwitch"]').first().click({
    force: true
  });
});

Cypress.Commands.add('navCreateTournament', () => {
  cy.get('[data-cy="tournaments"]').click();
  cy.contains('Create Tournament').click();
});

Cypress.Commands.add('navFinishedTournaments', cancelled => {
  cy.get('[data-cy="tournaments"]').click();
  cy.contains('Finished Tournaments').click();
  cancelled ? cy.get('[data-cy="cancelledTournamentsTab"]').click() :
              cy.get('[data-cy="solvedTournamentsTab"]').click();
});

Cypress.Commands.add('navOngoingTournaments', available => {
  cy.get('[data-cy="tournaments"]').click();
  cy.contains('Ongoing Tournaments').click();
  available ? cy.get('[data-cy="availableTournamentsTab"]').click() :
              cy.get('[data-cy="completedTournamentsTab"]').click();
})

Cypress.Commands.add('navStudentDashboard', () => {
  cy.contains('Dashboard').click();
})

Cypress.Commands.add('changeDashboardVisibility', () => {
  cy.get('[data-cy="visibilitySwitch"]').first().click({force: true});
})

Cypress.Commands.add('createTournament', name => {
  cy.get('[data-cy="tournamentName"]').click().type(name);
  cy.get('[data-cy="tournamentNumberOfQuestions"]').click().type(5);

  cy.get('[data-cy="beginDate"]').click();
  cy.get(
        '#beginDateInput-picker-container-DatePicker > .calendar > .datepicker-controls > .text-right > .datepicker-button > svg')
      .click();
  cy.wait(500);
  cy.get(
        '#beginDateInput-picker-container-DatePicker > .calendar > .month-container')
      .contains('1')
      .click();
  cy.get(
        '#beginDateInput-wrapper > .datetimepicker > .datepicker > .datepicker-buttons-container > .validate')
      .click();

  cy.get('[data-cy="endDate"]').click();
  cy.get(
        '#endDateInput-picker-container-DatePicker > .calendar > .datepicker-controls > .text-right > .datepicker-button > svg')
      .click();
  cy.wait(500);
  cy.get(
        '#endDateInput-picker-container-DatePicker > .calendar > .month-container')
      .contains('2')
      .click();
  cy.get(
        '#endDateInput-wrapper > .datetimepicker > .datepicker > .datepicker-buttons-container > .validate')
      .click();

  cy.get('.v-data-table-header > tr > .text-start').click();
  cy.get('[data-cy="submit"]').click();
  cy.contains(name);
});

Cypress.Commands.add('createOngoingTournament', name => {
  cy.get('[data-cy="tournamentName"]').click().type(name);
  cy.get('[data-cy="tournamentNumberOfQuestions"]').click().type(5);

  cy.get('[data-cy="beginDate"]').click();
  cy.wait(500);
  cy.get('#beginDateInput-wrapper > .datetimepicker > .datepicker > .datepicker-buttons-container > .now').click();
  cy.get('[data-cy="beginDate"]').click();
  cy.wait(500);
  cy.get('#beginDateInput-wrapper > .datetimepicker > .datepicker > .pickers-container > .time-picker > .time-picker-column-minutes > :nth-child(1) > :nth-child(3)').click();
  cy.get(
    '#beginDateInput-wrapper > .datetimepicker > .datepicker > .datepicker-buttons-container > .validate')
  .click();
  
  cy.get('[data-cy="endDate"]').click();
  cy.wait(500);
  cy.get('#endDateInput-picker-container-DatePicker > .calendar > .datepicker-controls > .text-right > .datepicker-button > svg').click();
  cy.wait(500);
  cy.get(
        '#endDateInput-picker-container-DatePicker > .calendar > .month-container')
      .contains('1')
      .click();
  cy.get(
      '#endDateInput-wrapper > .datetimepicker > .datepicker > .datepicker-buttons-container > .validate')
    .click();
  
    cy.get('.v-data-table-header > tr > .text-start').click();
  cy.get('[data-cy="submit"]').click();
})

Cypress.Commands.add('deleteTournament', name => {
  cy.exec(
      'psql -d tutordb -U ars -h localhost -c "DELETE FROM topics_tournaments tt USING tournaments t WHERE t.id=tt.tournaments_id AND t.name=\'' +
      name +
      // eslint-disable-next-line
      '\'; DELETE FROM users_enrolled_in uei USING tournaments t WHERE t.id=uei.enrolled_in_id AND t.name=\'' +
      name +
      // eslint-disable-next-line
      '\'; DELETE FROM tournaments WHERE name=\'' + name + '\';"');
});

Cypress.Commands.add('navOpenTournament', s => {
  cy.get('[data-cy="tournaments"]').click();
  cy.contains('Open Tournaments').click();
  if (s)
    cy.get('[data-cy="otherTournamentsTab"]').click();
  else
    cy.get('[data-cy="myTournamentsTab"]').click();
});

Cypress.Commands.add('enrollInTournament', name => {
  cy.get('.text-center > .v-icon').click();
});

Cypress.Commands.add('unenrollInTournament', name => {
  cy.get('.text-center > .v-icon').click();
});

Cypress.Commands.add('cancelTournament', name => {
  cy.get('.text-center > .material-icons').click();
});

Cypress.Commands.add('solveTournamentQuiz', name => {
  cy.get('[data-cy="startButton"]').click();
  cy.get(':nth-child(1) > [data-cy=optionButton]').click();
  cy.get('[data-cy=nextQuestionButton]').click();
  cy.get(':nth-child(1) > [data-cy=optionButton]').click();
  cy.get('[data-cy=nextQuestionButton]').click();
  cy.get(':nth-child(1) > [data-cy=optionButton]').click();
  cy.get('[data-cy=nextQuestionButton]').click();
  cy.get(':nth-child(1) > [data-cy=optionButton]').click();
  cy.get('[data-cy=nextQuestionButton]').click();
  cy.get(':nth-child(1) > [data-cy=optionButton]').click();
  cy.get('[data-cy=endQuizButton]').click();
  cy.get('.primary--text > .v-btn__content').click();
})

Cypress.Commands.add('clickStudentViewProposalTopbar', () => {
  cy.contains('Question Proposals').click();
  cy.contains('View Question Proposals').click();
});

Cypress.Commands.add('checkProposals', () => {
  cy.contains('PENDING');
});

Cypress.Commands.add('clickStudentCreateProposalTopbar', () => {
  cy.contains('Question Proposals').click();
  cy.contains('Create Question Proposal').click();
});

Cypress.Commands.add('submitQuestionProposalClickBtn', (title, content) => {
  cy.get('[data-cy="createSubmitProposalBtn"]').click();
  cy.get('[data-cy="submitProposalTitleArea"]').type(title);
  cy.get('[data-cy="submitProposalContentArea"]').type(content);
});

Cypress.Commands.add('searchProposal', (title, description) => {
  cy.get('[data-cy="searchProposal"]').type(title);
  cy.contains(title);
  cy.contains(description);
});

Cypress.Commands.add(
    'submitQuestionProposalNoNeedToClick', (title, content) => {
      cy.get('[data-cy="submitProposalTitleArea"]').type(title);
      cy.get('[data-cy="submitProposalContentArea"]').type(content);
    });

Cypress.Commands.add('addOptionsWithoutCorrect', optionContent => {
  cy.get('[data-cy="addOptionsBtn"]').click();
  cy.get('[optionContentId="1"]').type(optionContent);
  cy.get('[optionContentId="2"]').type(optionContent);
  cy.get('[optionContentId="3"]').type(optionContent);
});

Cypress.Commands.add('addOptionsWithCorrect', optionContent => {
  cy.get('[data-cy="addOptionsBtn"]').click();
  cy.get('[optionContentId="1"]').type(optionContent);
  cy.get('[optionContentId="2"]').type(optionContent);
  cy.get('[optionContentId="3"]').type(optionContent);
  cy.get('[optionContentId="0"]').type(optionContent);

  cy.get('[data-cy="optionSwitch"]').get('[type="checkbox"]').last().check({
    force: true
  });
});

Cypress.Commands.add('addImage', (url, width) => {
  cy.get('[data-cy="addImageBtn"]').click();
  cy.get('[data-cy="submitProposalUrlArea"]').type(url);
  cy.get('[data-cy="submitProposalImageArea"]')
      .get('.v-slider')
      .first()
      .click(2, 40, {force: true});
});

Cypress.Commands.add('clickSubmitProposalBtn', () => {
  cy.get('[data-cy="submitProposalBtn"]').click();
});

Cypress.Commands.add('checkError', () => {
  cy.contains('Error');
});

Cypress.Commands.add('getProposalCount', () => {
  cy.contains('PENDING').parent().parent().get('tbody').find('tr');
});
