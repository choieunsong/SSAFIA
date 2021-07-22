<template>
  <div>
    <el-button
      type="primary"
      size="medium"
      v-if="!isLogin"
      @click="sendGoogleUrl"
    >
      <img
        class="google-icon"
        src="https://upload.wikimedia.org/wikipedia/commons/5/53/Google_%22G%22_Logo.svg"
      />
      <span>Google로 로그인</span>
    </el-button>
    <el-button
      type="primary"
      size="medium"
      v-else
      @click="handleLogout"
    >
      로그 아웃
    </el-button>
  </div>
</template>

<script>
import { computed } from "vue";
import { onMounted } from "@vue/runtime-core";
import { useStore } from "vuex";
import { reactive } from "@vue/reactivity";
import axios from "axios";

export default {
  name: "Login",
  setup() {
    const store = useStore();
    const state = reactive({
      googleUrl:
        "http://localhost:8080/oauth2/authorize/google?redirect_uri=http://localhost:8081/oauth2/redirect",
      user: {
        name: "",
        imageUrl: "",
        email: "",
      },
    });

    const isLogin = computed(() => store.getters["token/getIsLogin"]);
    const sendGoogleUrl = () => {
      console.log("sendGoogleUrl");
      location.href = state.googleUrl;
    };
    const handleLogout = () => {
      console.log("logout");
      localStorage.removeItem("token");
      store.dispatch("token/setIsLogin", false);
    };

    onMounted(() => {
      if (isLogin.value) {
        const Token = store.getters["token/getToken"];
        console.log("token", Token);
        const headers = {
          "Content-Type": "application/json",
          Authorization: "Bearer " + Token,
        };
        const url = "http://localhost:8080/profile";
        console.log("url", url);

        axios({
          method: "get",
          url: url,
          headers: headers,
        })
          .then(({ data }) => {
            console.log("axios get success", data);
            state.user.imageUrl = data.imageUrl;
            state.user.name = data.name;
            state.user.email = data.email;
          })
          .catch((err) => {
            console.log("err", err);
          });
      }
    });

    return {
      state,
      isLogin,
      sendGoogleUrl,
      handleLogout,
    };
  },
};
</script>

<style>
.google-icon {
  width: 20px;
  height: 20px;
  margin-right: 1rem;
}
</style>
