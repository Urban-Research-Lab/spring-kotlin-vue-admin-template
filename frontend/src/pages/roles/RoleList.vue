<template>
    <div>
        <b-row>
            <b-col>
                <h1>{{$t("role.list")}}</h1>
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
                         :fields="fields"
                >

                    <template v-slot:cell(actions)="row">
                        <b-button size="sm" @click="editRoleClicked(row.item)" class="btn-primary mr-1">
                            <i class="fa fa-edit"></i> {{$t("edit")}}
                        </b-button>
                        <b-button size="sm" @click="deleteRoleClicked(row.item)" class="btn-danger">
                            <i class="fa fa-trash"></i> {{$t("delete")}}
                        </b-button>
                    </template>
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
        data() {
            return {
                fields: ["id", "name", "permissions", "actions"]
            }
        },
        methods: {
            async itemProvider() {
                return this.getRoles()
            },
            // called by parent component
            async updateData() {
                this.$refs.table.refresh();
            },
            editRoleClicked(item) {
                this.$router.push("/roles/" + item.id)
            },

            async deleteRoleClicked(item) {
                if (confirm("Do you really want to delete this item?")) {
                    await this.deleteRole(item.id);
                    this.updateData();
                }
            }
        }
    }
</script>

<style scoped>

</style>