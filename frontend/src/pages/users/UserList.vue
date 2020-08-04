<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
    <div>
        <b-row>
            <b-col>
                <h1>{{ $t("user.list")}}</h1>
            </b-col>
            <b-col class="flex-row-reverse">
                <b-button variant="success" to="/create_user"><i class="fa fa-user-plus"></i>{{ $t("user.create")}}</b-button>
            </b-col>
        </b-row>
        <b-row>
            <b-col>
                <b-table id="users-table" striped hover
                         :items="itemProvider"
                         :per-page="15"
                         :fields="fields"
                         ref="table"
                >
                    <template v-slot:cell(registrationTimestamp)="data">
                        {{ new Date(data.value).toLocaleDateString() }}
                    </template>
                    <template v-slot:cell(roles)="data">
                        {{ data.value.map(role => role.name) }}
                    </template>

                    <template v-slot:cell(actions)="row">
                        <b-button size="sm" @click="editUserClicked(row.item)" class="btn-primary mr-1">
                            <i class="fa fa-edit"></i> {{ $t("edit")}}
                        </b-button>
                        <b-button size="sm" @click="deleteUserClicked(row.item)" class="btn-danger">
                            <i class="fa fa-trash"></i> {{ $t("delete")}}
                        </b-button>
                    </template>

                </b-table>
                <b-pagination
                        v-model="currentPage"
                        :total-rows="rows"
                        :per-page="15"
                        aria-controls="users-table"
                ></b-pagination>
            </b-col>
        </b-row>

    </div>
</template>

<script>
    import UserAPI from "../../mixins/UserAPI";

    export default {
        name: "UserList",
        data() {
            return {
                currentPage: 1,
                rows: 0,
                fields: ["id", "email", "name", "roles", "registrationTimestamp", "actions"]
            }
        },
        mixins: [UserAPI],
        methods: {
            async itemProvider(ctx) {
                return this.listUsers(ctx.currentPage - 1, ctx.perPage)
            },
            // called by parent component
            async updateData() {
                this.currentPage = await this.countUsers()
                this.$refs.table.refresh();
            },
            editUserClicked(item) {
                this.$router.push("/users/" + item.id)
            },

            async deleteUserClicked(item) {
                if (confirm("Do you really want to delete this item?")) {
                    await this.deleteUser(item.id);
                    this.updateData();
                }
            }
        },
        async mounted() {
           this.updateData()
        }
    }
</script>

<style scoped>

</style>