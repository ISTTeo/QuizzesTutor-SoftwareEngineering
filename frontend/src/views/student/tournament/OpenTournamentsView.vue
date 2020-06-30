<template>
  <div class="container">
    <h2>Open Tournaments</h2>

    <v-tabs :grow="true" background-color="primary" dark>
      <v-tab data-cy="myTournamentsTab">My Tournaments</v-tab>
      <v-tab data-cy="otherTournamentsTab">Other Tournaments</v-tab>

      <v-tab-item>
        <v-container>
          <v-data-table
            :headers="headers"
            :items="myTournaments"
            :mobile-breakpoint="0"
            :hide-default-footer="true"
          >
            <template v-slot:item.nEnrolled="{item}">{{item.enrolled.length}}</template>
            <template v-slot:item.actions="{item}">
              <v-tooltip bottom>
                <template v-slot:activator="{ on }">
                  <v-icon large v-on="on" class="mr-2" @click="cancel(item)" color="red">delete</v-icon>
                </template>
                <span>Cancel</span>
              </v-tooltip>
              <v-tooltip bottom v-if="isEnrolledMyTournaments(item)" data-cy="unenrollButton">
                <template v-slot:activator="{ on }">
                  <v-icon
                    large
                    v-on="on"
                    class="mr-2"
                    @click="unenroll(item)"
                    color="red"
                  >mdi-account-minus</v-icon>
                </template>
                <span>Unenroll</span>
              </v-tooltip>
              <v-tooltip bottom v-else data-cy="unenrollButton">
                <template v-slot:activator="{ on }">
                  <v-icon
                    large
                    v-on="on"
                    class="mr-2"
                    @click="enroll(item)"
                    color="blue"
                  >mdi-account-plus</v-icon>
                </template>
                <span>Enroll</span>
              </v-tooltip>
            </template>
          </v-data-table>
        </v-container>
      </v-tab-item>
      <v-tab-item>
        <v-container>
          <v-data-table
            :headers="headers"
            :items="tournaments"
            :mobile-breakpoint="0"
            :hide-default-footer="true"
          >
            <template v-slot:item.nEnrolled="{item}">{{item.enrolled.length}}</template>
            <template v-slot:item.actions="{item}">
              <v-tooltip bottom v-if="isEnrolled(item)">
                <template v-slot:activator="{ on }">
                  <v-icon
                    large
                    v-on="on"
                    class="mr-2"
                    @click="unenroll(item)"
                    color="red"
                  >mdi-account-minus</v-icon>
                </template>
                <span>Unenroll</span>
              </v-tooltip>
              <v-tooltip bottom v-else>
                <template v-slot:activator="{ on }">
                  <v-icon
                    large
                    v-on="on"
                    class="mr-2"
                    @click="enroll(item)"
                    color="blue"
                  >mdi-account-plus</v-icon>
                </template>
                <span>Enroll</span>
              </v-tooltip>
            </template>
          </v-data-table>
        </v-container>
      </v-tab-item>
    </v-tabs>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import User from '@/models/user/User';
import Store from '@/store';
import Tournament from '../../../models/management/Tournament';

@Component
export default class OpenTournamentsView extends Vue {
  tournaments: Tournament[] = [];
  myTournaments: Tournament[] = [];
  username: String = this.$store.getters.getUser.username;
  headers: object = [
    {
      text: 'Tournament',
      value: 'name',
      align: 'left'
    },
    {
      text: 'Creation Date',
      value: 'creationDate',
      align: 'left'
    },
    {
      text: 'Begin Date',
      value: 'beginDate',
      align: 'left'
    },
    {
      text: 'Enrolled',
      value: 'nEnrolled',
      align: 'left'
    },
    {
      text: 'Actions',
      value: 'actions',
      align: 'center',
      sortable: false,
      width: 150
    }
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      let tours = (await RemoteServices.getOpenTournaments()).reverse();
      tours.forEach(t => {
        if (t.createdBy.username == this.username) {
          this.myTournaments.push(t);
        } else {
          this.tournaments.push(t);
        }
      });
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  isEnrolled(tournament: Tournament): boolean {
    return this.tournaments[this.tournaments.indexOf(tournament)].enrolled.some(
      u => u.username === this.username
    );
  }

  isEnrolledMyTournaments(tournament: Tournament): boolean {
    return this.myTournaments[
      this.myTournaments.indexOf(tournament)
    ].enrolled.some(u => u.username === this.username);
  }

  async enroll(tournament: Tournament) {
    this.tournaments = [];
    this.myTournaments = [];
    try {
      await RemoteServices.enrollInTournament(tournament);
      let tours = (await RemoteServices.getOpenTournaments()).reverse();
      tours.forEach(t => {
        if (t.createdBy.username == this.username) {
          this.myTournaments.push(t);
        } else {
          this.tournaments.push(t);
        }
      });
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

  async unenroll(tournament: Tournament) {
    this.tournaments = [];
    this.myTournaments = [];
    try {
      await RemoteServices.unenrollFromTournament(tournament);
      let tours = (await RemoteServices.getOpenTournaments()).reverse();
      tours.forEach(t => {
        if (t.createdBy.username == this.username) {
          this.myTournaments.push(t);
        } else {
          this.tournaments.push(t);
        }
      });
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

  async cancel(tournament: Tournament) {
    this.tournaments = [];
    this.myTournaments = [];
    try {
      await RemoteServices.cancelTournament(tournament);
      let tours = (await RemoteServices.getOpenTournaments()).reverse();
      tours.forEach(t => {
        if (t.createdBy.username == this.username) {
          this.myTournaments.push(t);
        } else {
          this.tournaments.push(t);
        }
      });
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
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
      margin-top: 10px;
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
