<template>
    <ul class="navbar-nav bg-gradient-primary sidebar sidebar-dark accordion" id="accordionSidebar">

        <!-- Sidebar - Brand -->
        <a class="sidebar-brand d-flex align-items-center justify-content-center" href="index.html">
            <div class="sidebar-brand-icon rotate-n-15">
                <i class="fas fa-laugh-wink"></i>
            </div>
            <div class="sidebar-brand-text mx-3">Admin Template</div>
        </a>

        <!-- Divider -->
        <hr class="sidebar-divider my-0">

        <!-- Nav Item - Dashboard -->
        <li class="nav-item">
            <a class="nav-link" href="index.html">
                <i class="fas fa-fw fa-tachometer-alt"></i>
                <span>Dashboard</span></a>
        </li>

        <!-- Divider -->
        <hr class="sidebar-divider">


        <!-- Nav Item - Pages Collapse Menu -->
        <li class="nav-item" v-if="hasAuthority('MANAGE_USERS')">
            <a class="nav-link collapsed" href="#" data-toggle="collapse" data-target="#collapseTwo" aria-expanded="true"
               aria-controls="collapseTwo">
                <i class="fas fa-fw fa-users"></i>
                <span>Users</span>
            </a>
            <div id="collapseTwo" class="collapse" aria-labelledby="headingTwo" data-parent="#accordionSidebar">
                <div class="bg-white py-2 collapse-inner rounded">
                    <b-link class="collapse-item" to="/users">List Users</b-link>
                    <a class="collapse-item" href="#">Create User</a>
                </div>
            </div>
        </li>


        <!-- Divider -->
        <hr class="sidebar-divider d-none d-md-block">

        <li class="nav-item" v-if="hasAuthority('SERVER_ADMIN')">
            <a class="nav-link collapsed" href="#" data-toggle="collapse" data-target="#serverAdminCollapse"
               aria-expanded="true" aria-controls="serverAdminCollapse">
                <i class="fas fa-fw fa-cogs"></i>
                <span>Server Admin</span>
            </a>
            <div id="serverAdminCollapse" class="collapse" aria-labelledby="serverAdminCollapseHeading" data-parent="#accordionSidebar">
                <div class="bg-white py-2 collapse-inner rounded">
                    <b-link class="collapse-item" to="/server_logs">View Logs</b-link>
                </div>
            </div>
        </li>

        <hr class="sidebar-divider d-none d-md-block">


        <!-- Sidebar Toggler (Sidebar) -->
        <div class="text-center d-none d-md-inline">
            <button class="rounded-circle border-0" id="sidebarToggle"></button>
        </div>

    </ul>

</template>

<script>
    export default {
        name: "SidePanel",
        methods: {
            hasAuthority(name) {
                return this.$store.getters.hasAuthority(name)
            }
        }
    }
</script>

<style scoped>

</style>