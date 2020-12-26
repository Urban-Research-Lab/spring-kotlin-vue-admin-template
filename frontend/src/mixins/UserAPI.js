import Axios from '../axios'
import toastMixin from "./toast";

const UserAPI = {
    methods: {
        async registerUser(registerRequest) {
            let response = await Axios.post(process.env.VUE_APP_API_URL + `/api/v1/user/new`, registerRequest);
            if (response.data.code === 0) {
                this.successToast("User created")
                return true;
            }

            this.errorToast(`Failed to fetch user list: ` + response);
            return false
        },

        async registerUserWithResponse(registerRequest) {
            let response = await Axios.post(process.env.VUE_APP_API_URL + `/api/v1/user/new`, registerRequest);
            if (response.data.code === 0) {
                this.successToast("User created");
                return response.data;
            }
            return response.data;
        },

        async activateUser(token) {
            let response = Axios.post(process.env.VUE_APP_API_URL + `/api/v1/user/activate?token=` + token);
            return response;
        },

        async activateUserAdmin(email) {
            let response = await Axios.post(process.env.VUE_APP_API_URL + `/api/v1/user/admin/activate?email=` + email);
            if (response.data.code === 0) {
                return true;
            }
            return false;
        },

        async createPasswordRestoreToken(email) {
            let response = await Axios.post(process.env.VUE_APP_API_URL + `/api/v1/user/createPasswordRestoreToken?email=` + email);
            return response.data;
        },

        async restorePassword(token, newPassword) {
            let response = await Axios.post(process.env.VUE_APP_API_URL + `/api/v1/user/restorePassword?token=` + token + `&newPassword=` + newPassword);
            return response.data;
        },

        async updateUser(userId, updateRequest, updateCurrentUser = false) {
            let response = await Axios.post(process.env.VUE_APP_API_URL + `/api/v1/user/${userId}`, updateRequest);
            if (response.data.code === 0) {
                if (updateCurrentUser) {
                    this.$store.dispatch('updateUser', response.data.data);
                }
                this.successToast("User profile updated");
                return true;
            } else {
                this.errorToast(`Failed to update user data: ` + response.message);
                return false;
            }
        },

        async listUsers(pageNumber, pageSize) {
            let response = await Axios.get(process.env.VUE_APP_API_URL + `/api/v1/user/list?page=${pageNumber}&size=${pageSize}`);
            if (response.data.code === 0) {
                return response.data.objects;
            }

            this.errorToast(`Failed to fetch user list: ` + response);
            return []
        },

        async deleteUser(id) {
            let response = await Axios.delete(process.env.VUE_APP_API_URL + `/api/v1/user/${id}`);
            if (response.data.code === 0) {
                this.successToast("User deleted");
                return true;
            }

            this.errorToast(`Failed to delete user: ` + response);
            return false
        },

        async getUser(id) {
            let response = await Axios.get(process.env.VUE_APP_API_URL + `/api/v1/user/${id}`);
            if (response.data.code === 0) {
                return response.data.data;
            }

            this.errorToast(`Failed to fetch user: ` + response);
            return {}
        },

        async countUsers() {
            let response = await Axios.get(process.env.VUE_APP_API_URL + `/api/v1/user/count`);
            if (response.data.code === 0) {
                return response.data.number;
            }

            this.errorToast(`Failed to fetch user count: ` + response);
            return 0
        },

        async banUser(id, reason) {
            let response = await Axios.post(process.env.VUE_APP_API_URL + `/api/v1/user/${id}/ban?reason=${reason}`);
            if (response.data.code === 0) {
                this.successToast("User banned");
                return true;
            }

            this.errorToast(`Failed to ban user: ` + response);
            return false
        },

        async unbanUser(id) {
            let response = await Axios.post(process.env.VUE_APP_API_URL + `/api/v1/user/${id}/unban`);
            if (response.data.code === 0) {
                this.successToast("User unbanned");
                return true;
            }

            this.errorToast(`Failed to unban user: ` + response);
            return false
        },
    },
    mixins: [
        toastMixin
    ]
};

export default UserAPI