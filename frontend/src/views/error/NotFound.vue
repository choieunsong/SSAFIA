<template>
  <div class="wrap">
    <div>
      <img class="bg-image" src="@/assets/image/404.png" alt="404 image" />
    </div>
    <div class="mt-5">
      <el-button type="info" round @click="goHome">
        <span class="font-jua">홈으로</span>
      </el-button>
    </div>
  </div>
</template>

<script>
import { useRouter } from "vue-router";

export default {
  name: "NotFound",
  setup() {
    const router = useRouter();
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
.bg-image {
  margin-top: 5rem;
  width: 50rem;
}
</style>
