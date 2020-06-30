import Topic from '@/models/management/Topic';
import User from '../user/User';
import { Quiz } from './Quiz';
import { ISOtoString } from '@/services/ConvertDateService';

export default class Tournament {
  id!: number;
  name!: string;
  creationDate!: string | undefined;
  beginDate!: string | undefined;
  endDate!: string | undefined;
  numberOfQuestions!: number;
  createdBy!: User;
  quiz!: Quiz;

  topics: Topic[] = [];
  enrolled: User[] = [];

  constructor(jsonObj?: Tournament) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.name = jsonObj.name;
      if (jsonObj.creationDate != null)
        this.creationDate = ISOtoString(jsonObj.creationDate);
      if (jsonObj.beginDate != null)
        this.beginDate = ISOtoString(jsonObj.beginDate);
      if (jsonObj.endDate != null)
        this.endDate = ISOtoString(jsonObj.endDate);
      this.numberOfQuestions = jsonObj.numberOfQuestions;
      this.createdBy = new User(jsonObj.createdBy);

      if (jsonObj.topics) {
        this.topics = jsonObj.topics.map((topic: Topic) => new Topic(topic));
      }
      if (jsonObj.enrolled) {
        this.enrolled = jsonObj.enrolled.map((user: User) => new User(user));
      }
      if (jsonObj.quiz) {
        this.quiz = new Quiz(jsonObj.quiz);
      }
    }
  }
}
