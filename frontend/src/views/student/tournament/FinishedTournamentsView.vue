<template>
  <div class="container">
    <h2>Finished Tournaments</h2>
    <v-card>
      <v-tabs :grow="true" background-color="primary" dark>
        <v-tab data-cy="solvedTournamentsTab">Solved Tournaments</v-tab>
        <v-tab data-cy="cancelledTournamentsTab">Cancelled Tournaments</v-tab>

        <v-tab-item>
          <v-data-table
            :headers="headers1"
            :items="solvedQuizzes"
            :mobile-breakpoint="0"
            :hide-default-footer="true"
          ></v-data-table>
          <template v-slot:item.score="{item}">{{ calculateScore(item) }}</template>
        </v-tab-item>

        <v-tab-item>
          <v-data-table
            :headers="headers2"
            :items="cancelledTournaments"
            :mobile-breakpoint="0"
            :hide-default-footer="true"
          ></v-data-table>
        </v-tab-item>
      </v-tabs>
    </v-card>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import User from '@/models/user/User';
import Store from '@/store';
import Tournament from '../../../models/management/Tournament';
import SolvedQuiz from '@/models/statement/SolvedQuiz';
import StatementManager from '@/models/statement/StatementManager';

@Component
export default class FinishedTournamentsView extends Vue {
  solvedQuizzes: SolvedQuiz[] = [];
  cancelledTournaments: Tournament[] = [];
  username: String = this.$store.getters.getUser.username;
  headers1: object = [
    {
      text: 'Tournament',
      value: 'statementQuiz.title',
      align: 'center'
    },
    {
      text: 'Score',
      value: 'score',
      align: 'center'
    }
  ];
  headers2: object = [
    {
      text: 'Tournament',
      value: 'name',
      align: 'center'
    },
    {
      text: 'Canceled by ',
      value: 'createdBy.username',
      align: 'center'
    }
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      let canTours = (await RemoteServices.getCancelledTournaments()).reverse();
      canTours.forEach(t => this.cancelledTournaments.push(t));
      let solQuizzes = (await RemoteServices.getSolvedTournaments()).reverse();
      solQuizzes.forEach(t => this.solvedQuizzes.push(t));
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  calculateScore(quiz: SolvedQuiz) {
    let correct = 0;
    for (let i = 0; i < quiz.statementQuiz.questions.length; i++) {
      if (
        quiz.statementQuiz.answers[i] &&
        quiz.correctAnswers[i].correctOptionId ===
          quiz.statementQuiz.answers[i].optionId
      ) {
        correct += 1;
      }
    }
    return `${correct}/${quiz.statementQuiz.questions.length}`;
  }
}
</script>

<style lang="scss" scoped>
.container {
  margin-left: auto;
  margin-right: auto;
  padding-left: 10px;
  padding-right: 10px;

  h2 {
    font-size: 26px;
    margin: 20px 0;
    text-align: center;
    small {
      font-size: 0.5em;
    }
  }

  ul {
    overflow: hidden;
    padding: 0 5px;

    li {
      border-radius: 3px;
      padding: 15px 10px;
      display: flex;
      justify-content: space-between;
      margin-bottom: 10px;
    }

    .list-header {
      background-color: #1976d2;
      color: white;
      font-size: 14px;
      text-transform: uppercase;
      letter-spacing: 0.03em;
      text-align: center;
    }

    .col {
      flex-basis: 25% !important;
      margin: auto; /* Important */
      text-align: center;
    }

    .list-row {
      background-color: #ffffff;
      box-shadow: 0 0 9px 0 rgba(0, 0, 0, 0.1);
      display: flex;
    }

    .list-divide {
      background-color: #a9a9a9;
      box-shadow: 0 0 9px 0 rgba(0, 0, 0, 0.1);
      display: flex;
    }
  }
}
</style>
