<template>
    <video v-if="this.alive" autoplay class="video-size" />
    <div v-else class="ghost">
        <img
            class="ghost-img"
            src="https://cdn2.iconfinder.com/data/icons/gaming-entertainment-vol-3/64/ghost-2-512.png"
        />
    </div>
</template>

<script>
export default {
    name: "OvVideo",

    props: {
        streamManager: Object,
        alive: Boolean,
        phase: String,
    },

    mounted() {
        if (this.streamManager.stream) {
            this.streamManager.addVideoElement(this.$el);
        }
    },
    updated() {
        if (this.streamManager.stream) {
            this.streamManager.addVideoElement(this.$el);
        }
    },
};
</script>
<style>
.video-size {
    position: absolute;
    left: 0;
    width: 100%;
    height: 100%;
}

.ghost {
    position: absolute;
    width: 100%;
    height: 100%;
    background-color: black;
    animation-duration: 1s;
    animation-name: slide-in-bottom;
}

.ghost-img {
    position: absolute;
    height: 70%;
    left: 50%;
    top: 50%;
    transform: translate(-50%, -50%);
}
@keyframes slide-in-bottom {
    0% {
        transform: translateY(15vh);
        opacity: 0;
    }
    100% {
        transform: translateY(0);
        opacity: 1;
    }
}

@keyframes fade-in {
    0% {
        opacity: 0;
    }
    100% {
        opacity: 1;
    }
}
</style>
