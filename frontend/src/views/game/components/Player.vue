<template>
    <div class="section container-fluid">
        <!--정보박스-->
        <div id="info-box" class="font-jua">
            <span class="info-text">최소 4명부터 게임을 시작할 수 있습니다.</span>
        </div>

        <div class="card-box-parent">
            <!--첫번째 줄-->
            <div class="card-box row gx-5 d-flex" :class="getJustifyClassFirstRow">
                <user-video :playerName="subscribeName2" @setVoteData="setVoteData"></user-video>
                <user-video :playerName="subscribeName3" @setVoteData="setVoteData"></user-video>
                <user-video :playerName="subscribeName6" @setVoteData="setVoteData"></user-video>
                <user-video :playerName="subscribeName7" @setVoteData="setVoteData"></user-video>
            </div>

            <!-- 두번째 줄 -->
            <div class="card-box row gx-5">
                <user-video :playerName="subscribeName8" @setVoteData="setVoteData"></user-video>
                <user-video
                    :playerName="subscribeName9"
                    @setVoteData="setVoteData"
                    class="offset-md-6"
                ></user-video>
            </div>

            <!-- 세번째 줄 -->
            <div class="card-box row gx-5 d-flex" :class="getJustifyClassThirdRow">
                <user-video :playerName="subscribeName4" @setVoteData="setVoteData"></user-video>
                <user-video
                    :playerName="this.publisher"
                    id="video-mine"
                    @setVoteData="setVoteData"
                ></user-video>
                <user-video :playerName="subscribeName5" @setVoteData="setVoteData"></user-video>
                <user-video :playerName="subscribeName10" @setVoteData="setVoteData"></user-video>
            </div>
        </div>
    </div>
    <!-- <button @click="enterRoom">+</button>
    <button @click="leaveRoom">-</button> -->
</template>
<script>
import "./player.css";
import UserVideo from "./UserVideo";

class Player {
    name = null;
    color = null;
    voters = [];
    constructor(name, color) {
        this.name = name;
        this.color = color;
    }
}

var colorCode = [
    "#800080",
    "#DFFF00",
    "#FFBF00",
    "#FF7F50",
    "#DE3163",
    "#9FE2BF",
    "#40E0D0",
    "#6495ED",
    "#CCCCFF",
    "#808080",
    "#000000",
];

export default {
    name: "Player",
    components: {
        UserVideo,
    },
    data() {
        return {
            subscribers: [
                new Player("이현정", colorCode[1]),
                new Player("유태규", colorCode[2]),
                new Player("김용훈", colorCode[3]),
                new Player("김지훈", colorCode[4]),
            ],
            publisher: "String",
            playerNum: 1,
            subscribeName2: "",
            subscribeName3: "",
            subscribeName4: "",
            subscribeName5: "",
            subscribeName6: "",
            subscribeName7: "",
            subscribeName8: "",
            subscribeName9: "",
            subscribeName10: "",
        };
    },
    computed: {
        getJustifyClassFirstRow: function() {
            if (this.playerNum == 2) {
                console.log("justift center", this.playerNum);
                return "justify-content-center";
            } else {
                console.log("justift between", this.playerNum);
                return "justify-content-between";
            }
        },
        getJustifyClassThirdRow: function() {
            if (this.playerNum <= 3) {
                console.log("justift center", this.playerNum);
                return "justify-content-center";
            } else {
                console.log("justift between", this.playerNum);
                return "justify-content-between";
            }
        },
    },
    created() {
        console.log("mounted");
        this.publisher = new Player("최은송", colorCode[0]);

        //subscribers 초기화
        let i;
        for (i = 0; i < this.subscribers.length; i++) {
            this.playerNum += 1;
            console.log(i + 2);

            this.passProps(i, true);
        }

        //icon test
        this.subscribers[0].voters.push({
            playerId: "유태규",
            color: colorCode[2],
        });
    },
    methods: {
        leaveRoom() {
            if (this.playerNum == 1) return;

            let removeIdx = Math.floor(Math.random() * this.subscribers.length);
            console.log("removeIdx", removeIdx);
            if (removeIdx > -1) {
                this.subscribers.splice(removeIdx, 1);
                this.passProps(removeIdx, false);

                let idx = removeIdx;
                for (; idx < this.subscribers.length; idx++) {
                    this.passProps(idx + 1, false);
                    this.passProps(idx, true);
                }
            }

            this.playerNum -= 1;
        },
        enterRoom() {
            let idx = this.subscribers.length;
            console.log("idx", idx);
            this.subscribers.push(new Player("최최최", colorCode[idx + 1]));
            this.passProps(idx, true);

            this.playerNum += 1;
        },
        passProps(i, flag) {
            switch (i + 2) {
                case 2:
                    this.subscribeName2 = flag ? this.subscribers[i] : "";
                    // console.log(2);
                    break;
                case 3:
                    this.subscribeName3 = flag ? this.subscribers[i] : "";
                    // console.log(3);
                    break;
                case 4:
                    this.subscribeName4 = flag ? this.subscribers[i] : "";
                    // console.log(4);
                    break;
                case 5:
                    this.subscribeName5 = flag ? this.subscribers[i] : "";
                    // console.log(5);
                    break;
                case 6:
                    this.subscribeName6 = flag ? this.subscribers[i] : "";
                    // console.log(6);
                    break;
                case 7:
                    this.subscribeName7 = flag ? this.subscribers[i] : "";
                    // console.log(7);
                    break;
                case 8:
                    this.subscribeName8 = flag ? this.subscribers[i] : "";
                    // console.log(8);
                    break;
                case 9:
                    this.subscribeName9 = flag ? this.subscribers[i] : "";
                    // console.log(9);
                    break;
                case 10:
                    this.subscribeName10 = flag ? this.subscribers[i] : "";
                    // console.log(10);
                    break;
            }
        },
        setVoteData(data) {
            console.log(data);
            // 투표한 playerId를 찾아서 그사람의 voted에 추가해줌

            let idx = 0;
            for (; idx < this.subscribers.length; idx++) {
                if (this.subscribers[idx].name == data.playerId) {
                    // 첫 투표라면 => 투표하기
                    if (data.isFirstVote) {
                        this.subscribers[idx].voters.push({
                            playerId: this.publisher.name,
                            color: this.publisher.color,
                        });
                        this.passProps(idx, true);
                    } else {
                        //두번째 클릭 => 투표취소

                        let result = this.subscribers[idx].voters.findIndex(
                            (voter) => voter.playerId == this.publisher.name
                        );
                        console.log(result);
                        this.subscribers[idx].voters.splice(result, 1);
                        console.log(this.subscribers);
                        this.passProps(idx, true);
                    }
                    break;
                }
            }

            console.log(this.subscribers);
        },
    },
};
</script>
