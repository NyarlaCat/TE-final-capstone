<template>

      <nav class="navbar navbar-expand-sm" role="navigation" aria-label="main navigation">
        <div class="navbar-nav">
            <img id="fitPic" src="/img/logoTransparent.png" >
            <div class="left-links">
                    <router-link v-if="isLoggedIn" class ="nav-item nav-link" to="/">Home</router-link>
                    <router-link v-if="isLoggedIn" class ="nav-item nav-link" to="/newevent">Create New Event</router-link>
                    <p v-if="isLoggedIn" class ="nav-item nav-link logonoff" @click="logout()">Logoff</p>

            </div>
        </div>
      </nav>

</template>

<script>
import auth from '../auth';
import Logo from './Logo.vue';

export default {
    name: "nav-header",
    components:{
        Logo
    },
    data() {
        return {
            isLoggedIn: auth.getUser()!==null
        }
    },
    methods: {
        logout() {
            auth.logout();
            this.$router.push({ name: 'login' }).catch((err)=> {});
        },
        isLoggedInFunc() {
            this.isLoggedIn = auth.getUser()!==null;
        }
    },
    created() {
        setInterval(this.isLoggedInFunc, 100);
    }
}
</script>

<style>
:root {
  --gxorange: #ff7f68;
  --gxyellow: #ffdb2b;
  --gxpink: #ef2871;
  --gxgreen: #71d861;
  --gxgreendark: #63bd55;
  --gxwhite: #effffb;
  --gxgreentransparent: rgb(113, 216, 97, 1)
}
nav {
    background-color: var(--gxgreentransparent);
    
}
.left-links {
    display: flex;
    margin: 50px;
}

.navbar-nav {
    display: flex;
    justify-content: space-between;
    width: 100%;
    font-size: 1.5rem;
}
.navbar {
    height: 5rem;
    text-shadow: 0 0 4px white;
}

.logonoff {
    align-items: right;
}

.nav-link {
    margin: 20px;
}
#fitPic {
    vertical-align: middle;
    border-style: none;
    height: 25%;
    width: 25%;
    margin: 40px;
}
</style>
