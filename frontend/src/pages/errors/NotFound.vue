<template>
    <div class="text-center">
        <div class="error mx-auto" data-text="404">404</div>
        <p class="lead text-gray-800 mb-5">{{$t("errors.not_found")}}</p>
        <p class="text-gray-500 mb-0">{{$t("errors.not_found.text")}}</p>
        <b-link to="/">← {{$t("forms.name")}}</b-link>
    </div>
</template>

<script>
    export default {
        name: "NotFound"
    }
</script>

<style scoped>

</style>