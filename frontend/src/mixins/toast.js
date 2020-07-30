const toastMixin = {
    methods: {
        errorToast: function (message) {
            this.$bvToast.toast(message, {
                title: 'Error',
                autoHideDelay: 5000,
                variant: "danger"
            });

        },
        successToast: function (message) {
            this.$bvToast.toast(message, {
                title: 'Success',
                autoHideDelay: 5000,
                variant: "success"
            });

        }
    }
};

export default toastMixin;