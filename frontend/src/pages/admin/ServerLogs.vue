<template>
    <b-table id="users-table" striped hover
             :items="itemProvider"
             :fields="fields"
             :per-page="200"
             :tbody-tr-class="rowClass"
    >
        <template v-slot:cell(timestamp)="data">
            {{ new Date(data.value).toISOString()}}
        </template>

    </b-table>
</template>

<script>
    import AdminAPI from "../../mixins/AdminAPI";

    export default {
        name: "ServerLogs",
        mixins: [AdminAPI],
        methods: {
            async itemProvider() {
                return this.getLogs()
            },
            rowClass(item, type) {
                if (!item || type !== 'row') return
                switch (item.level) {
                    case 'WARN':
                        return "table-warning";
                    case 'ERROR':
                        return "table-error";
                    default:
                        return "table-default";

                }
            }
        },
        data() {
            return {
                fields: ["level", "timestamp", "message"]
            }
        },
    }
</script>

<style scoped>

</style>