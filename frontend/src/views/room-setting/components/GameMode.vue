<template>
  <div class="mt-5">
    <div>
      <el-radio v-model="accessType" label="1" border class="radio-btn">Private</el-radio>
      <el-radio v-model="accessType" label="2" border class="radio-btn" disabled>Public</el-radio>
    </div>
    <div class="my-5">
      <el-radio v-model="roomType" label="1" border class="radio-btn">Basic</el-radio>
      <el-radio v-model="roomType" label="2" border class="radio-btn" disabled>Custom</el-radio>
    </div>
    <el-button type="success" plain>방 설정</el-button>
  </div>
</template>

<script>
import { defineComponent, ref } from "vue";
import { useRouter } from "vue-router"

export default defineComponent({
  name: 'GameMode',
  setup() {
    const router = useRouter()
    const redirectToNickname = function() {
      console.log('active')
      router.push('nickname')
    }
    return {
      accessType: ref("1"),
      roomType: ref("1"),
      redirectToNickname
    };
  },
});
</script>

<style>
.radio-btn {
  width: 10rem;
}
</style>
