<template>
    <div id="animtaion-wrap" ref="background">
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
    mounted() {
        this.$refs.background.classList.add("animation-day");
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
                } else if (this.phase == "DAY_ELIMINATION") {
                    console.log("day elimination");
                    this.$refs.background.classList.remove("animation-day");
                    this.$refs.background.classList.add("animation-day-elimination");
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
    animation: suntrack 30s infinite forwards linear, sun-effect 2s alternate infinite;
}

.move-cloud {
    animation: animateCloud 105s linear alternate infinite;
}
</style>
