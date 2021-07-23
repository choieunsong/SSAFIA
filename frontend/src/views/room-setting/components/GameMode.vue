<template>
  <div class="mt-5 bg-white my-5">
    <div>
      <div v-if="!state.isLast">
        <div class="my-1">
          <el-radio v-model="state.accessType" label="private" border class="radio-btn">Private</el-radio>
        </div>
        <div class="my=1">
          <el-radio v-model="state.accessType" label="public" border class="radio-btn" disabled >Public</el-radio>
        </div>
        <el-button @click="goLast">다음</el-button>
      </div>
      <div v-else class="my-5">
        <div class="my-1">
          <el-radio v-model="state.roomType" label="basic" border class="radio-btn">Basic</el-radio>
        </div>
        <div class="my-1">
          <el-radio v-model="state.roomType" label="custom" border class="radio-btn" disabled>Custom</el-radio>
        </div>
        <el-button class="my-3" @click="goBefore">이전</el-button>
        <footer>
          <el-button type="success" plain>방 설정</el-button>
        </footer>
      </div>
    </div>
  </div>
</template>

<script>
import { defineComponent, reactive, ref } from "vue";
import { useRouter } from "vue-router"

export default defineComponent({
  name: 'GameMode',
  setup() {
    const router = useRouter()
    const state = reactive({
      accessType: 'private',
      roomType: 'basic',
      isLast: false,
    })
    const redirectToNickname = function() {
      router.push('nickname')
    }
    const goLast = function() {
      state.isLast = true
      console.log(state.isLast)
    }
    const goBefore = function() {
      state.isLast = false
      console.log(state.isLast)
    }
    return {
      state,
      redirectToNickname,
      goLast,
      goBefore
    };
  },
});
</script>

<style>
.radio-btn {
  width: 10rem;
}
</style>
