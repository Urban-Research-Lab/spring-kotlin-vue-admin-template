import VueRouter from 'vue-router'
import Dashboard from "./pages/Dashboard";
import SignIn from "./pages/SignIn";
import Landing from "./pages/Landing";
import AdminMain from "./pages/AdminMain";
import UserList from "./pages/users/UserList";
import Forbidden from "./pages/errors/Forbidden";
import Vue from 'vue'
import store from './storage.js';

Vue.use(VueRouter);

const router = new VueRouter({
    routes:  [
        {
            path: '/',
            component: AdminMain,
            children: [{component: Dashboard, path: ''}],
            meta: {
                requiresPermissions: []
            }
        },
        {
            path: '/users',
            component: AdminMain,
            children: [{component: UserList, path: ''}],
            meta: {
                requiresPermissions: ['MANAGE_USERS']
            }
        },
        {
            path: '/signin', component: SignIn
        },
        {
            path: '/landing', component: Landing
        },
        {
            path: '/403', component: Forbidden
        }
    ]
});

router.beforeEach((to, from, next) => {
    if(to.matched.some(record => record.meta.requiresPermissions !== undefined)) {
        console.log("State: " + store.state)
        console.log("Token: " + store.state.token)
        if (store.state.token === undefined) {
            next({
                path: '/signin',
                params: { nextUrl: to.fullPath }
            })
        } else {
            let authorities = store.getters.getAuthorities;
            if(to.matched.some(record => record.meta.requiresPermissions.every(permission => authorities.includes(permission)))) {
                next();
            }else {
                next({
                    path: '/403'
                })
            }
        }
    } else {
        next()
    }
});

export default router