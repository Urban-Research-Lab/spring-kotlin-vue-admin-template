<template>
    <b-row>
        <b-col cols="6">
            <b-card
                    title="Create New Role"
                    class="mb-2"
            >
                <b-form @submit="onRoleCreate">
                    <b-card-body>
                        <b-row class="mb-2">
                            <b-col>
                                <label for="nameInput">{{$t("forms.name")}}</label>
                            </b-col>
                            <b-col>
                                <b-form-input
                                        id="nameInput"
                                        v-model="name"
                                        type="text"
                                        required="true"
                                        placeholder="Role Name"
                                ></b-form-input>

                            </b-col>
                        </b-row>

                        <b-row class="mb-2">
                            <b-col>
                                <label for="permissionsInput">{{$t("forms.permissions")}}</label>
                            </b-col>
                            <b-col>
                                <b-form-select v-model="permissions"
                                               :options="availablePermissions"
                                               multiple
                                               id="permissionsInput"
                                ></b-form-select>

                            </b-col>
                        </b-row>


                    </b-card-body>

                    <b-button type="submit" variant="primary">{{$t("role.create")}}</b-button>
                </b-form>
            </b-card>
        </b-col>
    </b-row>
</template>

<script>
    import RoleAPI from "../../mixins/RoleAPI";
    import toastMixin from "../../mixins/toast";

    export default {
        name: "CreateRole",
        mixins: [RoleAPI, toastMixin],
        data() {
            return {
                name: null,
                permissions: [],
                availablePermissions: []
            }
        },
        async mounted() {
            this.availablePermissions = (await this.getPermissions()).map(i => {return {text: i, value: i}})
        },
        methods: {
            async onRoleCreate(evt) {
                evt.preventDefault();
                let request = {
                    name: this.name,
                    permissions: this.permissions
                };

                let result = await this.createRole(request);
                if (result) {
                    await this.router.push('/roles')
                }
            }
        }
    }
</script>

<style scoped>

</style>