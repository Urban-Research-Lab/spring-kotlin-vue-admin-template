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
                                        v-model="updateUserForm.newName"
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
                    <b-row>
                        <b-col>
                            New Password
                        </b-col>
                        <b-col>
                            <b-form-input
                                    id="newPasswordInput"
                                    v-model="updatePasswordForm.newPassword"
                                    type="password"
                                    required
                                    placeholder="Enter new password"
                            ></b-form-input>
                        </b-col>
                    </b-row>
                    <b-row>
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
    export default {
        name: "ProfilePage",
        data() {
            return {
                updateUserForm: {
                    newName: this.displayName
                },
                updatePasswordForm: {
                    newPassword: null
                },
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
        methods: {
            onProfileUpdate(evt) {
                evt.preventDefault();
                alert(JSON.stringify(this.updateUserForm));
            },

            onPasswordUpdate(evt) {
                evt.preventDefault();
                alert(JSON.stringify(this.updatePasswordForm));
            }
        }
    }
</script>

<style scoped>

</style>