<template>
  <div class="wrap">
      <div class="d-flex justify-content-center">
        <img
            id="room-setting-logo"
            src="@/assets/image/logo.png"
            alt="logo image"
        />
      </div>
      <div class="d-flex justify-content-center">
        <el-form class="nickname-form">
            <el-label>닉네임을 입력해주세요</el-label>
            <el-input placehlder="닉네임을 적어주세요" class="nickname-input" v-model="state.nickname" clearable></el-input>
            <el-button class="button" type="success" >제출</el-button>
        </el-form>
      </div>
      <el-alert class="nickname-alert" type="error" :title="state.errorMessage"></el-alert>
  </div>
</template>

<script>
import { onBeforeMount, reactive } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import axios from 'axios'


export default {
  name: "Nickname",
  setup() {
      const store = useStore()
      const router = useRouter()
      const state = reactive({
          nickname: '',
          is404: false,
          isError: false,
          errorMessage: '정원이 초과되었습니다',
          URL: '',
      })
      onBeforeMount(() => {

      })
      const redirectTo404 = function() {
          router.push('notfound')
      }
      const redirectToMain = function() {
          router.push({name:"main", params:{}})
      }
    return { state, redirectTo404, redirectToMain } 
  }
};
</script>

<style>
.wrap {
  width: 100vw;
  height: 100vh;
  background-color: black;
  text-align: center;
}
.nickname-form {
  width: 40vw;
  margin-top: 5vw;
  color: white;
}
.nickname-input {
  display: inline;
}
.nickname-alert {
  width: 60vw;
  height: 5vw;
  margin-top: 10vw;
  margin-left: 20vw;
}

</style>
