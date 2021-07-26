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
      <el-form :model="ruleForm" status-icon :rules="rules" ref="ruleForm" label-width="100px" class="demo-ruleForm">      
        
        <el-input
        placeholder="게임에서 사용할 닉네임을 입력하세요."
        class="font-jua mb-4"
        v-model="ruleForm.nickname"
        maxlength="15"
        show-word-limit
        clearable
        @keyup.enter="redirectToGame"
        ></el-input>
        
        <el-button type="success" size="small" @click="submitForm('nicknameValidateForm')">
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
import axios from "axios";

export default {
  name: "Nickname",
  props: {
    roomId: String
  },
  data(){
    var validateNickname = (rule, value, callback) => {
      console.log('hello');
      if(value.length > 15){
        callback(new Error('닉네임은 15자 이하로 입력해주세요.'));
      }else if(value == ''){
        callback(new Error('닉네임을 입력해 주세요'));
      }else if(value.test(/[~!@#$%^&*()_+|<>?:{}]/)){
        callback(new Error('닉네임에는 특수문자가 포함될 수 없습니다.')); 
      }else{
        callback();
      }
    };
    return {
      ruleForm:{
        nickname: '',
      },
      rules: {
        nickname: [
          {validator: validateNickname, trigger: 'blur'}
        ]
      }
    }
  },
  methods: {   
    submitForm(formName) {
      console.log('submit form');
      this.$refs[formName].validate((valid) => {
        if (valid) {
          alert('submit!');
        } else {
          console.log('error submit!!');
          return false;
        }
      });
    },
  },
  setup() {
    const store = useStore();
    const router = useRouter();
    let isShow = ref(false);
    const state = reactive({
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
