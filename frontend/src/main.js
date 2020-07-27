import Vue from 'vue'
import App from './App.vue'
import BootstrapVue from 'bootstrap-vue'
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'
import LoadScript from 'vue-plugin-load-script';

import store from './storage.js';
import router from './router.js'

Vue.config.productionTip = false;

Vue.use(BootstrapVue);
Vue.use(LoadScript);

// initialize template, some jquery hacks

Vue.loadScript("/sbadmin/vendor/jquery/jquery.min.js");
Vue.loadScript("/sbadmin/vendor/bootstrap/js/bootstrap.bundle.min.js");
global.jQuery = require('jquery');
global.$ = global.jQuery;
window.$ = global.jQuery;
window.jQuery = global.jQuery;

Vue.loadScript("/sbadmin/js/sb-admin-2.min.js");

import("../public/sbadmin/css/sb-admin-2.css");
import("../public/sbadmin/vendor/fontawesome-free/css/all.min.css");

// tmplate init end

new Vue({
  router: router,
  store: store,
  render: h => h(App)
}).$mount('#app');