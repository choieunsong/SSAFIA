<template>
  <div class="wrap">
    <div class="py-4">
      <img src="@/assets/image/logo.png" alt="logo image" />
    </div>
    <div class="mt-5 mb-4">
      <el-button
        type="success"
        size="medium"
        round
        @click="moveToNickname"
        class="home-btn"
      >
        <span class="font-jua">방 만들기</span>
      </el-button>
    </div>
    <Login />
  </div>
</template>

<script>
import "./home.css";
import Login from "@/views/home/components/Login.vue";
import { router } from "@/router/router.js";
import { ElMessage } from "element-plus";
import {useStore} from 'vuex';
import { computed } from "vue";
// import { onMounted } from '@vue/runtime-core';

export default {
  name: "Home",
  components: {
    Login,
  },
  // created() {
  //   const token = localStorage.getItem("token");
  //   if (token) {
  //     this.$store.dispatch("token/setIsLogin", true);
  //     this.$store.dispatch("token/setToken", token);
  //   } else {
  //     this.$store.dispatch("token/setIsLogin", false);
  //   }
  // },
  setup() {
    const store = useStore();
    const isLogin = computed(() => store.getters["token/getIsLogin"])
    const moveToNickname = () => {
      if (isLogin.value) {
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

<style></style>
