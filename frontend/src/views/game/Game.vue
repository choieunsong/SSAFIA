<template>
    <div id="main-container">
        <background :phase="state.gameStatus.phase"></background>
        <!-- 헤더 -->
        <nav-header
            :currentPlayerNum="state.playerNum"
            :isHost="state.amIHost"
            :gameStatus="state.gameStatus"
            :finishStartAnimation="state.finishStartAnimation"
            @emitConfirmDataUpdate="emitConfirmDataUpdate"
            @gameStart="sendMessageStart"
        ></nav-header>

        <!-- 플레이어 비디오 -->
        <div class="container-fluid">
            <div class="card-box-parent">
                <!--첫번째 줄-->
                <div class="card-box row gx-5 d-flex" :class="getJustifyClassFirstRow">
                    <user-video
                        :stream-manager="state.subscribers[0]"
                        :playersGameInfo="state.playersGameInfo[0]"
                        :gameStatus="state.gameStatus"
                        :isConfirm="state.isConfirm"
                        :role="state.role"
                        :playerMe="false"
                        @emitVoteDataUpdate="emitVoteDataUpdate"
                    ></user-video>
                    <user-video
                        :stream-manager="state.subscribers[1]"
                        :playersGameInfo="state.playersGameInfo[1]"
                        :gameStatus="state.gameStatus"
                        :isConfirm="state.isConfirm"
                        :role="state.role"
                        :playerMe="false"
                        @emitVoteDataUpdate="emitVoteDataUpdate"
                    ></user-video>
                    <user-video
                        :stream-manager="state.subscribers[4]"
                        :playersGameInfo="state.playersGameInfo[4]"
                        :gameStatus="state.gameStatus"
                        :isConfirm="state.isConfirm"
                        :role="state.role"
                        :playerMe="false"
                        @emitVoteDataUpdate="emitVoteDataUpdate"
                    ></user-video>
                    <user-video
                        :stream-manager="state.subscribers[5]"
                        :playersGameInfo="state.playersGameInfo[5]"
                        :gameStatus="state.gameStatus"
                        :isConfirm="state.isConfirm"
                        :role="state.role"
                        :playerMe="false"
                        @emitVoteDataUpdate="emitVoteDataUpdate"
                    ></user-video>
                </div>

                <!-- 두번째 줄 -->
                <div class="card-box row gx-5">
                    <user-video
                        :stream-manager="state.subscribers[6]"
                        :playersGameInfo="state.playersGameInfo[6]"
                        :gameStatus="state.gameStatus"
                        :isConfirm="state.isConfirm"
                        :role="state.role"
                        :playerMe="false"
                        @emitVoteDataUpdate="emitVoteDataUpdate"
                    ></user-video>

                    <!--정보박스-->
                    <div id="info-box" class="font-jua typing-txt">
                        <span
                            class="info-text"
                            v-html="state.message"
                            :style="[
                                state.gameStatus.phase == 'READY'
                                    ? 'padding-top:50px'
                                    : ' vertical-align: middle; text-align: center; ',
                            ]"
                        ></span>
                        <br />
                        <span
                            class="info-text"
                            v-html="state.submessage"
                            :style="[' vertical-align: middle; text-align: center; ']"
                        ></span>

                        <div v-if="state.gameStatus.phase == 'READY'" class="url-copy-box">
                            <span class="url-title">친구를 초대해 보세요!</span>
                            <span class="url-copy-text">{{ state.inviteUrl }}</span>
                            <i class="fas fa-copy" id="url-copy-btn" @click="copyUrl"></i>
                            <input type="text" id="urlInput" />
                        </div>
                    </div>

                    <user-video
                        :stream-manager="state.subscribers[7]"
                        :playersGameInfo="state.playersGameInfo[7]"
                        :gameStatus="state.gameStatus"
                        :isConfirm="state.isConfirm"
                        :role="state.role"
                        :playerMe="false"
                        @emitVoteDataUpdate="emitVoteDataUpdate"
                        class="offset-md-6"
                    ></user-video>
                </div>

                <!-- 세번째 줄 -->
                <div class="card-box row gx-5 d-flex" :class="getJustifyClassThirdRow">
                    <user-video
                        :stream-manager="state.subscribers[2]"
                        :playersGameInfo="state.playersGameInfo[2]"
                        :gameStatus="state.gameStatus"
                        :isConfirm="state.isConfirm"
                        :role="state.role"
                        :playerMe="false"
                        @emitVoteDataUpdate="emitVoteDataUpdate"
                    ></user-video>
                    <user-video
                        :stream-manager="state.publisher"
                        :playersGameInfo="state.playerMe"
                        :gameStatus="state.gameStatus"
                        :isConfirm="state.isConfirm"
                        :role="state.role"
                        :playerMe="true"
                        id="video-mine"
                        @emitVoteDataUpdate="emitVoteDataUpdate"
                    ></user-video>
                    <user-video
                        :stream-manager="state.subscribers[3]"
                        :playersGameInfo="state.playersGameInfo[3]"
                        :gameStatus="state.gameStatus"
                        :isConfirm="state.isConfirm"
                        :role="state.role"
                        :playerMe="false"
                        @emitVoteDataUpdate="emitVoteDataUpdate"
                    ></user-video>
                    <user-video
                        :stream-manager="state.subscribers[8]"
                        :playersGameInfo="state.playersGameInfo[8]"
                        :gameStatus="state.gameStatus"
                        :isConfirm="state.isConfirm"
                        :role="state.role"
                        :playerMe="false"
                        @emitVoteDataUpdate="emitVoteDataUpdate"
                    ></user-video>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import axios from "axios";
import { OpenVidu } from "openvidu-browser";
import UserVideo from "@/views/game/components/UserVideo";
import { computed, reactive } from "vue";
import { useStore } from "vuex";
import { useRoute } from "vue-router";
import { API_CLIENT_URL } from "@/constant/index";
import { API_BASE_URL } from "@/constant/index";
import SockJS from "sockjs-client";
import Stomp from "webstomp-client";

import NavHeader from "@/views/game/components/NavHeader";
import Background from "@/views/game/components/Background";
import "./Game.css";

axios.defaults.headers.post["Content-Type"] = "application/json";

export default {
    name: "Game",
    components: {
        UserVideo,
        NavHeader,
        Background,
    },
    setup() {
        const route = useRoute();
        const store = useStore();
        const state = reactive({
            // openvidu 관련
            OV: undefined,
            session: undefined,
            publisher: undefined,
            subscribers: [],
            mySessionId: route.params.roomId,
            myUserName: undefined,
            openviduToken: undefined,
            playerId: undefined,

            // socket 연결 게임 데이터 관련
            amIHost: true,
            role: undefined,

            gameStatus: {
                day: 0,
                phase: "READY",
                timer: 0,
                aliveMafia: 0,
            },
            stompClient: undefined,
            jobClient: undefined,
            mafias: undefined,
            message: undefined,
            submessage: "",
            isConfirm: false,
            removeList: [],

            playerNum: 1,
            playerMe: {}, //publisher
            playersGameInfo: [], //player 정보 저장

            inviteUrl: "",
            //inviteurl 없애기
            closeInviteUrl: false,

            //tempPlayerMap
            tempPlayerMap: null,
            newSubscriberOn: false,
        });

        // 화상 채팅 관련
        // 세션 나가기
        var leaveSession = function() {
            // --- Leave the session by calling 'disconnect' method over the Session object ---
            if (state.session) state.session.disconnect();

            state.session = undefined;
            state.publisher = undefined;
            state.subscribers = [];
            state.OV = undefined;
        };

        // 세션 참가하기
        var joinSession = function() {
            // --- Get an OpenVidu object ---
            state.OV = new OpenVidu();

            // --- Init a session ---
            state.session = state.OV.initSession();

            // 새로운 player가 입장
            state.session.on("streamCreated", ({ stream }) => {
                console.log("~~~~~~new subscriber in~~~~~~");
                const subscriber = state.session.subscribe(stream);
                const array = subscriber.stream.connection.data.split('"');
                const tmp = array[3].split(",");
                subscriber.nickname = tmp[0];
                subscriber.playerId = tmp[1];

                if (state.gameStatus.phase == "READY") {
                    state.subscribers.push(subscriber);
                    console.log(state.subscribers);
                    //subscribers의 info 세팅
                    state.playersGameInfo.push({
                        playerId: tmp[1],
                        nickname: tmp[0],
                        alive: true,
                        suspicious: false,
                        voters: [],
                        isMafia: null,
                        color: null,
                        isHost: false,
                        role: null,
                    });
                    // 플레이어 수 1 증가
                    state.playerNum += 1;

                    //stomp에서 color 값이 먼저 들어왔으면 tempPlayerMap에서 갱신
                    state.newSubscriberOn = true;
                    if (state.tempPlayerMap !== null) {
                        let idx = state.playersGameInfo.length;
                        let playerId = state.playersGameInfo[idx - 1].playerId;
                        state.playersGameInfo[idx - 1].color = state.tempPlayerMap[playerId].color;
                        state.tempPlayerMap = null;
                        state.newSubscriberOn = false;
                    }
                } else {
                    let index = false;
                    for (let i = 0; i < state.subscribers.length; i++) {
                        if (subscriber.playerId === state.subscribers[i].playerId) {
                            index = i;
                            break;
                        }
                    }
                    if (index !== false) {
                        for (let j = 0; j < state.removeList.length; j++) {
                            if (state.removeList[j] === index) {
                                state.removeList.splice(j, 1);
                            }
                        }
                        state.subscribers.splice(index, 1, subscriber);
                    }
                }
            });

            // 플레이어 나갔을 때
            state.session.on("streamDestroyed", ({ stream }) => {
                if (state.gameStatus.phase === "READY") {
                    const index = state.subscribers.indexOf(stream.streamManager, 0);
                    console.log("!!!!!!stream destroyed", index);
                    if (index >= 0) {
                        state.subscribers.splice(index, 1);
                        state.playersGameInfo.splice(index, 1);
                    }
                    console.log(state.subscribers);
                    // 한명 제거
                    state.playerNum -= 1;
                } else {
                    const index = state.subscribers.indexOf(stream.streamManager, 0);
                    state.removeList.push(index);
                    console.log(state.removeList);
                }
            });

            state.session.on("exception", ({ exception }) => {
                console.warn(exception);
                // exception시 다시 연결하려는 시도 해보고 오류생기면 지워야할듯
                 state.session
                .connect(state.openviduToken, {
                    clientData: `${state.myUserName},${state.playerId}`,
                })
                .then(() => {
                    // --- Get your own camera stream with the desired properties ---

                    let publisher = state.OV.initPublisher(undefined, {
                        audioSource: undefined, // The source of audio. If undefined default microphone
                        videoSource: undefined, // The source of video. If undefined default webcam
                        publishAudio: false, // Whether you want to start publishing with your audio unmuted or not
                        publishVideo: true, // Whether you want to start publishing with your video enabled or not
                        resolution: "311x170", // The resolution of your video
                        frameRate: 30, // The frame rate of your video
                        insertMode: "APPEND", // How the video is inserted in the target element 'video-container'
                        mirror: false, // Whether to mirror your local video or not
                    });

                    state.publisher = publisher;

                    state.session.publish(state.publisher);

                    //내 정보 playerMe에 저장하기
                    state.playerMe = {
                        playerId: state.playerId,
                        nickname: state.myUserName,
                        alive: true,
                        suspicious: false,
                        voters: [],
                        color: "red",
                        isMafia: null,
                        isHost: false,
                        isTalking: false,
                        role: null,
                    };

                    state.newSubscriberOn = true;
                })
                .catch((error) => {
                    console.log(
                        "There was an error connecting to the session:",
                        error.code,
                        error.message
                    );
                });
            });

            state.session.on("publisherStartSpeaking", (event) => {
                const array = event.connection.data.split('"');
                const tmp = array[3].split(",");
                const targetPlayerId = tmp[1];
                console.log(targetPlayerId);
                console.log(state.playerId);
                if (state.playerId === targetPlayerId) {
                    console.log("playerMe");
                    state.playerMe.isTalking = true;
                    console.log(state.playerMe);
                } else {
                    for (let i = 0; i < state.playersGameInfo.length; i++) {
                        if (state.playersGameInfo[i].playerId === targetPlayerId) {
                            state.playersGameInfo[i].isTalking = true;
                            break;
                        }
                    }
                }
            });
            state.session.on("publisherStopSpeaking", (event) => {
                const array = event.connection.data.split('"');
                const tmp = array[3].split(",");
                const targetPlayerId = tmp[1];
                if (state.playerId === targetPlayerId) {
                    console.log("playerMe");
                    state.playerMe.isTalking = false;
                    console.log(state.playerMe);
                } else {
                    for (let i = 0; i < state.playersGameInfo.length; i++) {
                        if (state.playersGameInfo[i].playerId === targetPlayerId) {
                            state.playersGameInfo[i].isTalking = false;
                            break;
                        }
                    }
                }
            });

            state.session
                .connect(state.openviduToken, {
                    clientData: `${state.myUserName},${state.playerId}`,
                })
                .then(() => {
                    // --- Get your own camera stream with the desired properties ---

                    let publisher = state.OV.initPublisher(undefined, {
                        audioSource: undefined, // The source of audio. If undefined default microphone
                        videoSource: undefined, // The source of video. If undefined default webcam
                        publishAudio: false, // Whether you want to start publishing with your audio unmuted or not
                        publishVideo: true, // Whether you want to start publishing with your video enabled or not
                        resolution: "311x170", // The resolution of your video
                        frameRate: 30, // The frame rate of your video
                        insertMode: "APPEND", // How the video is inserted in the target element 'video-container'
                        mirror: false, // Whether to mirror your local video or not
                    });

                    state.publisher = publisher;

                    state.session.publish(state.publisher);

                    //내 정보 playerMe에 저장하기
                    state.playerMe = {
                        playerId: state.playerId,
                        nickname: state.myUserName,
                        alive: true,
                        suspicious: false,
                        voters: [],
                        color: "red",
                        isMafia: null,
                        isHost: false,
                        isTalking: false,
                        role: null,
                    };

                    state.newSubscriberOn = true;
                })
                .catch((error) => {
                    console.log(
                        "There was an error connecting to the session:",
                        error.code,
                        error.message
                    );
                });
        };

        // 게임 관련 소켓통신
        function onConnected() {
            state.message = `최소 <span style='font-size:25px;'>4인</span> 이상부터 플레이가 가능합니다.`;
            // 개인 채널 구독
            state.stompClient.subscribe(
                `/sub/${state.mySessionId}/${state.playerId}`,
                onPersonalMessageReceived
            );
            // 방 채널 구독
            state.stompClient.subscribe(`/sub/${state.mySessionId}`, onMessageReceived);

            // 구독했다고 서버에 알리기, 나갔다 오면 다른 경로로
            if (store.getters["ingame/getPhase"]) {
                const localPhase = store.getters["ingame/getPhase"];
                if (localPhase === "READY") {
                    state.stompClient.send(`/pub/${state.mySessionId}/join`, {});
                } else {
                    state.stompClient.send(`/pub/${state.mySessionId}/rejoin`);
                }
            } else {
                state.stompClient.send(`/pub/${state.mySessionId}/join`, {});
            }
        }
        // 에러시 할 것
        function onError(error) {
            console.log("websocket connection failed, try agin or change your code");
        }
        // 실제 연결
        function connect() {
            var socket = new SockJS(`${API_BASE_URL}/ws/gamesession`);
            state.stompClient = Stomp.over(socket);
            state.stompClient.connect({ playerId: state.playerId }, onConnected, onError);
        }
        // 투표 메세지 보내는 함수
        function sendMessageVote(targetPlayerId) {
            if (state.stompClient) {
                const Message = {
                    vote: targetPlayerId,
                    phase: state.gameStatus.phase,
                };
                console.log("send vote", JSON.stringify(Message));
                state.stompClient.send(
                    `/pub/${state.mySessionId}/vote`,
                    JSON.stringify(Message),
                    {}
                );
            }
        }
        // 투표 확정 메세지 보내는 함수
        function sendMessageConfirm() {
            if (state.stompClient) {
                const Message = {
                    phase: state.gameStatus.phase,
                };
                state.stompClient.send(
                    `/pub/${state.mySessionId}/confirm`,
                    JSON.stringify(Message),
                    {}
                );
            }
        }

        // 게임 시작 메세지 보내는 함수
        function sendMessageStart() {
            if (state.stompClient) {
                state.stompClient.send(`/pub/${state.mySessionId}/start`, {});
            }
            //응답 받고
            // startCountDownAnimation();
            state.closeInviteUrl = true;
        }

        // 밤 투표 메세지 보내는 함수
        function sendMessageNightVote(targetPlayerId) {
            if (state.stompClient) {
                const message = {
                    phase: state.gameStatus.phase,
                    vote: targetPlayerId,
                };
                state.stompClient.send(
                    `/pub/${state.mySessionId}/${state.role}/vote`,
                    JSON.stringify(message),
                    {}
                );
            }
        }

        // 밤 투표 확정 메세지 보내는 함수
        function sendMessageNightConfirm() {
            if (state.stompClient) {
                const message = {
                    phase: state.gameStatus.phase,
                };
                state.stompClient.send(
                    `/pub/${state.mySessionId}/${state.role}/confirm`,
                    JSON.stringify(message),
                    {}
                );
            }
        }

        // playersGameInfo 업데이트용 함수
        function infoUpdater(key, message) {
            console.log("infoUpdater key:", key, " message: ", message);
            if (key === "voters") {
                if (message === null) {
                    state.playerMe[key] = [];
                    for (let i = 0; i < state.playersGameInfo.length; i++) {
                        state.playersGameInfo[i][key] = [];
                    }
                } else {
                    // 내 voters 갱신하는 로직
                    let tmp = [];
                    if (message.playerMap[state.playerMe.playerId] === state.playerMe.playerId) {
                        tmp.push(state.playerMe.color);
                    }
                    for (let i = 0; i < state.playersGameInfo.length; i++) {
                        if (
                            message.playerMap[state.playersGameInfo[i].playerId] ===
                            state.playerMe.playerId
                        ) {
                            tmp.push(state.playersGameInfo[i].color);
                        }
                    }
                    state.playerMe[key] = tmp;
                    // subscribers voters 갱신하는 로직
                    for (let i = 0; i < state.playersGameInfo.length; i++) {
                        let tmp = [];
                        if (
                            message.playerMap[state.playerMe.playerId] ===
                            state.playersGameInfo[i].playerId
                        ) {
                            tmp.push(state.playerMe.color);
                        }
                        for (let j = 0; j < state.playersGameInfo.length; j++) {
                            if (
                                message.playerMap[state.playersGameInfo[j].playerId] ===
                                state.playersGameInfo[i].playerId
                            ) {
                                tmp.push(state.playersGameInfo[j].color);
                            }
                            state.playersGameInfo[i][key] = tmp;
                        }
                    }
                }
                console.log("vote result", message);
            } else if (key === "isHost") {
                if (state.playerId === message.hostId) {
                    state.amIHost = true;
                    state.playerMe.isHost = true;
                } else {
                    state.amIHost = false;
                    state.playerMe.isHost = false;
                }
                for (let i = 0; i < state.playersGameInfo.length; i++) {
                    if (state.playersGameInfo[i].playerId === message.hostId) {
                        state.playersGameInfo[i].isHost = true;
                    } else {
                        state.playersGameInfo[i].isHost = false;
                    }
                }
            } else {
                //color, suspicious, alive update
                if (message === null || typeof message == "boolean") {
                    state.playerMe[key] = message === null ? null : message;
                    for (let i = 0; i < state.playersGameInfo.length; i++) {
                        state.playersGameInfo[i][key] = message === null ? null : message;
                    }
                } else {
                    state.playerMe[key] = message.playerMap[state.playerMe.playerId][key];
                    console.log("newSubscriberOn", state.newSubscriberOn);
                    // 만약 openVidu보다 먼저 stomp 정보 들어오는 경우 temp에 저장
                    if (!state.newSubscriberOn) {
                        state.tempPlayerMap = message.playerMap;
                    } else {
                        // 순서대로 들어왔을 경우 그대로 갱신
                        for (let i = 0; i < state.playersGameInfo.length; i++) {
                            state.playersGameInfo[i][key] =
                                message.playerMap[state.playersGameInfo[i].playerId][key];
                        }
                        console.log("!!!!!!!!!playersGameInfo", state.playersGameInfo);
                        state.newSubscriberOn = false;
                    }
                }
            }
        }

        // 공통 채널에서 메세지를 받았을 경우 할 일
        function onMessageReceived(payload) {
            var message = JSON.parse(payload.body);
            console.log("onmessageReceived", message);
            if (message.type === "JOIN") {
                infoUpdater("color", message);
                infoUpdater("isHost", message);
            } else if (message.type === "LEAVE") {
                infoUpdater("isHost", message);
            } else if (message.type === "REJOIN") {
                for (let i = 0; i.state.playersGameInfo.length; i++) {
                    if (state.playersGameInfo[i].playerID === message.rejoiningPlayerId) {
                        state.playersGameInfo[i].alive = message.alive;
                        state.playersGameInfo[i].suspicious = message.suspicious;
                        break;
                    }
                }
            } else if (message.type === "PHASE_CHANGED") {
                switch (message.gameStatus.phase) {
                    case "START": {
                        const audio = new Audio(
                            "https://soundbible.com/mp3/Air Plane Ding-SoundBible.com-496729130.mp3"
                        );
                        audio.play();
                        state.gameStatus = message.gameStatus;
                        state.newSubscriberOn = true;
                        infoUpdater("alive", message);
                        store.dispatch("ingame/setPhase", state.gameStatus.phase);
                        break;
                    }
                    case "DAY_DISCUSSION": {
                        const audio = new Audio(
                            "https://soundbible.com/mp3/Air Plane Ding-SoundBible.com-496729130.mp3"
                        );
                        audio.play();
                        state.submessage = "";
                        if (state.role !== "OBSERVER") {
                            state.message = `<span style='font-size: 25px; color:pink'>낮 투표시간</span>이 되었습니다. <br/> 각자 의심되는 사람을 지목해 주세요. <br/> 최다 득표를 한 사람들은 최종투표에 나가게 됩니다.`;
                        } else {
                            state.message =
                                "당신은 <span style='font-size: 25px; color:pink'>관전자</span>입니다. <br/> 게임에 개입할 수는 없지만, 모든 종류의 일어나고 있는 일들에 대한 정보를 받아볼 수 있습니다.";
                        }
                        state.gameStatus = message.gameStatus;
                        state.newSubscriberOn = true;
                        infoUpdater("alive", message);
                        store.dispatch("ingame/setPhase", state.gameStatus.phase);
                        break;
                    }
                    case "DAY_ELIMINATION": {
                        const audio = new Audio(
                            "https://soundbible.com/mp3/Air Plane Ding-SoundBible.com-496729130.mp3"
                        );
                        audio.play();
                        state.vote = null;
                        if (state.role !== "OBSERVER") {
                            state.message =
                                "<span style='font-size: 25px; color:pink'>최종투표시간</span>이 되었습니다. <br/> 최종투표 후보자들 중에 제거할 사람을 클릭해 <span style='color:crimson;'>KILL</span>할 수 있습니다. <br/> 최다득표자는 제거됩니다.";
                        } else {
                            state.message =
                                "당신은 관전자입니다. <br/> 게임에 개입할 수는 없지만, 모든 종류의 일어나고 있는 일들에 대한 정보를 받아볼 수 있습니다.";
                        }
                        state.gameStatus = message.gameStatus;
                        state.newSubscriberOn = true;
                        infoUpdater("suspicious", message);
                        infoUpdater("voters", null);
                        state.isConfirm = false;
                        store.dispatch("ingame/setPhase", state.gameStatus.phase);
                        break;
                    }
                    case "DAY_TO_NIGHT": {
                        const audio = new Audio(
                            "https://soundbible.com/mp3/Air Plane Ding-SoundBible.com-496729130.mp3"
                        );
                        audio.play();
                        state.vote = null;
                        console.log("day to night", state.gameStatus.phase);
                        if (state.gameStatus.phase === "DAY_DISCUSSION") {
                            state.message =
                                "최다 득표자가 너무 많거나 또는 무효투표자가 너무 많은 관계로,<br/>최종 투표를 스킵하고 밤으로 넘어갑니다.";
                        } else {
                            if (message.gameStatus.victim) {
                                console.log("day elimination", message);
                                let victimNickname = "nickname";
                                if (message.gameStatus.victim === state.playerMe.playerId) {
                                    victimNickname = state.playerMe.nickname;
                                } else {
                                    for (let i = 0; i < state.playersGameInfo.length; i++) {
                                        if (
                                            state.playersGameInfo[i].playerId ===
                                            message.gameStatus.victim
                                        ) {
                                            victimNickname = state.playersGameInfo[i].nickname;
                                            break;
                                        }
                                    }
                                    const victimJob = message.gameStatus.victimIsMafia ? "마피아" : "시민";
                                    state.message = `낮의 투표 결과로 인해, ${victimNickname}님이 제거되었습니다.  ${victimNickname}님의 직업은 ${victimJob}이였습니다  곧 밤으로 넘어갑니다.`;

                                    // 죽는 애니메이션
                                }
                            } else {
                                state.message =
                                    "최다 득표자가 너무 많거나 또는 무효투표자가 너무 많은 관계로,<br/>최종 투표를 스킵하고 밤으로 넘어갑니다.";
                            }
                        }
                        state.gameStatus = message.gameStatus;
                        state.newSubscriberOn = true;
                        infoUpdater("alive", message);
                        infoUpdater("suspicious", false);
                        infoUpdater("voters", null);
                        state.isConfirm = false;
                        store.dispatch("ingame/setPhase", state.gameStatus.phase);
                        break;
                    }
                    case "NIGHT_VOTE": {
                        const audio = new Audio(
                            "https://soundbible.com/mp3/Air Plane Ding-SoundBible.com-496729130.mp3"
                        );
                        audio.play();
                        if (state.role === "MAFIA") {
                            state.message =
                                "밤이 되었습니다. 마피아는 시민 중 제거할 사람을 투표하여 주시기 바랍니다.";
                        } else if (state.role === "DOCTOR") {
                            state.message =
                                "밤이 되었습니다. 의사는 시민 중 제거당할 것 같은 사람에게 투표하여 주시기 바랍니다.";
                        } else if (state.role === "POLICE") {
                            state.message =
                                "밤이 되었습니다. 경찰은 의심되는 사람을 지목하여 그 사람의 직업을 확인해보시기 바랍니다.";
                        } else if (state.role === "CIVILIAN") {
                            state.message = "밤이 되었습니다. 마이크와 비디오가 중단됩니다.";
                        } else {
                            state.message =
                                "당신은 관전자입니다. <br/> 게임에 개입할 수는 없지만, 모든 종류의 일어나고 있는 일들에 대한 정보를 받아볼 수 있습니다.";
                        }
                        if (state.role === "MAFIA") {
                            for (let i = 0; i < state.subscribers.length; i++) {
                                if (state.playersGameInfo[i].isMafia !== true) {
                                    state.subscribers[i].subscribeToAudio(false);
                                    state.subscribers[i].subscribeToVideo(false);
                                }
                            }
                        } else if (state.role === "OBSERVER") {
                            for (let i = 0; i < state.subscribers.length; i++) {
                                state.subscribers[i].subscribeToAudio(true);
                                state.subscribers[i].subscribeToVideo(true);
                            }
                        } else {
                            for (let i = 0; i < state.subscribers.length; i++) {
                                state.subscribers[i].subscribeToAudio(false);
                                state.subscribers[i].subscribeToVideo(false);
                            }
                        }
                        state.gameStatus = message.gameStatus;
                        store.dispatch("ingame/setPhase", state.gameStatus.phase);
                        break;
                    }
                    case "NIGHT_TO_DAY": {
                        const audio = new Audio(
                            "https://soundbible.com/mp3/Air Plane Ding-SoundBible.com-496729130.mp3"
                        );
                        audio.play();
                        state.vote = null;
                        if (message.gameStatus.victim) {
                            let victimNickname = "";
                            if (message.gameStatus.victim === state.playerMe.playerId) {
                                victimNickname = state.playerMe.nickname;
                            } else {
                                for (let i = 0; i < state.playersGameInfo.length; i++) {
                                    if (
                                        state.playersGameInfo[i].playerId ===
                                        message.gameStatus.victim
                                    ) {
                                        victimNickname = state.playersGameInfo[i].nickname;
                                        break;
                                    }
                                }
                            }
                            let victimJob = message.gameStatus.victimIsMafia ? "마피아" : "시민";
                            state.message = `낮의 투표 결과로 인해, ${victimNickname}님이 제거되었습니다.  <span style="font-size: 25px; color:#FF1493">${victimNickname}</span>님의 직업은  <span style="font-size: 25px; color:#1E90FF">${victimJob}</span>이였습니다  곧 밤으로 넘어갑니다.`;
                        } else {
                            state.message = "밤의 투표 결과, 아무도 죽지 않았습니다.";
                        }
                        state.gameStatus = message.gameStatus;
                        state.newSubscriberOn = true;
                        infoUpdater("alive", message);
                        infoUpdater("voters", null);
                        state.isConfirm = false;
                        for (let i = 0; i < state.subscribers.length; i++) {
                            state.subscribers[i].subscribeToAudio(true);
                            state.subscribers[i].subscribeToVideo(true);
                        }
                        store.dispatch("ingame/setPhase", state.gameStatus.phase);
                        break;
                    }
                    case "END": {
                        const audio = new Audio(
                            "https://soundbible.com/mp3/Air Plane Ding-SoundBible.com-496729130.mp3"
                        );
                        audio.play();
                        if (message.gameStatus.turnOver === true) {
                            state.message = `게임이 종료되었습니다 <br> 15턴이 지나 자동으로 <span style="font-size: 25px; color:#1E90FF">시민측 진영</span>이 승리하였습니다. `
                        } else {
                            let victims = message.gameStatus.victims.join(',')
                            if (message.gameStatus.winner === "MAFIA") {
                                state.message = `게임이 종료되었습니다. <br> ${victims}가 사망하면서 시민 수가 마피아 수 이하가 됐으므로 <span style="font-size: 25px; color:#DC143C">마피아측 진영</span>의 승리입니다.`;
                            } else {
                                state.message = `게임이 종료되었습니다. <br> ${victims}가 사망하면서 마피아를 모두 제거하였으므로 <span style="font-size: 25px; color:#1E90FF">시민측 진영</span>이 승리하였습니다.`;
                            }
                        }
                        break;
                    }
                    case "READY": {
                        // 초기화
                        state.role = undefined;
                        state.gameStatus = {
                            date: 0,
                            phase: "READY",
                            timer: 0,
                            aliveMafia: 0,
                        };
                        if (state.jobClient) {
                            state.jobClient.unsubscribe()
                        }
                        state.jobClient = undefined;
                        state.mafias = undefined;
                        state.message = `Room: ${state.mySessionId}에 오신 걸 환영합니다.  부디 SSAFIA를 즐겨주시기 바랍니다`;
                        state.submessage = "";
                        for (let j = state.subscribers.length-1; j >= 0; j--) {
                            if (state.removeList.includes(j)) {
                                state.subscribers.splice(j, 1);
                                state.playersGameInfo.splice(j, 1);
                                state.playerNum--;
                            }
                        }
                        state.removeList = []
                        state.publisher.publishAudio(true)
                        state.publisher.publishVideo(true)
                        for (let i = 0; i < state.subscribers.length; i++) {
                            state.subscribers[i].subscribeToAudio(true);
                            state.subscribers[i].subscribeToVideo(true);
                        }
                        infoUpdater("alive", true);
                        infoUpdater("suspicious", null);
                        infoUpdater("voters", null);
                        infoUpdater("isMafia", null);
                        infoUpdater("isHost", message);
                        state.vote = null;
                        state.isConfirm = false;
                        store.dispatch("ingame/setPhase", state.gameStatus.phase);
                        break;
                    }
                }
            } else if (message.type === "UPDATE") {
                console.log("update voters", message);
                infoUpdater("voters", message);
            } else {
                console.log(
                    `sorry, unexpected message type. this is what we'v got ${message.type}`
                );
            }
        }
        // 개인 메세지 채널로 온 메세지에 따라 할 일
        function onPersonalMessageReceived(payload) {
            const message = JSON.parse(payload.body);
            console.log("ON Personal Message Received", message);
            if (message.type === "ROLE") {
                state.role = message.role;
                state.mafias = message.mafias;
                if (state.role === "MAFIA") {
                    if (state.mafias.length === 1) {
                        state.message =
                            "게임이 시작되었습니다. <br/> 당신은 <span style='font-size: 25px; color:DodgerBlue'>마피아</span>입니다. <br/>마피아 동료와 함께 시민의 수를 마피아의 수와 같게 만들면 당신의 승리입니다. <br/>밤마다 마피아 동료들과 상의해 시민을 한명씩 제거해나가세요. \n 마피아는 당신 한명 입니다";
                    } else {
                        let mafiaNicknames = [];
                        for (let i = 0; i < state.playersGameInfo.length; i++) {
                            if (state.mafias.includes(state.playersGameInfo[i].playerId)) {
                                mafiaNicknames.push(state.playersGameInfo[i].nickname);
                            }
                        }
                        const mafiaNicknameString = mafiaNicknames.join(" , ");
                        state.message = `게임이 시작되었습니다. <br/>당신은 <span style='font-size: 25px; color:DodgerBlue'>마피아<span>입니다. <br/>마피아 동료와 함께 시민의 수를 마피아의 수와 같게 만들면 당신의 승리입니다. <br/>밤마다 마피아 동료들과 상의해 시민을 한명씩 제거해나가세요. <br/>당신의 마피아 동료는 ${mafiaNicknameString}입니다`;
                    }
                } else if (state.role === "POLICE") {
                    state.message =
                        "게임이 시작되었습니다. <br/>당신은 <span style='font-size: 25px; color:DodgerBlue'>경찰</span>입니다. <br/>시민을 도와 마피아를 모두 제거하면 당신의 승리입니다. <br/>밤마다 의심가는 사람 한 명을 지목하여 그 사람의 직업을 알아낼 수 있습니다.";
                } else if (state.role === "DOCTOR") {
                    state.message =
                        "게임이 시작되었습니다.<br/>당신은 <span style='font-size: 25px; color:DodgerBlue'>의사</span>입니다. <br/>시민을 도와 마피아를 모두 제거하면 당신의 승리입니다. <br/>밤마다 죽을 것 같은 사람에게 투표하여 그 사람을 구할 수 있습니다.";
                } else if (state.role === "CIVILIAN") {
                    state.message =
                        "게임이 시작되었습니다. <br/>당신은 <span style='font-size: 25px; color:DodgerBlue'>시민</span>입니다. <br/>다른 시민과 함께 마피아를 모두 제거하면 당신의 승리입니다.";
                } else {
                    state.message =
                        "당신은 관전자입니다. <br/>게임에 개입할 수는 없지만, 일어나고 있는 일들에 대한 모든 정보를 받아볼 수 있습니다.";
                    state.publisher.publishAudio(false);
                    state.publisher.publishVideo(false);
                    for (let i = 0; i < state.subscribers.length; i++) {
                        state.subscribers[i].subscribeToAudio(true);
                        state.subscribers[i].subscribeToVideo(true);
                    }
                }
                if (state.mafias) {
                    infoUpdater("isMafia", null);
                } else {
                    for (let i = 0; i < state.playersGameInfo.length; i++) {
                        if (state.mafias.includes(state.playersGameInfo[i].playerId)) {
                            state.playersGameInfo[i].isMafia = true;
                        } else {
                            state.playersGameInfo[i].isMafia = false;
                        }
                    }
                }
                if (state.jobClient) {
                    state.jobClient.unsubscribe();
                }
                state.jobClient = state.stompClient.subscribe(
                    `/sub/${state.mySessionId}/${state.role}`,
                    onJobMessageReceived
                );
                if (state.role === "OBSERVER") {
                    console.log("OBSERVER ROLE messgae received");
                    state.stompClient.send(`/pub/${state.mySessionId}/${state.role}`);
                }
            } else if (message.type == "DEAD") {
                console.log("OBSERVER ROLE messgae received");
                state.stompClient.send(
                    `/pub/${state.mySessionId}/${state.role}`,
                    onJobMessageReceived
                );
            } else if (message.type === "REJOIN") {
                const keys = Object.keys(message.playerMap);
                for (let i = 0; i < keys.length; i++) {
                    const key = keys[i];
                    let flag = true;
                    for (let j = 0; j < state.playersGameInfo.length; j++) {
                        if (state.playersGameInfo[j].playerId === key) {
                            flag = false;
                            break;
                        }
                    }
                    if (flag) {
                        const subscriber = {};
                        subscriber.nickname = "";
                        subscriber.playerId = key;
                        state.subscribers.push(subscriber);
                        state.removeList.push(state.playerNum);
                        //subscribers의 info 세팅
                        // 플레이어 수 1 증가
                        state.playerNum += 1;
                        state.playersGameInfo.push({
                            playerId: key,
                            nickname: "",
                            alive: null,
                            suspicious: null,
                            voters: [],
                            isMafia: null,
                            color: null,
                            isHost: false,
                        });
                    }
                }
                infoUpdater("color", message);
                infoUpdater("nickname", message);
                infoUpdater("isHost", message);
                state.role = message.role;
                state.mafias = message.mafias;
                if (state.role === "OBSERVER") {
                    state.message =
                        "당신은 관전자입니다. <br/>게임에 개입할 수는 없지만, 일어나고 있는 일들에 대한 모든 정보를 받아볼 수 있습니다.";
                    state.publisher.publishAudio(false);
                    state.publisher.publishVideo(false);
                    for (let i = 0; i < state.subscribers.length; i++) {
                        state.subscribers[i].subscribeToAudio(true);
                        state.subscribers[i].subscribeToVideo(true);
                    }
                }
                if (state.mafias) {
                    infoUpdater("isMafia", null);
                } else {
                    for (let i = 0; i < state.playersGameInfo.length; i++) {
                        if (state.mafia.includes(state.playersGameInfo[i].playerId)) {
                            state.playersGameInfo[i].isMafia = true;
                        } else {
                            state.playersGameInfo[i].isMafia = false;
                        }
                    }
                }
                state.jobClient = state.stompClient.subscribe(
                    `/sub/${state.mySessionId}/${state.role}`,
                    onJobMessageReceived
                );
                if (state.role === "OBSERVER") {
                    state.message = ``;
                    state.stompClient.send(`/pub/${state.mySessionId}/${state.role}`);
                }
                switch (message.gameStatus.phase) {
                    case "START": {
                        const audio = new Audio(
                            "https://soundbible.com/mp3/Air Plane Ding-SoundBible.com-496729130.mp3"
                        );
                        audio.play();
                        state.gameStatus = message.gameStatus;
                        state.newSubscriberOn = true;
                        infoUpdater("alive", message);
                        store.dispatch("ingame/setPhase", state.gameStatus.phase);
                        break;
                    }
                    case "DAY_DISCUSSION": {
                        const audio = new Audio(
                            "https://soundbible.com/mp3/Air Plane Ding-SoundBible.com-496729130.mp3"
                        );
                        audio.play();
                        state.submessage = "";
                        if (state.role !== "OBSERVER") {
                            state.message =
                                "낮 투표시간이 되었습니다.  각자 의심되는 사람을 지목해 주세요.  최다 득표를 한 사람들은 최종투표에 나가게 됩니다.";
                        } else {
                            state.message =
                                "당신은 관전자입니다.  게임에 개입할 수는 없지만, 모든 종류의 일어나고 있는 일들에 대한 정보를 받아볼 수 있습니다.";
                        }
                        state.gameStatus = message.gameStatus;
                        state.newSubscriberOn = true;
                        infoUpdater("alive", message);
                        store.dispatch("ingame/setPhase", state.gameStatus.phase);
                        break;
                    }
                    case "DAY_ELIMINATION": {
                        const audio = new Audio(
                            "https://soundbible.com/mp3/Air Plane Ding-SoundBible.com-496729130.mp3"
                        );
                        audio.play();
                        state.vote = null;
                        if (state.role !== "OBSERVER") {
                            state.message =
                                "최종투표시간이 되었습니다.  최종투표 후보자들 중에 제거할 사람에게 투표해 주세요.  최다득표자는 제거되게 됩니다.";
                        } else {
                            state.message =
                                "당신은 관전자입니다.  게임에 개입할 수는 없지만, 모든 종류의 일어나고 있는 일들에 대한 정보를 받아볼 수 있습니다.";
                        }
                        state.gameStatus = message.gameStatus;
                        infoUpdater("suspicious", message);
                        infoUpdater("voters", null);
                        state.isConfirm = false;
                        store.dispatch("ingame/setPhase", state.gameStatus.phase);
                        break;
                    }
                    case "DAY_TO_NIGHT": {
                        const audio = new Audio(
                            "https://soundbible.com/mp3/Air Plane Ding-SoundBible.com-496729130.mp3"
                        );
                        audio.play();
                        state.vote = null;
                        if (state.gameStatus === "DAY_DISCUSSION") {
                            state.message =
                                "최다 득표자가 너무 많거나 또는 무효투표자가 너무 많은 관계로,  최종 투표를 스킵하고 밤으로 넘어갑니다.";
                        } else {
                            if (message.gameStatus.victime) {
                                let victimNickname = "";
                                if (message.gameStatus.victim === state.playerMe.playerId) {
                                    victimNickname = state.playerMe.nickname;
                                } else {
                                    for (let i = 0; i < state.playersGameInfo.length; i++) {
                                        if (
                                            state.playersGameInfo[i].playerId ===
                                            message.gameStatus.victim
                                        ) {
                                            victimNickname = state.playersGameInfo[i].nickname;
                                            break;
                                        }
                                    }
                                    let victimJob = message.gameStatus.victimIsMafia ? "마피아" : "시민";
                                    state.message = `낮의 투표 결과로 인해, ${victimNickname}님이 제거되었습니다.  ${victimNickname}님의 직업은 ${victimJob}이였습니다  곧 밤으로 넘어갑니다.`
                                }
                            } else {
                                state.message =
                                    "최종투표로 인해 아무도 죽지 않았습니다. 밤으로 넘어갑니다.";
                            }
                        }
                        state.gameStatus = message.gameStatus;
                        state.newSubscriberOn = true;
                        infoUpdater("alive", message);
                        infoUpdater("suspicious", null);
                        infoUpdater("voters", null);
                        state.isConfirm = false;
                        store.dispatch("ingame/setPhase", state.gameStatus.phase);
                        break;
                    }
                    case "NIGHT_VOTE": {
                        const audio = new Audio(
                            "https://soundbible.com/mp3/Air Plane Ding-SoundBible.com-496729130.mp3"
                        );
                        audio.play();
                        if (state.role === "MAFIA") {
                            state.message =
                                "밤이 되었습니다. 마피아는 시민 중 제거할 사람을 투표하여 주시기 바랍니다.";
                        } else if (state.role === "DOCTOR") {
                            state.message =
                                "밤이 되었습니다. 의사는 시민 중 제거당할 것 같은 사람에게 투표하여 주시기 바랍니다.";
                        } else if (state.role === "POLICE") {
                            state.message =
                                "밤이 되었습니다. 경찰은 의심되는 사람을 지목하여 그 사람의 직업을 확인해보시기 바랍니다.";
                        } else if (state.role === "CIVILIAN") {
                            state.message = "밤이 되었습니다. 마이크와 비디오가 중단됩니다.";
                        } else {
                            state.message =
                                "당신은 관전자입니다.  게임에 개입할 수는 없지만, 모든 종류의 일어나고 있는 일들에 대한 정보를 받아볼 수 있습니다.";
                        }
                        if (state.role === "MAFIA") {
                            for (let i = 0; i < state.subscribers.length; i++) {
                                if (state.playersGameInfo[i].isMafia !== true) {
                                    console.log("mafia turn off");
                                    state.subscribers[i].subscribeToAudio(false);
                                    state.subscribers[i].subscribeToVideo(false);
                                }
                            }
                        } else if (state.role === "OBSERVER") {
                            for (let i = 0; i < state.subscribers.length; i++) {
                                console.log("observer turn on");
                                state.subscribers[i].subscribeToAudio(true);
                                state.subscribers[i].subscribeToVideo(true);
                            }
                        } else {
                            for (let i = 0; i < state.subscribers.length; i++) {
                                console.log("civilian turn on");
                                state.subscribers[i].subscribeToAudio(false);
                                state.subscribers[i].subscribeToVideo(false);
                            }
                        }
                        state.gameStatus = message.gameStatus;
                        store.dispatch("ingame/setPhase", state.gameStatus.phase);
                        break;
                    }
                    case "NIGHT_TO_DAY": {
                        const audio = new Audio(
                            "https://soundbible.com/mp3/Air Plane Ding-SoundBible.com-496729130.mp3"
                        );
                        audio.play();
                        state.vote = null;
                        if (message.gameStatus.victim) {
                            let victimNickname = "";
                            if (message.gameStatus.victim === state.playerMe.playerId) {
                                victimNickname = state.playerMe.nickname;
                            } else {
                                for (let i = 0; i < state.playersGameInfo.length; i++) {
                                    if (
                                        state.playersGameInfo[i].playerId ===
                                        message.gameStatus.victim
                                    ) {
                                        victimNickname = state.playersGameInfo[i].nickname;
                                        break;
                                    }
                                }
                            }
                            let victimJob = message.gameStatus.victimIsMafia ? "마피아" : "시민";
                            state.message = `밤의 투표 결과로 인해, ${victimNickname}님이 제거되었습니다.  ${victimNickname}님의 직업은 ${victimJob}이였습니다  곧 낮으로 넘어갑니다.`;
                        } else {
                            state.message = "밤의 투표 결과, 아무도 죽지 않았습니다.";
                        }
                        state.gameStatus = message.gameStatus;
                        state.newSubscriberOn = true;
                        infoUpdater("alive", message);
                        infoUpdater("voters", null);
                        state.isConfirm = false;
                        for (let i = 0; i < state.subscribers.length; i++) {
                            state.subscribers[i].subscribeToAudio(true);
                            state.subscribers[i].subscribeToVideo(true);
                        }
                        store.dispatch("ingame/setPhase", state.gameStatus.phase);
                        break;
                    }
                    case "END": {
                        const audio = new Audio(
                            "https://soundbible.com/mp3/Air Plane Ding-SoundBible.com-496729130.mp3"
                        );
                        audio.play();
                        if (message.gameStatus.turnOver === true) {
                            state.message = `게임이 종료되었습니다 <br> 15턴이 지나 자동으로 <span style="font-size: 25px; color:#1E90FF">시민측 진영</span>이 승리하였습니다. `
                        } else {
                            let victims = message.gameStatus.victims.join(',')
                            if (message.gameStatus.winner === "MAFIA") {
                                state.message = `게임이 종료되었습니다. <br> ${victims}가 사망하면서 시민 수가 마피아 수 이하가 됐으므로 <span style="font-size: 25px; color:#DC143C">마피아측 진영</span>의 승리입니다.`;
                            } else {
                                state.message = `게임이 종료되었습니다. <br> ${victims}가 사망하면서 마피아를 모두 제거하였으므로 <span style="font-size: 25px; color:#1E90FF">시민측 진영</span>이 승리하였습니다.`;
                            }
                        }
                        break;
                    }
                }
            }
        }

        // 직업 채널로 온 메세지에 따라 할 일
        function onJobMessageReceived(payload) {
            const message = JSON.parse(payload.body);
            if (message.type === "UPDATE") {
                state.newSubscriberOn = true;
                console.log("OBSERVER vote update", message);
                infoUpdater("voters", message);
            } else if (message.type === "SUSPECT") {
                let targetNickname = "";
                for (let i = 0; i < state.playersGameInfo.length; i++) {
                    if (state.playersGameInfo[i].playerId === message.vote) {
                        targetNickname = state.playersGameInfo[i].nickname;
                        break;
                    }
                }
                const targetJob = message.mafia ? "마피아" : "시민";
                if (state.role === "POLICE") {
                    state.submessage = `당신이 지목한 ${targetNickname}의 직업은 ${targetJob}입니다.`;
                } else {
                    state.submessage = `경찰이 지목한 ${targetNickname}의 직업은 ${targetJob}입니다.`;
                }
            } else if (message.type === "DEAD") {
                console.log("OBSERVER dead meesage", message);
                state.newSubscriberOn = true;
                infoUpdater("role", message);
            }
        }

        // 게임 페이지 떠날 때 할일
        function leaveGame() {
            state.stompClient.send(`/pub/${state.mySessionId}/leave`, {});
            state.stompClient.disconnect();
        }

        function leave() {
            leaveSession();
            leaveGame();
        }

        /////////////////set url//////////////
        state.inviteUrl = API_CLIENT_URL + "/nickname/" + route.params.roomId;

        //////////// 플레이어 수에 따라 그리드 변경
        const getJustifyClassFirstRow = computed(() => {
            if (state.playerNum <= 2) {
                return "justify-content-center";
            } else {
                return "justify-content-between";
            }
        });

        const getJustifyClassThirdRow = computed(() => {
            if (state.playerNum <= 3) {
                return "justify-content-center";
            } else {
                return "justify-content-between";
            }
        });

        function emitVoteDataUpdate(targetPlayerId) {
            if (!state.isConfirm) {
                if (
                    state.gameStatus.phase === "DAY_DISCUSSION" ||
                    state.gameStatus.phase === "DAY_ELIMINATION"
                ) {
                    if (state.vote === targetPlayerId) {
                        state.vote = null;
                        targetPlayerId = null;
                        sendMessageVote(targetPlayerId);
                    } else {
                        state.vote = targetPlayerId;
                        sendMessageVote(targetPlayerId);
                    }
                } else if (state.gameStatus.phase === "NIGHT_VOTE") {
                    if (state.vote === targetPlayerId) {
                        state.vote = null;
                        targetPlayerId = null;
                        sendMessageNightVote(targetPlayerId);
                    } else {
                        state.vote = targetPlayerId;
                        sendMessageNightVote(targetPlayerId);
                    }
                }
            }
        }

        function emitConfirmDataUpdate() {
            if (state.isConfirm !== true) {
                state.isConfirm = true;
                if (
                    state.gameStatus.phase === "DAY_DISCUSSION" ||
                    state.gameStatus.phase === "DAY_ELIMINATION"
                ) {
                    sendMessageConfirm();
                } else if (state.gameStatus.phase === "NIGHT_VOTE") {
                    sendMessageNightConfirm();
                }
            }
        }

        //url 클립보드에 복사
        function copyUrl() {
            const urlInput = document.getElementById("urlInput");
            urlInput.value = state.inviteUrl;

            //clipboard 복사
            urlInput.select();
            document.execCommand("copy");
        }

        state.openviduToken = store.getters["token/getOpenviduToken"];
        state.myUserName = store.getters["token/getNickname"];
        state.playerId = store.getters["token/getPlayerId"];
        console.log(
            "state.playerId:",
            state.playerId,
            "getters:",
            store.getters["token/getPlayerId"]
        );
        joinSession();
        setTimeout(connect, 500);

        window.onbeforeunload = function(event) {
            leave();
            return "";
        };
        store.dispatch("token/setRoomId", route.params.roomId);
        console.log(state.gameStatus.phase);
        store.dispatch("ingame/setPhase", state.gameStatus.phase);

        return {
            state,
            store,
            emitVoteDataUpdate,
            emitConfirmDataUpdate,
            getJustifyClassFirstRow,
            getJustifyClassThirdRow,
            sendMessageStart,
            copyUrl,
            leave,
        };
    },
    watch: {
        $route(to, from) {
            if (confirm("정말 나가시겠습니까?")) {
                this.leave();
            }
        },
    },
};
</script>

<style></style>
