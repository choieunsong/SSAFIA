<template>
  <div class="wrap">
    <div>
      <img src="@/assets/image/logo.png" alt="logo image">
    </div>
    <div class="my-4">
      <el-button
        type="primary"
        size="medium"
        round
        @click="moveToNickname"
        class="font-jua"
      >
        방 만들기
      </el-button>
    </div>
    <Login />
  </div>
</template>

<script>
import "@/common/css/common.css";
import Login from "@/views/home/components/Login.vue";
import { router } from "@/router/router.js";
import { ElMessage } from "element-plus";
// import { onMounted } from '@vue/runtime-core'
// import {useStore} from 'vuex';

export default {
  name: "Home",
  components: {
    Login,
  },
  created() {
    const Token = localStorage.getItem("token");
    console.log("Token", Token);
    if (Token) {
      this.$store.dispatch("token/setIsLogin", true);
      this.$store.dispatch("token/setToken", Token);
    } else {
      this.$store.dispatch("token/setIsLogin", false);
    }
  },
  setup() {
    const moveToNickname = () => {
      const token = localStorage.getItem("token");
      if (token) {
        router.push("room-setting");
      } else {
        ElMessage.error("로그인이 필요합니다!");
      }
    };

    return {
      moveToNickname,
    };
    // const store = useStore();

    // onMounted(() => {
    //     const Token = localStorage.getItem("token");
    //     console.log('Token',Token);
    //     if(Token){
    //         store.dispatch('token/setIsLogin', true);
    //         store.dispatch('token/setToken', Token);
    //     }else{
    //         store.dispatch('token/setIsLogin', false);
    //     }
    // })
  },
};
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}
.wrap {
  width: 100vw;
  height: 100vh;
  background-image: url("../../assets/image/background.png");
  background-size: cover;
}
</style>
