import Vue from 'vue'
import Router from 'vue-router'
import auth from './auth'
import Home from './views/Home.vue'
import Login from './views/Login.vue'
import Register from './views/Register.vue'
import Rsvp from './views/Rsvp.vue'
import EventDetails from './views/EventDetails.vue'
import NewEvent from './views/NewEvent.vue'
import SendInvite from './views/SendInvite.vue'
import CreateMenu from './views/CreateMenu.vue'
import { RSA_SSLV23_PADDING } from 'constants';

Vue.use(Router)

/**
 * The Vue Router is used to "direct" the browser to render a specific view component
 * inside of App.vue depending on the URL.
 *
 * It also is used to detect whether or not a route requires the user to have first authenticated.
 * If the user has not yet authenticated (and needs to) they are redirected to /login
 * If they have (or don't need to) they're allowed to go about their way.
 */

const router = new Router({
  mode: 'history',
  base: process.env.BASE_URL,
  routes: [
    {
      path: '/',
      name: 'home',
      component: Home,
      meta: {
        requiresAuth: true
      }
    },
    {
      path: "/login",
      name: "login",
      component: Login,
      meta: {
        requiresAuth: false
      }
    },
    {
      path: "/register",
      name: "register",
      component: Register,
      meta: {
        requiresAuth: false
      }
    },
    {
      path: "/:eventId/rsvp",
      name: "rsvp",
      component: Rsvp,
      meta: {
        requiresAuth: false
      }
    },
    {
      path: "/newevent",
      name: "newevent",
      component: NewEvent,
      meta: {
        requiresAuth: true
      },
       
    },
    {
      path: "/:eventId/sendinvite",
      name: "sendinvite",
      component: SendInvite,
      meta: {
        requiresAuth: true
      }
       
    },
    {
      path: "/:eventId/createmenu",
      name: "createmenu",
      component: CreateMenu,
      meta: {
        requiresAuth: true
      }
       
      },
    {
      path: "/:eventId/eventDetails",
      name: "eventDetails",
      component: EventDetails,
      meta: {
        requiresAuth: true
      } 
    }
  ]
})

router.beforeEach((to, from, next) => {
  // Determine if the route requires Authentication
  const requiresAuth = to.matched.some(x => x.meta.requiresAuth);
  const user = auth.getUser();

  // If it does and they are not logged in, send the user to "/login"
  if (requiresAuth && !user) {
    next("/login");
  } else {
    // Else let them go to their next destination
    next();
  }
});

export default router;
