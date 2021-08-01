<template>
  <div v-if="streamManager">
    <ov-video :stream-manager="streamManager" />
    <div>
      <p>{{ state.clientData }}</p>
    </div>
  </div>
</template>

<script>
import OvVideo from "@/views/game/components/OvVideo";
import { reactive, onBeforeMount, onMounted } from "vue";
import { useStore } from 'vuex'

export default {
  name: "UserVideo",

  components: {
    OvVideo,
  },

  props: {
    streamManager: Object,
  },
  setup(props) {
    const state = reactive({
      clientData: undefined,
    });
    const store = useStore()
	state.clientData = store.getters['token/getNickname']
    return {
      state,
    };
  },

  // computed: {
  // 	clientData () {
  // 		const { clientData } = this.getConnectionData();
  // 		return clientData;
  // 	},
  // },

  // methods: {
  // 	getConnectionData () {
  // 		const { connection } = this.streamManager.stream;
  // 		return JSON.parse(connection.data);
  // 	},
  // },
};
</script>
