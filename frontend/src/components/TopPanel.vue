<template>
    <!-- Topbar -->
    <nav class="navbar navbar-expand navbar-light bg-white topbar mb-4 static-top shadow">

        <!-- Sidebar Toggle (Topbar) -->
        <button id="sidebarToggleTop" class="btn btn-link d-md-none rounded-circle mr-3">
            <i class="fa fa-bars"></i>
        </button>

        <!-- Topbar Navbar -->
        <ul class="navbar-nav ml-auto">


            <div class="topbar-divider d-none d-sm-block"></div>

            <!-- Nav Item - User Information -->
            <li class="nav-item dropdown no-arrow">
                <UserProfileButton id="userDropdown"></UserProfileButton>
                <!-- Dropdown - User Information -->
                <div class="dropdown-menu dropdown-menu-right shadow animated--grow-in" aria-labelledby="userDropdown">
                    <b-link class="dropdown-item" to="/profile">
                        <i class="fas fa-user fa-sm fa-fw mr-2 text-gray-400"></i>
                        Profile
                    </b-link>
                    <div class="dropdown-divider"></div>
                    <LogoutButton></LogoutButton>
                </div>
            </li>

        </ul>

    </nav>
    <!-- End of Topbar -->

</template>

<script>
    import UserProfileButton from "./UserProfileButton";
    import LogoutButton from "./LogoutButton";

    export default {
        name: "TopPanel",
        components: {LogoutButton, UserProfileButton}
    }
</script>

<style scoped>

</style>