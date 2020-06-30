<template>
  <div class="container">
    <h2>Discussions</h2>
    <ul>
      <li class="list-header">
        <div class="col">Title</div>
        <div class="col">Content</div>
        <div class="col">Number of Replies</div>
        <div class="col">Is Public</div>
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
        <div class="col">
          <div v-if="discussion.isPublic">
            <i>Public</i>
          </div>
          <div v-else>
            <i>Private</i>
          </div>
        </div>

        <div class="col last-col" data-cy="showReplyButton">
          <div
            v-if="discussion.numberOfReplies"
            @click="openDialog(discussion)"
          >
            <i class="fas fa-eye"></i>
          </div>
          <div v-else>
            <i class="fas fa-eye-slash"></i>
          </div>
        </div>
      </li>
    </ul>

    <v-card>
      <show-reply-dialog
        v-if="showReplyDialog"
        v-model="showReplyDialog"
        :discussion="currentDiscussion"
        :replies="currentReplies"
        v-on:close-dialog="onCloseDialog"
      />
    </v-card>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Discussion from '@/models/management/Discussion';
import Reply from '@/models/management/Reply';
import ShowReplyDialog from '@/views/student/ShowReplyDialog.vue';

@Component({
  components: {
    'show-reply-dialog': ShowReplyDialog
  }
})
export default class DiscussionsView extends Vue {
  discussions: Discussion[] = [];
  currentDiscussion: Discussion | null = null;
  currentReplies: Reply[] = [];
  showReplyDialog: boolean = false;

  async created() {
    this.loadDiscussions();
  }

  async loadDiscussions() {
    await this.$store.dispatch('loading');
    try {
      this.discussions = (
        await RemoteServices.getStudentDiscussions()
      ).reverse();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async openDialog(discussion: Discussion) {
    this.currentDiscussion = new Discussion(discussion);
    try {
      this.currentReplies = await RemoteServices.getDiscussionReply(
        this.currentDiscussion.id
      );
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    this.showReplyDialog = true;
  }

  onCloseDialog() {
    this.loadDiscussions();
    this.currentDiscussion = null;
    this.showReplyDialog = false;
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
