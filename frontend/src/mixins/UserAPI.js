import Axios from 'axios'
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
        }
    },
    mixins: [
        toastMixin
    ]
};

export default UserAPI