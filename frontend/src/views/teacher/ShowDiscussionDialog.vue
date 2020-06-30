<template>
  <v-dialog
    id="dialog"
    :value="dialog"
    @input="$emit('dialog', false)"
    @keydown.esc="$emit('dialog', false)"
    max-width="75%"
  >
    <v-card v-if="discussion">
      <v-card-title></v-card-title>
      <v-card-title></v-card-title>
      <v-card-subtitle>
        <show-question :question="question" />
      </v-card-subtitle>
      <v-card-title>{{ discussion.title }}</v-card-title>
      <v-card-text>{{ discussion.content }}</v-card-text>
      <li class="nobull" v-for="r in replies" :key="r.id">
        <div v-if="r.userDto.role == 'TEACHER'">
          <span class="font-weight-medium blue--text text--darken-1">
            {{ r.userDto.name }}:
          </span>
          <span> {{ r.replyContent }} </span>
        </div>
        <div v-else>
          <span class="font-weight-medium orange--text text--darken-3">
            {{ r.userDto.name }}:
          </span>
          <span> {{ r.replyContent }} </span>
        </div>
      </li>

      <v-card-text class="text-left" v-if="createReply">
        <v-container grid-list-md fluid>
          <v-layout column wrap>
            <v-flex xs24 sm12 md8>
              <p v-if="isCreateReply">
                <b>Reply:</b> {{ createReply.replyContent }}
              </p>
              <v-text-field
                v-if="!isCreateReply"
                v-model="createReply.replyContent"
                label="Reply"
                data-cy="Reply"
              />
            </v-flex>
          </v-layout>
        </v-container>
      </v-card-text>

      <v-switch
        v-model="discussion.isPublic"
        class="ma-4"
        label="Make public"
        data-cy="MakePublicToggle"
      />

      <v-card-actions>
        <v-spacer />
        <v-btn dark color="blue darken-1" @click="$emit('close-dialog')"
          >close</v-btn
        >
        <v-btn color="blue darken-1" @click="saveReply" data-cy="saveButton"
          >Save</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Vue, Prop, Model } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Discussion from '@/models/management/Discussion';
import Question from '@/models/management/Question';
import Reply from '@/models/management/Reply';
import ShowQuestion from '@/views/teacher/questions/ShowQuestion.vue';

@Component({
  components: {
    'show-question': ShowQuestion
  }
})
export default class ShowDiscussionDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Discussion, required: true }) readonly discussion!: Discussion;
  @Prop({ type: Question, required: true }) readonly question!: Question;
  @Prop({ type: Reply, required: true }) readonly reply!: Reply;
  @Prop({ type: Array, required: true }) readonly replies!: Reply[];

  createReply!: Reply;
  isCreateReply: boolean = false;

  created() {
    this.createReply = new Reply(this.reply);
    this.createReply.discussionId = this.discussion.id;
  }

  async saveReply() {
    if (this.createReply && !this.createReply.replyContent) {
      await this.$store.dispatch('error', 'Reply must have content');
      return;
    }
    if (this.createReply) {
      try {
        const result1 = await RemoteServices.createReply(
          this.discussion.id,
          this.createReply
        );
        const result2 = await RemoteServices.changeDiscussionPublicStatus(
          this.discussion.id,
          this.discussion.isPublic
        );
        this.$emit('close-dialog');
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.v-dialog:not(.v-dialog--fullscreen) {
  max-height: 75%;
}
.v-card {
  padding-left: 0px;
  text-align: left;
  small {
    font-size: 0.5em;
  }
}
.nobull {
  list-style-type: none;
  padding-left: 35px;
}
</style>
