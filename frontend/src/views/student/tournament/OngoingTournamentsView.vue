<template>
  <div class="container">
    <h2>Ongoing Tournaments</h2>
    <v-tabs :grow="true" background-color="primary" dark>
      <v-tab data-cy="availableTournamentsTab">Available Tournaments</v-tab>
      <v-tab data-cy="completedTournamentsTab">Completed Tournaments</v-tab>

      <v-tab-item>
        <v-data-table
          :headers="headers1"
          :items="openQuizzes"
          :mobile-breakpoint="0"
          :hide-default-footer="true"
        >
          <template v-slot:item.actions="{item}">
            <v-tooltip bottom>
              <template v-slot:activator="{ on }">
                <v-icon
                  large
                  v-on="on"
                  class="mr-2"
                  @click="solveQuiz(item)"
                  color="green"
                  data-cy="startButton"
                >mdi-chevron-right-circle</v-icon>
              </template>
              <span>Start!</span>
            </v-tooltip>
          </template>
        </v-data-table>
      </v-tab-item>
      <v-tab-item>
        <v-data-table
          :headers="headers2"
          :items="closedQuizzes"
          :mobile-breakpoint="0"
          :hide-default-footer="true"
        >
          <template v-slot:item.score="{item}">{{calculateScore(item)}}</template>
        </v-data-table>
      </v-tab-item>
    </v-tabs>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import StatementManager from '@/models/statement/StatementManager';
import StatementQuiz from '@/models/statement/StatementQuiz';
import StatementQuestion from '@/models/statement/StatementQuestion';
import StatementAnswer from '@/models/statement/StatementAnswer';
import SolvedQuiz from '../../../models/statement/SolvedQuiz';

@Component
export default class OngoingTournamentsView extends Vue {
  openQuizzes: StatementQuiz[] = [];
  closedQuizzes: SolvedQuiz[] = [];
  headers1: object = [
    {
      text: 'Title',
      value: 'title',
      align: 'left'
    },
    {
      text: 'Available Date',
      value: 'availableDate',
      align: 'left'
    },
    {
      text: 'Conclusion Date',
      value: 'conclusionDate',
      align: 'left'
    },
    {
      text: 'Actions',
      value: 'actions',
      align: 'left',
      width: 50,
      sortable: false
    }
  ];
  headers2: object = [
    {
      text: 'Title',
      value: 'statementQuiz.title',
      align: 'left'
    },
    {
      text: 'Available Date',
      value: 'statementQuiz.availableDate',
      align: 'left'
    },
    {
      text: 'Conclusion Date',
      value: 'statementQuiz.conclusionDate',
      align: 'left'
    },
    {
      text: 'Score',
      value: 'score',
      align: 'left',
      width: 50
    }
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.openQuizzes = (
        await RemoteServices.getOngoingTournaments()
      ).reverse();
      this.closedQuizzes = (
        await RemoteServices.getSolvedOngoingTournaments()
      ).reverse();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async solveQuiz(quiz: StatementQuiz) {
    let statementManager: StatementManager = StatementManager.getInstance;
    statementManager.statementQuiz = quiz;
    await this.$router.push({ name: 'solve-quiz' });
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
