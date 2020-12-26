import Axios from '../axios'
import toastMixin from "./toast";

const AdminAPI = {
    methods: {
        async getLogs() {
            let response = await Axios.get(process.env.VUE_APP_API_URL + `/api/v1/logs`);
            if (response.data.code === 0) {
                return response.data.objects;
            }

            this.errorToast(`Failed to fetch logs: ` + response);
            return []
        }
    },
    mixins: [toastMixin]
};

export default AdminAPI;