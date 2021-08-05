<template>
    <div>
        <nav class="navbar navbar-expand-sm navbar-light bg-light">
            <div>
                <img src="../../../assets/image/sun.png" id="sun-logo" />
                <span id="sun-logo-title" class="font-jua">{{ getDate }}</span>
            </div>
            <div style="margin-left: 70px">
                <img
                    src="https://www.clipartmax.com/png/full/110-1108103_home-expansion-syndicate-mafia-logo.png"
                    alt=""
                    style="width: 50px; margin-right: 2% display: inline-block"
                />
                <span id="alive-mafia-num" class="font-jua">{{ this.gameStatus.aliveMafia }}</span>
            </div>

            <!-- playerNum가 4 이상 됐을 때 활성화 되기 -->
            <!-- <button
                v-if="this.gameStatus.phase == 'READY' && this.currentPlayerNum < 2"
                class="font-jua"
                id="start-button-inactive"
            >
                게임 준비
            </button> -->
            <button
                v-if="isHost && this.gameStatus.phase == 'READY'"
                @click="gameStart"
                type="button"
                class="font-jua"
                id="start-button"
            >
                게임 시작
            </button>
            <button v-else @click="confirmVote" type="button" class="font-jua" id="confirm-button">
                투표 확정
            </button>

            <span id="timer" class="font-jua">{{ this.time }}</span>
            <i @click="openRuleBook" class="fas fa-info-circle fa-2x" id="rule-book"></i>
        </nav>

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
export default {
    name: "NavHeader",
    props: {
        currentPlayerNum: Number,
        isHost: Boolean,
        gameStatus: Object,
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
                }
            },
        },
    },
    data() {
        return {
            time: this.gameStatus.timer,
            leftTime: 0, //progress bar를 위한 남은 시간(0 -> 100)
            multiplier: 0,
            minPlayerNumToPlay: 4,
            clickStartButton: false, //한번 start button 누르면 끝날때까지 비활성화
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
                return "DAY " + this.gameStatus.date;
            }
        },
    },
    created() {
        // this.isHost = true;
    },
    methods: {
        openRuleBook() {},
        gameStart() {
            //Game.vue에 게임 시작 전송
            console.log("start");
            this.$emit("gameStart");
        },
        startCountDown() {
            if (!this.clickStartButton) {
                this.multiplier = 100 / this.gameStatus.timer; //곱해줄 값 구하기
                this.time = this.gameStatus.timer; // 시간 구하기
                this.leftTime = 0;

                console.log("multiplier", this.multiplier);
                console.log("time", this.time);
                console.log("leftTime", this.leftTime);
                console.log("maxTime", this.gameStatus.timer);

                this.clickStartButton = true;
                setTimeout(() => {
                    this.countDownTimer();
                }, 200);
            }
        },
        countDownTimer() {
            var interval = setInterval(() => {
                this.time -= 1;
                this.leftTime = this.gameStatus.timer - this.time;
            }, 1000);

            setTimeout(() => {
                clearInterval(interval);
                this.leftTime = 0;
                this.clickStartButton = false; //다시 start button 활성화
            }, 1000 * this.gameStatus.timer);
        },
    },
};
</script>
