<template>
<router-link tag="div" class="preview" v-bind:to="{ name:'eventDetails', params:{eventId:list.eventId}}">
    <!-- make this populate from the API -->
    <h2>{{list.name}}</h2>  
    <h3>Time: {{list.time}}</h3>
    <h3>{{list.date.dayOfWeek}} {{list.date.month}} {{list.date.day}} {{list.date.year}}</h3>
    
</router-link>
</template>

<script>


export default {
    name: 'event-preview',
    props: {
        list: Object
    }

}

</script>

<style scoped>
    h2 {
        border-bottom: rgba(0,127,0,0.5) dashed 2px;
    }
</style>
