import Vue from 'vue'
import App from './App.vue'
import VueRouter from 'vue-router'
import BootstrapVue from 'bootstrap-vue'
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'
import Dashboard from "@/pages/Dashboard";
import SignIn from "@/pages/SignIn";
import Landing from "@/pages/Landing";
import LoadScript from 'vue-plugin-load-script';
import AdminMain from "./pages/AdminMain";
import UserList from "./pages/users/UserList";

Vue.config.productionTip = false

Vue.use(BootstrapVue)
Vue.use(VueRouter)
Vue.use(LoadScript);

// initialize template, some jquery hacks

Vue.loadScript("/sbadmin/vendor/jquery/jquery.min.js")
Vue.loadScript("/sbadmin/vendor/bootstrap/js/bootstrap.bundle.min.js")
global.jQuery = require('jquery');
global.$ = global.jQuery;
window.$ = global.jQuery;
window.jQuery = global.jQuery;

Vue.loadScript("/sbadmin/js/sb-admin-2.min.js")

import("../public/sbadmin/css/sb-admin-2.css")
import("../public/sbadmin/vendor/fontawesome-free/css/all.min.css")

// tmplate init end


const router = new VueRouter({
  routes:  [
    { path: '/', component: AdminMain, children: [{component: Dashboard, path: ''}] },
    { path: '/users', component: AdminMain, children: [{component: UserList, path: ''}] },
    { path: '/signin', component: SignIn},
    { path: '/landing', component: Landing}
  ]
});

new Vue({
  router: router,
  render: h => h(App)
}).$mount('#app')