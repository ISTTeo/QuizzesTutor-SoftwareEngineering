<template>
  <div>
    <h1>Question Proposals</h1>
    <v-btn @click="newProposal" data-cy="createSubmitProposalBtn"
      >New Proposal</v-btn
    >
    <submit-proposal-dialog
      v-if="currentProposal"
      v-model="dialog"
      :question="currentProposal"
      v-on:submit-proposal="submitProposal"
    />

    <proposal-list :proposals="proposals" @submit-proposal="submitProposal"
 />
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import QuestionProposal from '@/models/management/QuestionProposal';
import SubmitQuestionProposalDialog from '@/views/student/proposal/SubmitQuestionProposalDialog.vue';
import router from '@/router';
import ProposalList from '@/views/student/proposal/ProposalList.vue';
import RemoteServices from '@/services/RemoteServices';

@Component({
  components: {
    'submit-proposal-dialog': SubmitQuestionProposalDialog,
    'proposal-list': ProposalList
  }
})
export default class SubmitQuestionProposalsView extends Vue {
  currentProposal: QuestionProposal | null = null;
  dialog: boolean = false;
  proposals: QuestionProposal[] = [];

  created() {
    if (this.$attrs.newProp) {
      this.newProposal();
    }
    this.fetchProposals();
  }

  async fetchProposals() {
    await this.$store.dispatch('loading');
    try {
      this.proposals = await RemoteServices.getQuestionProposals();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');

  }

  newProposal() {
    this.currentProposal = new QuestionProposal();
    this.dialog = true;
  }

  closeProposalDialog() {
    this.currentProposal = null;
    this.dialog = false;
  }

  submitProposal() {    
    this.fetchProposals(); // Reload list
    this.closeProposalDialog();
  }
}
</script>

<style lang="scss" scoped></style>
