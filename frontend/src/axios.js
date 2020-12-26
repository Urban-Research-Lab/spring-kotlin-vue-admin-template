import Axios from 'axios'
import LRUCache from 'lru-cache'
import { cacheAdapterEnhancer, throttleAdapterEnhancer } from 'axios-extensions';
import store from './storage'
import router from "./router";


let axiosInstance = (() =>
    Axios.create({
        baseURL: '/',
        headers: { 'Cache-Control': 'no-cache' },
        adapter: throttleAdapterEnhancer(cacheAdapterEnhancer(
            Axios.defaults.adapter,
            { enabledByDefault: false, defaultCache: new LRUCache({ maxAge: 1000*60, max: 100 })} //one-minute cache
        ))
    }))();

axiosInstance.interceptors.request.use(function (config) {
    config.headers.Authorization =  `Bearer ${store.getters.getToken}`;
    return config;
});

// check for 401 error code - this means our token has expired
axiosInstance.interceptors.response.use( (response) => {
    // Return a successful response back to the calling service
    console.log("Success");
    return response;
}, (error) => {
    console.log("Token expired, logging out");
    // Return any error which is not due to authentication back to the calling service
    if (error.response.status !== 401) {
        return new Promise((resolve, reject) => {
            reject(error);
        });
    }
    store.dispatch('logout');
    router.push({path: '/signin?logout=true'})

});

export default axiosInstance;