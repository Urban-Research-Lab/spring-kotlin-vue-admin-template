<template>
    <div>
        <div id="wrapper">
            <SidePanel></SidePanel>

            <div id="content-wrapper" class="d-flex flex-column">

                <div id="content">
                    <TopPanel></TopPanel>

                    <div class="container-fluid">
                        <router-view ref="child"></router-view>
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
        components: {PageFooter, LogoutModal, SidePanel, TopPanel},

        beforeRouteEnter (to, from, next) {
            next(vm => {
                // hack: use this hardcoded method to force refresh chld component data when its shown again
                // since vue-router does not recreate components but reuses old ones
                if (vm.$refs.child.updateData !== undefined) {
                    vm.$refs.child.updateData()
                }
            })
        }


    }
</script>

<style scoped>

</style>