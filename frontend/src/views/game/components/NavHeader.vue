<template>
    <div>
        <nav class="navbar navbar-expand-sm navbar-light bg-light">
            <div>
                <img src="../../../assets/image/sun.png" id="sun-logo" />
                <span id="sun-logo-title" class="font-jua">READY</span>
            </div>
            <div style="margin-left: 70px">
                <img
                    src="https://www.clipartmax.com/png/full/110-1108103_home-expansion-syndicate-mafia-logo.png"
                    alt=""
                    style="width: 50px; margin-right: 2% display: inline-block"
                />
                <span id="alive-mafia-num" class="font-jua">2</span>
            </div>

            <!-- playerNum가 4 이상 됐을 때 활성화 되기 -->
            <button @click="startCountDown" type="button" class="font-jua" id="start-button">
                게임 시작
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
    data() {
        return {
            maxTime: 100, //최대시간
            time: this.maxTime, //표시될 시간(100 -> 0)
            leftTime: 0, //progress bar를 위한 남은 시간(0 -> 100)
            multiplier: 0,
        };
    },
    computed: {
        getLeftTime() {
            return this.leftTime * this.multiplier;
        },
    },
    created() {
        this.multiplier = 100 / this.maxTime;
    },
    methods: {
        openRuleBook() {},
        startCountDown() {
            this.time = this.maxTime;
            this.leftTime = 0;
            this.$emit("setTime", this.leftTime);
            setTimeout(() => {
                this.countDownTimer();
            }, 200);
        },
        countDownTimer() {
            var interval = setInterval(() => {
                this.time -= 1;
                this.leftTime = this.maxTime - this.time;
            }, 1000);

            setTimeout(() => {
                clearInterval(interval);
            }, 1000 * this.maxTime);
        },
    },
};
</script>
