<template>
  <div class="wrap">
    <div>
      <img
        id="nickname-logo"
        src="@/assets/image/logo-name.png"
        alt="logo name"
        @click="goHome"
      />
    </div>

    <div id="nickname-form" v-show="isShow">
      <el-input
        placeholder="게임에서 사용할 닉네임을 입력하세요."
        class="font-jua mb-4"
        v-model="state.nickname"
        maxlength="15"
        show-word-limit
        clearable
        @keyup.enter="redirectToGame"
      ></el-input>
      <el-button type="success" size="small" @click="redirectToGame">
        <span class="font-jua">입장</span>
      </el-button>
    </div>

    <!-- <el-alert
      class="nickname-alert"
      type="error"
      :title="state.errorMessage"
    ></el-alert> -->
  </div>
</template>

<script>
import "./nickname.css";
import { onMounted, reactive, ref } from "vue";
import { useStore } from "vuex";
import { useRouter } from "vue-router";
import axios from "axios";

export default {
  name: "Nickname",
  setup() {
    const store = useStore();
    const router = useRouter();
    let isShow = ref(false);
    const state = reactive({
      nickname: "",
      isError: false,
      errorMessage: "정원이 초과되었습니다",
      URL: "",
    });
    onMounted(() => {
      setTimeout(() => {
        isShow.value = true;
      }, 1000);
    });

    const redirectToGame = () => {
      // nickname validation 필요함
      router.push({ name: "Game", params: {} });
    };
    const goHome = () => {
      router.push("home");
    };
    return {
      isShow,
      state,
      redirectToGame,
      goHome,
    };
  },
};
</script>

<style></style>
