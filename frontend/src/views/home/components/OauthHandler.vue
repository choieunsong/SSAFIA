<template>
    <div>Oauth</div>
</template>
<script>
// import { onMounted } from '@vue/runtime-core'
// import useRoute from 'vue-router'
// import {useStore} from "vuex"

export default {
    name: 'OauthHandler',
    mounted(){
        const url = this.$route.fullPath;
        const slice = url.split("token=");
        const token = slice[1];
        // 분기처리 
        // token 존재 => /profile
        // error 존재 => /
        if(token){
            this.$store.dispatch('token/setToken', token);
            localStorage.setItem('token', token);
            this.$store.dispatch('token/setIsLogin', true);
            console.log(this.$store.getters['token/getToken']);
            
            this.$router.push({name: 'Home'});
        }else{
            
            this.$router.push('/');
        }
    }

    // setup() {
    //     const router = useRoute();    
    //     const store = useStore();
        
    //     onMounted(() => {
    //         const url = router.fullPath;
    //         const slice = url.split("token=");
    //         const token = slice[1];
            
    //         // 분기처리 
    //         // token 존재 => /profile
    //         // error 존재 => /
    //         if(token){
    //             store.dispatch('token/setToken', token);
    //             localStorage.setItem('token', token);
    //             store.dispatch('token/setIsLogin', true);
    //             console.log(store.getters['token/getToken']);
                
    //             router.replace('/profile');
    //         }else{
    //             router.replace('/');
    //         }
    //     })
    //     return{
    //         router,
    //         store
    //     }
    // },
}
</script>
