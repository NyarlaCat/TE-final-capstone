<template>
<div id="create-menu" class = "container-fluid">
    <form @submit.prevent="createMenu" class = "offset-md-3 col-md-6">
        <br><br>
        
        <h4>Entrees</h4>
        <entree-input v-for="(entree,index) in entrees" :key="index+'-entree'" v-model="entrees[index]" :index="index" />
        <button @click.prevent="addEntree()">Add Another Entree</button>
        
        <br><br>
        <h4>Sides</h4>
        <side-input v-for="(side,index) in sides" :key="index+'-side'" v-model="sides[index]"/>
        <button @click.prevent="addSide()">Add Another Side</button>

        <br><br>

        <h4>Beverages</h4>
        <beverage-input v-for="(beverage,index) in beverages" :key="index+'-beverage'" v-model="beverages[index]"/>
        <button @click.prevent="addBeverage()">Add Another Beverage</button>
        
        <br><br>
        <h4>Desserts</h4>
        <dessert-input v-for="(dessert,index) in desserts" :key="index+'-dessert'" v-model="desserts[index]"/>
        <button @click.prevent="addDessert()">Add Another Dessert</button>

        <br /><br />

        <button type="submit" class = "btn btn-secondary" :disabled="formSubmitting">Create Menu</button>
        
        <br>
 
  
    </form>
</div>
    
</template>
<script>
import auth from '../auth'
import EntreeInput from '../components/EntreeInput'
import SideInput from '../components/SideInput'
import BeverageInput from '../components/BeverageInput'
import DessertInput from '../components/DessertInput'
export default {
    name : 'create-menu',
    components : {
        EntreeInput,
        SideInput,
        BeverageInput,
        DessertInput
},
    data() {
        return {
            formSubmitting: false,
            entrees: [{
                foodName: '',
                vegetarian: false,
                vegan: false,
                glutenFree: false,
                nutFree: false,
                description: '',
                foodCategory: 'Entree'
            }],
            sides: [{
                foodName: '',
                vegetarian: '',
                vegan: false,
                glutenFree: false,
                nutFree: false,
                description: '',
                foodCategory: 'Side'
            }],
            desserts: [{
                foodName: '',
                vegetarian: false,
                vegan: false,
                glutenFree: false,
                nutFree: false,
                description: '',
                foodCategory: 'Dessert'
            }],
            beverages: [{
                foodName: '',
                vegetarian: false,
                vegan: false,
                glutenFree: false,
                nutFree: false,
                description: '',
                foodCategory: 'Beverage'
            }],
        }
    },
    methods: {
        fetcherMethod(menuItem){
            if(menuItem.foodName !== ''){
                return fetch(`${process.env.VUE_APP_REMOTE_API}/api/event/${this.$route.params.eventId}/menu`, {
                    method: 'POST',
                    headers: {
                        "Authorization": "Bearer "+ auth.getToken(),
                        "Accept": 'application/json',
                        'Content-Type': 'application/json',
            
                    },
                    body: JSON.stringify(menuItem),
                })
            }
            return null;
        },
        addEntree() {
            this.entrees.push({
                foodName: '',
                vegetarian: false,
                vegan: false,
                glutenFree: false,
                nutFree: false,
                description: '',
                foodCategory: 'Entree'
            });
        },
        addBeverage() {
            this.beverages.push({
                foodName: '',
                vegetarian: false,
                vegan: false,
                glutenFree: false,
                nutFree: false,
                description: '',
                foodCategory: 'Beverage'
            });
        },
        addDessert() {
            this.desserts.push({
                foodName: '',
                vegetarian: false,
                vegan: false,
                glutenFree: false,
                nutFree: false,
                description: '',
                foodCategory: 'Dessert'
            });
        },
        addSide() {
            this.sides.push({
                foodName: '',
                vegetarian: '',
                vegan: false,
                glutenFree: false,
                nutFree: false,
                description: '',
                foodCategory: 'Side'
            });
        },
        createMenu() {
            this.formSubmitting = true;
            let promises = [];

            for( let entree of this.entrees ) {
                let promise = this.fetcherMethod(entree);
                if( promise != null ) {
                    promises.push(promise);
                }
            }
            for( let side of this.sides ) {
                let promise = this.fetcherMethod(side);
                if( promise != null ) {
                    promises.push(promise);
                }
            }
            for( let dessert of this.desserts ) {
                let promise = this.fetcherMethod(dessert);
                if( promise != null ) {
                    promises.push(promise);
                }
            }
            for( let beverage of this.beverages ) {
                let promise = this.fetcherMethod(beverage);
                if( promise != null ) {
                    promises.push(promise);
                }
            }

            Promise.all(promises).then(response=>{
                if (response.length == 0 || response[0].ok) {
                    this.$router.push({ path: `/${this.$route.params.eventId}/eventdetails`, query: { createEventStatus: 'success' } });
                } else {
                    this.registrationErrors = true;
                }
            });
        },
    },
}
</script>
<style scoped>
button {
    cursor: pointer;
}
</style>