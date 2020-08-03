import Axios from 'axios'
import toastMixin from "./toast";

const RoleAPI = {
    methods: {
        async getRoles() {
            let response = await Axios.get(process.env.VUE_APP_API_URL + `/api/v1/role/list`);
            if (response.data.code === 0) {
                return response.data.objects;
            }

            this.errorToast(`Failed to fetch roles: ` + response);
            return []
        },

        async getPermissions() {
            let response = await Axios.get(process.env.VUE_APP_API_URL + `/api/v1/role/permissions`);
            if (response.data.code === 0) {
                return response.data.objects;
            }

            this.errorToast(`Failed to fetch permissions: ` + response);
            return []
        },

        async createRole(request) {
            let response = await Axios.post(process.env.VUE_APP_API_URL + `/api/v1/role/create`, request);
            if (response.data.code === 0) {
                this.successToast("Role created");
                return true;
            }

            this.errorToast(`Failed to create role: ` + response);
            return false
        }
    },
    mixins: [toastMixin]
};

export default RoleAPI;