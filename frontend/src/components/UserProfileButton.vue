<template>
    <a class="nav-link dropdown-toggle" href="#" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
        <i class="fa fa-user mr-1"></i>
        <span class="mr-2 d-none d-lg-inline text-gray-600 small">{{userName}}</span>
    </a>
</template>

<script>
    export default {
        name: "UserProfileButton",
        computed: {
            userName() {
                return this.$store.getters.getUsername;
            }
        }
    }
</script>

<style scoped>

</style>