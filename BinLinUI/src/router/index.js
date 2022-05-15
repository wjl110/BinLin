import {createRouter, createWebHistory} from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/index',
  },
  {
    path: '/index',
    name: 'Index',
    component: () => import('/src/views/index/MoIndex.vue')
  },
  {
    path: '/search',
    name: 'Search',
    component: () => import('/src/views/search/MoIndex.vue')
  },
  {
    path: '/publish',
    name: 'Publish',
    component: () => import('/src/views/publish/MoIndex.vue')
  },
  {
    path: '/metaUniverse',
    name: 'MetaUniverse',
    component: () => import('/src/views/metaUniverse/MoIndex.vue')
  },
  {
    path: '/mine',
    name: 'Mine',
    component: () => import('/src/views/mine/MoIndex.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})
router.beforeEach((to, from) => {
  /*if (to.meta.requiresAuth && !sharedData.isLogin) {
    sharedMethod.login()
    return {
      path: '/',
      query: { redirect: to.fullPath },
    }
  }*/
})
export default router;
