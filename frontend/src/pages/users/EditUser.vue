<template>
    <b-row>
        <b-col cols="6">
            <b-card
                    class="mb-2"
            >
                <b-card-title>
                    Edit User {{id}}
                </b-card-title>
                <b-form @submit="onUserUpdate">
                    <b-card-body>
                        <b-row class="mb-2">
                            <b-col>
                                {{ $t("forms.email")}}
                            </b-col>
                            <b-col>
                                <input type="text" readonly class="form-control-plaintext" v-bind:value="user.email">
                            </b-col>
                        </b-row>
                        <b-row class="mb-2">
                            <b-col>
                                <label for="newNameInput">{{ $t("forms.display_name")}}</label>
                            </b-col>
                            <b-col>
                                <b-form-input
                                        id="newNameInput"
                                        v-model="user.name"
                                        type="text"
                                        required
                                        placeholder="Enter user name"
                                ></b-form-input>

                            </b-col>
                        </b-row>
                        <b-row class="mb-2">
                            <b-col>
                                <label for="passwordInput">{{ $t("forms.roles")}}</label>
                            </b-col>
                            <b-col>
                                <b-form-select v-model="user.roleIds"
                                               :options="availableRoles"
                                               multiple
                                ></b-form-select>

                            </b-col>
                        </b-row>


                    </b-card-body>

                    <b-button type="submit" variant="primary">{{ $t("save_changes")}}</b-button>
                </b-form>
            </b-card>
        </b-col>
        <b-col>
            <b-form @submit="onPasswordUpdate">
                <b-card
                        title="Change password">
                    <b-card-body>
                        <b-row class="mb-1">
                            <b-col>
                                {{ $t("forms.new_password")}}
                            </b-col>
                            <b-col>
                                <b-form-input
                                        id="newPasswordInput"
                                        v-model="newPassword"
                                        type="password"
                                        required
                                        placeholder="Enter new password"
                                ></b-form-input>
                                <span v-if="!$v.newPassword.minLength" class="text-danger">{{ $t("forms.at_least_chars", {chars: 4})}}</span>
                            </b-col>
                        </b-row>
                        <b-row class="mb-1">
                            <b-col>
                                {{ $t("forms.confirm_password")}}
                            </b-col>
                            <b-col>
                                <b-form-input
                                        id="newPasswordConfirmation"
                                        v-model="newPasswordConfirmation"
                                        type="password"
                                        required
                                        placeholder="Repeat new password"
                                ></b-form-input>
                                <span v-if="!$v.newPasswordConfirmation.sameAsPassword" class="text-danger">
                                {{ $t("forms.passwords_do_not_match")}}</span>
                            </b-col>
                        </b-row>
                    </b-card-body>
                    <b-button type="submit" variant="primary">{{ $t("user.change_password")}}</b-button>
                </b-card>
            </b-form>
        </b-col>
    </b-row>
</template>

<script>
    import UserAPI from "../../mixins/UserAPI";
    import RoleAPI from "../../mixins/RoleAPI";
    import {minLength, required, sameAs} from 'vuelidate/lib/validators'

    export default {
        name: "EditUser",
        props: ['id'],
        mixins: [UserAPI, RoleAPI],
        data() {
            return {
                user: {
                    email: "",
                    name: "",
                    roles: [],
                    roleIds: []
                },
                newPassword: null,
                newPasswordConfirmation: null,
                availableRoles: []
            }
        },
        validations: {

            newPassword: {
                required,
                minLength: minLength(4)
            },
            newPasswordConfirmation: {
                required,
                minLength: minLength(4),
                sameAsPassword: sameAs('newPassword')
            },
        },
        async mounted() {
            this.availableRoles = (await this.getRoles()).map(i => {return {text: i.name, value: i.id}});
            this.user = await this.getUser(this.id);
            this.user.roleIds = this.user.roles.map((role) => {
                return role.id;
            })
        },
        methods: {
            async onUserUpdate(evt) {
                evt.preventDefault();
                let request = {
                    newName: this.user.name,
                    newRoles: this.user.roleIds
                };

                let result = await this.updateUser(this.id, request, false);
                if (result) {
                    this.$router.push('/users')
                }
            },

            async onPasswordUpdate(evt) {
                evt.preventDefault();
                this.$v.$touch();
                if (this.$v.newPasswordConfirmation.$invalid || this.$v.newPassword.$invalid) {
                    return;
                }
                this.updateUser(this.id, {'newPassword': this.$data.newPassword}, true);
            }
        }
    }
</script>

<style scoped>

</style>