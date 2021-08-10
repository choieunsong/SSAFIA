<template>
    <!-- overlay-->
    <div class="overlay" v-if="this.showRuleBook" @click="closeRuleBook()">
        <!-- rulebook -->
        <div class="rulebook-modal font-jua" v-if="this.showRuleBook">
            <i class="far fa-times-circle fa-2x rulebook-close" @click="closeRuleBook"></i>
            <h3 class="mt-3">싸피아 규칙</h3>
            <div class="rulebook-modal-description">
                <p>
                    <span style="color: tomato; font-size: 25px;">마피아</span>: 시민 수가 마피아 수
                    이하이면 <span style="color: lightCoral;">마피아 측의 승리</span>입니다. 밤
                    투표동안 마피마들끼리 소통하여 시민을 제거하세요.
                </p>
                <p>
                    <span style="color: tomato; font-size: 24px;">경찰</span>: 밤이 되면 마피아로
                    의심되는 플레이어를 투표해 해당 플레이어가 마피아인지 시민인지 알 수 있습니다.
                </p>
                <p>
                    <span style="color: tomato; font-size: 24px;">의사</span>: 밤이 되면
                    마피아로부터 보호하고 싶은 플레이어를 선택하면 해당 플레이어는 그날 제거되지
                    않습니다.
                </p>
                <p>
                    <span style="color: tomato; font-size: 24px;">시민</span>: 마피아, 경찰, 의사
                    외의 모든 플레이어로 구성됩니다. 마피아를 전부 제거하면
                    <span style="color: lightCoral;"> 시민 측의 승리</span>입니다. 낮이 되면
                    마피아로 의심되는 플레이어를 투표해 제거할 수 있습니다.
                </p>
                <p>
                    <span style="color: #ADFF2F; font-size: 24px;">게임시작</span>: 방장은 플레이어
                    4인 이상이 모이면 게임시작 버튼을 누를 수 있습니다.
                </p>
                <p>
                    <span style="color: #ADFF2F; font-size: 24px;">투표확정</span>: 투표확정 버튼을
                    누르면 재투표 하실 수 없습니다.
                </p>
                <p>
                    <span style="color: #6495ED; font-size: 24px;">낮 토론 시간</span>: 100초동안
                    플레이어들끼리 소통하며 마피아로 의심되는 플레이어를 투표해주세요. 플레이어
                    화면을 클릭하면 투표하실 수 있습니다. 시간 안에 투표하지 않으면 무효표로
                    처리됩니다.
                </p>
                <p>
                    <span style="color: #6495ED; font-size: 24px;">최종 변론 시간</span>: 지목된
                    플레이어는 최종 변론을 하게 됩니다. 다른 플레이어는 변론자의 화면을 클릭해
                    <span style="color: #87CEFA">KILL</span>할 수 있습니다. 클릭하지 않으면 SAVE로
                    간주됩니다.
                </p>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name: "RuleBook",
    props: {
        showRuleBook: Boolean,
    },
    methods: {
        closeRuleBook() {
            this.$emit("closeRuleBook");
        },
    },
};
</script>

<style lang="scss" scoped></style>
