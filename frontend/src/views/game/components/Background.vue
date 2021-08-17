<template>
    <div id="animtaion-wrap" class="js-container" ref="background">
        <div
            v-if="this.phase != 'NIGHT_VOTE'"
            class="sun"
            ref="sun"
            :style="[
                this.phase == 'READY' || this.phase == 'END'
                    ? 'animation-play-state: paused;'
                    : 'animation-play-state: running;',
            ]"
        ></div>
        <div v-if="this.phase == 'NIGHT_VOTE'" class="moon" ref="moon">
            <div class="carter first"></div>
            <div class="carter second"></div>
            <div class="carter third"></div>
        </div>
        <div
            class="x1"
            ref="c1"
            :style="[
                this.phase == 'READY' || this.phase == 'END'
                    ? 'animation-play-state: paused;'
                    : 'animation-play-state: running;',
            ]"
        >
            <div v-if="this.phase != 'NIGHT_VOTE'" class="cloud c1"></div>
        </div>

        <div
            class="x2"
            ref="c2"
            :style="[
                this.phase == 'READY' || this.phase == 'END'
                    ? 'animation-play-state: paused;'
                    : 'animation-play-state: running;',
            ]"
        >
            <div v-if="this.phase != 'NIGHT_VOTE'" class="cloud c2"></div>
        </div>

        <div
            class="x3"
            ref="c3"
            :style="[
                this.phase == 'READY' || this.phase == 'END'
                    ? 'animation-play-state: paused;'
                    : 'animation-play-state: running;',
            ]"
        >
            <div v-if="this.phase != 'NIGHT_VOTE'" class="cloud c3"></div>
        </div>

        <div
            class="x4"
            ref="c4"
            :style="[
                this.phase == 'READY' || this.phase == 'END'
                    ? 'animation-play-state: paused;'
                    : 'animation-play-state: running;',
            ]"
        >
            <div v-if="this.phase != 'NIGHT_VOTE'" class="cloud c4"></div>
        </div>

        <div
            class="x5"
            ref="c5"
            :style="[
                this.phase == 'READY'
                    ? 'animation-play-state: paused;'
                    : 'animation-play-state: running;',
            ]"
        >
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
            confettiInterval: null,
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
                    if (this.$refs.background.classList.contains("animation-day-elimination")) {
                        this.$refs.background.classList.remove("animation-day-elimination");
                    }
                    if (this.$refs.background.classList.contains("animation-night-vote")) {
                        this.$refs.background.classList.remove("animation-night-vote");
                    }
                } else if (this.phase == "DAY_ELIMINATION") {
                    console.log("day elimination");
                    this.$refs.background.classList.remove("animation-day");
                    this.$refs.background.classList.add("animation-day-elimination");
                } else if (this.phase == "NIGHT_VOTE") {
                    if (this.$refs.background.classList.contains("animation-day")) {
                        this.$refs.background.classList.remove("animation-day");
                    }
                    if (this.$refs.background.classList.contains("animation-day-elimination")) {
                        this.$refs.background.classList.remove("animation-day-elimination");
                    }
                    this.$refs.background.classList.add("animation-night-vote");
                } else if (this.phase == "NIGHT_TO_DAY") {
                    this.$refs.background.classList.add("animation-day");
                    this.$refs.background.classList.remove("animation-night-vote");
                } else if (this.phase == "END") {
                    if (this.$refs.background.classList.contains("animation-day-elimination")) {
                        this.$refs.background.classList.remove("animation-day-elimination");
                    }
                    if (this.$refs.background.classList.contains("animation-night-vote")) {
                        this.$refs.background.classList.remove("animation-night-vote");
                    }
                    console.log("PHASE END");
                    this.$refs.background.classList.add("animation-day");

                    const Confettiful = function(el) {
                        this.el = el;
                        this.containerEl = null;

                        this.confettiFrequency = 3;
                        this.confettiColors = ["#fce18a", "#ff726d", "#b48def", "#f4306d"];
                        this.confettiAnimations = ["slow", "medium", "fast"];

                        this._setupElements();
                        this._renderConfetti();
                    };

                    Confettiful.prototype._setupElements = function() {
                        const containerEl = document.createElement("div");
                        const elPosition = this.el.style.position;

                        containerEl.classList.add("confetti-container");

                        this.el.appendChild(containerEl);

                        this.containerEl = containerEl;
                    };

                    Confettiful.prototype._renderConfetti = function() {
                        this.confettiInterval = setInterval(() => {
                            const confettiEl = document.createElement("div");
                            const confettiSize = Math.floor(Math.random() * 3) + 10 + "px";
                            const confettiBackground = this.confettiColors[
                                Math.floor(Math.random() * this.confettiColors.length)
                            ];
                            const confettiLeft =
                                Math.floor(Math.random() * this.el.offsetWidth) + "px";
                            const confettiAnimation = this.confettiAnimations[
                                Math.floor(Math.random() * this.confettiAnimations.length)
                            ];

                            confettiEl.classList.add(
                                "confetti",
                                "confetti--animation-" + confettiAnimation
                            );
                            confettiEl.style.left = confettiLeft;
                            confettiEl.style.width = confettiSize;
                            confettiEl.style.height = confettiSize;
                            confettiEl.style.backgroundColor = confettiBackground;

                            confettiEl.removeTimeout = setTimeout(function() {
                                confettiEl.parentNode.removeChild(confettiEl);
                            }, 4000);

                            this.containerEl.appendChild(confettiEl);
                        }, 25);
                    };

                    window.confettiful = new Confettiful(document.querySelector(".js-container"));
                    console.log(window.confettiful);
                    setTimeout(() => {
                        console.log("remove confetti");
                        var elem = document.querySelector(".confetti-container");
                        elem.parentNode.removeChild(elem);
                    }, 15000);
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

.animation-pause {
    animation-play-state: paused;
}

.animation-running {
    animation-play-state: running;
}
</style>
