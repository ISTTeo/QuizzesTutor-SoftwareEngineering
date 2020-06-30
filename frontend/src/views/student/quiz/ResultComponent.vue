<template>
  <div
    v-if="question"
    v-bind:class="[
      'question-container',
      answer.optionId === null ? 'unanswered' : '',
      answer.optionId !== null &&
      correctAnswer.correctOptionId === answer.optionId
        ? 'correct-question'
        : 'incorrect-question'
    ]"
  >
    <div class="question">
      <span
        @click="decreaseOrder"
        @mouseover="hover = true"
        @mouseleave="hover = false"
        class="square"
      >
        <i v-if="hover && questionOrder !== 0" class="fas fa-chevron-left" />
        <span v-else>{{ questionOrder + 1 }}</span>
      </span>
      <div
        class="question-content"
        v-html="convertMarkDown(question.content, question.image)"
      ></div>
      <div
        data-cy="createDiscussionButton"
        @click="newDiscussion"
        class="square"
      >
        <i class="fas fa-question" />
      </div>
      <div @click="increaseOrder" class="square">
        <i
          v-if="questionOrder !== questionNumber - 1"
          class="fas fa-chevron-right"
        />
      </div>
    </div>
    <ul class="option-list">
      <li
        v-for="(n, index) in question.options.length"
        :key="index"
        v-bind:class="[
          answer.optionId === question.options[index].optionId ? 'wrong' : '',
          correctAnswer.correctOptionId === question.options[index].optionId
            ? 'correct'
            : '',
          'option'
        ]"
      >
        <i
          v-if="
            correctAnswer.correctOptionId === question.options[index].optionId
          "
          class="fas fa-check option-letter"
        />
        <i
          v-else-if="answer.optionId === question.options[index].optionId"
          class="fas fa-times option-letter"
        />
        <span v-else class="option-letter">{{ optionLetters[index] }}</span>
        <span
          class="option-content"
          v-html="convertMarkDown(question.options[index].content)"
        />
      </li>
    </ul>
    <div class="table">
      <h2>Public Discussions</h2>
      <div>
        <div v-if="!discussions.length">
          <v-card-title>
            No public discussions associated with this question.
          </v-card-title>
        </div>
        <div v-else>
          <ul>
            <li class="list-header">
              <div class="col">Title</div>
              <div class="col">Content</div>
              <div class="col">Number of Replies</div>
              <div class="col last-col"></div>
            </li>
            <li
              class="list-row"
              v-for="discussion in discussions"
              :key="discussion.id"
            >
              <div class="col">{{ discussion.title }}</div>
              <div class="col">{{ discussion.content }}</div>
              <div class="col">{{ discussion.numberOfReplies }}</div>
              <div class="col last-col" data-cy="showReplyButton">
                <div
                  v-if="discussion.numberOfReplies"
                  @click="openReplyDialog(discussion)"
                >
                  <i class="fas fa-eye"></i>
                </div>
                <div v-else>
                  <i class="fas fa-eye-slash"></i>
                </div>
              </div>
            </li>
          </ul>
        </div>
      </div>
    </div>

    <v-card>
      <create-discussion-dialog
        v-if="currentDiscussion"
        v-model="createDiscussionDialog"
        :discussion="currentDiscussion"
        v-on:new-discussion="onCreateDiscussion"
        v-on:close-dialog="onCloseDialog"
      />
    </v-card>
    <v-card>
      <show-reply-dialog
        v-if="showReplyDialog"
        v-model="showReplyDialog"
        :discussion="currentDiscussion"
        :reply="currentReply"
        :replies="currentReplies"
        v-on:close-dialog="onCloseReplyDialog"
      />
    </v-card>
  </div>
</template>

<script lang="ts">
import {
  Component,
  Vue,
  Prop,
  Model,
  Emit,
  Watch
} from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import StatementQuestion from '@/models/statement/StatementQuestion';
import StatementAnswer from '@/models/statement/StatementAnswer';
import StatementCorrectAnswer from '@/models/statement/StatementCorrectAnswer';
import Image from '@/models/management/Image';
import RemoteServices from '@/services/RemoteServices';
import Discussion from '@/models/management/Discussion';
import Question from '@/models/management/Question';
import CreateDiscussionDialog from '@/views/student/quiz/CreateDiscussionDialog.vue';
import ShowReplyDialog from '@/views/student/ShowReplyDialog.vue';
import Reply from '@/models/management/Reply';

@Component({
  components: {
    'create-discussion-dialog': CreateDiscussionDialog,
    'show-reply-dialog': ShowReplyDialog
  }
})
export default class ResultComponent extends Vue {
  @Model('questionOrder', Number) questionOrder: number | undefined;
  @Prop(StatementQuestion) readonly question!: StatementQuestion;
  @Prop(StatementCorrectAnswer) readonly correctAnswer!: StatementCorrectAnswer;
  @Prop(StatementAnswer) readonly answer!: StatementAnswer;
  @Prop() readonly questionNumber!: number;
  hover: boolean = false;
  optionLetters: string[] = ['A', 'B', 'C', 'D'];
  currentDiscussion: Discussion | null = null;
  createDiscussionDialog: boolean = false;
  discussions: Discussion[] = [];
  currentReply: Reply | null = null;
  currentReplies: Reply[] = [];
  showReplyDialog: boolean = false;

  @Emit()
  increaseOrder() {
    return 1;
  }

  @Emit()
  decreaseOrder() {
    return 1;
  }

  newDiscussion() {
    this.currentDiscussion = new Discussion();
    this.currentDiscussion.questionId = this.question.questionId;
    this.currentDiscussion.questionAnswerId = this.question.questionAnswerId;
    this.currentDiscussion.numberOfReplies = 0;
    this.currentDiscussion.isPublic = false;
    this.createDiscussionDialog = true;
  }

  async created() {
    this.changeQuestion();
  }

  @Watch('question', { deep: true })
  async changeQuestion() {
    this.loadDiscussions();
  }

  async loadDiscussions() {
    await this.$store.dispatch('loading');
    try {
      this.discussions = await RemoteServices.getQuestionPublicDiscussions(
        this.question.questionId
      );
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }

  async onCreateDiscussion(discussion: Discussion) {
    this.createDiscussionDialog = false;
    this.currentDiscussion = null;
  }

  onCloseDialog() {
    this.createDiscussionDialog = false;
    this.currentDiscussion = null;
  }

  async openReplyDialog(discussion: Discussion) {
    this.currentDiscussion = new Discussion(discussion);
    try {
      this.currentReplies = await RemoteServices.getDiscussionReply(
        this.currentDiscussion.id
      );
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    this.currentReply = new Reply(this.currentReplies[0]);
    this.showReplyDialog = true;
  }

  onCloseReplyDialog() {
    this.loadDiscussions();
    this.currentDiscussion = null;
    this.currentReply = null;
    this.showReplyDialog = false;
  }
}
</script>

<style lang="scss" scoped>
.table {
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
.unanswered {
  .question {
    background-color: #761515 !important;
    color: #fff !important;
  }
  .correct {
    .option-content {
      background-color: #333333;
      color: rgb(255, 255, 255) !important;
    }

    .option-letter {
      background-color: #333333 !important;
      color: rgb(255, 255, 255) !important;
    }
  }
}

.correct-question {
  .question .question-content {
    background-color: #285f23 !important;
    color: white !important;
  }
  .question .square {
    background-color: #285f23 !important;
    color: white !important;
  }
  .correct {
    .option-content {
      background-color: #299455;
      color: rgb(255, 255, 255) !important;
    }

    .option-letter {
      background-color: #299455 !important;
      color: rgb(255, 255, 255) !important;
    }
  }
}

.incorrect-question {
  .question .question-content {
    background-color: #761515 !important;
    color: white !important;
  }
  .question .square {
    background-color: #761515 !important;
    color: white !important;
  }
  .wrong {
    .option-content {
      background-color: #cf2323;
      color: rgb(255, 255, 255) !important;
    }

    .option-letter {
      background-color: #cf2323 !important;
      color: rgb(255, 255, 255) !important;
    }
  }
  .correct {
    .option-content {
      background-color: #333333;
      color: rgb(255, 255, 255) !important;
    }

    .option-letter {
      background-color: #333333 !important;
      color: rgb(255, 255, 255) !important;
    }
  }
}
</style>
