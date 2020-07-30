<template>
    <div>
        <div id="wrapper">
            <SidePanel></SidePanel>

            <div id="content-wrapper" class="d-flex flex-column">

                <div id="content">
                    <TopPanel></TopPanel>

                    <div class="container-fluid">
                        <router-view></router-view>
                    </div>

                </div>

                <PageFooter></PageFooter>
            </div>
        </div>
        <LogoutModal></LogoutModal>
    </div>
</template>

<script>
    import TopPanel from '../components/TopPanel'
    import SidePanel from "../components/SidePanel";
    import LogoutModal from "../components/modals/LogoutModal";
    import PageFooter from "../components/PageFooter";

    export default {
        name: "AdminMain",
        components: {PageFooter, LogoutModal, SidePanel, TopPanel}
    }
</script>

<style scoped>

</style>