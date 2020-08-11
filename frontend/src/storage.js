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
        return state.token != null && state.token !== '';
    },
    getUsername: state => {
        return state.user.name !== undefined ? state.user.name : state.user.email;
    },
    getEmail: state => {
        return state.user.email;
    },
    getUserId: state => {
        return state.user.id;
    },
    getAuthorities: state => {
        return state.user.authorities;
    },
    hasAuthority: state => authority => {
        return state.user.authorities && state.user.authorities.includes(authority);
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
    },
    update_user: (state, user) => {
        localStorage.setItem('user', JSON.stringify(user));
        state.user = user;
    }
};

const actions = {
    login: (context, {user, token}) => {
        context.commit('auth_login', {user, token})
    },
    logout: (context) => {
        context.commit('auth_logout');
    },
    updateUser: (context, user) => {
        context.commit('update_user', user)
    }
};


const store = new Vuex.Store({
    state,
    getters,
    mutations,
    actions
});

export default store;