import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '../views/Home.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/findcaps',
    name: 'FindCaps',
    component: () => import(/* webpackChunkName: "findcaps" */ '../views/FindCaps.vue')
  }
]

const router = new VueRouter({
  routes
})

export default router
