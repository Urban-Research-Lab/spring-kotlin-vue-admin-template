<template>
    <div>
        <div class="text-center">
            <div class="error mx-auto" data-text="403">403</div>
            <p class="lead text-gray-800 mb-5">Forbidden</p>
            <p class="text-gray-500 mb-0">{{$t("errors.forbidden.text")}}</p>
            <b-link to="/">← {{$t("back_to_dashboard")}}</b-link>
        </div>
    </div>
</template>

<script>
    export default {
        name: "Forbidden"
    }
</script>

<style scoped>

</style>