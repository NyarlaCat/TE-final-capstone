<template>
    <div class = "side-div">
        <div class="form-group">
            <label for="side-name">Side Name</label>
            <input v-model="value.foodName" type="text" :id="getId('side-name')" placeholder = "Salacious sound for your side" class= "form-control">
        </div>
        <div class = "form-group">
            <label for="side-description">Side Description</label>
            <textarea v-model="value.description" type="text-area" class="form-control" :id="getId('side-description')" placeholder="Specifics sketching out your sustenance "/>
        </div>

        <div class="form-check form-check-inline">
                <input v-model="value.vegetarian"  class="form-check-input" type="checkbox" :id="getId('checkbox-vegetarian')" value="true">
                <label class="form-check-label" :for="getId('checkbox-vegetarian')">Vegetarian</label>
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
    name: 'side-input',
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
            return `side-${type}-${this.index}`;
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