<template>
  <div>
    <div v-if="!state.isLast" class="mt-4">
      <span class="font-jua">누구와 게임을 하시겠어요?</span>
      <div class="mt-5 mb-4">
        <el-button
          class="gamemode-btn1"
          round
          @click="chooseAccessType('private')"
        >
          <span class="font-jua">친구와 함께 플레이!</span>
        </el-button>
      </div>
      <div>
        <el-button
          class="gamemode-btn2"
          round
          @click="chooseAccessType('public')"
        >
          <span class="font-jua">모르는 사람과 플레이!</span>
        </el-button>
      </div>
      <div class="mt-5">
        <el-button size="medium" round @click="goHome">
          <span class="font-jua">이전 페이지로</span>
        </el-button>
      </div>
    </div>

    <div v-else class="mt-4">
      <span class="font-jua">어떤 모드를 플레이 하시겠어요?</span>
      <div class="mt-5 mb-4">
        <el-button class="gamemode-btn1" round @click="chooseRoomType('basic')">
          <span class="font-jua">기본 모드</span>
        </el-button>
      </div>
      <div>
        <el-button
          class="gamemode-btn2"
          round
          @click="chooseRoomType('custom')"
        >
          <span class="font-jua">커스텀 모드</span>
        </el-button>
      </div>
      <div class="mt-5">
        <el-button size="medium" round @click="goBack">
          <span class="font-jua">이전 페이지로</span>
        </el-button>
      </div>
    </div>
  </div>
</template>

<script>
import { defineComponent, reactive, ref } from "vue";
import { useRouter } from "vue-router";

export default defineComponent({
  name: "GameMode",
  setup() {
    const router = useRouter();
    const state = reactive({
      accessType: "private",
      roomType: "basic",
      isLast: false,
    });
    const chooseAccessType = (type) => {
      state.accessType = type;
      state.isLast = true;
    };
    const chooseRoomType = (type) => {
      state.roomType = type;
      router.push("nickname");
    };
    const goBack = () => {
      state.isLast = false;
    };
    const goHome = () => {
      router.push("home");
    };
    return {
      state,
      chooseAccessType,
      chooseRoomType,
      goBack,
      goHome,
    };
  },
});
</script>

<style></style>
