import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '../views/Home.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    component: Home
  },
  {
    path: '/lookup/mod',
    component: () => import(/* webpackChunkName: "lookupmod" */ '../views/ModLookup.vue')
  },
  {
    path: '/lookup/pack',
    component: () => import(/* webpackChunkName: "lookuppack" */ '../views/PackLookup.vue')
  },
  {
    path: '/addoninfo',
    component: () => import(/* webpackChunkName: "addoninfo" */ '../views/AddonInfo.vue')
  }
]

const router = new VueRouter({routes})

export default router
