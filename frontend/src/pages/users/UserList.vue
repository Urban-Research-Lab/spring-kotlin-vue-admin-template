<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
  <div>
    <b-row>
      <b-col>
        <h1>{{ $t("user.list")}}</h1>
      </b-col>
      <b-col class="flex-row-reverse">
        <b-button variant="success" to="/create_user"><i class="fa fa-user-plus"></i>{{ $t("user.create")}}</b-button>
      </b-col>
    </b-row>
    <b-row>
      <b-col>
        <b-table id="users-table" striped hover
                 :items="itemProvider"
                 :per-page="15"
                 :fields="fields"
                 ref="table"
        >
          <template v-slot:cell(registrationTimestamp)="data">
            {{ new Date(data.value).toLocaleDateString() }}
          </template>
          <template v-slot:cell(roles)="data">
            {{ data.value.map(role => role.name) }}
          </template>

          <template v-slot:cell(actions)="row">
            <b-button size="sm" @click="editUserClicked(row.item)" class="btn-primary mr-1">
              <i class="fa fa-edit"></i> {{ $t("edit")}}
            </b-button>
            <b-button size="sm" @click="activateUserClicked(row.item)" class="btn-info mr-1" v-if="row.item.status === 'REGISTERED'">
              <i class="fa fa-check"></i> {{ $t("user.activate")}}
            </b-button>
            <span v-if="hasAuthority('BAN_USERS')">
                            <b-button size="sm" @click="banUserClicked(row.item)" class="btn-warning mr-1" v-if="row.item.status !== 'BANNED'">
                                <i class="fa fa-stop-circle"></i> {{ $t("user.ban")}}
                            </b-button>
                            <b-button size="sm" @click="unbanUserClicked(row.item)" class="btn-success mr-1" v-if="row.item.status === 'BANNED'">
                                <i class="fa fa-play-circle"></i> {{ $t("user.unban")}}
                            </b-button>
                        </span>
            <b-button size="sm" @click="deleteUserClicked(row.item)" class="btn-danger">
              <i class="fa fa-trash"></i> {{ $t("delete")}}
            </b-button>
          </template>

        </b-table>
        <b-pagination
            v-model="currentPage"
            :total-rows="rows"
            :per-page="15"
            aria-controls="users-table"
        ></b-pagination>
      </b-col>
    </b-row>

    <b-modal id="modal-1" :title="$t('user.ban')" v-model="showBanModal" @ok="doBanUser" :ok-disabled="!reasonEntered">
      Please enter reason for banning {{userToBan.email}}
      <b-form-textarea v-model="banReason" class="mt-3">

      </b-form-textarea>
    </b-modal>

  </div>
</template>

<script>
import UserAPI from "../../mixins/UserAPI";

export default {
  name: "UserList",
  data() {
    return {
      currentPage: 1,
      rows: 0,
      fields: ["id", "email", "name", "roles", "registrationTimestamp", "actions"],
      showBanModal: false,
      userToBan: {},
      banReason: ""
    }
  },
  mixins: [UserAPI],
  methods: {
    async itemProvider(ctx) {
      return this.listUsers(ctx.currentPage - 1, ctx.perPage)
    },
    // called by parent component
    async updateData() {
      this.currentPage = await this.countUsers()
      this.$refs.table.refresh();
    },
    editUserClicked(item) {
      this.$router.push("/users/" + item.id)
    },

    async deleteUserClicked(item) {
      if (confirm("Do you really want to delete this item?")) {
        await this.deleteUser(item.id);
        this.updateData();
      }
    },
    banUserClicked(item) {
      this.userToBan = item;
      this.showBanModal = true;
    },
    async activateUserClicked(item) {
      var activated = await this.activateUserAdmin(item.email);
      if (activated) {
        this.successToast("User activated");
        this.updateData();
      } else {
        this.errorToast("User cannot be activated");
      }
    },
    async doBanUser() {
      await this.banUser(this.userToBan.id, this.banReason);
      this.updateData();
    },
    async unbanUserClicked(item) {
      if (confirm("Do you really want to unban this user?")) {
        await this.unbanUser(item.id);
        this.updateData();
      }
    },
    hasAuthority(name) {
      return this.$store.getters.hasAuthority(name)
    }
  },
  computed: {
    reasonEntered() {
      return this.banReason.length > 0
    }
  },
  async mounted() {
    this.updateData()
  }
}
</script>

<style scoped>

</style>