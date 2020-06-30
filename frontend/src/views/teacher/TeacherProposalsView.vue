<template v-if="proposals">
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :custom-filter="customFilter"
      :items="proposals"
      :search="search"
      :mobile-breakpoint="0"
      :items-per-page="30"
      :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
    >
      <template v-slot:top>
        <v-card-title>
          <v-text-field
            v-model="search"
            append-icon="search"
            label="Search"
            single-line
            hide-details
            data-cy="searchProposal"
          />
        </v-card-title>
      </template>
      <template v-slot:item.action="{ item }">
        <v-tooltip v-if="!isAccepted(item)" bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              small
              class="mr-2"
              v-on="on"
              @click="reviewProposal(item)"
              data-cy="reviewProposalButton"
              >edit</v-icon
            >
          </template>
          <span>Review Proposal</span>
        </v-tooltip>
        <v-icon v-if="isAccepted(item)" data-cy="openQstFromPropDialogBtn"
          @click='openCreateQuestionDialog(item)'
        >
          mdi-autorenew
        </v-icon>
      </template>

    </v-data-table>

    <v-dialog v-model="proposalDialog" max-width="75%">
      <v-card>
        <v-card-title>
          <span class="headline">Review Question Proposal</span>
        </v-card-title>

        <v-card-text v-if="editedReview">
          <v-container grid-list-md fluid>
            <v-layout column wrap>
              <v-flex xs24 sm12 md8>
                <v-text-field
                  v-model="editedReview.reason"
                  label="Reason"
                  data-cy="reasonBar"
                />
              </v-flex>
            </v-layout>
          </v-container>
        </v-card-text>

        <v-card-actions>
          <v-spacer />
          <v-btn color="blue darken-1" @click="closeDialogue">Cancel</v-btn>
          <v-btn color="green darken-1" @click="acceptProposal">Accept</v-btn>
          <v-btn color="red darken-1" @click="rejectProposal">Reject</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog v-model="questionDialog" max-width="75%">
      <v-card>
        <v-card-title>
          <span class="headline">Edit and Save Question</span>
        </v-card-title>

        <v-card-text>
          <v-container grid-list-md fluid>
            <v-layout column wrap>
              <v-flex xs24 sm12 md8>
                <v-text-field
                  v-model="proposalToQuestion.title"
                  label="Title"
                  data-cy="qstFromPropTitleBar"
                />
              </v-flex>
              <v-flex xs24 sm12 md12>
              <v-textarea
                outline
                outlined
                rows="5"
                v-model="proposalToQuestion.content"
                label="Content"
              ></v-textarea>
            </v-flex>

            <!-- IMAGE -->
            <v-card-title>
              <span class="">
                Image
              </span>
            </v-card-title>
            <v-flex xs24 sm12 md8>
              <v-text-field
                v-model="proposalToQuestion.image.url"
                label="Url"
              />
            </v-flex>
            <v-slider
              v-model="proposalToQuestion.image.width"
              min="0"
              max="500"
              label="Image Width"
              ticks
              thumb-label="always"
            ></v-slider>

            <v-card-title >
              <span class="">
                Options
              </span>
            </v-card-title>
            <v-layout>
              <v-flex
                xs24
                sm12
                md12
                v-for="index in proposalToQuestion.options.length"
                :key="index"
              >
                <v-switch
                  v-model="proposalToQuestion.options[index - 1].correct"
                  class="ma-4"
                  label="Is the Correct Option"
                  data-cy="optionSwitch"
                />
                <v-textarea
                  outline
                  auto-grow
                  outlined
                  rows="2"
                  v-model="proposalToQuestion.options[index - 1].content"
                  :optionContentId="index - 1"
                  label="Content"
                  data-cy="proposalToQuestion[index-1]"
                ></v-textarea>
              </v-flex>
            </v-layout>

            </v-layout>
          </v-container>
        </v-card-text>

        <v-card-actions>
          <v-spacer />
          <v-btn color="blue darken-1" @click="closeCreateQuestionDialog">Cancel</v-btn>
          <v-btn color="green darken-1" data-cy="qstFromPropSaveBtn" @click="saveQuestion">Save Question</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import QuestionProposal from '@/models/management/QuestionProposal';
import ProposalReview from '@/models/management/ProposalReview';
import Question from '@/models/management/Question';

@Component
export default class ProposalsView extends Vue {
  proposals: QuestionProposal[] = [];

  editedReview: ProposalReview | null = null;
  proposalUnderReview: QuestionProposal | null = null;
  proposalDialog: boolean = false;
  questionDialog: boolean = false;
  editQuestion!: Question | null;
  proposalToQuestion: QuestionProposal  = new QuestionProposal();

  search: string = '';
  headers: object = [
    {
      text: 'Title',
      value: 'title',
      align: 'left'
    },
    {
      text: 'Question',
      value: 'content',
      align: 'left'
    },
    {
      text: 'Topics',
      value: 'topics',
      align: 'center',
      sortable: false
    },
    {
      text: 'Status',
      value: 'status',
      align: 'center',
      width: '7%'
    },
    {
      text: 'Actions',
      value: 'action',
      align: 'center',
      width: '7%',
      sortable: false
    }
  ];

  closeCreateQuestionDialog() {
    this.questionDialog = false;
    this.proposalToQuestion = new QuestionProposal();
  }

  openCreateQuestionDialog(prop: QuestionProposal){
    this.proposalToQuestion = prop;
    this.questionDialog = true;
  }

  async saveQuestion(){
    if (
      this.proposalToQuestion &&
      (!this.proposalToQuestion.title || !this.proposalToQuestion.content)
    ) {
      await this.$store.dispatch(
        'error',
        'Question must have title and content'
      );
      return;
    }

    try {
      var result = await RemoteServices.editQuestionProposal(
        this.proposalToQuestion.id,
        this.proposalToQuestion.courseId,
        this.proposalToQuestion
      );

      result = await RemoteServices.convertQuestionProposal(
        result.id,
        result.courseId,
        result
      );
      this.$emit('submit-proposal', result);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }

    this.closeCreateQuestionDialog();
  }

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.proposals = await RemoteServices.getQuestionProposals();

    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  customFilter(value: string, search: string) {
    return (
      search != null &&
      typeof value === 'string' &&
      value.toLocaleLowerCase().indexOf(search.toLocaleLowerCase()) !== -1
    );
  }

  isAccepted(prop:QuestionProposal) {
    return prop.status == 'ACCEPTED';
  }

  closeDialogue() {
    this.proposalDialog = false;
    this.editedReview = null;
    this.proposalUnderReview = null;
  }

  reviewProposal(proposal: QuestionProposal) {
    this.editedReview = new ProposalReview();
    this.proposalUnderReview = proposal;
    this.proposalDialog = true;
  }

  async acceptProposal() {
    if (this.proposalUnderReview && this.editedReview) {
      this.editedReview.approve = true;
      try {
        await RemoteServices.reviewQuestionProposal(
          this.proposalUnderReview.id,
          this.proposalUnderReview.courseId,
          this.editedReview
        );
        this.proposals = await RemoteServices.getQuestionProposals();
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
    this.closeDialogue();
  }

  async rejectProposal() {
    if (this.proposalUnderReview && this.editedReview) {
      this.editedReview.approve = false;
      try {
        await RemoteServices.reviewQuestionProposal(
          this.proposalUnderReview.id,
          this.proposalUnderReview.courseId,
          this.editedReview
        );
        this.proposals = await RemoteServices.getQuestionProposals();
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
    this.closeDialogue();
  }
}
</script>

<style lang="scss" scoped />
