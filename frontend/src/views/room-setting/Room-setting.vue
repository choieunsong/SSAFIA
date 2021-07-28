<template>
  <div class="wrap">
    <div>
      <img
        id="room-setting-logo"
        src="@/assets/image/logo-name.png"
        alt="logo name"
        @click="goHome"
      />
    </div>
    <GameMode id="gamemode" v-show="isShow" />
  </div>
</template>

<script>
import "./room-setting.css";
import GameMode from "@/views/room-setting/components/GameMode.vue";
import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import axios from 'axios';

export default {
  name: "Room-setting",
  components: {
    GameMode,
  },
  setup() {
    const router = useRouter();
    let isShow = ref(false);
    onMounted(() => {
      // Mafia logo 올라가고 라디오 버튼이 나와야하므로
      setTimeout(() => {
        isShow.value = true;
      }, 1000);
    });
    const goHome = () => {
      router.push("home");
    };
    return {
      isShow,
      goHome,
    };
  },
};
</script>

<style></style>
