import { createApp } from 'vue';
import App from './App.vue';

import ElementPlus from 'element-plus';
import 'element-plus/lib/theme-chalk/index.css';

import axios from 'axios';
import VueAxios from 'vue-axios';    //axios 추가 
import store from "./store";        //vuex 추가
import { router } from './router/router';  //라우터 추가

const app = createApp(App);
app.use(VueAxios, axios);
app.use(router);
app.use(store);
app.use(ElementPlus);
app.mount('#app');
