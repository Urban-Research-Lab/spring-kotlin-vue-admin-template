<template>
    <div>
        <b-row>
            <b-col>
                <h1>Roles</h1>
            </b-col>
            <b-col class="flex-row-reverse">
                <b-button variant="success" to="/create_role"><i class="fa fa-user-plus"></i> Create Role</b-button>
            </b-col>
        </b-row>
        <b-row>
            <b-col>
                <b-table id="roles-table" striped hover
                         :items="itemProvider"
                         ref="table"
                >
                </b-table>
            </b-col>
        </b-row>

    </div>
</template>

<script>
    import RoleAPI from "../../mixins/RoleAPI";

    export default {
        name: "RoleList",
        mixins: [RoleAPI],
        methods: {
            async itemProvider() {
                return this.getRoles()
            },
            // called by parent component
            async updateData() {
                this.$refs.table.refresh();
            }
        }
    }
</script>

<style scoped>

</style>