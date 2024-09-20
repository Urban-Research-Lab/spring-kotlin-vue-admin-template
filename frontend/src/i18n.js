import { createI18n } from 'vue-i18n'
import axios from 'axios'
import en from './lang/en'

const messages = {
    en
};

export const i18n = createI18n({
    legacy: false,
    locale: 'en',
    fallbackLocale: 'en',
    messages
});

const loadedLanguages = ['en'];

function setI18nLanguage(lang) {
    i18n.global.locale.value = lang;
    axios.defaults.headers.common['Accept-Language'] = lang;
    document.querySelector('html').setAttribute('lang', lang);
    return lang;
}

export function loadLanguageAsync(lang) {
    if (i18n.global.locale.value === lang) {
        return Promise.resolve(setI18nLanguage(lang));
    }

    if (loadedLanguages.includes(lang)) {
        return Promise.resolve(setI18nLanguage(lang));
    }

    return import(/* webpackChunkName: "lang-[request]" */ `@/lang/${lang}.js`).then(
        messages => {
            i18n.global.setLocaleMessage(lang, messages.default);
            loadedLanguages.push(lang);
            return setI18nLanguage(lang);
        }
    );
}
