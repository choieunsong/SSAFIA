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
        
        <div class="box-card">
            <img 
                :src="state.user.imageUrl"
                :alt="state.user.name"
                width="150"
                class="rounded-circle"
            />
            <h3>{{state.user.email}}</h3>
            <p>{{state.user.name}}</p>
        </div>
    </div>
</div>
</template>
<script>
import {computed} from 'vue';
import {onMounted} from '@vue/runtime-core'
import {useStore} from 'vuex';
import {reactive} from '@vue/reactivity';
import axios from "axios";
export default {
    name: 'Login',
    setup(){
        const store = useStore();
        const state = reactive({
            googleUrl: 'http://localhost:8080/oauth2/authorize/google?redirect_uri=http://localhost:8081/oauth2/redirect',
            user: {
                name: '',
                imageUrl: '',
                email: ''
            }
        })

        const isLogin = computed(() => store.getters['token/getIsLogin']);
        const sendGoogleUrl = () => {
            console.log('sendGoogleUrl');
            location.href=state.googleUrl;
        }
        const handleLogout = () => {
            console.log('logout');
            localStorage.removeItem("token");
            store.dispatch('token/setIsLogin', false);
        }
        
        onMounted(() => {
            if(isLogin.value){
                const Token = store.getters['token/getToken'];
                console.log('token',Token);
                const headers = {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer '+Token
                }
                const url = 'http://localhost:8080/profile';
                console.log('url',url);

                axios({
                    method: 'get',
                    url: url,
                    headers: headers
                })
                .then(({data}) => {
                    console.log('axios get success', data);
                    state.user.imageUrl = data.imageUrl;
                    state.user.name = data.name;
                    state.user.email = data.email;
                }).catch((err) =>{
                    console.log('err',err);
                })
            }
        })

        return{
            state,
            isLogin,
            sendGoogleUrl,
            handleLogout
        }
    }
};
</script>
<style >
.rounded-circle{
    border-radius: 50% !important;
    margin: 30px 0 auto;
}

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