<template>
    <div class="wrap">
        <el-alert
            class="nickname-alert font-jua"
            type="error"
            :title="state.errorMessage"
            v-if="state.isError"
        ></el-alert>
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

        const validateNickname = (rule, value, callback) => {
            console.log(rule.message);
            if (value == "" || value.length < 3) {
                // rule.message = '닉네임은 3자 이상 15자 이하여야 합니다';
                callback(new Error("Please input nickname"));
            } else {
                callback();
            }
        };

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

        onMounted(() => {
            if (
                store.getters["token/getRoomId"] === route.params.roomId &&
                store.getters["ingame/getPhase"] !== "READY"
            ) {
                const url = API_BASE_URL + `/api/gamesession/${route.params.roomId}`;
                axios
                    .get(url, {
                        params: {
                            playerId: store.getters["token/getPlayerId"],
                        },
                    })
                    .then(({ data }) => {
                        if (data.code === "success" && data.data.state === "REJOIN") {
                            store.dispatch("ingame/setIsREJOIN", true);
                            store.dispatch("token/setOpenviduToken", data.data.token).then(
                                router.push({
                                    name: "Game",
                                    params: { roomId: route.params.roomId },
                                })
                            );
                        } else {
                            if (data.code === "fail") {
                                state.errorMessage = data.message;
                                state.isError = true;
                            }
                        }
                    })
                    .catch(({ response }) => {
                        if (response.status === 500) {
                            router.push({ path: "/:catchAll(.*)" });
                        } else if (response.status === 404) {
                            router.push({ path: "/:catchAll(.*)" });
                        } else {
                            console.log(response);
                        }
                    });
                setTimeout(() => {
                    isShow.value = true;
                }, 1000);
            } else {
                axios({
                    method: "GET",
                    url: API_BASE_URL + "/api/gamesession/" + roomId,
                })
                    .then(({ data }) => {
                        if (data.code === "fail") {
                            state.errorMessage = data.message;
                            state.isError = true;
                        }
                    })
                    .catch(({ response }) => {
                        if (response.status === 500) {
                            router.push({ path: "/:catchAll(.*)" });
                        } else if (response.status === 404) {
                            router.push({ path: "/:catchAll(.*)" });
                        } else {
                            console.log(response);
                        }
                    });
                setTimeout(() => {
                    isShow.value = true;
                }, 1000);
            }
        });

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
                                store.dispatch("ingame/setIsREJOIN", false);
                                store
                                    .dispatch("token/setNickname", state.form.nickname)
                                    .then(() => {
                                        router.push({ name: "Game", params: route.params.roomId });
                                    });
                            }
                        })
                        .catch(({ response }) => {
                            if (response.status === 500) {
                                router.push({ path: "/:catchAll(.*)" });
                            } else if (response.status === 404) {
                                router.push({ path: "/:catchAll(.*)" });
                            } else {
                                console.log(response);
                            }
                        });
                }
            });
        };

        const goHome = () => {
            router.push({ name: "Home" });
        };

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
