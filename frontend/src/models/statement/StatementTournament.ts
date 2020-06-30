import User from '@/models/user/User';
import Topic from '@/models/management/Topic';

export default class StatementTournament {
  id!: number;
  name!: string;
  creator!: User;
  topics: Topic[] = [];
  numberOfQuestions!: number;
  enrolled: User[] = [];
  creationDate!: string;
  beginDate!: string;
  endDate!: string;

  constructor(jsonObj?: StatementTournament) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.name = jsonObj.name;
      this.numberOfQuestions = jsonObj.numberOfQuestions;
      this.creationDate = jsonObj.creationDate;
      this.beginDate = jsonObj.beginDate;
      this.endDate = jsonObj.endDate;

      this.creator = new User(jsonObj.creator);

      this.enrolled = jsonObj.enrolled.map(user => {
        return new User(user);
      });

      this.topics = jsonObj.topics.map(topic => {
        return new Topic(topic);
      });
    }
  }
}
