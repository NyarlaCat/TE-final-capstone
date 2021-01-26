<template>
<div id="grad">
  <div id="register" class="text-center">
    <div class = "container col-3 container-fluid">
    <form class="form-register" @submit.prevent="register">
      <h1 class="h3 mb-3 font-weight-normal">Create Account</h1>
      <div class="alert alert-danger" role="alert" v-if="registrationErrors">
        There were problems registering this user.
      </div>
      
      <div class = "form-group">
      <label for="username" class="sr-only ">Username</label>
      <input
        type= "text"
        id="username"
        class="form-control"
        placeholder="Username"
        v-model="user.username"
        required
        autofocus
      />
      <div class="error" v-if="errors.username">{{errors.username}}</div>
      </div>
      <div class = "form-group">

      <label for="email" class="sr-only">Email</label>
      <input
        type="text"
        id="email"
        class="form-control"
        placeholder="Email"
        v-model="user.email"
        required
        autofocus
      />
      <div class="error" v-if="errors.email">{{errors.email}}</div>
      </div>
 <div class = "form-group">
      <label for="password" class="sr-only">Password</label>
      <input
        type="password"
        id="password"
        class="form-control"
        placeholder="Password"
        v-model="user.password"
        required
      />
      </div>

      <div class = "form-group">
      <input
        type="password"
        id="confirmPassword"
        class="form-control"
        placeholder="Confirm Password"
        v-model="user.confirmPassword"
        required
      />
      <div class="error" v-if="errors.password">{{errors.password}}</div>
      </div>
      <div class = "form-group">
      <button class="btn btn-lg btn-primary btn-block" type="submit">
        Create Account
      </button>
      <small>
        <router-link :to="{ name: 'login' }">
            Have an account?
        </router-link>
      </small>
      </div>
    </form>
    </div>
  </div>
  <logo></logo>
</div>
</template>

<script>
import Logo from '../components/Logo.vue';
  
export default {
  name: 'register',
  components: {
    Logo
  },
  data() {
    return {
      user: {
        username: '',
        password: '',
        confirmPassword: '',
        email: '',
        role: 'user',
      },
      errors: {

      },
      registrationErrors: false,
    };
  },
  methods: {
    register() {
      fetch(`${process.env.VUE_APP_REMOTE_API}/api/user/register`, {
        method: 'POST',
        headers: {
          Accept: 'application/json',
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(this.user),
      })
        .then((response) => {
          if (response.ok) {
            this.$router.push({ path: '/login', query: { registration: 'success' } });
          } else {
            this.registrationErrors = true;
            return response.json();
          }
        }).then((response)=>{
          if( this.registrationErrors && response.errors ) {
            this.errors = {};
            response.errors.forEach((current)=>{
              switch( current.field ) {
                case "username":
                  this.errors.username = current.error;
                  break;
                case "email":
                  this.errors.email = current.error;
                  break;
                case "passwordMatching":
                  this.errors.password = current.error;
                  break;
              }
            })
          }
        })
        .then((err) => console.error());
    },
  },
};
</script>

<style scoped>
  .error {
    color: red;
    font-weight: bold;
  }
#grad {
  height: 100%;
  background-color:  var(--gxyellow); /* For browsers that do not support gradients */
  background-image: linear-gradient(to right, var(--gxpink), var(--gxorange), var(--gxyellow), var(--gxyellow), var(--gxyellow), var(--gxorange), var(--gxpink)); /* Standard syntax (must be last) */
}
</style>
