import Vue from 'vue'
import App from './App.vue'
import VueRouter from 'vue-router'
import BootstrapVue from 'bootstrap-vue'
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'
import Dashboard from "@/pages/Dashboard";
import SignIn from "@/pages/SignIn";

Vue.config.productionTip = false

Vue.use(BootstrapVue)
Vue.use(VueRouter)

const router = new VueRouter({
  routes:  [
    { path: '/', component: Dashboard },
    { path: '/signin', component: SignIn}
  ]
});

new Vue({
  router: router,
  render: h => h(App)
}).$mount('#app')