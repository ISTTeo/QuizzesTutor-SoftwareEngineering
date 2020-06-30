<template>
  <v-card class="table">
    <v-card-title>
      <span>Create Tournament</span>

      <v-spacer />
    </v-card-title>
    <v-card-text>
      <v-row>
        <v-col cols="12" sm="6">
          <v-text-field data-cy="tournamentName" v-model="newTournament.name" label="*Name" />
        </v-col>
        <v-col cols="12" sm="6">
          <v-text-field
            data-cy="tournamentNumberOfQuestions"
            v-model="newTournament.numberOfQuestions"
            label="*Number of questions"
            type="number"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col cols="12" sm="6">
          <VueCtkDateTimePicker
            data-cy="beginDate"
            label="*Begin Date"
            id="beginDateInput"
            v-model="newTournament.beginDate"
            format="YYYY-MM-DDTHH:mm:ssZ"
          ></VueCtkDateTimePicker>
        </v-col>
        <v-spacer></v-spacer>
        <v-col cols="12" sm="6">
          <VueCtkDateTimePicker
            data-cy="endDate"
            label="*End Date"
            id="endDateInput"
            v-model="newTournament.endDate"
            format="YYYY-MM-DDTHH:mm:ssZ"
          ></VueCtkDateTimePicker>
        </v-col>
      </v-row>

      <v-row>
        <v-col>
          <v-tooltip bottom>
            <template v-slot:activator="{ on }">
              <v-switch v-on="on" v-model="newTournament.quiz.scramble" label="Scramble" />
            </template>
            <span>Question order is scrambled</span>
          </v-tooltip>
        </v-col>
        <v-col>
          <v-tooltip bottom>
            <template v-slot:activator="{ on }">
              <v-switch v-on="on" v-model="newTournament.quiz.oneWay" label="One Way Quiz" />
            </template>
            <span>Students cannot go to previous question</span>
          </v-tooltip>
        </v-col>
        <v-col>
          <v-tooltip bottom>
            <template v-slot:activator="{ on }">
              <v-switch v-on="on" v-model="newTournament.quiz.timed" label="Timer" />
            </template>
            <span>Displays a timer to conclusion and to show results</span>
          </v-tooltip>
        </v-col>
        <v-col>
          <!-- Placeholder so number of elements in row is even -->
        </v-col>
      </v-row>
      <v-spacer />

      <v-container>
        <h4 class="pl-0">Topics</h4>
        <v-data-table
          :headers="headers"
          :custom-filter="customFilter"
          :items="topics"
          :search="search"
          :mobile-breakpoint="0"
          :items-per-page="5"
          :footer-props="{ itemsPerPageOptions: [5, 10, 25, 50] }"
          :v-model="selected"
          v-on:item-selected="select"
          v-on:toggle-select-all="selectAll"
          show-select
        >
          <template v-slot:top>
            <v-card-title>
              <v-text-field
                v-model="search"
                append-icon="search"
                label="Search"
                single-line
                hide-details
              />
            </v-card-title>
          </template>
        </v-data-table>
      </v-container>

      <v-btn color="primary" data-cy="submit" v-if="canSave" @click="submit">Submit</v-btn>
    </v-card-text>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Topic from '../../../models/management/Topic';
import Tournament from '../../../models/management/Tournament';
import router from '../../../router';
import User from '../../../models/user/User';
import { Quiz } from '../../../models/management/Quiz';

@Component
export default class CreateTournamentView extends Vue {
  newTournament: Tournament = new Tournament();
  topics: Topic[] = [];
  selected: boolean[] = [];
  hasTopics: boolean = false;
  search: string = '';
  headers: object = [
    { text: 'Topic', value: 'name', align: 'left' },
    {
      text: 'Questions',
      value: 'numberOfQuestions',
      align: 'center',
      width: '115px'
    }
  ];

  customFilter(value: string, search: string) {
    // noinspection SuspiciousTypeOfGuard,SuspiciousTypeOfGuard
    return (
      search != null &&
      typeof value === 'string' &&
      value.toLocaleLowerCase().indexOf(search.toLocaleLowerCase()) !== -1
    );
  }

  select(val: any) {
    this.selected[this.topics.indexOf(val.item)] = val.value;
    this.hasTopics = this.selected.indexOf(true) > -1;
  }

  selectAll(all: any) {
    all.items.forEach((item: any) => {
      this.select({ item: item, value: all.value });
    });
  }

  get canSave(): boolean {
    return (
      !!this.newTournament.name &&
      !!this.newTournament.beginDate &&
      !!this.newTournament.endDate &&
      !!this.newTournament.numberOfQuestions &&
      this.hasTopics
    );
  }

  async submit() {
    this.newTournament.quiz.numberOfQuestions = this.newTournament.numberOfQuestions;
    if (this.newTournament.beginDate)
      this.newTournament.quiz.availableDate = this.newTournament.beginDate;
    if (this.newTournament.endDate)
      this.newTournament.quiz.conclusionDate = this.newTournament.endDate;
    this.newTournament.quiz.title = this.newTournament.name + ' - Quiz';
    this.topics.forEach((topic, index) => {
      if (this.selected[index]) this.newTournament.topics.push(topic);
    });
    try {
      await RemoteServices.createTournament(this.newTournament);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    this.cleanup();
    this.$router.push('/student/tournaments/open');
  }

  cleanup() {
    this.selected = [];
    this.newTournament = new Tournament();
    this.search = '';
  }

  async created() {
    this.newTournament.quiz = new Quiz();
    await this.$store.dispatch('loading');
    try {
      this.topics = await RemoteServices.getTopics();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }
}
</script>

<style lang="scss" scoped>
.create-buttons {
  width: 80% !important;
  background-color: white;
  border-width: 10px;
  border-style: solid;
  border-color: #818181;
}

.topics {
  width: 80% !important;
  border-width: 5px;
  border-style: solid;
  border-color: #818181;
}

.topic-item {
  border-bottom-width: 0 0 1 0;
}

.button-group {
  flex-wrap: wrap;
  justify-content: center;
}
</style>
