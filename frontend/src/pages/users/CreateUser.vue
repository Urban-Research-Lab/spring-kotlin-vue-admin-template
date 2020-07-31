<template>
    <b-row>
        <b-col cols="6">
            <b-card
                    title="Create New User"
                    class="mb-2"
            >
                <b-form @submit="onUserCreate">
                    <b-card-body>
                        <b-row class="mb-2">
                            <b-col>
                                <label for="emailInput">Email<sup>*</sup></label>
                            </b-col>
                            <b-col>
                                <b-form-input
                                        id="emailInput"
                                        v-model="email"
                                        type="email"
                                        required
                                        placeholder="User email"
                                ></b-form-input>

                            </b-col>
                        </b-row>
                        <b-row class="mb-2">
                            <b-col>
                                <label for="newNameInput">Display Name</label>
                            </b-col>
                            <b-col>
                                <b-form-input
                                        id="newNameInput"
                                        v-model="name"
                                        type="text"
                                        placeholder="User Name"
                                ></b-form-input>

                            </b-col>
                        </b-row>

                        <b-row class="mb-2">
                            <b-col>
                                <label for="passwordInput">Password<sup>*</sup></label>
                            </b-col>
                            <b-col>
                                <b-form-input
                                        id="newNameInput"
                                        v-model="password"
                                        type="password"
                                        required
                                        placeholder="User Password"
                                ></b-form-input>

                            </b-col>
                        </b-row>

                        <b-row class="mb-2">
                            <b-col>
                                <label for="passwordInput">Roles</label>
                            </b-col>
                            <b-col>
                                <b-form-select v-model="roles"
                                               :options="availableRoles"
                                               multiple
                                ></b-form-select>

                            </b-col>
                        </b-row>


                    </b-card-body>

                    <b-button type="submit" variant="primary">Create User</b-button>
                </b-form>
            </b-card>
        </b-col>
    </b-row>
</template>

<script>
    import UserAPI from "../../mixins/UserAPI";
    import RoleAPI from "../../mixins/RoleAPI";
    import toastMixin from "../../mixins/toast";

    export default {
        name: "CreateUser",
        mixins: [UserAPI, RoleAPI, toastMixin],
        data() {
            return {
                email: null,
                name: null,
                password: null,
                roles: [],
                availableRoles: []
            }
        },
        async mounted() {
           this.availableRoles = (await this.getRoles()).map(i => {return {text: i.name, value: i.id}})
        },
        methods: {
            onUserCreate(evt) {
                evt.preventDefault();
                let request = {
                    name: this.name,
                    email: this.email,
                    password: this.password,
                    roles: this.roles
                };

                let result = this.registerUser(request);
                if (result) {
                    this.$router.push('/users')
                }
            }
        }
    }
</script>

<style scoped>

</style>