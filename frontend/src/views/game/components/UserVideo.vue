<template>
    <div v-if="streamManager" class="cell-box col-md-3">
        <div class="cell col-md-12" :style="'background-color: ' + this.playerInfo.color">
            <ov-video :stream-manager="streamManager" />
            <span id="nickname" class="font-jua">
                {{ this.playerInfo.nickname }}
            </span>
        </div>
    </div>
</template>

<script>
import OvVideo from "./OvVideo";
import "./uservideo.css";
export default {
    name: "UserVideo",

    components: {
        OvVideo,
    },

    props: {
        streamManager: Object,
        playerInfo: Object,
    },

    computed: {},

    methods: {
        getConnectionData() {
            const { connection } = this.streamManager.stream;
            return JSON.parse(connection.data);
        },
    },
};
</script>
