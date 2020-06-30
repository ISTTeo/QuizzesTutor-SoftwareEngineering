import User from '@/models/user/User';

export default class Reply {
  id: number | undefined;
  userDto: User | undefined;
  discussionId!: number;
  replyContent: string | undefined;

  constructor(jsonObj?: Reply) {
    if (jsonObj) {
      this.userDto = jsonObj.userDto;
      this.discussionId = jsonObj.discussionId;
      this.replyContent = jsonObj.replyContent;
    }
  }
}
