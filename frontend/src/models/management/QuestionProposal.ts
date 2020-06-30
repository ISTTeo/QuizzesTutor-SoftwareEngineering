import Option from '@/models/management/Option';
import Topic from '@/models/management/Topic';
import Image from '@/models/management/Image';

export default class QuestionProposal {
  id: number = 0;
  title: string = '';
  content: string = '';
  courseId: number = 0;
  authorId: number = 0;
  status: string = 'PENDING';
  options: Option[] = [new Option(), new Option(), new Option(), new Option()];
  topics: Topic[] = [];
  image: Image = new Image();

  constructor(jsonObj?: QuestionProposal) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.title = jsonObj.title;
      this.content = jsonObj.content;
      this.status = jsonObj.status;
      this.image = jsonObj.image;
      this.options = jsonObj.options.map(
        (option: Option) => new Option(option)
      );
      this.topics = jsonObj.topics.map((topic: Topic) => new Topic(topic));
      this.courseId = jsonObj.courseId;
      this.authorId = jsonObj.authorId;
    }
  }
}
