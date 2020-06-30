<template>
  <div class="container">
    <h2>Dashboard</h2>
    <feature-stats
      :fstVal="solvedTour"
      :fstStr="solvedTourStr"
      :sndVal="avgTour"
      :sndStr="avgTourStr"
      :pref="tournamentPref"
    />
    <feature-stats
      :fstVal="discussions"
      :fstStr="strDiscussions"
      :sndVal="publicDiscussions"
      :sndStr="strPublicDiscussions"
      :pref="discussionPref"
    />
    <feature-stats
      :fstVal="subProp"
      :fstStr="subStr"
      :sndVal="appProp"
      :sndStr="appStr"
      :pref="propPref"
    />
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import AnimatedNumber from '@/components/AnimatedNumber.vue';
import FeatureStatsComponent from '@/components/FeatureStatsComponent.vue';
import FeatureStats from '@/models/statement/FeatureStats';

@Component({
  components: {
    AnimatedNumber,
    'feature-stats': FeatureStatsComponent
  }
})
export default class StatsView extends Vue {
  stats: FeatureStats | null = null;
  pref: Number = 0;

  solvedTour: Number = 0;
  solvedTourStr: String = 'Number of Solved Tournaments';
  avgTour: Number = 0.0;
  avgTourStr: String = 'Average Tournament Score';
  tournamentPref: Number = 3;

  discussions: Number = 0;
  publicDiscussions: Number = 0;
  strDiscussions: String = 'Discussions';
  strPublicDiscussions: String = 'Public Discussions';
  discussionPref: Number = 2;

  subProp: number = 0;
  subStr: String = 'Number of Submitted Proposals'
  appProp: number = 0;
  appStr: String = 'Number of Accepted Proposals'
  propPref: Number = 1;  
    
  async created() {
    await this.$store.dispatch('loading');
    try {
      this.stats = await RemoteServices.getStudentStats();

      this.solvedTour = this.stats.numberOfSolvedTournaments;
      this.avgTour = this.stats.averageTournamentScore * 100;

      this.discussions = this.stats.numberOfDiscussions;
      this.publicDiscussions = this.stats.numberOfPublicDiscussions;

      this.appProp = this.stats.numberOfAcceptedProposals;
      this.subProp = this.stats.numberOfSubmitedProposals;

    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }
}
</script>
<style lang="scss" scoped></style>
