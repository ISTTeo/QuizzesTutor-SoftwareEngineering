<template>
    <v-row class='stats-container'>
        <div class="items">
            <div class="icon-wrapper" ref="percentageOfSeenQuestions">
            <animated-number
                :number="fstVal"></animated-number>
            </div>
            <div class="project-name">
            <p>{{fstStr}}</p>
            </div>
        </div>
        <div class="items">
            <div class="icon-wrapper" ref="percentageOfSeenQuestions">
            <animated-number
                :number="sndVal"
                >%</animated-number
            >
            </div>
            <div class="project-name">
            <p>{{sndStr}}</p>
            </div>
        </div>
        <div class="preferences settings">
            <div class="project-name">Privacy Settings</div>
            <div class="project-name">
             Private 
             <v-switch data-cy="visibilitySwitch" v-model="myPref" @change="changePreference" insert></v-switch>
             Public
            </div>
        </div>
        
    </v-row>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import AnimatedNumber from '@/components/AnimatedNumber.vue';
import RemoteServices from '@/services/RemoteServices';
import FeaturePreferences from '@/models/statement/FeaturePreferences';

@Component({
  components: { AnimatedNumber }
})
export default class FeatureStatsComponent extends Vue {
  @Prop(Number) fstVal!: number;
  @Prop(Number) sndVal!: number;
  @Prop(String) fstStr!: String;
  @Prop(String) sndStr!: String;  
  @Prop(Number) pref!: number;
  preferences: FeaturePreferences = new FeaturePreferences();
  myPref: boolean = false;

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.preferences = await RemoteServices.getStudentPreferences();
        
        if(this.pref == 0) {        
          this.myPref = this.preferences.answerStatsVisibility;
        } 

        if(this.pref == 1) {        
          this.myPref = this.preferences.proposalVisibility;
          
        }

        if(this.pref == 2) {        
          this.myPref = this.preferences.discussionStatsVisibility;
          
        } 

        if(this.pref == 3) {
          this.myPref = this.preferences.tournamentStatsVisibility;
        }
       
      
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
      await this.$store.dispatch('clearLoading');
  }

  async changePreference() {
    if (this.pref == 0) {
     this.preferences.answerStatsVisibility =  this.myPref;
     
    }

    if (this.pref ==  1) {
     this.preferences.proposalVisibility =  this.myPref;
     
    }

    if (this.pref == 2) {
     this.preferences.discussionStatsVisibility =  this.myPref;
     
    }

    if(this.pref == 3){
      this.preferences.tournamentStatsVisibility =  this.myPref;
    }

    
    

    await this.$store.dispatch('loading');
    try {
      await RemoteServices.setStudentPreferences(this.preferences);
    } catch (error) {
        await this.$store.dispatch('error', error);
      }
      await this.$store.dispatch('clearLoading');
  }
}
</script>


<style lang="scss" scoped>
.stats-container {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  justify-content: center;
  align-items: stretch;
  align-content: center;
  height: 100%;

  .items {
    background-color: rgba(255, 255, 255, 0.75);
    color: #1976d2;
    border-radius: 5px;
    flex-basis: 25%;
    margin: 20px;
    cursor: pointer;
    transition: all 0.6s;
  }

  
  .preferences {
    background-color:rgba(255, 255, 255, 0.75);
    color: #1976d2;
    border-radius: 5px;
    flex-basis: 25%;
    margin: 20px;
    padding: 50px;
    cursor: pointer;
    transition: all 0.6s;
  }

}

.icon-wrapper,
.project-name {
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-wrapper {
  font-size: 100px;
  transform: translateY(0px);
  transition: all 0.6s;
}

.icon-wrapper {
  align-self: end;
}

.project-name {
  align-self: start;
}
.project-name {
  font-size: 24px;
  font-weight: bold;
  letter-spacing: 2px;
  transform: translateY(0px);
  transition: all 0.5s;
}

.items:hover {
  border: 3px solid black;

  & .project-name p {
    transform: translateY(-10px);
  }
  & .icon-wrapper i {
    transform: translateY(5px);
  }
}
</style>


