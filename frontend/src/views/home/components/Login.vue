<template>
  <div>
    <el-button
      type="primary"
      size="medium"
      v-if="!isLogin"
      @click="sendGoogleUrl"
      round
      class="home-btn"
    >
      <img
        class="google-icon"
        src="https://upload.wikimedia.org/wikipedia/commons/5/53/Google_%22G%22_Logo.svg"
      />
      <span class="font-jua">Google로 로그인</span>
    </el-button>
    <el-button
      type="danger"
      size="medium"
      v-else
      round
      @click="handleLogout"
      class="home-btn"
    >
      <span class="font-jua">로그아웃</span>
    </el-button>
  </div>
</template>

<script>
import { computed } from "vue";
import { onMounted } from "@vue/runtime-core";
import { useStore } from "vuex";
import { reactive } from "@vue/reactivity";
import { ElNotification } from "element-plus";
import { GOOGLE_AUTH_URL, API_BASE_URL} from "@/constant/index";
import axios from "axios";

export default {
  name: "Login",
  setup() {
    const store = useStore();
    const state = reactive({
      googleUrl:
        GOOGLE_AUTH_URL,
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
      ElNotification({
        title: "Good Bye!",
        message: "로그아웃 되었습니다. 감사합니다!",
        type: "info",
        duration: "2500",
      });
      localStorage.removeItem("token");
      store.dispatch("token/setIsLogin", false);
      store.dispatch("token/setToken", "");
    };

    onMounted(() => {
      if (isLogin.value) {
        const Token = store.getters["token/getToken"];
        console.log("token", Token);
        
        const url =  API_BASE_URL + "/api/user/profile";
        console.log("url", url);
        axios({
          method: "get",
          url: url,
          headers: store.getters["token/getHeaders"],
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

<style></style>
