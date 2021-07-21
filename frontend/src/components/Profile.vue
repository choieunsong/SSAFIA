<template>
    <div>
        <img 
            :src="user.imageUrl"
            :alt="user.name"
            width="150"
            class="rounded-circle"
        />
        <h3>{{user.email}}</h3>
        <p>{{user.name}}</p>
    </div>
</template>
<script>
import axios from "axios";
export default {
    name: 'Profile',
    data: () => {
        return {
            user: {
                name: '',
                imageUrl: '',
                email: ''
            }
        }
    },
    created(){
        const Token = this.$store.getters['token/getToken'];
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
            this.user.imageUrl = data.imageUrl;
            this.user.name = data.name;
            this.user.email = data.email;
        }).catch((err) =>{
            console.log('err',err);
        })
    }
}
</script>
<style >
.rounded-circle{
    border-radius: 50% !important;
    margin: 30px 0 auto;
}
</style>
