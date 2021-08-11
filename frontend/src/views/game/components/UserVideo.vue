<template>
    <div v-if="streamManager" class="cell-box col-md-3">
        <div
            class="cell col-md-12"
            :style="'background-color: ' + getColor"
            ref="cell"
        >
            <span v-if="this.playersGameInfo.suspicious" class="cell-suspicious"></span>
            <div class="ov-video-wrap">
                <ov-video
                    :stream-manager="streamManager"
                    :alive="this.playersGameInfo.alive"
                    :phase="this.gameStatus.phase"
                />
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
                    if (this.playersGameInfo && this.playerInfo.alive === true) {
                        if (this.gameStatus.phase === "DAY_DISCUSSION") {
                            this.$refs.cell.classList.add("cell-hover");
                            this.$refs.cell.addEventListener("click", this.votePlayer)
                        } else if (this.gameStatus.phase === "DAY_ELIMINATION") {
                            console.log("userVideo day elimination");
                            this.$refs.cell.classList.add("cell-hover")
                            this.$refs.cell.addEventListener("click", this.votePlayer)
                            if (!this.playersGameInfo.suspicious) {
                                console.log("not suspicious");
                                this.$refs.cell.classList.add("cell-unsuspicious");
                                this.$refs.cell.classList.remove("cell-hover");
                            }
                        } else if (this.gameStatus.phase === "NIGHT_VOTE") {
                            if (this.role === "MAFIA" && this.playersGameInfo.isMafia === false) {
                                this.$refs.cell.classList.add("cell-hover")
                                this.$refs.cell.addEventListener("click", this.votePlayer)
                            } else if (this.role === "DOCTOR") {
                                this.$refs.cell.classList.add("cell-hover")
                                this.$refs.cell.addEventListener("click", this.votePlayer)
                            } else if (this.role === "POLICE") {
                                if (this.playerMe.playerId !== this.playersGameInfo.playerId) {
                                    this.$refs.cell.classList.add("cell-hober")
                                    this.$refs.cell.addEventListener("click", this.votePlayer)
                                }
                            }
                        }
                    }
                }
            },
        },
        playersGameInfo: {
            deep: true,
            handler() {
                console.log("user video playersGameInfo change");
                if (this.playersGameInfo !== undefined && this.$refs.cell !== undefined) {
                    if (this.playersGameInfo.isTalking === true) {
                        this.$refs.cell.classList.add('talking-border')
                    } else {
                        this.$refs.cell.classList.remove('talking-border')
                    } 
                    if (this.plaayersGameInfo.alive === false) {
                        this.$refs.cell.classList.remove("cell-hover")
                        this.$refs.removeEventListener("click", this.votePlayer)
                    } else {
                        if (this.gameStatus.phase === "DAY_ELIMINATION" && this.playersGameInfo.suspicious === false) {
                            this.$refs.cell.classList.remove('cell-hover')
                            this.$refs.cell.removeEventListener('click', this.votePlayer)
                        }
                    }
                    
                }
            },
        },
        isConfirm: {
            handler() {
                this.$refs.cell.classList.remove("cell-hover");
                this.$refs.cell.removeEventListener('click', this.votePlayer)
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
                this.$emit("emitVoteDataUpdate", this.playersGameInfo.playerId)
        },
    },
};
</script>
<style>
.cell-hover {
    box-shadow: 0 3px 6px rgba(0, 0, 0, 0.16), 0 3px 6px rgba(0, 0, 0, 0.23);
    cursor: pointer;
}
.talking-border {
    border-style: solid;
    border-color: #00ff26;
    border-width: thick;
}
</style>
