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
import { useStore } from "vuex";
import { ElNotification } from "element-plus";
import { GOOGLE_AUTH_URL } from "@/constant/index";

export default {
  name: "Login",
  setup() {
    const store = useStore();
    const isLogin = computed(() => store.getters["token/getIsLogin"])
    const sendGoogleUrl = () => {
      location.href = GOOGLE_AUTH_URL;
    };
    const handleLogout = () => {
      ElNotification({
        title: "Good Bye!",
        message: "로그아웃 되었습니다. 감사합니다!",
        type: "info",
        duration: "2500",
        customClass: "font-jua",
      });
      store.dispatch("token/setToken", "");
      store.dispatch("token/setLogout");
      store.dispatch("token/setProfile", { name: "", imageUrl: "", email: "" });
    };
    return {
      isLogin,
      sendGoogleUrl,
      handleLogout,
    };
  },
};
</script>

<style></style>
