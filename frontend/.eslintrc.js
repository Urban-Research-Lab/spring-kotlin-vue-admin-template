module.exports = {
  "root": true,
  extends: [
    'plugin:vue/vue3-recommended',],
  rules: {
    'vue/multi-word-component-names': ['error', {
      'ignores': ['Register', 'Forbidden', 'Landing', 'Dashboard']
    }]
  },
  parserOptions: {
    sourceType: "module",
    requireConfigFile: false,
    parser: "@babel/eslint-parser",
    ecmaVersion: "latest",
    ecmaFeatures: {
      "jsx": true,
      "modules": true
    },
    babelOptions: {
      babelrc: false,
      configFile: false,
    },
  },
}