<template>
<div class="wrap">
    <div v-if="!isLogin" class="google-btn" @click="sendGoogleUrl">
        <div class="google-icon-wrapper">
            <img class="google-icon" src="https://upload.wikimedia.org/wikipedia/commons/5/53/Google_%22G%22_Logo.svg"/>
        </div>
        <p class="btn-text"><b>Sign in with google</b></p>
    </div>
    <div v-else class="google-btn-login" @click="handleLogout">
        <div class="google-icon-wrapper">
            <img class="google-icon" src="https://upload.wikimedia.org/wikipedia/commons/5/53/Google_%22G%22_Logo.svg"/>
        </div>
        <p class="btn-text-login"><b>Sign out</b></p>
    </div>
</div>
</template>
<script>
import {computed} from 'vue';
import {useStore} from 'vuex';
import { reactive } from '@vue/reactivity';
import googleLogo from '.././assets/google-logo.png';
export default {
    name: 'Login',
    methods:{
        handleLogout(){
            localStorage.removeItem("token");
            this.$store.dispatch('token/setToken', false);
            this.$router.push("/");
        }
    },
    setup(){
        const store = useStore();
        const state = reactive({
            googleUrl: 'http://localhost:8080/oauth2/authorize/google?redirect_uri=http://localhost:8081/oauth2/redirect',
            googleImage: googleLogo,
            response: '',
        })

        const isLogin = computed(() => store.getters['token/getToken']);
        const sendGoogleUrl = () => {
            console.log('sendGoogleUrl');
            location.href=state.googleUrl;
        }
        return{
            state,
            isLogin,
            sendGoogleUrl,
        }
    }
};
</script>
<style >
.wrap{
    width: 100%;
    height:100%;
}
.google-btn {
    width: 240px;
    height: 55px;
    background-color: #4285f4;
    border-radius: 2px;
    box-shadow: 0 3px 4px 0 rgba(0,0,0,.25);

    margin: 50px auto 0;
}

.google-btn-login{
    width: 240px;
    height: 55px;
    background-color: #fff;
    border-radius: 2px;
    box-shadow: 0 3px 4px 0 rgba(0,0,0,.25);

    margin: 50px auto 0;
}
.google-icon-wrapper {
    position: absolute;
    margin-top: 1px;
    margin-left: 1px;
    width: 53px;
    height: 53px;
    border-radius: 2px;
    background-color: #fff;
}
.google-icon {
    position: absolute;
    margin-top: 18px;
    margin-right: 18px; 
    width: 24px;
    height: 24px;
}
.btn-text {
    float: right;
    margin: 18px 18px 0 0;
    color: #fff;
    font-size: 17px;
    letter-spacing: 0.2px;
    font-family: "Roboto";
}
.btn-text-login{
    float: right;
    margin: 18px 18px 0 0;
    color: #4285f4;
    font-size: 17px;
    letter-spacing: 0.2px;
    font-family: "Roboto";
}
.google-btn:hover {
    box-shadow: 0 0 6px #4285f4;
}
.google-btn:active {
    background: #1669F2;
}
.google-btn-login:hover {
    box-shadow: 0 0 6px #4285f4;
}
.google-btn-login:active {
    background: #1669F2;
}

</style>