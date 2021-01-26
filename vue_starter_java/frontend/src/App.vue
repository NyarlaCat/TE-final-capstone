<template>
  <div id="app" v-bind:class="{ picture:$router.currentRoute.name =='home' }">
    <nav-header v-if="$router.currentRoute.name!='login' && $router.currentRoute.name!='register'" />
    <router-view />
  </div>
</template>

<script>
import NavHeader from './components/Header.vue';
import auth from './auth.js'
export default {
  components: {
    NavHeader
  },
  data() {
    return {
    }
  },
  
}
</script>

<style>
@import url('https://fonts.googleapis.com/css?family=Titan+One&display=swap');
@import url('https://fonts.googleapis.com/css?family=Baloo+Bhai&display=swap');

html{
  height: 100%;
}
body {
  height: 100%;
  background:#ff7d49;
  background: linear-gradient(to right, #ff7f68, #ffdb2b, #ffdb2b,#ffdb2b,#ffdb2b,#ffdb2b, #ff7f68);
  font-family: 'Baloo Bhai', cursive;
}
.picture::after {
  background: url(/img/homeBackground.jpeg) ;
  content: "";
  background-size: 100%;
  opacity: 0.3;
  top: 0;
  left: 0;
  bottom: 0;
  right: 0;
  position: fixed;
  z-index: -1;
  height: 100%;
}

#app {
height: 100%;
}

h1 {
  text-align:center;
  color:#ef2871;
  text-shadow: 0 0 4px white;
  text-transform: uppercase;
  font-family: 'Titan One', cursive;
  
}

</style>
