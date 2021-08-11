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
        this.streamManager.addVideoElement(this.$el);
    },
    updated() {
        this.streamManager.addVideoElement(this.$el);
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
}

.ghost-img {
    position: absolute;
    height: 70%;
    left: 50%;
    top: 50%;
    transform: translate(-50%, -50%);
}
</style>
