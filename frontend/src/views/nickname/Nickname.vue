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
      <el-form :model="state.form" status-icon :rules="state.rules" ref="nickname" label-width="100px" class="demo-ruleForm">      
        <el-form-item prop="nickname" label-width="50px">
          <el-input
          placeholder="게임에서 사용할 닉네임을 입력하세요."
          class="font-jua"
          v-model.trim="state.form.nickname"
          autocomplete="off"
          maxlength="15"
          show-word-limit
          clearable
          @keyup.enter="redirectToGame"
          ></el-input>
        </el-form-item>
        <el-button type="success" size="small" @click="redirectToGame">
          <span class="font-jua">입장</span>
        </el-button>
      </el-form>
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
import { API_BASE_URL} from "@/constant/index";
import axios from "axios";

export default {
  name: "Nickname",
  data(){
    return {
      roomId: '',
      URL: ''
    }
  },
  created(){
    //roomId 세팅
    this.roomId = this.$route.params.roomId;
    console.log(this.roomId);
    //url 세팅 
    this.URL = API_BASE_URL + '/' + this.roomId;
    console.log(this.URL);
  },
  setup() {
    const store = useStore();
    const router = useRouter();

    let isShow = ref(false);

    const nickname = ref(null)

    const validateNickname = (rule, value, callback) => {
      console.log(rule.message);
      if(value == '' || value.length < 3){
        // rule.message = '닉네임은 3자 이상 15자 이하여야 합니다';
        callback(new Error('Please input nickname'));
      }else{
        callback();
      }
    }

    const state = reactive({
      isError: false,
      errorMessage: "정원이 초과되었습니다",

      form: {
        nickname: ''
      },
      rules: {
        nickname: [
          {required: true, validator: validateNickname, trigger: 'blur', message: '닉네임은 3자 이상 15자 이하여야 합니다'}
        ]
      }
    });

    onMounted(() => {
      setTimeout(() => {
        isShow.value = true;
      }, 1000);
    });
    
    const redirectToGame = (formName) => {
      // nickname validation
      nickname.value.validate((valid) => {
        if(valid){
          console.log('nickname:',state.form.nickname);
        }else{
          //  alert('Validate error');
        }
      })
      // router.push({ name: "Game", params: {} });
    };
    const goHome = () => {
      router.push("home");
    };
    return {
      isShow,
      state,
      redirectToGame,
      goHome,
      nickname,
      validateNickname
    };
  },
};
</script>

<style>
</style>
