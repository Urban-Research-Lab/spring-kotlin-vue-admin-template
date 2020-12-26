<template>
    <div class="d-flex flex-column">
        <div class="container">
            <div class="login-form">
                <b-card
                        :title="$t('register')"
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
                        <b-form-input class="mt-2" type="email" autocomplete="off" :placeholder="$t('forms.email')" v-model="email" />
                        <b-form-input class="mt-2" type="password" autocomplete="off" :placeholder="$t('forms.password')" v-model="password" />
                        <b-form-input class="mt-2" type="password" autocomplete="off" :placeholder="$t('forms.confirm_password')" v-model="passwordConfirm" />
                        <b-form-input class="mt-2 mb-2" type="text" autocomplete="off" :placeholder="$t('forms.name')" v-model="name" />
                    </div>
                    <div class="mb-2 text-center">{{$t('register.alreadyHave')}} <b-link href="#/signin">{{$t('login')}}</b-link></div>
                    <b-button v-on:click="register" variant="primary" style="width: 100%">{{$t('register')}}</b-button>
                </b-card>
            </div>
        </div>
    </div>
</template>
<script>
    import UserAPI from "../mixins/UserAPI";

    export default {
        name: 'Register',
        mixins: [UserAPI],
        data() {
            return {
                email: '',
                password: '',
                passwordConfirm: '',
                name: '',
                roles: [],
                dismissSecs: 5,
                dismissCountDown: 0,
                alertMessage: 'Request error'
            }
        },
        methods: {
            async register() {
                if (this.email == null || this.email.length == 0) {
                    this.alertMessage = this.$t("forms.requiredMessage", {field:this.$t("forms.email")});
                    this.showAlert();
                    return;
                }
                if (this.password == null || this.password.length == 0) {
                    this.alertMessage = this.$t("forms.requiredMessage", {field:this.$t("forms.password")});
                    this.showAlert();
                    return;
                }
                if (this.passwordConfirm == null || this.passwordConfirm.length == 0 || this.password != this.passwordConfirm) {
                    this.alertMessage = this.$t("forms.passwords_do_not_match");
                    this.showAlert();
                    return;
                }
                let request = {
                    name: this.name,
                    email: this.email,
                    password: this.password,
                    roles: this.roles
                };
                let result = await this.registerUserWithResponse(request);
                if (result.code === 0) {
                    this.$store.dispatch('logout');
                    this.$router.push({path: '/signin?registered=true&email=' + this.email})
                } else {
                    this.alertMessage = result.message;
                    this.showAlert();
                }
            },
            countDownChanged(dismissCountDown) {
                this.dismissCountDown = dismissCountDown
            },
            showAlert() {
                this.dismissCountDown = this.dismissSecs
            }
        }
    }
</script>