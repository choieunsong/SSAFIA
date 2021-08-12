<template>
    <div id="animtaion-wrap" ref="background">
        <div v-if="this.phase != 'NIGHT_VOTE'" class="sun" ref="sun"></div>
        <div v-if="this.phase == 'NIGHT_VOTE'" class="moon" ref="moon">
            <div class="carter first"></div>
            <div class="carter second"></div>
            <div class="carter third"></div>
        </div>
        <div class="x1" ref="c1">
            <div v-if="this.phase != 'NIGHT_VOTE'" class="cloud c1"></div>
        </div>

        <div class="x2" ref="c2">
            <div v-if="this.phase != 'NIGHT_VOTE'" class="cloud c2"></div>
        </div>

        <div class="x3" ref="c3">
            <div v-if="this.phase != 'NIGHT_VOTE'" class="cloud c3"></div>
        </div>

        <div class="x4" ref="c4">
            <div v-if="this.phase != 'NIGHT_VOTE'" class="cloud c4"></div>
        </div>

        <div class="x5" ref="c5">
            <div v-if="this.phase != 'NIGHT_VOTE'" class="cloud c5"></div>
        </div>
        <!-- <div v-if="this.phase == 'NIGHT_VOTE'"> -->
        <div v-if="this.phase == 'NIGHT_VOTE'">
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
            <div class="star" ref="star"></div>
        </div>
    </div>
</template>
<script>
import "./background.scss";
export default {
    name: "Background",
    props: {
        phase: String,
    },
    data() {
        return {
            p: true,
            firstNight: true,
        };
    },
    methods: {
        pclick() {
            if (this.p) {
                this.p = false;
            } else {
                this.p = true;
            }
            console.log("ppppp", this.p);
        },
    },
    mounted() {
        this.$refs.background.classList.add("animation-day");
    },
    watch: {
        phase: {
            handler() {
                if (this.phase == "READY") {
                    this.$refs.background.classList.add("animation-day");
                }
                if (this.phase == "START") {
                    // this.$refs.sun.classList.add("sun-rise");
                    // this.$refs.c1.classList.add("move-cloud");
                    // this.$refs.c2.classList.add("move-cloud");
                    // this.$refs.c3.classList.add("move-cloud");
                    // this.$refs.c4.classList.add("move-cloud");
                    // this.$refs.c5.classList.add("move-cloud");
                } else if (this.phase == "DAY_ELIMINATION") {
                    console.log("day elimination");
                    this.$refs.background.classList.remove("animation-day");
                    this.$refs.background.classList.add("animation-day-elimination");
                } else if (this.phase == "NIGHT_VOTE") {
                    this.$refs.background.classList.remove("animation-day");
                    this.$refs.background.classList.remove("animation-day-elimination");

                    this.$refs.background.classList.add("animation-night-vote");
                } else if (this.phase == "NIGHT_TO_DAY") {
                    this.$refs.background.classList.add("animation-day");
                    this.$refs.background.classList.remove("animation-night-vote");
                }
            },
        },
    },
};
</script>
<style>
.sun-rise {
    animation: suntrack 30s infinite forwards linear, sun-effect 2s alternate infinite;
}

.moon-rise {
    animation: suntrack 30s infinite forwards linear, moon-effect 2s alternate infinite;
}

.move-cloud {
    animation: animateCloud 105s linear alternate infinite;
}

.move-cloud {
    animation: animateCloud 105s linear alternate infinite;
}

.animation-pause {
    animation-play-state: paused;
}

.animation-running {
    animation-play-state: running;
}

.temp {
    z-index: 1000;
    background-color: red;
    width: 100px;
    height: 100px;
    position: absolute;
    left: 0px;
}
</style>
