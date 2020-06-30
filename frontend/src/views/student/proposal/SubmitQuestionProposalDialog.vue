<template>
  <v-dialog
    :value="dialog"
    @input="$emit('dialog', false)"
    @keydown.esc="$emit('dialog', false)"
    max-width="75%"
    max-height="80%"
  >
    <v-card>
      <v-card-title>
        <span class="headline">
          Submit new proposal
        </span>
      </v-card-title>

      <v-card-text class="text-left" v-if="editProposal">
        <v-container grid-list-md fluid>
          <v-layout column wrap>
            <!-- TITLE and CONTENT -->
            <v-flex xs24 sm12 md8>
              <v-text-field
                v-model="editProposal.title"
                data-cy="submitProposalTitleArea"
                label="Title"
              />
            </v-flex>
            <v-flex xs24 sm12 md12>
              <v-textarea
                outline
                outlined
                rows="5"
                v-model="editProposal.content"
                label="Content"
                data-cy="submitProposalContentArea"
              ></v-textarea>
            </v-flex>

            <!-- IMAGE -->
            <v-btn
              @click="imageButton"
              :color="imageButtonColor"
              data-cy="addImageBtn"
            >
              {{ imageButtonText }}</v-btn
            >
            <v-card-title v-if="imageInProposal">
              <span class="">
                Image (Optional Field)
              </span>
            </v-card-title>
            <v-flex xs24 sm12 md8>
              <v-text-field
                v-if="imageInProposal"
                v-model="editProposal.image.url"
                data-cy="submitProposalUrlArea"
                label="Url"
              />
            </v-flex>
            <v-slider
              v-if="imageInProposal"
              v-model="editProposal.image.width"
              min="0"
              max="500"
              label="Image Width"
              data-cy="submitProposalImageArea"
              ticks
              thumb-label="always"
            ></v-slider>
            <!-- OPTIONS -->
            <v-btn
              @click="optionsButton"
              :color="optionsButtonColor"
              data-cy="addOptionsBtn"
            >
              {{ optionsButtonText }}</v-btn
            >
            <v-card-title v-if="optionsInProposal">
              <span class="">
                Options (Optional Field)
              </span>
            </v-card-title>
            <v-layout v-if="optionsInProposal">
              <v-flex
                xs24
                sm12
                md12
                v-for="index in editProposal.options.length"
                :key="index"
              >
                <v-switch
                  v-model="editProposal.options[index - 1].correct"
                  class="ma-4"
                  label="Is the Correct Option"
                  data-cy="optionSwitch"
                />
                <v-textarea
                  outline
                  auto-grow
                  outlined
                  rows="2"
                  v-model="editProposal.options[index - 1].content"
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
        <v-btn color="blue darken-1" @click="$emit('dialog', false)"
          >Cancel</v-btn
        >
        <v-btn
          color="blue darken-1"
          @click="submitProposal"
          data-cy="submitProposalBtn"
          >Submit</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import Question from '@/models/management/Question';
import RemoteServices from '@/services/RemoteServices';
import QuestionProposal from '@/models/management/QuestionProposal';

@Component
export default class SubmitQuestionProposalDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  editProposal!: QuestionProposal;
  optionsInProposal: boolean = false;
  optionsButtonText: string = 'Add Options to Proposal - Optional';
  optionsButtonColor: string = 'green';
  imageInProposal: boolean = false;
  imageButtonText: string = 'Add Image to Proposal - Optional';
  imageButtonColor: string = 'green';
  
  created() {
    this.editProposal = new QuestionProposal();
  }

  async submitProposal() {
    if (
      this.editProposal &&
      (!this.editProposal.title || !this.editProposal.content)
    ) {
      await this.$store.dispatch(
        'error',
        'Question must have title and content'
      );
      return;
    }
    
    for(var i=0; i < this.editProposal.options.length ; i++) {
      this.editProposal.options[i].sequence = i;
    }


    if (this.editProposal) {

      for(var i = 0;i < this.editProposal.options.length; i++ ) {
        this.editProposal.options[i].sequence = i;
      }


      try {
        const result = await RemoteServices.submitQuestionProposal(
          this.editProposal
        );
        this.$emit('submit-proposal', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  optionsButton() {
    this.optionsInProposal = !this.optionsInProposal;
    if (this.optionsInProposal) {
      this.optionsButtonColor = 'red';
      // eslint-disable-next-line
      this.optionsButtonText = 'Don\'t Add Options to Proposal';
    } else {
      this.optionsButtonColor = 'green';
      this.optionsButtonText = 'Add Options to Proposal - Optional';
    }
  }

  imageButton() {
    this.imageInProposal = !this.imageInProposal;
    if (this.imageInProposal) {
      this.imageButtonColor = 'red';
      // eslint-disable-next-line
      this.imageButtonText = 'Don\'t Add Image to Proposal';
    } else {
      this.imageButtonColor = 'green';
      this.imageButtonText = 'Add Image to Proposal - Optional';
    }
  }
}
</script>
