<template>
    <div class = "food-btn container-fluid btn-group-vertical btn-group-toggle col-md-8 form-group">
        <label class="btn btn-secondary btn-lg btn-block" type="radio">
        <input name ="radio-food" type="radio" value="true">
            <h4>{{foodItem.foodName}}</h4>
            <h5 v-if="foodItem.glutenFree">Gluten Free </h5>
            <h5 v-if="foodItem.nutFree">Nut Free </h5>
            <h5 v-if="foodItem.vegetarian">Vegetarian </h5>  
            <h5 v-if="foodItem.vegan">Vegan </h5>    
            <p>{{foodItem.description}}</p>
        </label>
    </div>
</template>

<script>
export default {
    name: 'rsvp-menu',
    props: {
        foodItem: Object
    }
    
}
</script>

<style scoped>

:root {
  --gxorange: #ff7f68;
  --gxyellow: #ffdb2b;
  --gxpink: #ef2871;
  --gxgreen: rgb(217, 224, 216);
   --gxgreendark: #63bd55;
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
  color: var(--gxwhite) ; 
  background-color: var(--gxgreendark) ; 
  border-color: var(--gxgreendark) ;
  
}
h3 {
    color: var(--gxpink);
}
.card {
    background-color: var(--gxwhite) ;
}
.selected {
    background-color: var(--gxgreendark) ;
}


</style>