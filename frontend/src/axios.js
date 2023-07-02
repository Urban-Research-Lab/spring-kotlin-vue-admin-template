import store from './storage'
import router from "./router";

const Axios = require('axios');
const api = Axios.default.create({
    baseURL: '/',
    headers: {'Cache-Control': 'no-cache' },
    cache: {
        maxAge: 1000 * 60 * 100, exclude: { query: false }
    }
})

const addToken = (config) => {
        config.headers.Authorization = `Bearer ${store.getters.getToken}`;
}
api.interceptors.request.use(
    (config) => {
        addToken(config)
        return config
    },
    (error) => {
        return Promise.reject(error)
    }
)

// check for 401 error code - this means our token has expired
api.interceptors.response.use( (response) => {
    // Return a successful response back to the calling service
    console.log("Success");
    return response;
}, (error) => {
    console.log("Token expired, logging out");
    console.log(error);
    // Return any error which is not due to authentication back to the calling service
    if (error.response.status !== 401) {
        return new Promise((resolve, reject) => {
            reject(error);
        });
    }
    store.dispatch('logout').then(r => console.log("SUCCESS"));
    router.push({path: '/signin?logout=true'}).then(r =>console.log("SUCCESS") )

});

export default api;

