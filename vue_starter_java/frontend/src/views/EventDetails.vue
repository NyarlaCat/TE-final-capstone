<template>
<div id="wrapper" v-if="event">

    <h1>{{event.name}}</h1>
    <div id="content">
      <div>
        <iframe width="600" height="450" frameborder="0" :src="mapsurl" allowfullscreen v-if="fullAddress!=''" ></iframe>
     </div>   
        <section class="whole-card">
           
        <section class="this-group">   
            <section class="details">
                <h2>{{event.time}} on {{event.date.dayOfWeek}} {{event.date.month}} {{event.date.day}}, {{event.date.year}}</h2>
                <h2>{{event.date.daysAway}} day{{isPlural(event.date.daysAway)}} away!</h2>
                <h4>{{address.streetAddress}} {{address.city}} {{address.state}} {{address.zip}}</h4>
                <p>{{event.description}}</p>
             </section>
                
                <section class="guest-list">
                <section class="attending">
                    <h5>Guest List:</h5>
                    <em v-if="yesAttending.length===0">No guests have RSVPd yet</em>
                    <ul>
                     <li v-for="guest in yesAttending" v-bind:key="guest.userId"> {{guest.firstName}} {{guest.lastName}} + {{guest.adultGuests}} Adults, {{guest.childGuests}} Children
                        <ul v-if="event.hosting">
                            <li v-for="(order,index) in guest.orders" v-bind:key="guest.userId+'-order-'+index">
                                {{order.food.foodName}} x{{order.quantity}}
                            </li>
                        </ul>
                     </li>
                     </ul>
                </section>
        </section>
                <section class="not-attending" v-if="event.hosting && notAttending.length">
                <h5 class="no" v-if="notAttending.length">Declined Invitation:</h5>
                <ul>
                <li v-for = "guest in notAttending" v-bind:key="guest.userId">
                    {{guest.firstName}} {{guest.lastName}}
                </li>
                </ul>
                </section>
            </section>
            
            <section class="rsvp" v-if="event.invited && event.deadline.daysAway >= 0">
                You have {{event.deadline.daysAway}} day{{isPlural(event.deadline.daysAway)}} left to RSVP!
                <router-link tag="h1" v-bind:to="{ name:'rsvp', params:{eventId:event.eventId}}">
                <button class="btn">Send RSVP</button>
                 </router-link>
            </section>
        <section class="buttons">
            <div v-if="menu.length === 0" class="button">
                <router-link  tag="button" v-if="event.hosting" :to="{ name: 'createmenu', params: { eventId: this.$route.params.eventId } }">Create Menu</router-link>
            </div>
                
            <div class="button">
                <router-link  tag="button" v-if="event.hosting" :to="{ name: 'sendinvite', params: { eventId: this.$route.params.eventId } }">Send Invitation</router-link>
            </div>
        </section>

            


        </section>

    </div>
    <div v-if="event === null">
        <h1>Loading event...please stand by</h1>
    </div>
</div>
</template>

<script>
import auth from '../auth.js'

export default {
    data(){
        return{
            event: null,
            address: Object,
            attendees: [],
            yesAttending: [],
            notAttending: [],
            notRsvp: [],
            menu: [],
        
            mapsapikey: "AIzaSyC-fS5k_yLVqWldx2d0Pdz995Nuc7EuL8Y"
        }
    },
    methods:{
        fetchDescription(){
            fetch(`${process.env.VUE_APP_REMOTE_API}/api/event/${this.$route.params.eventId}`,{
                 method : "GET",
                headers: { //this header will insert the user ID into the called upon methods
                    
                    "Authorization": "Bearer "+ auth.getToken(),
                    "Content-Type" : "application/json",
                    "Accepts" : "application/json"
                }
            })
            .then((response) => response.json())
            .then((data)=>{
                const tempEvent = data.object;
                //setting the promises returned by the defined fetsches
                const promises = [
                    this.fetchAddress(tempEvent.addressId),
                    this.fetchAttendees(tempEvent.eventId),
                    this.fetchMenu(tempEvent.eventId)
                ];
                //mega promise over an iterible set
                Promise.all(promises)
                .then(()=>{
                    this.event = tempEvent;
                });
                
            
            })
        },
        fetchAddress(addressId){
            return fetch(`${process.env.VUE_APP_REMOTE_API}/api/address/${addressId}`,{
                 method : "GET",
                    headers: {
                    "Authorization": "Bearer "+ auth.getToken(),
                    "Content-Type" : "application/json",
                    "Accepts" : "application/json"
                    }
                })
                .then((response)=> response.json())
                .then((data) => {
                    this.address = data.object
                });
        },
        fetchAttendees(eventId){
            return fetch(`${process.env.VUE_APP_REMOTE_API}/api/event/${eventId}/attendees`,{
                 method : "GET",
                    headers: {
                    "Authorization": "Bearer "+ auth.getToken(),
                    "Content-Type" : "application/json",
                    "Accepts" : "application/json"
                    }
                })
                .then((response)=> response.json())
                .then((data) => {
                    this.attendees = data.object
                    this.generateGuestList();
                });
        },
        fetchMenu(eventId){
            return fetch(`${process.env.VUE_APP_REMOTE_API}/api/event/${eventId}/menu`,{
                 method : "GET",
                    headers: {
                    "Authorization": "Bearer "+ auth.getToken(),
                    "Content-Type" : "application/json",
                    "Accepts" : "application/json"
                    }
                })
                .then((response)=> response.json())
                .then((data) => {
                    this.menu = data.object
                });
        },
        generateGuestList(){
            this.yesAttending = this.attendees.filter((current) => {
                return current.attending === true && current.host ===false;
            })
            this.notAttending = this.attendees.filter((current) => {
                return current.attending === false && current.hasRsvped && current.host ===false;
            })
            this.notRsvp = this.attendees.filter((current) => {
                return !current.hasRsvped && current.host ===false;
            })
        },
        isPlural(val) {
            return val==1?"":"s";
        }
    },
    computed: {
        fullAddress() {
            return `${this.address.streetAddress} ${this.address.city} ${this.address.state} ${this.address.zip}`.trim();
        },
        mapsurl() {
            return `https://www.google.com/maps/embed/v1/search?q=${this.fullAddress}&key=${this.mapsapikey}`;
        }
    },
    created() {
        this.fetchDescription();
    }

  
}
</script>
<style scoped>
section {
    background: rgba(255,255,255,0.95);
    color: #63bd55;
    text-align: center;
    border-radius: 17px;
    padding: 15px;
}

ul {
    list-style: none;
    text-align: center;
    
}
.not-attending {
    color: #FF0033;
    text-align: center;
    
}
.no {
    color: #FF0033;
    text-align: center;
}
.noRsvp {
    color: #FFCC33;
}
.buttons {
    display: flex;
    justify-content: center;
    display: flex;
    flex-direction: column;
    

}
#content {
    display: flex;
    justify-content: center;
    margin: 25px;
    grid-gap: 10px;
    align-items: start;
}
iframe {
    border:0;
    width: 300px;
    height:300px;
    border-radius: 17px;
}
button {
    cursor: pointer;
    border-radius: 17px;
    padding: 20px;
    margin: 20px;
    
}
button:hover {
    background-color: #7fdb70 !important;
    
}
.whole-card {
    margin-left: 25px;
    display: flex;
}
</style>