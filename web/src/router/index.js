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
    path: '/findmod/caps',
    name: 'ModCaps',
    component: () => import(/* webpackChunkName: "findmod" */ '../views/ModCaps.vue')
  },
  {
    path: '/findmod/registries',
    name: 'ModRegistries',
    component: () => import(/* webpackChunkName: "findmod" */ '../views/ModRegistries.vue')
  },
  {
    path: '/findpack/registries',
    name: 'PackRegistries',
    component: () => import(/* webpackChunkName: "findpack" */ '../views/PackRegistries.vue')
  }
]

const router = new VueRouter({routes})

export default router
