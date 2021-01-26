<template>
    <div class = "entree-div">
         <div class="form-group">
        <label for="entree-name">Entree Name</label>
        <input v-model="value.foodName" type="text" :id="getId('entree-name')" placeholder = "Enviable expression for your entree" class= "form-control">
        </div>

        <div class = "form-group">
            <label for="entree-description">Entree Description</label>
            <textarea  v-model="value.description" type="text-area" class="form-control" :id="getId('entree-description')" placeholder="Delicious details describing your dish"/>
        </div>


        <div class="form-check form-check-inline">
                <input v-model="value.vegetarian" class="form-check-input" type="checkbox" :id="getId('checkbox-vegetarian')" value="true">
                <label class="form-check-label" :for="getId('checkbox-vegetarian')">Vegetarian</label>
        </div>
        <div class="form-check form-check-inline">
                <input v-model="value.vegan" class="form-check-input" type="checkbox" :id="getId('checkbox-vegan')">
                <label class="form-check-label" :for="getId('checkbox-vegan')">Vegan</label>
        </div>
        <div class="form-check form-check-inline">
            <input v-model="value.glutenFree" class="form-check-input" type="checkbox" :id="getId('checkbox-gluten-free')" >
            <label class="form-check-label" :for="getId('checkbox-gluten-free')">Gluten-Free</label>
        </div>
        <div class="form-check form-check-inline">
            <input v-model="value.nutFree" class="form-check-input" type="checkbox" :id="getId('checkbox-nut-free')" >
            <label class="form-check-label" :for="getId('checkbox-nut-free')">Nut-Free</label>
        </div>
        <br><br><br>
        </div>
</template>
<script>
export default {
    name: "entree-input",
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
            return `entree-${type}-${this.index}`;
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