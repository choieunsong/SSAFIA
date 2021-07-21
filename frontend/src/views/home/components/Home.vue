<template>
    <img alt="Vue logo" src="@/assets/logo.png">
    <login></login> 
</template>

<script>
import Login from '@/views/home/components/Login.vue'
// import { onMounted } from '@vue/runtime-core'
// import {useStore} from 'vuex';

export default {
    name: 'Home',
    components: {
        Login,
    },
    created(){
            const Token = localStorage.getItem("token");
            console.log('Token',Token);
            if(Token){
                this.$store.dispatch('token/setIsLogin', true);
                this.$store.dispatch('token/setToken', Token);
            }else{
                this.$store.dispatch('token/setIsLogin', false);
            }
    },
    // setup(){
    //     const store = useStore();
        
    //     onMounted(() => {
    //         const Token = localStorage.getItem("token");
    //         console.log('Token',Token);
    //         if(Token){
    //             store.dispatch('token/setIsLogin', true);
    //             store.dispatch('token/setToken', Token);
    //         }else{
    //             store.dispatch('token/setIsLogin', false);
    //         }
    //     })
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
