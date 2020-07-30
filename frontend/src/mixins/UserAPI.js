import Axios from 'axios'
import toastMixin from "./toast";

const UserAPI = {
    methods: {
        updateUser(userId, updateRequest) {
            Axios.post(process.env.VUE_APP_API_URL + `/api/v1/user/${userId}`,
                updateRequest)
                .then(response => {
                    if (response.data.code === 0) {
                        this.$store.dispatch('updateUser', response.data.data);
                        this.successToast("User profile updated")
                    } else {
                        this.errorToast(`Failed to update user data: ` + response.data.message)
                    }
                }, error => {
                    this.errorToast(`Failed to update user data: ` + error);
                    console.log(error);
                })

        }
    },
    mixins: [
        toastMixin
    ]
};

export default UserAPI