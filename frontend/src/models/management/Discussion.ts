import Question from './Question';

export default class Discussion {
  id!: number;
  questionId!: number;
  questionDto!: Question;
  userId: number | undefined;
  questionAnswerId: number | undefined;
  title: string | undefined;
  content: string | undefined;
  numberOfReplies: number | undefined;
  isPublic!: boolean;

  constructor(jsonObj?: Discussion) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.questionId = jsonObj.questionId;
      this.questionDto = jsonObj.questionDto;
      this.userId = jsonObj.userId;
      this.questionAnswerId = jsonObj.questionAnswerId;
      this.title = jsonObj.title;
      this.content = jsonObj.content;
      this.numberOfReplies = jsonObj.numberOfReplies;
      this.isPublic = jsonObj.isPublic;
    }
  }
}
