<template>
    <b-row>
        <b-col cols="6">
            <b-card
                    title="User Profile"
                    class="mb-2"
            >
                <b-form @submit="onProfileUpdate">
                    <b-card-body>
                        <b-row>
                            <b-col>
                                Email
                            </b-col>
                            <b-col>
                                <input type="text" readonly class="form-control-plaintext" v-bind:value="email">
                            </b-col>
                        </b-row>
                        <b-row>
                            <b-col>
                                <label for="newNameInput">Display Name</label>
                            </b-col>
                            <b-col>
                                <b-form-input
                                        id="newNameInput"
                                        v-model="newName"
                                        type="text"
                                        required
                                        placeholder="Enter your name"
                                ></b-form-input>

                            </b-col>
                        </b-row>

                    </b-card-body>

                    <b-button type="submit" variant="primary">Update</b-button>
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
                            New Password
                        </b-col>
                        <b-col>
                            <b-form-input
                                    id="newPasswordInput"
                                    v-model="newPassword"
                                    type="password"
                                    required
                                    placeholder="Enter new password"
                            ></b-form-input>
                            <span v-if="!$v.newPassword.minLength" class="text-danger">At least 4 characters.</span>
                        </b-col>
                    </b-row>
                    <b-row class="mb-1">
                        <b-col>
                            Confirm Password
                        </b-col>
                        <b-col>
                            <b-form-input
                                    id="newPasswordConfirmation"
                                    v-model="passwordConfirmation"
                                    type="password"
                                    required
                                    placeholder="Repeat new password"
                            ></b-form-input>
                            <span v-if="!$v.passwordConfirmation.sameAsPassword" class="text-danger">
                                Passwords do not match.</span>
                        </b-col>
                    </b-row>
                </b-card-body>
                <b-button type="submit" variant="primary">Change Password</b-button>
            </b-card>
            </b-form>
        </b-col>
    </b-row>
</template>

<script>
    import {minLength, required, sameAs} from 'vuelidate/lib/validators'
    import toastMixin from "../../mixins/toast";
    import UserAPI from "../../mixins/UserAPI";

    export default {
        name: "ProfilePage",
        data() {
            return {
                newName: this.$store.state.user.name,
                newPassword: null,
                passwordConfirmation: null
            }
        },
        computed: {
            email() {
                return this.$store.state.user.email;
            },
            displayName() {
                return this.$store.state.user.name;
            }
        },
        mixins: [
            toastMixin,
            UserAPI
        ],
        methods: {
            onProfileUpdate(evt) {
                evt.preventDefault();
                this.updateUser(this.$store.getters.getUserId, {'newName': this.$data.newName}, true);
            },

            onPasswordUpdate(evt) {
                evt.preventDefault();
                this.$v.$touch();
                if (this.$v.passwordConfirmation.$invalid || this.$v.newPassword.$invalid) {
                    return;
                }
                this.updateUser(this.$store.getters.getUserId, {'newPassword': this.$data.newPassword}, true);
            }
        },
        validations: {
            newName: {
                required,
                minLength: minLength(1)
            },

            newPassword: {
                required,
                minLength: minLength(4)
            },
            passwordConfirmation: {
                required,
                minLength: minLength(4),
                sameAsPassword: sameAs('newPassword')
            },
        }
    }
</script>

<style scoped>

</style>