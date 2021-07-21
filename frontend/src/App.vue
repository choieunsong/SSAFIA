<template>
  <img alt="Vue logo" src="./assets/logo.png">
  <login></login> 
  <router-view></router-view>
</template>

<script>
import Login from './components/Login.vue'
// import { onMounted } from '@vue/runtime-core'
// import {computed} from 'vue';
// import {useStore} from 'vuex';

export default {
  name: 'App',
  components: {
    Login,
  },

  // setup(){
  //   const store = useStore();
    
  //   var isLogin = computed(() => store.getters['token/getIsLogin']);
    
  //   onMounted(() => {
  //     if(localStorage.getItem("token")){
  //       store.dispatch('token/setIsLogin', true);
  //     }else{
  //       store.dispatch('token/setIsLogin', false);
  //     }
  //   })

  //   return{
  //     isLogin
  //   }
  // }
}
</script>
  
  
<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
