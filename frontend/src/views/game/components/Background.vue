<template>
    <div id="animtaion-wrap">
        <svg xmlns="http://www.w3.org/2000/svg" id="animation-body">
            <!-- <linearGradient
                v-if="this.phase == 'START'"
                id="skyGradient"
                x1="0%"
                y1="0%"
                x2="0%"
                y2="100%"
            >
                <stop stop-color="rgb(0,0,12)" offset="0%" id="zenith">
                    <animate
                        attributeName="stop-color"
                        dur="20s"
                        values="rgba(0,0,12,0);rgba(2,1,17,0);rgba(2,1,17,0);rgba(2,1,17,0);rgba(32,32,44,0.5);rgba(64,64,92,.8);rgb(74,73,105);rgb(117,122,191);rgb(130,173,219);rgb(148,197,248);rgb(183,234,255);rgb(144,223,254);rgb(87,193,235);rgb(45,145,194);rgb(36,115,171);rgb(30,82,142);rgb(30,82,142);rgb(21,66,119);rgba(22,60,82,0.8);rgba(7,27,38,.5);rgba(1,10,16,.3);rgba(9,4,1,0);rgba(0,0,12,0);rgba(0,0,12,0)"
                        repeatCount="indefinite"
                    />
                    <animate
                        attributeName="offset"
                        dur="20s"
                        values="0;.85;.6;.1;0;0;0;0;0;.01;0;0;0;0;0;0;0;0;0;0;.3,.5,.8,0"
                        repeatCount="indefinite"
                    />
                </stop>

                <stop stop-color="rgb(0,0,12)" offset="100%" id="horizon">
                    <animate
                        attributeName="stop-color"
                        dur="20s"
                        values="rgba(0,0,12,0);rgba(25,22,33,.3);rgba(32,32,44,.8);rgb(58,58,82);rgb(81,81,117);rgb(138,118,171);rgb(205,130,160);rgb(234,176,209);rgb(235,178,177);rgb(177,181,234);rgb(148,223,255);rgb(103,209,251);rgb(56,163,209);rgb(36,111,168);rgb(30,82,142);rgb(91,121,131);rgb(157,166,113);rgb(233,206,93);rgb(178,99,57);rgb(47,17,7);rgb(36,14,3);rgb(47,17,7);rgba(75,29,6,.4);rgba(21,8,0,0);rgba(0,0,12,0)"
                        repeatCount="indefinite"
                    />
                </stop>
            </linearGradient>
            <rect id="sky" x="0" y="0" width="100%" height="100%" style="fill:url(#skyGradient)" /> -->
        </svg>
        <div class="sun" ref="sun"></div>
        <div class="x1" ref="c1">
            <div class="cloud c1"></div>
        </div>

        <div class="x2" ref="c2">
            <div class="cloud c2"></div>
        </div>

        <div class="x3" ref="c3">
            <div class="cloud c3"></div>
        </div>

        <div class="x4" ref="c4">
            <div class="cloud c4"></div>
        </div>

        <div class="x5" ref="c5">
            <div class="cloud c5"></div>
        </div>
    </div>
</template>
<script>
import "./background.css";
export default {
    name: "Background",
    props: {
        phase: String,
    },
    watch: {
        phase: {
            handler() {
                if (this.phase == "START") {
                    this.$refs.sun.classList.add("sun-rise");
                    this.$refs.c1.classList.add("move-cloud");
                    this.$refs.c2.classList.add("move-cloud");
                    this.$refs.c3.classList.add("move-cloud");
                    this.$refs.c4.classList.add("move-cloud");
                    this.$refs.c5.classList.add("move-cloud");
                } else if (this.phase == "NIGHT") {
                    this.$refs.sun.classList.remove("sun-rise");
                }
            },
        },
    },
};
</script>
<style>
.sun-rise {
    animation: suntrack 20s infinite forwards linear;
}

.move-cloud {
    -webkit-animation: animateCloud 105s linear infinite;
    -moz-animation: animateCloud 150s linear infinite;
    animation: animateCloud 105s linear infinite;
}
</style>
