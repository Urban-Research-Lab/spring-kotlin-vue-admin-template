import Vue from 'vue';
import Vuex from 'vuex';

Vue.use(Vuex);

function loadUser() {
    try {
        return JSON.parse(localStorage.getItem('user') || '{}')
    } catch (e) {
        return {}
    }
}

const state = {
    token: localStorage.getItem('user-token') || '',
    user: loadUser(),
};

const getters = {
    isAuthenticated: state => {
        return state.token != null && state.token != '';
    },
    getUsername: state => {
        return state.user.username !== undefined ? state.user.username : state.user.email;
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
    auth_login: (state, {user, token}) => {
        localStorage.setItem('user-token', token);
        localStorage.setItem('user', JSON.stringify(user));
        state.token = token;
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
    login: (context, {user, token}) => {
        context.commit('auth_login', {user, token})
    },
    logout: (context) => {
        context.commit('auth_logout');
    }
};


const store = new Vuex.Store({
    state,
    getters,
    mutations,
    actions
});

export default store;