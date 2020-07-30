<template>
    <div>
        <div class="text-center">
            <div class="error mx-auto" data-text="403">403</div>
            <p class="lead text-gray-800 mb-5">Forbidden</p>
            <p class="text-gray-500 mb-0">It looks like you do not have access to this content</p>
            <b-link to="/">← Back to Dashboard</b-link>
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