import { createApp } from 'vue';
import App from './App.vue'
import BootstrapVue from 'bootstrap-vue';
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'
import LoadScript, {loadScript} from 'vue-plugin-load-script';


import store from './storage.js';
import router from './router.js'
import {Vuelidate} from "vuelidate";
import { i18n } from './i18n';


const app = createApp(App);

app.config.productionTip = false;

app.use(i18n);
app.use(BootstrapVue);
app.use(LoadScript);
app.use(Vuelidate);
app.use(router);
app.use(store);


// initialize template, some jquery hacks

loadScript("/sbadmin/vendor/jquery/jquery.min.js").then(r => console.log("SUCESS"));
loadScript("/sbadmin/vendor/bootstrap/js/bootstrap.bundle.min.js").then(r => console.log("SUCESS"));
global.jQuery = require('jquery');
global.$ = global.jQuery;
window.$ = global.jQuery;
window.jQuery = global.jQuery;

loadScript("/sbadmin/js/sb-admin-2.min.js").then(r => console.log("SUCESS"));

import("../public/sbadmin/css/sb-admin-2.css");
import("../public/sbadmin/vendor/fontawesome-free/css/all.min.css");

// tmplate init end

app.mount('#app');
