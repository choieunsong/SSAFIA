<template>
  <div class="wrap">
    <div>
      <img class="bg-image" src="@/assets/image/404.png" alt="404 image" />
    </div>
    <div class="my-4">
      <el-button type="primary" size="medium">
        <router-link to="/" class="text-white text-decoration-none">메인 화면으로</router-link>
      </el-button>
    </div>
  </div>
</template>

<script>
export default {
  name: "NotFound",
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
