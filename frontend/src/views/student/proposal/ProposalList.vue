<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="proposals"
      :search="search"
      :sort-by="['title']"
      :mobile-breakpoint="0"
      multi-sort
      :items-per-page="15"
      :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
    >
      <template v-slot:top>
        <v-card-title>
          <v-text-field
            v-model="search"
            append-icon="search"
            label="Search"
            class="mx-4"
            data-cy="searchProposal"
          />
        </v-card-title>
      </template>
      <template v-slot:item.action="{ item }">
        <v-icon v-if="isRejectedOrPending(item)"
          data-cy="resubmitDialogBtn"
          @click='resubmitProposal(item)'
        >
          mdi-pencil
        </v-icon>
        

      </template>
    </v-data-table>
    <v-dialog v-model="editProposal" max-width="75%">
      <v-card>
        <v-card-title>
          <span class="headline">Edit and Resubmit Question Proposal</span>
        </v-card-title>

        <v-card-text v-model="editProposal">
          <v-container grid-list-md fluid>
            <v-layout column wrap>
              <v-flex xs24 sm12 md8>
                <v-text-field
                  v-model="proposal.title"
                  label="Title"
                  data-cy="titleBar"
                />
              </v-flex>
              <v-flex xs24 sm12 md12>
              <v-textarea
                outline
                outlined
                rows="5"
                v-model="proposal.content"
                label="Content"
                data-cy="submitProposalContentArea"
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
                v-model="proposal.image.url"
                data-cy="submitProposalUrlArea"
                label="Url"
              />
            </v-flex>
            <v-slider
              v-model="proposal.image.width"
              min="0"
              max="500"
              label="Image Width"
              data-cy="submitProposalImageArea"
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
                v-for="index in proposal.options.length"
                :key="index"
              >
                <v-switch
                  v-model="proposal.options[index - 1].correct"
                  class="ma-4"
                  label="Is the Correct Option"
                  data-cy="optionSwitch"
                />
                <v-textarea
                  outline
                  auto-grow
                  outlined
                  rows="2"
                  v-model="proposal.options[index - 1].content"
                  :optionContentId="index - 1"
                  label="Content"
                  data-cy="optionContent[index-1]"
                ></v-textarea>
              </v-flex>
            </v-layout>

            </v-layout>
          </v-container>
        </v-card-text>

        <v-card-actions>
          <v-spacer />
          <v-btn color="blue darken-1" @click="closeDialog">Cancel</v-btn>
          <v-btn color="green darken-1" data-cy="resubmitBtn" @click="resendProposal">Resubmit</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import QuestionProposal from '@/models/management/QuestionProposal';

@Component
export default class ProposalList extends Vue {
  @Prop({ type: Array, required: true })
  readonly proposals!: QuestionProposal[];
  proposal: QuestionProposal  = new QuestionProposal();
  proposalUnderEdit: QuestionProposal  = new QuestionProposal();
  editProposal: boolean = false;

  search: string = '';
  headers: object = [
    { text: 'Title', value: 'title', align: 'left' },
    { text: 'Question', value: 'content', align: 'left' },
    {
      text: 'Topics',
      value: 'topics',
      align: 'center',
      sortable: false
    },
    { text: 'Status', value: 'status', align: 'center', width: '7%' },
    {
      text: 'Actions',
      value: 'action',
      align: 'center',
      width: '7%',
      sortable: false
    }
  ];

  resubmitProposal(prop: QuestionProposal) {
    this.proposal = new QuestionProposal(prop)
    this.editProposal = true;
    
  }

  async resendProposal() {
    this.$emit('updateList');
    if (this.editProposal) {
      if(this.proposal.status == 'REJECTED') {
        try {
          const result = await RemoteServices.reSubmitQuestionProposal(
            this.proposal.id,
            this.proposal.courseId,
            this.proposal
          );
          this.$emit('submit-proposal', result);
        
        } catch (error) {
          await this.$store.dispatch('error', error);
        }
      } else {
        try {
          const result = await RemoteServices.editQuestionProposal(
            this.proposal.id,
            this.proposal.courseId,
            this.proposal
          );
          this.$emit('submit-proposal', result);
      
        } catch (error) {
          await this.$store.dispatch('error', error);
        }
      }
    }
    this.closeDialog();
    
  }

  isRejectedOrPending(prop:QuestionProposal) {
    return prop.status == 'REJECTED'  || prop.status == 'PENDING' ;
  }
  closeDialog() {
    this.proposal = new QuestionProposal();
    this.editProposal = false;
  }
}
</script>

<style lang="scss" scoped />
