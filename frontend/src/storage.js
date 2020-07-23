import Vue from 'vue';
import Vuex from 'vuex';

Vue.use(Vuex);

const state = {
    token: localStorage.getItem('user-token') || '',
    user: JSON.parse(localStorage.getItem('user') || '{}'),
};

const getters = {
    isAuthenticated: state => {
        return state.token != null && state.token != '';
    },
    getUsername: state => {
        return state.user.username !== null ? state.user.username : state.user.email;
    },
    getEmail: state => {
        return state.user.email;
    },
    getAuthorities: state => {
        return state.user.authorities;
    },
    getToken: state => {
        return state.token;
    }
};

const mutations = {
    auth_login: (state, user) => {
        localStorage.setItem('user-token', user.token);
        localStorage.setItem('user', JSON.stringify(user));
        state.token = user.token;
        state.user = user;
    },
    auth_logout: () => {
        state.token = '';
        state.user= {};
        localStorage.removeItem('user-token');
        localStorage.removeItem('user');

    }
};

const actions = {
    login: (context, user) => {
        context.commit('auth_login', user)
    },
    logout: (context) => {
        context.commit('auth_logout');
    }
};

export const store = new Vuex.Store({
    state,
    getters,
    mutations,
    actions
});