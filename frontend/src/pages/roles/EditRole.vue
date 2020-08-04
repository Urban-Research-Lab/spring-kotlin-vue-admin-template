<template>
    <b-row>
        <b-col cols="6">
            <b-card
                    class="mb-2"
            >
                <b-card-title>
                    {{$t("role.edit")}} {{id}}
                </b-card-title>
                <b-form @submit="onRoleUpdate">
                    <b-card-body>
                        <b-row class="mb-2">
                            <b-col>
                                <label for="nameInput">{{$t("forms.name")}}</label>
                            </b-col>
                            <b-col>
                                <input type="text" id="nameInput" readonly class="form-control-plaintext" v-bind:value="role.name">

                            </b-col>
                        </b-row>

                        <b-row class="mb-2">
                            <b-col>
                                <label for="permissionsInput">{{$t("forms.permissions")}}</label>
                            </b-col>
                            <b-col>
                                <b-form-select v-model="role.permissions"
                                               :options="availablePermissions"
                                               multiple
                                               id="permissionsInput"
                                ></b-form-select>

                            </b-col>
                        </b-row>


                    </b-card-body>

                    <b-button type="submit" variant="primary">{{$t("save_changes")}}</b-button>
                </b-form>
            </b-card>
        </b-col>
    </b-row>
</template>

<script>
    import RoleAPI from "../../mixins/RoleAPI";

    export default {
        name: "EditRole",
        props: ['id'],
        mixins: [RoleAPI],
        data() {
            return {
                role: {
                    permissions: [],
                    name: ""
                },
                availablePermissions: []
            }
        },
        async mounted() {
            this.availablePermissions = (await this.getPermissions()).map(i => {
                return {text: i, value: i}
            });
            this.role = await this.getRole(this.id);
        },
        methods: {
            async onRoleUpdate(evt) {
                evt.preventDefault();
                let request = {
                    permissions: this.role.permissions
                };

                let result = await this.updateRole(this.id, request);
                if (result) {
                    this.$router.push('/roles')
                }
            }
        }
    }
</script>

<style scoped>

</style>