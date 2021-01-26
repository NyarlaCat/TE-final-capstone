<template>
<div class="rsvp-form container">
    <div class="alert alert-danger" role="alert" v-if="errors.length">
        <h2>There were problems with your RSVP:</h2>
        <p v-for="error in errors" v-bind:key="error">{{error}}</p>
    </div>
    <form @submit.prevent="sendRSVP()">
        <br>
        <div class = "attending-btn container-fluid">
            <h3>Will you be attending?</h3>
            <div class="form-group btn-group btn-group-toggle">
                <label class="btn btn-secondary" v-bind:class="{ active: rsvp.attending==='true' }">
                <input 
                name = "attending" 
                type="radio" 
                v-model="rsvp.attending"
                value="true" />Yes
                </label>

                <label class="btn btn-secondary" v-bind:class="{ active: rsvp.attending==='false' }">
                <input 
                name = "attending" 
                type="radio"
                v-model="rsvp.attending" 
                value="false" />No
                </label>
            </div>

            <div v-if="rsvp.attending!==''">
                <div class="form-group">
                    <h3>Your First Name</h3>
                    <label for="firstName">
                    <input
                    type="text"
                    placeholder="First Name"
                    v-model="rsvp.firstName"
                    >
                    </label>
                    <h3>Your Last Name</h3>
                    <label for="firstName">
                    <input
                    type="text"
                    placeholder="Last Name"
                    v-model="rsvp.lastName"
                    >
                    </label>
                </div>
            </div>
            <div v-if="rsvp.attending==='true'">
                <div class = "form-group">
                    <h3>How many adult guests will you bring (excluding you)?</h3>
                    <input type="number" v-model="rsvp.adultGuests" min="0" max="10" class="btn btn-secondary col-md-1" @blur="correctAdultPeople()" />
                </div>
                <div class = "form-group">
                    <h3>How many children will you bring?</h3>
                    <input type="number" v-model="rsvp.childGuests" min="0" max="10" class="btn btn-secondary col-md-1" @blur="correctChildPeople()" />
                </div>

                <div class="rsvp-menu" v-for="(personNumber,index) in totalNumberOfPeople" v-bind:key="index">
                    <h2 v-if="entree.length || side.length || dessert.length">Order for {{personLabel(index)}}</h2>
                    <h3 v-if="entree.length">Please pick one of the following entrees</h3>

                    <div class="menu-items">
                        <div class = "food-btn btn-group-horizontal btn-group-toggle form-group" 
                                    v-for="food in entree" v-bind:key="food.foodId">
                            <label class="btn btn-secondary btn-lg btn-block" type="radio" v-bind:class="{ active: rsvp.food[index] != undefined && rsvp.food[index].entreeId===food.foodId }">
                            <input name ="radio-food" type="radio" v-bind:value="food.foodId" v-model="rsvp.food[index].entreeId" />
                                <h4>{{food.foodName}}</h4>  
                                <p>{{food.description}}</p>
                                <h5 v-if="getFoodAttributes(food).length">{{getFoodAttributes(food)}}</h5>
                            </label>
                        </div>
                    </div>
                    <!--rsvp-menu v-for="food in entree" v-bind:foodItem="food" v-bind:key="food.foodId"></rsvp-menu-->
                    
                    <h3 v-if="side.length">Choose up to {{ maxSidesText }} side{{ isPlural(maxSides) }}</h3>
                    <!--rsvp-menu v-for="food in side" v-bind:foodItem="food" v-bind:key="food.foodId"></rsvp-menu-->
                    <div class="menu-items">
                        <div class = "food-btn btn-group-vertical btn-group-toggle form-group" 
                                    v-for="food in side" v-bind:key="food.foodId">
                            <label class="btn btn-secondary btn-lg btn-block" type="radio" v-bind:class="{ active: hasSide(index, food.foodId) }">
                            <input name ="radio-food" type="checkbox" v-bind:value="food.foodId" @change="sideChanged(index, food.foodId)" />
                                <h4>{{food.foodName}}</h4>  
                                <p>{{food.description}}</p>
                                <h5 v-if="getFoodAttributes(food).length">{{getFoodAttributes(food)}}</h5>
                            </label>
                        </div>
                    </div>

                    <h3 v-if="dessert.length">Choose one dessert</h3>
                    <!--rsvp-menu v-for="food in dessert" v-bind:foodItem="food" v-bind:key="food.foodId"></rsvp-menu-->
                    <div class="menu-items">
                        <div class = "food-btn btn-group-vertical btn-group-toggle form-group" 
                                    v-for="food in dessert" v-bind:key="food.foodId">
                            <label class="btn btn-secondary btn-lg btn-block" type="radio" v-bind:class="{ active: rsvp.food[index] != undefined && rsvp.food[index].dessertId===food.foodId }">
                            <input name ="radio-food" type="radio" v-bind:value="food.foodId" v-model="rsvp.food[index].dessertId" />
                                <h4>{{food.foodName}}</h4>  
                                <p>{{food.description}}</p>
                                <h5 v-if="getFoodAttributes(food).length">{{getFoodAttributes(food)}}</h5>
                            </label>
                        </div>
                    </div>

                    <h3 v-if="beverage.length">Beverage options at the party</h3>
                    <ul>
                        <li v-for="drink in beverage" v-bind:key="drink.foodId">{{drink.foodName}} {{drink.description}}</li>
                    </ul>
                </div>
            </div>
        </div>
        
        <div class = "form-group col-md-8">
            <button type="submit" class="btn btn-secondary btn-lg" :disabled="submittingForm">Submit RSVP</button>
        </div>

    </form>
</div>

</template>

<script>
import auth from '../auth.js'
import RsvpMenu from '../components/RsvpMenu.vue';
export default {
    name: "rsvp",
    components:{
        RsvpMenu
    },
    data(){
        return {
            maxSides: 2,
            maxSidesText: "two",
            rsvp: {
              eventId:'',
              attending: '',
              firstName:'',
              lastName:'',
              adultGuests: 0,
              childGuests: 0,
              food: [
                  { entreeId: null, sideIds: [], dessertId: null }
              ]
            },
            menu: [],
            errors: [],
            submittingForm: false
        }

    },
    methods: {
        fetchEventMenu(){
            fetch(`${process.env.VUE_APP_REMOTE_API}/api/event/${this.$route.params.eventId}/menu`, {
                method : "GET",
                headers: { 
                    "Authorization": "Bearer "+ auth.getToken(),
                    "Content-Type" : "application/json",
                    "Accepts" : "application/json"
                }
            })
            .then((response) => response.json())
            .then((data) => {
                this.menu = data.object;
            })
        },
        personLabel(num) {
            if( num == 0 ) {
                return "You";
            }
            if( num <= this.rsvp.adultGuests ) {
                return "Adult Guest #"+(num);
            } else {
                return "Child Guest #"+(num-this.rsvp.adultGuests);
            }
        },
        scrollToTop() {
            window.scrollTo(0,0);
        },
        sendRSVP() {
            this.submittingForm = true;
            // validate form
            this.errors = [];
            if( this.rsvp.attending === '' ) {
                this.errors.push("Please say if you're attending");
            } else {
                // check more form
                if( this.rsvp.firstName.trim() === '' ) {
                    this.errors.push("Please tell us your first name");
                }
                if( this.rsvp.lastName.trim() === '' ) {
                    this.errors.push("Please tell us your last name");
                }
                if( this.rsvp.attending === 'true' ) {
                    this.rsvp.food.forEach((current,index)=>{
                        if( this.entree.length && current.entreeId == undefined ) {
                            this.errors.push("Please complete the entree order for "+this.personLabel(index));
                        }
                        if( this.dessert.length && current.dessertId == undefined ) {
                            this.errors.push("Please complete the dessert order for "+this.personLabel(index));
                        }
                    })
                }
            }

            if( this.errors.length === 0 ) {
                let path = "accept";
                if( this.rsvp.attending === 'false' ) {
                    this.rsvp.adultGuests = 0;
                    this.rsvp.childGuests = 0;
                }
                let promises = [];
                
                promises.push(fetch(`${process.env.VUE_APP_REMOTE_API}/api/event/${this.$route.params.eventId}/rsvp/${path}`, {
                    method : "POST",
                    headers: { 
                        "Authorization": "Bearer "+ auth.getToken(),
                        "Content-Type" : "application/json",
                        "Accepts" : "application/json"
                    },
                    body: JSON.stringify(this.rsvp)
                }).then((data)=>data.json())
                .then((response)=>{
                }));

                if( this.rsvp.attending === 'true' ) {
                    // push the food
                    //// pack it for quantity
                    let foodSend = this.rsvp.food.reduce((acc, cur)=>{
                        // see if food is currently there
                        for( let food of acc ) {
                            if( food.foodId === cur.entreeId ) {
                                food.quantity++;
                                cur.entreeId = null;
                            }
                            if( food.foodId === cur.dessertId ) {
                                food.quantity++;
                                cur.dessertId = null;
                            }
                            for( let sideIdx in cur.sideIds ) {
                                if( food.foodId === cur.sideIds[sideIdx] ) {
                                    food.quantity++;
                                    cur.sideIds[sideIdx] = null;
                                }
                            }
                        }
                        // check for nulls
                        if( cur.entreeId != null ) {
                            acc.push({ foodId: cur.entreeId, quantity: 1 });
                        }
                        if( cur.dessertId != null ) {
                            acc.push({ foodId: cur.dessertId, quantity: 1 });
                        }
                        for( let side of cur.sideIds ) {
                            if( side != null ) {
                                acc.push({ foodId: side, quantity: 1 });
                            }
                        }

                        return acc;
                    }, []);

                    console.log(foodSend);

                    foodSend.forEach((current)=>{
                        promises.push(fetch(`${process.env.VUE_APP_REMOTE_API}/api/event/${this.$route.params.eventId}/order`, {
                            method: "POST",
                            headers: { 
                                "Authorization": "Bearer "+ auth.getToken(),
                                "Content-Type" : "application/json",
                                "Accepts" : "application/json"
                            },
                            body: JSON.stringify(current)
                        }).then((data)=>data.json())
                        .then((response)=>{
                        }) );
                    });
                }

                Promise.all(promises).then((values)=>{
                    this.$router.push({ name: "eventDetails", params: { eventId: this.$route.params.eventId }, query: { rsvpSuccess: true } });
                })
            } else {
                this.submittingForm = false;
            }
        },
        fetchAttendeeInfo(){

        },
        adultGuestsChange(newVal, oldVal) {
            if( newVal === '' ) {
                return;
            }
            oldVal = parseInt(oldVal);

            for( let i=0; i < Math.abs(newVal - oldVal); i++ ) {
                if( newVal < oldVal ) {
                    this.rsvp.food.splice(oldVal-i, 1);
                } else {
                    this.rsvp.food.splice(1+oldVal, 0, { entreeId: null, sideIds: [], dessertId: null });
                }
            }
        },
        childGuestsChange(newVal, oldVal) {
            for( let i=0; i < Math.abs(newVal - oldVal); i++ ) {
                if( newVal < oldVal ) {
                    this.rsvp.food.pop();
                } else {
                    this.rsvp.food.push({ entreeId: null, sideIds: [], dessertId: null });
                }
            }
        },
        correctAdultPeople: function() {
            if( this.rsvp.adultGuests === '' ) {
                this.rsvp.adultGuests = '0';
                let childCount = parseInt(this.rsvp.childGuests);
                // check the count
                let oldVal = this.rsvp.food.length - childCount - 1;
                if( oldVal > 0 ) {
                    this.adultGuestsChange(0, oldVal);
                }
            }
        },
        correctChildPeople: function() {
            if( this.rsvp.childGuests === '' ) {
                this.rsvp.childGuests = '0';
                let adultCount = parseInt(this.rsvp.adultGuests);
                // check the count
                let oldVal = this.rsvp.food.length - adultCount - 1;
                if( oldVal > 0 ) {
                    this.childGuestsChange(0, oldVal);
                }
            }
        },
        hasSide: function(index, checkId) {
            for( let side of this.rsvp.food[index].sideIds ) {
                if( side == checkId ) {
                    return true;
                }
            }
            return false;
        },
        sideChanged: function(index, changedId) {
            // see if changedId is present
            let exists = this.rsvp.food[index].sideIds.findIndex((element)=>element==changedId);
            if( exists === -1 ) {
                if( this.rsvp.food[index].sideIds.unshift(changedId) > this.maxSides ) {
                    this.rsvp.food[index].sideIds.pop();
                }
            } else {
                this.rsvp.food[index].sideIds.splice(exists, 1);
            }
        },
        getFoodAttributes: function(food) {
            let attributes = [];
            if( food.glutenFree ) {
                attributes.push("Gluten Free");
            }
            if( food.nutFree ) {
                attributes.push("Nut Free");
            }
            if( food.vegan ) {
                attributes.push("Vegan");
            }
            if( food.vegetarian ) {
                attributes.push("Vegetarian");
            }
            return attributes.join(", ");
        },
        isPlural(num) {
            return num==1?"":"s";
        }
    },
    watch: {
        errors: function() {
            this.scrollToTop();
        },
        'rsvp.adultGuests': function(newVal, oldVal) {
            this.adultGuestsChange(newVal, oldVal);
        },
        'rsvp.childGuests': function(newVal, oldVal) {
            this.childGuestsChange(newVal, oldVal);
        }
    },
    created(){
        this.fetchEventMenu();
    },
    computed: {
        entree: function() {
            return this.menu.filter((current)=>{
                return current.foodCategory === "Entree"
            });
        },
        side: function() {
            return this.menu.filter((current)=>{
                return current.foodCategory === "Side"
            });
        },
        dessert: function() {
            return this.menu.filter((current)=>{
                return current.foodCategory === "Dessert"
            });
        },
        beverage: function() {
            return this.menu.filter((current)=>{
                return current.foodCategory === "Beverage"
            });
        },
        totalNumberOfPeople: function() {
            let people = 1;
            if( this.rsvp.adultGuests !== '' ) {
                people += parseInt(this.rsvp.adultGuests) 
            }
            if( this.rsvp.childGuests !== '' ) { 
                people += parseInt(this.rsvp.childGuests);
            }
            return people;
        }
    }
}
</script>

<style scoped>

:root {
  --gxorange: #ff7f68;
  --gxyellow: #ffdb2b;
  --gxpink: #ef2871;
  --gxgreen: rgb(217, 224, 216);
   --gxgreendark: #12300d;
  --gxwhite: #effffb;
  --gxgreentransparent: rgb(113, 216, 97, 1)
}

.btn {
    background-color: var(--gxgreen) ;
    border-color: var(--gxgreendark) ;
    color: var(--gxwhite);
    word-wrap: break-word;
    white-space: normal;
    text-align: left;
}

.attending-btn .btn{

    text-align: center;
}
.btn.active {
  color: var(--gxwhite) !important; 
  background-color: #12300d !important; 
  border-color: #12300d !important;
  
}
h3 {
    color: var(--gxpink);
}
.card {
    background-color: var(--gxwhite) ;
}
.selected {
    background-color: var(--gxgreendark) !important ;
}
.rsvp-menu {
    margin: 0 50px;
}
.rsvp-menu h2 {
    margin-left: -50px;
}
.menu-items  {
    display: flex;
    flex-wrap: wrap;
}
button {
    cursor: pointer;
}
</style>
