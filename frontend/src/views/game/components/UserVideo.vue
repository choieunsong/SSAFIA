<template>
    <div v-if="streamManager" class="cell-box col-md-3" @click="votePlayer">
        <div class="cell col-md-12" :style="'background-color: ' + getColor" ref="cell">
            <div class="ov-video-wrap">
                <ov-video :stream-manager="streamManager" />
            </div>
            <span id="nickname" class="font-jua">
                {{ this.playersGameInfo.nickname }}
            </span>
            <div
                v-if="this.gameStatus.phase == 'READY' && this.playersGameInfo.isHost"
                class="host"
            >
                <img
                    class="crown"
                    src="https://image.flaticon.com/icons/png/512/1980/1980126.png"
                />
                <span class="host-title font-jua">호스트</span>
            </div>
            <div class="vote-box" v-if="this.playersGameInfo.voters.length != 0">
                <span
                    v-for="voter in this.playersGameInfo.voters"
                    :key="voter"
                    class="voter-icon-span"
                    ><i class="fas fa-user-circle fa-2x" :style="'color:' + voter"></i
                ></span>
            </div>
        </div>
    </div>
</template>

<script>
import OvVideo from "./OvVideo";
import "./uservideo.css";

var colorCode = {
    RED: "#DC143C",
    YELLOW: "#FFFF00",
    GREEN: "#228B22",
    PURPLE: "#9400D3",
    ORANGE: "#FF8C00",
    PINK: "#FF69B4",
    BROWN: "#8B4513",
    AQUAMARINE: "#00CED1",
    BLUE: "#0000CD",
    GRAY: "#778899",
};
export default {
    name: "UserVideo",

    components: {
        OvVideo,
    },

    props: {
        streamManager: Object,
        playersGameInfo: Object,
        gameStatus: Object,
        isConfirm: Boolean,
        role: String,
        playerMe: Boolean,
    },

    computed: {
        getColor() {
            let color = colorCode[this.playersGameInfo.color];
            return color;
        },
    },

    watch: {
        gameStatus: {
            deep: true,
            handler() {
                if (this.streamManager) {
                    if (this.gameStatus.phase == "DAY_DISCUSSION" && !this.playerMe) {
                        this.$refs.cell.classList.add("cell-hover");
                    }
                }
            },
        },
        playersGameInfo: {
            deep: true,
            handler() {
                console.log("user video playersGameInfo change");
                // console.log(this.playersGameInfo.voters);
            },
        },
        isConfirm: {
            handler() {
                this.$refs.cell.classList.remove("cell-hover");
            },
        },
    },
    methods: {
        getConnectionData() {
            const { connection } = this.streamManager.stream;
            return JSON.parse(connection.data);
        },
        // 투표한 상대 플레이어의 playerId를 얻어오는 method
        votePlayer() {
            console.log("click", this.playersGameInfo.playerId);
            this.$emit("emitVoteDataUpdate", this.playersGameInfo.playerId);
        },
    },
};
</script>
<style>
.cell-hover {
    box-shadow: 0 3px 6px rgba(0, 0, 0, 0.16), 0 3px 6px rgba(0, 0, 0, 0.23);
    cursor: pointer;
}

.cell-hover:hover {
    box-shadow: 0 14px 28px rgba(0, 0, 0, 0.25), 0 10px 10px rgba(0, 0, 0, 0.22);
}
</style>
