<template>
  <div class="container">
    <h2>Discussions</h2>
    <ul>
      <li class="list-header">
        <div class="col">Title</div>
        <div class="col">Content</div>
        <div class="col">Number Of Replies</div>
        <div class="col last-col">Reply</div>
      </li>
      <li
        class="list-row"
        v-for="discussion in discussions"
        :key="discussion.id"
      >
        <div class="col">
          {{ discussion.title }}
        </div>
        <div class="col">
          {{ discussion.content }}
        </div>
        <div class="col">
          {{ discussion.numberOfReplies }}
        </div>
        <div class="col last-col">
          <v-icon @click="openDialog(discussion)" data-cy="createReplyButton"
            >fas fa-edit</v-icon
          >
        </div>
      </li>
    </ul>

    <v-card>
      <show-discussion-dialog
        v-if="showDiscussionDialog"
        v-model="showDiscussionDialog"
        :discussion="currentDiscussion"
        :question="currentQuestion"
        :reply="currentReply"
        :replies="replies"
        v-on:close-dialog="onCloseDialog"
      />
    </v-card>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Topic from '@/models/management/Topic';
import Discussion from '@/models/management/Discussion';
import Reply from '@/models/management/Reply';
import Question from '@/models/management/Question';
import ShowDiscussionDialog from '@/views/teacher/ShowDiscussionDialog.vue';

@Component({
  components: {
    'show-discussion-dialog': ShowDiscussionDialog
  }
})
export default class TeacherDiscussionsView extends Vue {
  topics: Topic[] = [];
  questions: Question[] = [];
  questionIds: number[] = [];
  currentQuestion: Question | null = null;
  currentDiscussion: Discussion | null = null;
  currentReply: Reply | null = null;
  discussions: Discussion[] = [];
  replies: Reply[] = [];
  showDiscussionDialog: boolean = false;
  headers: object = [
    { text: 'Topic', value: 'name', align: 'left' },
    {
      text: 'Questions',
      value: 'numberOfQuestions',
      align: 'center',
      width: '115px'
    },
    {
      text: 'Actions',
      value: 'action',
      align: 'center',
      width: '7%',
      sortable: false
    }
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      [this.topics, this.questions] = await Promise.all([
        RemoteServices.getTopics(),
        RemoteServices.getQuestions()
      ]);
      for (const question of this.questions) {
        if (question.id != null) {
          this.questionIds.push(question.id);
        }
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    try {
      this.discussions = (
        await RemoteServices.getQuestionsDiscussions(this.questionIds)
      ).reverse();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async loadDiscussions() {
    try {
      this.discussions = (
        await RemoteServices.getQuestionsDiscussions(this.questionIds)
      ).reverse();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

  async openDialog(discussion: Discussion) {
    this.currentDiscussion = new Discussion(discussion);
    this.currentReply = new Reply();
    try {
      this.currentQuestion = await RemoteServices.getDiscussionQuestion(
        this.currentDiscussion.id
      );
      this.replies = await RemoteServices.getDiscussionReply(
        this.currentDiscussion.id
      );
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    this.showDiscussionDialog = true;
  }

  onCloseDialog() {
    this.loadDiscussions();
    this.currentDiscussion = null;
    this.currentQuestion = null;
    this.currentReply = null;
    this.showDiscussionDialog = false;
  }
}
</script>

<style lang="scss" scoped>
.container {
  max-width: 1000px;
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
  }
}
</style>
