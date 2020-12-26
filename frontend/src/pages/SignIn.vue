<template>
    <div div="signin">
        <div class="login-form">
            <b-card
                    title="Login"
                    tag="article"
                    style="max-width: 20rem;"
                    class="mb-2"
            >
                <div>
                    <b-alert
                            :show="dismissCountDown"
                            dismissible
                            variant="danger"
                            @dismissed="dismissCountDown=0"
                            @dismiss-count-down="countDownChanged"
                    > {{ alertMessage }}
                    </b-alert>
                </div>
                <div>
                    <b-form-input type="email" placeholder="Email" v-model="email" />
                    <div class="mt-2"></div>

                    <b-form-input type="password" placeholder="Password" v-model="password" />
                    <div class="mt-2"></div>
                </div>
                <div class="mb-2 text-center" v-if="registrationEnabled">{{$t('login.dontHave')}} <b-link href="#/register">{{$t('register')}}</b-link></div>

                <b-button v-on:click="login" variant="primary" class="w-100">Login</b-button>

                <hr>

                <div v-if="oauthEnabled">
                    <a :href="googleLoginUrl" class="btn btn-google btn-user btn-block">
                        <i class="fab fa-google fa-fw"></i> {{$t('login.with_google')}}
                    </a>
                    <a :href="facebookLoginUrl" class="btn btn-facebook btn-user btn-block">
                        <i class="fab fa-facebook fa-fw"></i> {{$t('login.with_facebook')}}
                    </a>
                </div>

            </b-card>
        </div>
    </div>
</template>

<script>
    import Axios from 'axios'

    export default {
        name: 'SignIn',
        data() {
            return {
                email: '',
                password: '',
                dismissSecs: 5,
                dismissCountDown: 0,
                alertMessage: 'Request error',
            }
        },
        mounted() {
            if (this.$route.query.logout != null) {
                this.$data.alertMessage = "You have been logged out";
                this.showAlert();
            }
        },
        computed: {
            googleLoginUrl() {
                return process.env.VUE_APP_API_URL + `/oauth2/authorization/google`
            },

            facebookLoginUrl() {
                return process.env.VUE_APP_API_URL + `/oauth2/authorization/facebook`
            },

            oauthEnabled() {
                return process.env.VUE_APP_OAUTH_ENABLED
            },

            registrationEnabled() {
                return process.env.VUE_APP_REGISTRATION_ENABLED
            }
        },
        methods: {
            login() {
                Axios.post(process.env.VUE_APP_API_URL + `/api/v1/auth/login`, {'email': this.$data.email, 'password': this.$data.password})
                    .then(response => {
                        if (response.data.code === 0) {
                            this.$store.dispatch('login', {
                                'token': response.data.token,
                                'user': response.data.user
                            });
                            if (this.$route.params.nextUrl != null) {
                                this.$router.push(this.$route.params.nextUrl)
                            } else {
                                this.$router.push('/')
                            }
                        } else {
                            this.$data.alertMessage = response.data.message;
                            this.showAlert();
                        }
                    }, error => {
                        this.$data.alertMessage = (error.response.data.message.length < 150) ? error.response.data.message : 'Request error. Please, report this error website owners';
                        console.log(error);
                        this.showAlert();
                    })
                    .catch(e => {
                        console.log(e);
                        this.showAlert();
                    })
            },
            countDownChanged(dismissCountDown) {
                this.dismissCountDown = dismissCountDown
            },
            showAlert() {
                this.dismissCountDown = this.dismissSecs
            },
        }
    }
</script>

<style>
    .login-form {
        margin-left: 38%;
        margin-top: 50px;
    }
</style>