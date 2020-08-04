<template>
    <div class="container">
        <b-row class="mb-5">
            <b-col>
                <h1 class="text-center">{{ $t("landing.title")}}</h1>
            </b-col>
        </b-row>
        <b-row class="mb-5">
            <b-col>
                <h3>{{ $t("landing.feature1.title")}}</h3>
                <p>{{ $t("landing.feature1.text")}}</p>
            </b-col>
            <b-col>
                <h3>
                    {{ $t("landing.feature2.title")}}
                </h3>
                <p>
                    {{ $t("landing.feature2.text")}}
                </p>
            </b-col>
            <b-col>
                <h3>
                    {{ $t("landing.feature3.title")}}
                </h3>
                <p>
                    {{ $t("landing.feature3.text")}}
                </p>
            </b-col>
        </b-row>
        <b-row class="mb-5">
            <b-col>
               <b-button variant="success" href="/" style="width:100%">{{ $t("start")}}</b-button>
            </b-col>
        </b-row>
    </div>
</template>

<script>
    export default {
        name: "Landing"
    }
</script>

<style scoped>

</style>