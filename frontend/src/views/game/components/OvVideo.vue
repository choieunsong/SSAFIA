<template>
    <video v-if="this.alive" autoplay class="video-size" />
    <div v-else class="ghost">
        <img src="https://cdn.pixabay.com/photo/2017/05/12/20/26/spirit-2308072_960_720.png" />
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
</style>
