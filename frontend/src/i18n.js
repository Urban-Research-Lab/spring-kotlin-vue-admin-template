//i18n-setup.js
// copied from documentation https://kazupon.github.io/vue-i18n/guide/lazy-loading.html
import Vue from 'vue'
import VueI18n from 'vue-i18n'
import axios from 'axios'
import en from './lang/en'

Vue.use(VueI18n);

let messages = {
    en
};

export const i18n = new VueI18n({
    locale: 'en', // set locale
    fallbackLocale: 'en',
    messages // set locale messages
});

const loadedLanguages = ['en']; // our default language that is preloaded

function setI18nLanguage (lang) {
    i18n.locale = lang;
    axios.defaults.headers.common['Accept-Language'] = lang
    document.querySelector('html').setAttribute('lang', lang)
    return lang
}

export function loadLanguageAsync(lang) {
    // If the same language
    if (i18n.locale === lang) {
        return Promise.resolve(setI18nLanguage(lang))
    }

    // If the language was already loaded
    if (loadedLanguages.includes(lang)) {
        return Promise.resolve(setI18nLanguage(lang))
    }

    // If the language hasn't been loaded yet
    return import(/* webpackChunkName: "lang-[request]" */ `@/lang/${lang}.js`).then(
        messages => {
            i18n.setLocaleMessage(lang, messages.default)
            loadedLanguages.push(lang)
            return setI18nLanguage(lang)
        }
    )
}