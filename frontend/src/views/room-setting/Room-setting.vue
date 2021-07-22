<template>
  <div class="wrap">
    <div>
      <img
        id="room-setting-logo"
        src="@/assets/image/mafia-logo.png"
        alt="mafia logo image"
      />
    </div>
    <GameMode id="gamemode" v-show="isShow" />
  </div>
</template>

<script>
import "./room-setting.css";
import GameMode from "@/views/room-setting/components/GameMode.vue";
import {onMounted, ref} from 'vue';


export default {
  name: "Room-setting",
  components: {
    GameMode,
  },
  setup() {
    let isShow = ref(false)
    onMounted(() => {
      // Mafia logo 올라가고 라디오 버튼이 나와야하므로
      setTimeout(() => {
        isShow.value = true
      }, 3300);
    })
    return {
      isShow,
    }
  }
};
</script>

<style>
.wrap {
  width: 100vw;
  height: 100vh;
  background-color: black;
  text-align: center;
}
</style>
