<template>
  <div>Oauth</div>
</template>
<script>
import { ElNotification } from "element-plus";
import { useStore } from "vuex";
import { API_BASE_URL } from "@/constant/index";
import axios from "axios";

export default {
  name: "OauthHandler",
  mounted() {
    const store = useStore();
    const url = this.$route.fullPath;
    const slice = url.split("token=");
    const token = slice[1];
    // 분기처리
    // token 존재 => /profile
    // error 존재 => /
    if (token) {
      store.dispatch("token/setToken", token);
      store.dispatch("token/setLogin");

      axios({
        method: "get",
        url: API_BASE_URL + "/api/user/profile",
        headers: store.getters["token/getHeaders"],
      })
        .then(({ data }) => {
          return {
            name: data.data.name,
            imageUrl: data.data.imageUrl,
            email: data.data.email,
          };
        })
        .then((profile) => {
          store.dispatch("token/setProfile", profile);
          ElNotification({
            title: "Login Success!",
            message: `환영합니다! ${profile.name}님!`,
            type: "success",
            duration: "2500",
            customClass: "font-jua",
          });
          this.$router.push({ name: "Home" });
        })
        .catch((err) => {
          console.log("err", err);
        });
    } else {
      this.$router.push("/");
    }
  },
};
</script>
