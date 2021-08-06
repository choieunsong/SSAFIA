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

        <div id="nickname-roomid">
            <p>
                ROOM ID: <span>{{ roomId }}</span>
            </p>
        </div>

        <div id="nickname-form" v-show="isShow">
            <el-form
                :model="state.form"
                status-icon
                :rules="state.rules"
                ref="nickname"
                label-width="100px"
                class="demo-ruleForm"
                @submit.prevent
            >
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

        <el-alert
            class="nickname-alert"
            type="error"
            :title="state.errorMessage"
            v-if="state.isError"
        ></el-alert>
    </div>
</template>

<script>
import "./nickname.css";
import { onMounted, reactive, ref } from "vue";
import { useStore } from "vuex";
import { useRouter, useRoute } from "vue-router";
import { API_BASE_URL } from "@/constant/index";
import axios from "axios";

axios.defaults.headers.post["Content-Type"] = "application/json";

export default {
    name: "Nickname",
    setup() {
        const store = useStore();
        const router = useRouter();
        const route = useRoute();

        let isShow = ref(false);
        const roomId = route.params.roomId;

        const nickname = ref(null);

        

        const state = reactive({
            isError: false,
            errorMessage: "",
            form: {
                nickname: "",
            },
            rules: {
                nickname: [
                    {
                        required: true,
                        validator: validateNickname,
                        trigger: "blur",
                        message: "닉네임은 3자 이상 15자 이하여야 합니다",
                    },
                ],
            },
        });
        function isRejoin() {
            if (store.getters['token/roomId'] === route.params.roomId && store.getters['ingame/getGameStatus']!=="READY") {
                const url = API_BASE_URL + `/api/gamesession/${route.params.roomId}`
                    axios.get(url, {
                        params: {
                            playerId: store.getters['token/getPlayerId']
                        }
                    }).then(({data}) => {
                        if (data.code === "success") {
                            store.dispatch('setOpenviduToken', data.data.token)
                             .then(router.push({name:"Game", params: { roomId: route.params.roomId}}))
        
                        }
                    }).catch((error) => {
                        console.log(error)
                    })
                    router.push({ name: "Game", parmas:{ roomId: route.params.roomId }})
                } 
        }

        onMounted(() => {
            axios({
                method: "GET",
                url: API_BASE_URL + "/api/gamesession/" + roomId,
            })
                .then(({ data }) => {
                    if (data.code === "fail") {
                        if (data.message === "없는 리소스입니다") {
                            router.push({ name: "NotFound" });
                        } else {
                            state.errorMessage = data.message;
                            state.isError = true;
                        }
                    }
                })
                .catch((err) => {
                    console.log(err);
                });
            setTimeout(() => {
                isShow.value = true;
            }, 1000);
        });

        const validateNickname = (rule, value, callback) => {
            console.log(rule.message);
            if (value == "" || value.length < 3) {
                // rule.message = '닉네임은 3자 이상 15자 이하여야 합니다';
                callback(new Error("Please input nickname"));
            } else {
                callback();
            }
        };

        const redirectToGame = (formName) => {
            // nickname validation
            nickname.value.validate((valid) => {
                if (valid) {
                    console.log("nickname:", state.form.nickname);
                    axios({
                        method: "POST",
                        url: API_BASE_URL + "/api/gamesession/" + roomId,
                        data: {
                            nickname: state.form.nickname,
                        },
                    })
                        .then(({ data }) => {
                            if (data.code === "fail") {
                                state.errorMessage = data.message;
                                state.isError = true;
                            } else {
                                store.dispatch("token/setPlayerId", data.data.playerId);
                                store.dispatch("token/setOpenviduToken", data.data.token);
                                store
                                    .dispatch("token/setNickname", state.form.nickname)
                                    .then(() => {
                                        console.log(store.getters["token/getNickname"]);
                                        router.push({ name: "Game", params: route.params.roomId });
                                    });
                            }
                        })
                        .catch((err) => {
                            console.log(err);
                        });
                } else {
                    alert("Validate error");
                }
            });
        };

        const goHome = () => {
            router.push({ name: "Home" });
        };
        isRejoin()
        return {
            isShow,
            state,
            roomId,
            redirectToGame,
            goHome,
            nickname,
            validateNickname,
        };
    },
};
</script>

<style></style>
