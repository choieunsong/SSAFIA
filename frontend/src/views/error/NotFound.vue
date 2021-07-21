<template>
  <div class="wrap">
    <div>
      <img class="bg-image" src="@/assets/image/404.png" alt="404 image" />
    </div>
    <div class="my-4">
      <el-button type="primary" size="medium" @click="goHome">
        메인 화면으로
      </el-button>
    </div>
  </div>
</template>

<script>
import { router } from "@/router/router.js";

export default {
  name: "NotFound",
  setup() {
    const goHome = () => {
      router.push("home");
    };

    return {
      goHome,
    };
  },
};
</script>

<style>
.wrap {
  width: 100vw;
  height: 100vh;
  background-color: black;
  text-align: center;
}
.bg-image {
  width: 40%;
}
</style>
