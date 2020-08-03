<template>
    <h1>Edit user {{id}}</h1>
</template>

<script>
    import UserAPI from "../../mixins/UserAPI";
    import RoleAPI from "../../mixins/RoleAPI";

    export default {
        name: "EditUser",
        props: ['id'],
        mixins: [UserAPI, RoleAPI],
    }
</script>

<style scoped>

</style>