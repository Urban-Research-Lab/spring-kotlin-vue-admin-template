<template>
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
                ></b-table>
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
                rows: 0
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