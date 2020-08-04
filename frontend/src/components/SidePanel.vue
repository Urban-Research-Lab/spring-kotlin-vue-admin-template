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
                <span>{{ $t("menu.dashboard")}}</span></a>
        </li>

        <!-- Divider -->
        <hr class="sidebar-divider">


        <!-- Nav Item - Pages Collapse Menu -->
        <li class="nav-item" v-if="hasAuthority('MANAGE_USERS')">
            <a class="nav-link collapsed" href="#" data-toggle="collapse" data-target="#collapseTwo" aria-expanded="true"
               aria-controls="collapseTwo">
                <i class="fas fa-fw fa-users"></i>
                <span>{{ $t("menu.users")}}</span>
            </a>
            <div id="collapseTwo" class="collapse" aria-labelledby="headingTwo" data-parent="#accordionSidebar">
                <div class="bg-white py-2 collapse-inner rounded">
                    <b-link class="collapse-item" to="/users">{{ $t("menu.users.list")}}</b-link>
                    <b-link class="collapse-item" to="/create_user">{{ $t("menu.users.create")}}</b-link>
                </div>
            </div>
        </li>

        <li class="nav-item" v-if="hasAuthority('MANAGE_ROLES')">
            <a class="nav-link collapsed" href="#" data-toggle="collapse" data-target="#collapseRoles" aria-expanded="true"
               aria-controls="collapseRoles">
                <i class="fas fa-fw fa-user-tag"></i>
                <span>{{ $t("menu.roles")}}</span>
            </a>
            <div id="collapseRoles" class="collapse" aria-labelledby="headingTwo" data-parent="#accordionSidebar">
                <div class="bg-white py-2 collapse-inner rounded">
                    <b-link class="collapse-item" to="/roles">{{ $t("menu.roles.list")}}</b-link>
                    <b-link class="collapse-item" to="/create_role">{{ $t("menu.roles.create")}}</b-link>
                </div>
            </div>
        </li>


        <!-- Divider -->
        <hr class="sidebar-divider d-none d-md-block">

        <li class="nav-item" v-if="hasAuthority('SERVER_ADMIN')">
            <a class="nav-link collapsed" href="#" data-toggle="collapse" data-target="#serverAdminCollapse"
               aria-expanded="true" aria-controls="serverAdminCollapse">
                <i class="fas fa-fw fa-cogs"></i>
                <span>{{ $t("menu.admin")}}</span>
            </a>
            <div id="serverAdminCollapse" class="collapse" aria-labelledby="serverAdminCollapseHeading" data-parent="#accordionSidebar">
                <div class="bg-white py-2 collapse-inner rounded">
                    <b-link class="collapse-item" to="/server_logs">{{ $t("menu.admin.logs")}}</b-link>
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