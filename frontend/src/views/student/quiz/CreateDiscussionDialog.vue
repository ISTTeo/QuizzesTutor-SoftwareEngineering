<template>
  <v-dialog
    :value="dialog"
    @input="$emit('close-dialog')"
    @keydown.esc="$emit('close-dialog')"
    max-width="75%"
    max-height="80%"
  >
    <v-card>
      <v-card-title>
        <span class="headline">
          New Discussion
        </span>
      </v-card-title>

      <v-card-text class="text-left" v-if="createDiscussion">
        <v-container grid-list-md fluid>
          <v-layout column wrap>
            <v-flex xs24 sm12 md8>
              <p v-if="isCreateDiscussion">
                <b>Title:</b> {{ createDiscussion.title }}
              </p>
              <v-text-field
                v-if="!isCreateDiscussion"
                v-model="createDiscussion.title"
                label="Title"
                data-cy="Title"
              />
            </v-flex>
            <v-flex xs24 sm12 md8>
              <v-text-field
                v-model="createDiscussion.content"
                label="Content"
                data-cy="Content"
              />
            </v-flex>
          </v-layout>
        </v-container>
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn
          color="blue darken-1"
          @click="$emit('close-dialog')"
          data-cy="cancelButton"
          >Cancel</v-btn
        >
        <v-btn
          color="blue darken-1"
          @click="saveDiscussion"
          data-cy="saveButton"
          >Save</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Discussion from '@/models/management/Discussion';

@Component
export default class CreateDiscussionDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Discussion, required: true }) readonly discussion!: Discussion;

  createDiscussion!: Discussion;
  isCreateDiscussion: boolean = false;

  created() {
    this.createDiscussion = new Discussion(this.discussion);
    this.isCreateDiscussion = !!this.createDiscussion.userId;
  }

  async saveDiscussion() {
    if (
      this.createDiscussion &&
      (!this.createDiscussion.title || !this.createDiscussion.content)
    ) {
      await this.$store.dispatch(
        'error',
        'Course must have name, acronym and academicTerm'
      );
      return;
    }
    if (this.createDiscussion) {
      try {
        const result = await RemoteServices.createDiscussion(
          this.createDiscussion.questionId,
          this.createDiscussion
        );
        this.$emit('new-discussion', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>
