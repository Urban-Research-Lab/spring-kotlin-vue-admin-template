<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
    <div>
        <b-row>
            <b-col>
                <h1>Users</h1>
            </b-col>
            <b-col class="flex-row-reverse">
                <b-button variant="success" to="/create_user"><i class="fa fa-user-plus"></i> Create User</b-button>
            </b-col>
        </b-row>
        <b-row>
            <b-col>
                <b-table id="users-table" striped hover
                         :items="itemProvider"
                         :per-page="15"
                         :fields="fields"
                >
                    <template v-slot:cell(registrationTimestamp)="data">
                        {{ new Date(data.value).toLocaleDateString() }}
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
                fields: ["id", "email", "name", "roles", "registrationTimestamp"]
            }
        },
        mixins: [UserAPI],
        methods: {
            async itemProvider(ctx) {
                return this.listUsers(ctx.currentPage - 1, ctx.perPage)
            },
            async updateData() {
                this.currentPage = await this.countUsers()
            }
        },
        async mounted() {
           this.updateData()
        }
    }
</script>

<style scoped>

</style>