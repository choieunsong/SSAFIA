<template>
    <video autoplay class="video-size" />
</template>

<script>
export default {
    name: "OvVideo",

    props: {
        streamManager: Object,
    },

    mounted() {
        this.streamManager.addVideoElement(this.$el);
    },
};
</script>
<style>
.video-size {
    /* position: absolute;
    width: 93%;

    left: 10px;
    top: 10px; */
    position: absolute;
    left: 0;
    width: 100%;
    height: 100%;
}
</style>
