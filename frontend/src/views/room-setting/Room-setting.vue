<template>
  <div class="wrap">
    <div>
      <img
        id="room-setting-logo"
        src="@/assets/image/mafia-logo.png"
        alt="mafia logo image"
      />
    </div>
    <GameMode />
  </div>
</template>

<script>
import "./room-setting.css";
import GameMode from '@/views/room-setting/components/GameMode.vue'

export default {
  name: 'Room-setting',
  components: {
    GameMode,
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
