<template>
    <div>
        <div v-if="!state.isLast" class="mt-4">
            <span class="font-jua">1. 누구와 게임을 하시겠어요?</span>
            <div class="mt-5 mb-4">
                <el-button class="gamemode-btn1" round @click="chooseAccessType('private')">
                    <span class="font-jua">친구와 함께 플레이!</span>
                </el-button>
            </div>
            <div>
                <el-button class="gamemode-btn2" round disabled>
                    <span class="font-jua">모르는 사람과 플레이!</span>
                </el-button>
            </div>
        </div>

        <div v-else class="mt-4">
            <span class="font-jua">2. 어떤 모드를 플레이 하시겠어요?</span>
            <div class="mt-5 mb-4">
                <el-button class="gamemode-btn1" round @click="chooseRoomType('basic')">
                    <span class="font-jua">기본 모드</span>
                </el-button>
            </div>
            <div>
                <el-button class="gamemode-btn2" round disabled>
                    <span class="font-jua">커스텀 모드</span>
                </el-button>
            </div>
            <div class="mt-5">
                <el-button size="medium" round @click="goBack">
                    <span class="font-jua">이전 선택으로</span>
                </el-button>
            </div>
        </div>
    </div>
</template>

<script>
import { defineComponent, reactive } from "vue";
import { useRouter } from "vue-router";
import axios from "axios";
import { useStore } from "vuex";
import { API_BASE_URL } from "@/constant/index";
import { ElMessage } from "element-plus";

export default defineComponent({
    name: "GameMode",
    setup() {
        const router = useRouter();
        const store = useStore();
        const state = reactive({
            accessType: "private",
            roomType: "basic",
            isLast: false,
            roomId: "Es14g5f", //나중에 backend에서 받아올 부분
            isError: false,
            errorMessage: "",
        });
        const chooseAccessType = (type) => {
            state.accessType = type;
            state.isLast = true;
        };

        const getRoomIdFromServer = () => {
            axios({
                method: "post",
                url: API_BASE_URL + "/api/gamesession",
                headers: store.getters["token/getHeaders"],
                data: {
                    accessType: state.accessType,
                    roomType: state.roomType,
                },
            })
                .then(({ data }) => {
                    console.log(data);
                    if (data.code === "success") {
                        store.dispatch("token/setRoomId", data.data.id);
                        router.push({ name: "Nickname", params: { roomId: data.data.id } });
                    } else if (data.code === "fail") {
                        state.isError = true;
                        state.errorMessage = data.message;
                    }
                })
                .catch((error) => {
                    console.log(error);
                });
        };

        const chooseRoomType = (type) => {
            state.roomType = type;
            getRoomIdFromServer();
            // router.push({ name: "Nickname", params: { roomId: state.roomId } });
        };
        const goBack = () => {
            state.isLast = false;
        };
        return {
            state,
            chooseAccessType,
            chooseRoomType,
            goBack,
        };
    },
});
</script>

<style></style>
