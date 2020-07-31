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
        }
    },
    mixins: [toastMixin]
};

export default RoleAPI;