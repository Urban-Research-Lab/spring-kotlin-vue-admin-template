<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
    <div>
        <b-row>
            <b-col>
                <h1>Users</h1>
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
            }
        },
        async mounted() {
           this.currentPage = await this.countUsers()
        }
    }
</script>

<style scoped>

</style>