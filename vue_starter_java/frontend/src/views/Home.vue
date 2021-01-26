<template>
  <div class="home" id="picture">
      <div class="hosting container">
        <h1>Hosting</h1>
        <div class="nothing" v-if="hosting.length===0">Nothing here!</div>
        <event-preview v-for="event in hosting" v-bind:list="event" v-bind:key="event.eventId">
        </event-preview>
      </div>

      <div class="attending container">
        <h1>Attending</h1>
        <div class="nothing" v-if="attending.length===0">Nothing here!</div>
        <event-preview v-for="event in attending" v-bind:list="event" v-bind:key="event.eventId">
        </event-preview>
      </div>

      <div class="invites container">
        <h1>Invites</h1>
        <div class="nothing" v-if="invites.length===0">Nothing here!</div>
        <event-preview v-for="event in invites" v-bind:list="event" v-bind:key="event.eventId">
        </event-preview>
      </div>
  </div>
</template>

<script>
import EventPreview from '../components/EventPreview.vue';
import auth from '../auth.js'
import BackgroundPic from '../components/BackgroundPic.vue';
export default {
  name: 'home', 
  components: {
    EventPreview,
    BackgroundPic
  },
  data() {
    return {
      events: [],
      hosting: [],
      attending: [],
      invites: []

    }
  },
  methods : {
    fetchEvents() {
      fetch(`${process.env.VUE_APP_REMOTE_API}/api/events`, {
                method : "GET",
                headers: { //this header will insert the user ID into the called upon methods
                    
                    "Authorization": "Bearer "+auth.getToken(),
                    "Content-Type" : "application/json",
                    "Accepts" : "application/json"
                }
            })
            .then((response)=>response.json())
            .then((data)=>{
              this.events = data.object;
              
              this.hosting = this.events.filter((current) => {
                return current.hosting === true
              });
              this.attending = this.events.filter((current) =>{
                return current.attending === true && current.hosting !== true
              });
              this.invites = this.events.filter((current) =>{
                return current.attending === null && current.invited === true
              });
            })
    }
  },
  created(){
    this.fetchEvents();
  }
}
</script>

<style scoped>

  .home {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    grid-template-rows: 1fr;
    align-items: start;
    justify-items: stretch;
    text-align: center;
    padding-top: 2rem;
    
  }
  .home > * {
    padding: 10px;
    margin: 0;
  }
  .home > * > div {
    margin: 20px auto;
    color: #2ea71b;
    background: rgba(255,255,255,0.8);
    border-radius: 10px;
    border: rgba(0,127,0,0.5) 3px solid;
    cursor: pointer;
  }
  .home > * > div:hover {
    background: rgba(255,255,255,0.9);
  }
  .nothing {
    font-size: 24px;
    cursor: default !important;
  }
  .nothing:hover {
    background: rgba(255,255,255,0.8) !important;
  }
/*
#picture {
  background: url(/img/homeBackground.jpeg) no-repeat center center fixed ;
  content: "";
  background-size: 100%;
  opacity: 0.3;
  top: 0;
  left: 0;
  bottom: 0;
  right: 0;
  position: fixed;
  z-index: -1;
}
*/

</style>
