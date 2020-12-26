<template>
  <div class="d-flex flex-column">
    <div class="container">
      Please wait...
    </div>
  </div>
</template>

<script>
import UserAPI from "../mixins/UserAPI";

export default {
  name: "OAuthSignIn",
  mixins: [UserAPI],
  async mounted() {
    let  token = this.$route.query.jwt_token;
    this.$store.commit("update_token", token);

    let user = await this.getUser("me");
    console.log(`Logging in user ${user} with token ${token} `);
    this.$store.dispatch('login', {
      'token': token,
      'user': user
    });

    if (this.$route.params.nextUrl != null) {
      this.$router.push(this.$route.params.nextUrl)
    } else {
      this.$router.push('/')
    }

  }

}
</script>

<style scoped>

</style>