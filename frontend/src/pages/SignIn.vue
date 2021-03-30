<template>
  <div class="d-flex flex-column">
    <div class="container">

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

          <div v-if="oauthEnabled">
            <hr>
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
  </div>
</template>

<script>
import Axios from "../axios.js";
import UserAPI from "../mixins/UserAPI";
import toastMixin from "@/mixins/toast";

export default {
  name: 'SignIn',
  data() {
    return {
      email: '',
      password: '',
      dismissSecs: 5,
      dismissCountDown: 0,
      alertMessage: 'Request error',
      needActivate: null
    }
  },
  mixins: [UserAPI, toastMixin],
  mounted() {
    if (this.$route.path === "/activateUser") {
      this.needActivate = this.$route.query.token;
    } else {
      if (this.$route.query.logout != null) {
        this.$data.alertMessage = this.$t("youHaveBeenLoggedOut");
        this.showAlert();
      } else if (this.$route.query.banned !== undefined) {
        this.$data.alertMessage = this.$t("youHaveBeenBanned", {reason:this.$route.query.reason});
        this.showAlert();
      } else if (this.$route.query.registered !== undefined) {
        this.successToast(this.$t("newUserRegistered"));
        this.email = this.$route.query.email;
      } else if (this.$route.query.resetPassword !== undefined) {
        this.successToast(this.$t("passwordWasReset"));
      }
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
      return process.env.VUE_APP_OAUTH_ENABLED === "true"
    },

    registrationEnabled() {
      return process.env.VUE_APP_REGISTRATION_ENABLED === "true"
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
              if (this.$route.query.nextUrl) {
                this.$router.push(this.$route.query.nextUrl)
              } else {
                this.$router.push('/')
              }

            } else if (response.data.code === 103) {
              this.$data.alertMessage = this.$t('signin.you_are_banned') + ": " + response.data.banInfo.reason;
              this.showAlert();
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
  },
  watch: {
    needActivate: async function (val) {
      let ra = await this.activateUser(val);
      if (ra.data.code === 0) {
        this.successToast(this.$t("userActivated"));
        this.email = ra.data.result;
      }
    }
  }

}
</script>

<style>
.login-form {
  margin-left: 38%;
  margin-top: 50px;
}
</style>