<template>
    <div>
        <nav class="navbar navbar-expand-sm navbar-light bg-light">
            <div>
                <span class="logo"
                    ><img
                        src="../../../assets/image/sun.png"
                        class="sun-logo"
                        ref="sun"
                        :style="[
                            this.gameStatus.phase != 'NIGHT_VOTE' ? 'opacity: 1' : 'opacity: 0',
                        ]"
                /></span>
                <span class="logo"
                    ><img
                        src="../../../assets/image/moon.png"
                        class="moon-logo"
                        ref="moon"
                        :style="[
                            this.gameStatus.phase != 'NIGHT_VOTE' ? 'opacity: 0' : 'opacity: 1',
                        ]"
                /></span>
                <span id="sun-logo-title" class="font-jua">{{ getDate }}</span>
            </div>
            <div style="margin-left: 120px">
                <img
                    src="https://www.clipartmax.com/png/full/110-1108103_home-expansion-syndicate-mafia-logo.png"
                    alt=""
                    style="width: 50px; margin-right: 2% display: inline-block"
                />
                <span id="alive-mafia-num" class="font-jua">{{ this.gameStatus.aliveMafia }}</span>
            </div>

            <!-- playerNum가 4 이상 됐을 때 활성화 되기 -->
            <button
                v-if="
                    (this.currentPlayerNum < 4 || !this.isHost) && this.gameStatus.phase == 'READY'
                "
                class="font-jua"
                id="start-button-inactive"
            >
                게임 준비
            </button>
            <button
                v-else-if="this.isHost && this.gameStatus.phase == 'READY'"
                @click="gameStart"
                type="button"
                class="font-jua"
                id="start-button"
                ref="start"
            >
                게임 시작
            </button>
            <button
                v-else
                @click="confirmVote"
                type="button"
                class="font-jua unhover"
                id="confirm-button"
                ref="confirm"
            >
                투표 확정
            </button>

            <span id="timer" class="font-jua">{{ this.time }}</span>
            <i
                @click="this.showRuleBook = true"
                class="fas fa-info-circle fa-2x"
                id="rule-book"
            ></i>
        </nav>

        <!-- rulebook -->
        <rule-book
            :showRuleBook="this.showRuleBook"
            @closeRuleBook="this.showRuleBook = false"
        ></rule-book>
        <!-- progress bar -->
        <div class="progress">
            <div
                class="progress-bar"
                role="progressbar"
                :style="'width: ' + getLeftTime + '%'"
                aria-valuenow="100"
                aria-valuemin="0"
                aria-valuemax="100"
            ></div>
        </div>
    </div>
</template>

<script>
import "./navheader.css";
import RuleBook from "./RuleBook.vue";

export default {
    name: "NavHeader",
    components: {
        RuleBook,
    },
    props: {
        currentPlayerNum: Number,
        isHost: Boolean,
        gameStatus: Object,
        finishStartAnimation: Boolean,
        playerMe: Object,
        role: String,
    },
    watch: {
        gameStatus: {
            deep: true,
            handler() {
                console.log("watch change", this.gameStatus.phase);
                if (this.gameStatus.phase == "READY") {
                    console.log("watch ready");
                } else if (this.gameStatus.phase == "START") {
                    console.log("watch start");
                    this.startCountDown();
                } else if (this.getAlive && this.gameStatus.phase == "DAY_DISCUSSION") {
                    this.$refs.confirm.classList.remove("unhover");
                    this.$refs.confirm.classList.add("confirm-button-active");
                    this.startCountDown();
                } else if (this.getAlive && this.gameStatus.phase == "DAY_ELIMINATION") {
                    clearInterval(this.interval);
                    this.$refs.confirm.classList.remove("unhover");
                    this.$refs.confirm.classList.add("confirm-button-active");
                    this.startCountDown();
                } else if (this.gameStatus.phase == "DAY_TO_NIGHT") {
                    this.$refs.confirm.classList.add("unhover");
                    this.$refs.confirm.classList.remove("confirm-button-active");
                    this.startCountDown();
                } else if (this.gameStatus.phase == "NIGHT_VOTE") {
                    this.$refs.sun.classList.add("sun-logo-deactive");
                    this.$refs.moon.classList.add("moon-logo-active");
                    this.startCountDown();
                    if (this.role == "CIVILIAN" || !this.getAlive) {
                        this.$refs.confirm.classList.add("unhover");
                        this.$refs.confirm.classList.remove("confirm-button-active");
                    } else {
                        this.$refs.confirm.classList.remove("unhover");
                        this.$refs.confirm.classList.add("confirm-button-active");
                    }
                } else if (this.gameStatus.phase == "NIGHT_TO_DAY") {
                    this.$refs.sun.classList.remove("sun-logo-deactive");
                    this.$refs.moon.classList.remove("moon-logo-active");
                    this.$refs.sun.classList.add("sun-logo-active");
                    this.$refs.moon.classList.add("moon-logo-deactive");

                    this.$refs.confirm.classList.add("unhover");
                    this.$refs.confirm.classList.remove("confirm-button-active");
                    this.startCountDown();
                } else if (this.gameStatus.phase == "END") {
                    this.$refs.confirm.classList.add("unhover");
                    this.$refs.confirm.classList.remove("confirm-button-active");
                    this.startCountDown();
                }
            },
        },
    },
    data() {
        return {
            time: 0,
            leftTime: 0, //progress bar를 위한 남은 시간(0 -> 100)
            multiplier: 0,
            minPlayerNumToPlay: 4,
            clickStartButton: false,
            showRuleBook: false,
            interval: null,
        };
    },
    computed: {
        getLeftTime() {
            return this.leftTime * this.multiplier;
        },
        getDate() {
            if (this.gameStatus.phase == "READY") {
                return "READY";
            } else {
                if (this.gameStatus.phase !== "NIGHT_VOTE") {
                    return "DAY " + this.gameStatus.day;
                } else {
                    return "NIGHT " + this.gameStatus.day;
                }
            }
        },
        getAlive() {
            return this.playerMe.alive;
        },
    },
    methods: {
        gameStart() {
            //Game.vue에 게임 시작 전송
            console.log("start");

            //hover class 떼기
            this.$refs.start.classList.add("unhover");

            this.$emit("gameStart");
        },
        startCountDown() {
            if (this.interval) {
                clearInterval(this.interval);
            }

            this.multiplier = 100 / this.gameStatus.timer; //곱해줄 값 구하기
            this.time = this.gameStatus.timer; // 시간 구하기
            this.leftTime = 0;

            console.log("multiplier", this.multiplier);
            console.log("time", this.time);
            console.log("leftTime", this.leftTime);
            console.log("maxTime", this.gameStatus.timer);

            setTimeout(() => {
                this.countDownTimer();
            }, 200);
        },
        countDownTimer() {
            this.interval = setInterval(() => {
                if (this.leftTime == this.gameStatus.timer) {
                    clearInterval(this.interval);
                } else {
                    this.time -= 1;
                    this.leftTime = this.gameStatus.timer - this.time;
                }
            }, 1000);
        },
        confirmVote() {
            //투표 확정
            if (
                this.playerMe.alive &&
                (this.gameStatus.phase == "DAY_DISCUSSION" ||
                    this.gameStatus.phase == "DAY_ELIMINATION" ||
                    (this.gameStatus.phase == "NIGHT_VOTE" && this.playerMe.role != "CIVILIAN"))
            ) {
                this.$emit("emitConfirmDataUpdate");
                this.$refs.confirm.classList.add("unhover");
                this.$refs.confirm.classList.remove("confirm-button-active");
            }
        },
    },
};
</script>
<style>
.unhover {
    pointer-events: none;
    opacity: 0.7;
}
</style>
