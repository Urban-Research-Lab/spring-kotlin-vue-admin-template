import VueRouter from 'vue-router'
import Dashboard from "./pages/Dashboard";
import SignIn from "./pages/SignIn";
import Landing from "./pages/Landing";
import AdminMain from "./pages/AdminMain";
import UserList from "./pages/users/UserList";
import Forbidden from "./pages/errors/Forbidden";
import Vue from 'vue'
import store from './storage.js';
import ProfilePage from "./pages/users/ProfilePage";
import NotFound from "./pages/errors/NotFound";
import ServerLogs from "./pages/admin/ServerLogs";
import CreateUser from "./pages/users/CreateUser";
import RoleList from "./pages/roles/RoleList";
import CreateRole from "./pages/roles/CreateRole";
import EditRole from "./pages/roles/EditRole";
import EditUser from "./pages/users/EditUser";
import Register from "@/pages/Register";

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
            path: '/profile',
            component: AdminMain,
            children: [{component: ProfilePage, path: ''}],
            meta: {
                requiresPermissions: []
            }
        },
        {
            path: '/users/:id',
            component: AdminMain,
            children: [{component: EditUser, path: '', props: true}],
            meta: {
                requiresPermissions: ['MANAGE_USERS']
            }
        }, {
            path: '/users',
            component: AdminMain,
            children: [{component: UserList, path: ''}],
            meta: {
                requiresPermissions: ['MANAGE_USERS']
            }
        },{
            path: '/create_user',
            component: AdminMain,
            children: [{component: CreateUser, path: ''}],
            meta: {
                requiresPermissions: ['MANAGE_USERS']
            }
        },
        {
            path: '/roles/:id',
            component: AdminMain,
            children: [
                {
                    component: EditRole, path: '', props: true
                }],
            meta: {
                requiresPermissions: ['MANAGE_ROLES']
            }
        },
        {
            path: '/roles',
            component: AdminMain,
            children: [
                {
                    component: RoleList, path: ''
                }],
            meta: {
                requiresPermissions: ['MANAGE_ROLES']
            }
        },
        {
            path: '/create_role',
            component: AdminMain,
            children: [{component: CreateRole, path: ''}],
            meta: {
                requiresPermissions: ['MANAGE_ROLES']
            }
        },
        {
          path: "/server_logs",
            component: AdminMain,
            children: [{component: ServerLogs, path: ''}],
            meta: {
                requiresPermissions: ['SERVER_ADMIN']
            }
        },
        {
            path: '/signin', component: SignIn
        },
        {
            path: "/register", component: Register
        },
        {
            path: '/landing', component: Landing
        },
        {
            path: '/403', component: Forbidden
        },
        {
            path: "/404", component: NotFound
        },
        {
            path: '*',
            redirect: '/404'
        }
    ]
});

router.beforeEach((to, from, next) => {
    if(to.matched.some(record => record.meta.requiresPermissions !== undefined)) {
        if (store.state.token === undefined || store.state.token === "") {
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