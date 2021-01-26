<template>
    <div id="dessert-div">
                <div class="form-group">
            <label for="dessert-name">Dessert Name</label>
            <input v-model="value.foodName"  type="text" :id="getId('dessert-name')" placeholder = "Dessert Name" class= "form-control">
        </div>
        <div class = "form-group">
            <label for="dessert-description">Dessert Description</label>
            <textarea v-model="value.description" type="text-area" class="form-control" :id="getId('dessert-description')" placeholder="Dessert Description"/>
        </div>
      
        <div class="form-check form-check-inline">
                <input v-model="value.vegan" class="form-check-input" type="checkbox" :id="getId('checkbox-vegan')" value="true">
                <label class="form-check-label" :for="getId('checkbox-vegan')">Vegan</label>
        </div>
        <div class="form-check form-check-inline">
            <input v-model="value.glutenFree" class="form-check-input" type="checkbox" :id="getId('checkbox-gluten-free')" value="true">
            <label class="form-check-label" :for="getId('checkbox-gluten-free')">Gluten-Free</label>
        </div>
        <div class="form-check form-check-inline">
            <input v-model="value.nutFree" class="form-check-input" type="checkbox" :id="getId('checkbox-nut-free')" value="true">
            <label class="form-check-label" :for="getId('checkbox-nut-free')">Nut-Free</label>
        </div>
        <br><br><br>
    </div>
</template>
<script>
export default {
    name : 'dessert-input',
    props: {
        value: {
            type: Object,
            required: true
        },
        index: {
            type: Number
        }
    },
    methods: {
        getId(type) {
            return `dessert-${type}-${this.index}`;
        }
    },
    watch: {
        value() {
            this.$emit('input', this.value);
        }
    }
    
}
</script>
<style scoped>

</style>