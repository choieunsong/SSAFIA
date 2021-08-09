<template>
    <div class="wrap">
        <div class="ssafia-logo py-4">
            <img src="@/assets/image/logo.png" alt="logo image" />
        </div>
        <div class="mt-5 mb-4">
            <el-button type="success" size="medium" round @click="moveToNickname" class="home-btn">
                <span class="font-jua">방 만들기</span>
            </el-button>
        </div>
        <!-- <vue-typer
            class="display-3"
            :text="['안녕하세요.', '김정훈의 포트폴리오입니다!!!', '방문 감사합니다!!!']"
            :repeat="Infinity"
            initial-action="typing"
            :pre-type-delay="100"
            :type-delay="200"
            :pre-erase-delay="1500"
            :erase-delay="250"
            erase-style="backspace"
            :erase-on-complete="false"
            caret-animation="blink"
        ></vue-typer> -->
        <Login />
    </div>
</template>

<script>
import "./home.css";
import Login from "@/views/home/components/Login.vue";
import { router } from "@/router/router.js";
import { ElMessage } from "element-plus";
import { useStore } from "vuex";
import { computed } from "vue";

export default {
    name: "Home",
    components: {
        Login,
    },
    setup() {
        const store = useStore();
        const isLogin = computed(() => store.getters["token/getIsLogin"]);
        const moveToNickname = () => {
            if (isLogin.value) {
                router.push("room-setting");
            } else {
                ElMessage.error("로그인이 필요합니다!");
            }
        };

        return {
            moveToNickname,
        };
    },
};
</script>

<style></style>
