<template>
    <a class="dropdown-item" href="#" v-on:click="logout">
        <i class="fas fa-sign-out-alt fa-sm fa-fw mr-2 text-gray-400"></i>
        Logout
    </a>
</template>

<script>
    export default {
        name: "LogoutButton",
        methods: {
            logout() {
                this.$store.dispatch('logout');
                this.$router.push({path: '/signin?logout=true'})
            }
        }
    }
</script>

<style scoped>

</style>