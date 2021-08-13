<template>
    <div v-if="streamManager" class="cell-box col-md-3">
        <div class="cell col-md-12" :style="'background-color: ' + getColor" ref="cell">
            <!-- 최종변론자 빛나는 배경 -->
            <span v-if="this.playersGameInfo.suspicious" class="cell-suspicious"></span>
            <!-- 비디오 -->
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

            <!-- 호스트 -->
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

            <!-- OBSERVER에게 직업보이는 칸 -->
            <div v-if="this.playerMe.role == 'OBSERVER'" class="role-box">
                <img
                    v-if="this.playersGameInfo.role == 'CIVILIAN'"
                    src="../../../assets/image/civilian.png"
                    class="role-icon civilian"
                />
                <img
                    v-else-if="this.playersGameInfo.role == 'POLICE'"
                    src="../../../assets/image/police.png"
                    class="role-icon police"
                />
                <img
                    v-else-if="this.playersGameInfo.role == 'DOCTOR'"
                    src="../../../assets/image/doctor.png"
                    class="role-icon doctor"
                />
                <img v-else src="../../../assets/image/mafia.png" class="role-icon mafia" />
                <span class="font-jua role-title">{{ this.playersGameInfo.role }}</span>
            </div>

            <!-- 투표칸 -->
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
                if (this.streamManager && this.playersGameInfo) {
                    console.log("gameStatus changed");
                    console.log(this.playersGameInfo.alive);
                    console.log(this.gameStatus.phase);
                    if (this.playersGameInfo.alive) {
                        // 초기화
                        this.$refs.cell.classList.remove("cell-hover");
                        this.$refs.cell.removeEventListener("click", this.votePlayer);
                        this.$refs.cell.classList.remove("cell-unsuspicious");
                        if (this.gameStatus.phase === "DAY_DISCUSSION") {
                            console.log("DAY_DISCUSSION");
                            this.$refs.cell.classList.add("cell-hover");
                            this.$refs.cell.addEventListener("click", this.votePlayer);
                        } else if (this.gameStatus.phase === "DAY_ELIMINATION") {
                            console.log("userVideo day elimination");
                            if (this.playersGameInfo.suspicious === true) {
                                console.log(this.playersGameInfo.suspicious);
                                console.log("최종변론 투표");
                                this.$refs.cell.classList.add("cell-hover");
                                this.$refs.cell.addEventListener("click", this.votePlayer);
                            }
                            if (!this.playersGameInfo.suspicious) {
                                console.log("not suspicious");
                                this.$refs.cell.classList.add("cell-unsuspicious");
                                this.$refs.cell.classList.remove("cell-hover");
                            }
                        } else if (this.gameStatus.phase === "NIGHT_VOTE") {
                            console.log("NIGHT VOTE");
                            if (this.role === "MAFIA" && this.playersGameInfo.isMafia === false) {
                                this.$refs.cell.classList.add("cell-hover");
                                this.$refs.cell.addEventListener("click", this.votePlayer);
                            } else if (this.role === "DOCTOR") {
                                this.$refs.cell.classList.add("cell-hover");
                                this.$refs.cell.addEventListener("click", this.votePlayer);
                            } else if (this.role === "POLICE") {
                                if (this.playerMe === false) {
                                    this.$refs.cell.classList.add("cell-hober");
                                    this.$refs.cell.addEventListener("click", this.votePlayer);
                                }
                            }
                        }
                    } else {
                        this.$refs.cell.classList.remove("cell-hover");
                        this.$refs.cell.removeEventListener("click", this.votePlayer);
                        this.$refs.cell.classList.remove("cell-unsuspicious");
                    }
                }
            },
        },
        playersGameInfo: {
            deep: true,
            handler() {
                console.log("user video playersGameInfo change");
                if (this.streamManager && this.playersGameInfo && this.$refs.cell) {
                    // audio speaking 감지
                    if (this.playersGameInfo.isTalking === true) {
                        this.$refs.cell.classList.add("talking-border");
                    } else {
                        this.$refs.cell.classList.remove("talking-border");
                    }
                }
            },
        },
        isConfirm: {
            handler() {
                if (this.playersGameInfo && this.$refs.cell && this.isConfirm) {
                    if (this.isConfirm) {
                        this.$refs.cell.classList.remove("cell-hover");
                        this.$refs.cell.removeEventListener("click", this.votePlayer);
                    } else {
                        if (this.gameStatus.phase === "DAY_DISCUSSION") {
                            this.$refs.cell.classList.add("cell-hover");
                            this.$refs.cell.addEventListener("click", this.votePlayer);
                        } else if (
                            this.gameStatus.phase === "DAY_ELIMINATION" &&
                            this.playersGameInfo.suspicious === true
                        ) {
                            this.$refs.cell.classList.add("cell-hover");
                            this.$refs.cell.addEventListener("click", this.votePlayer);
                        } else if (this.gameStatus.phase === "NIGHT_VOTE") {
                            if (this.role === "MAFIA" && this.playersGameInfo.isMafia === false) {
                                this.$refs.cell.classList.add("cell-hover");
                                this.$refs.cell.addEventListener("click", this.votePlayer);
                            } else if (this.role === "DOCTOR") {
                                this.$refs.cell.classList.add("cell-hover");
                                this.$refs.cell.addEventListener("click", this.votePlayer);
                            } else if (this.role === "POLICE") {
                                if (this.playerMe === false) {
                                    this.$refs.cell.classList.add("cell-hober");
                                    this.$refs.cell.addEventListener("click", this.votePlayer);
                                }
                            }
                        }
                    }
                }
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
.talking-border {
    border-style: solid;
    border-color: #00ff26;
    border-width: thick;
}
</style>
