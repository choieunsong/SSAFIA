<template>
  <div id="main-container" class="container">
    <div id="session" v-if="state.session">
      <div id="session-header">
        <h1 id="session-title">{{ state.mySessionId }}</h1>
        <input
          class="btn btn-large btn-danger"
          type="button"
          id="buttonLeaveSession"
          @click="leaveSession"
          value="Leave session"
        />
      </div>
      <div id="video-container" class="col-md-12">
        <user-video :stream-manager="state.publisher" />
        <user-video
          v-for="sub in state.subscribers"
          :key="sub.stream.connection.connectionId"
          :stream-manager="sub"
        />
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import { OpenVidu } from "openvidu-browser";
import UserVideo from "@/views/game/components/UserVideo";
import { reactive } from "vue";
import { useStore } from "vuex";
import { useRoute } from "vue-router";
import SockJS from "sockjs-client";
import Stomp from "stomp-client";

axios.defaults.headers.post["Content-Type"] = "application/json";

export default {
  name: "Game",
  components: {
    UserVideo,
  },
  setup() {
    const route = useRoute();
    const store = useStore();
    const state = reactive({
      // openvidu 관련
      OV: undefined,
      session: undefined,
      mainStreamManager: undefined,
      publisher: undefined,
      subscribers: [],
      mySessionId: route.params.roomId,
      myUserName: undefined,
      openviduToken: undefined,
      playerId: undefined,

      // socket 연결 게임 데이터 관련
      isHost: undefined,
      role: undefined,
      gameStatus: {
        date: 0,
        phase: "ready",
        timer: 0,
        aliveMafia: 0,
      },
      playerMap: undefined,
      stompClient: undefined,
      jobClient: undefined,
      mafias: undefined,
      message: undefined,
      submessage: '',
    });

    // 화상 채팅 관련
    // 세션 나가기
    var leaveSession = function() {
      // --- Leave the session by calling 'disconnect' method over the Session object ---
      if (state.session) state.session.disconnect();

      state.session = undefined;
      state.mainStreamManager = undefined;
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

      // --- Specify the actions when events take place in the session ---

      // On every new Stream received...
      state.session.on("streamCreated", ({ stream }) => {
        const subscriber = state.session.subscribe(stream);
        const array = subscriber.stream.connection.data.split('"');
        const tmp = array[3].split(",");
        subscriber.nickname = tmp[0];
        subscriber.playerId = tmp[1];
        state.subscribers.push(subscriber);
      });

      // On every Stream destroyed...
      state.session.on("streamDestroyed", ({ stream }) => {
        const index = state.subscribers.indexOf(stream.streamManager, 0);
        if (index >= 0) {
          state.subscribers.splice(index, 1);
        }
      });

      // On every asynchronous exception...
      state.session.on("exception", ({ exception }) => {
        console.warn(exception);
      });

      console.log(state.openviduToken);
      state.session
        .connect(state.openviduToken, {
          clientData: `${state.myUserName},${state.playerId}`,
        })
        .then(() => {
          // --- Get your own camera stream with the desired properties ---

          let publisher = state.OV.initPublisher(undefined, {
            audioSource: undefined, // The source of audio. If undefined default microphone
            videoSource: undefined, // The source of video. If undefined default webcam
            publishAudio: true, // Whether you want to start publishing with your audio unmuted or not
            publishVideo: true, // Whether you want to start publishing with your video enabled or not
            resolution: "640x480", // The resolution of your video
            frameRate: 30, // The frame rate of your video
            insertMode: "APPEND", // How the video is inserted in the target element 'video-container'
            mirror: false, // Whether to mirror your local video or not
          });

          state.mainStreamManager = publisher;
          state.publisher = publisher;

          // --- Publish your stream ---

          state.session.publish(state.publisher);
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
      // Subscribe to the Public Topic
      state.message = `Room: ${state.mySessionId}에 오신 걸 환영합니다. \n 부디 SSAFIA를 즐겨주시기 바랍니다`;
      state.stompClient.subscribe(
        `/sub/${state.mySessionId}`,
        onMessageReceived
      );
      state.stompClient.subscribe(
        `/sub/${state.mySessionId}/${state.playerId}`,
        onPersonalMessageReceived
      );
      // Tell your username to the server
      state.stompClient.send(
        `/pub/${state.mySessionId}`,
        {},
        JSON.stringify({ id: state.playerId, type: "JOIN" })
      );
    }

    function onError(error) {
      console.log("websocket connection failed, try agin or change your code");
    }

    function connect() {
      var socket = new SockJS("/ws/gamesession");
      state.stompClient = Stomp.over(socket);
      state.stompClient.connect({}, onConnected, onError);
    }

    function sendMessageVote(targetPlayerId) {
      if (state.stompClient) {
        const Message = {
          id: state.playerId,
          vote: targetPlayerId,
          phase: state.gameStatus.phase,
        };
        state.stompClient.send(
          `/pub/${state.mySessionId}/vote`,
          {},
          JSON.stringify(Message)
        );
      }
    }

    function sendMessageConfirm() {
      if (state.stompClient) {
        const Message = {
          id: state.playerId,
          phase: state.gameStatus.phase,
        };
        state.stompClient.send(
          `/pub/${state.mySessionId}/confirm`,
          {},
          JSON.stringify(Message)
        );
      }
    }

    function sendMessageStart() {
      if (state.stompClient) {
        const Message = {
          id: state.playerId,
        };
        state.stompClient.send(
          `/pub/${state.mySessionId}/start`,
          {},
          JSON.stringify(Message)
        );
      }
    }

    function sendMessageNightVote(targetPlayerId) {
      if (state.stompClient) {
        const message = {
          phase = state.gameStatus.phase,
          id = state.playerId,
          vote = targetPlayerId
        }
        state.stompClient.send(
          `/pub/${state.mySessionId}/${state.role}/vote`,
          {},
          JSON.stringify(message)
        )
      }
    }

    function sendMessageNightConfirm() {
      if (state.stompClient) {
        const message = {
          phase = state.gameStatus.phase,
          id = state.playerId,
        }
        state.stompClient.send(
          `/pub/${state.mySessionId}/${state.role}/confirm`,
          {},
          JSON.stringify(message)
        )
      }
    }

    function onMessageReceived(payload) {
      var message = JSON.parse(payload.body);
      if (message.type === "JOIN") {
        state.players = message.players;
      } else if (message.type === "LEAVE") {
        delete state.players[message.leftPlayerId];
        if (message.hostId === state.playerId) {
          state.isHost = true;
        } else {
          state.isHost = false;
        }
      } else if (message.type === "PHASE_CHANED") {
        state.submessage = ''
        switch (message.gameStatus.phase) {
          case "START":
            if (state.role === "mafia") {
              state.message = `게임이 시작되었습니다. \n 당신은 마피아입니다. \n 마피아 동료와 함께 시민의 수를 마피아의 수와 같게 만들면 당신의 승리입니다. \n 밤마다 마피아 동료들과 상의해 시민을 한명씩 제거해나가세요. \n 당신의 마피아 동료는 ${state.mafias}들입니다`;
            } else if (state.role === "police") {
              state.message =
                "게임이 시작되었습니다. \n 당신은 경찰입니다. \n 시민을 도와 마피아를 모두 제거하면 당신의 승리입니다. \n 밤마다 의심가는 사람 한 명을 지목하여 그 사람의 직업을 알아낼 수 있습니다.";
            } else if (state.role === "doctor") {
              state.message =
                "게임이 시작되었습니다. \n 당신은 의사입니다. \n 시민을 도와 마피아를 모두 제거하면 당신의 승리입니다. \n 밤마다 죽을 것 같은 사람에게 투표하여 그 사람을 구할 수 있습니다.";
            } else if (state.role === "civilian") {
              state.message =
                "게임이 시작되었습니다. \n 당신은 시민입니다. \n 다른 시민과 함께 마피아를 모두 제거하면 당신의 승리입니다.";
            } else {
              state.message =
                "당신은 관전자입니다. \n 게임에 개입할 수는 없지만, 모든 종류의 일어나고 있는 일들에 대한 정보를 받아볼 수 있습니다.";
            }
            state.gameStatus = message.gameStatus;
            const keys = Object.keys(state.playerMap);
            for (let i = 0; i < keys.length; i++) {
              const key = keys[i]; // 각각의 키
              const value = state.playerMap[key]; // 각각의 키에 해당하는 각각의 값
              state.playerMap[value].alive = message.playerMap[value].alive;
              state.playerMap[value].voters = []
            }
            break;
          case "DAY_DISCUSSION":
            state.message =
              "낮 투표시간이 되었습니다. \n 각자 의심되는 사람을 지목해 주세요. \n 최다 득표를 한 사람들은 최종투표에 나가게 됩니다.";
            state.gameStatus = message.gameStatus;
            const keys = Object.keys(state.playerMap);
            for (let i = 0; i < keys.length; i++) {
              const key = keys[i]; // 각각의 키
              const value = state.playerMap[key]; // 각각의 키에 해당하는 각각의 값
              state.playerMap[value].alive = message.playerMap[value].alive;
              state.playerMap[value].voters = []
            }
            break;
          case "DAY_ELIMINATION":
            state.message =
              "최종투표시간이 되었습니다. \n 최종투표 후보자들 중에 제거할 사람에게 투표해 주세요. \n 최다득표자는 제거되게 됩니다.";
            state.gameStatus = message.gameStatus;
            const keys = Object.keys(state.playerMap);
            for (let i = 0; i < keys.length; i++) {
              const key = keys[i]; // 각각의 키
              const value = state.playerMap[key]; // 각각의 키에 해당하는 각각의 값
              state.playerMap[value].alive = message.playerMap[value].alive;
              state.playerMap[value].suspicious =
                message.playerMap[value].suspicious;
              state.playerMap[value].voters = []
            }
            break;
          case "DAY_TO_NIGHT":
            if (state.gaemStatus === "DAY_DISCUSSION") {
              state.message =
                "최다 득표자가 너무 많거나 또는 무효투표자가 너무 많은 관계로,\n  최종 투표를 스킵하고 밤으로 넘어갑니다.";
            } else {
              let victimNickname = ''
              for (let i = 0; i < state.subscribers.length; i++) {
                if (state.subscribers[i].playerId === message.victim) {
                  victimNickname = state.subscribers[i].nickname
                  break
                }
              }
              const victimJob = victimIsMafia ? '마피아' : '시민'
              state.message = `낮의 투표 결과로 인해, ${ victimNickname }님이 제거되었습니다. \n ${ victimNickname}님의 직업은 ${ victimJob }이였습니다 \n 곧 밤으로 넘어갑니다.`;
            }
            state.gameStatus = message.gameStatus;
            const keys = Object.keys(state.playerMap);
            for (let i = 0; i < keys.length; i++) {
              const key = keys[i]; // 각각의 키
              const value = state.playerMap[key]; // 각각의 키에 해당하는 각각의 값
              state.playerMap[value].alive = message.playerMap[value].alive;
              state.playerMap[value].voters = []
            }
            break;
          case "NIGHT_VOTE":
            if (state.role === 'mafia') {
              state.message = '밤이 되었습니다. 마피아는 시민 중 제거할 사람을 투표하여 주시기 바랍니다.'
            } else if (state.role === 'doctor' ) {
              state.message = '밤이 되었습니다. 의사는 시민 중 제거당할 것 같은 사람에게 투표하여 주시기 바랍니다.'
            } else if (state.role === 'police') {
              state.message = '밤이 되었습니다. 경찰은 의심되는 사람을 지목하여 그 사람의 직업을 확인해보시기 바랍니다.'
            } else if (state.role === 'civilian') {
              state.message = '밤이 되었습니다. 마이크와 비디오가 중단됩니다.'
            } else {
              state.message = '밤이 되었습니다. 각 직업마다 뭘 하고 있는지 지켜보시길 바랍니다.'
            }
            state.gameStatus = message.gameStatus;
            const keys = Object.keys(state.playerMap);
            for (let i = 0; i < keys.length; i++) {
              const key = keys[i]; // 각각의 키
              const value = obj[key]; // 각각의 키에 해당하는 각각의 값
              state.playerMap[value].alive = message.playerMap[value].alive;
              state.playerMap[value].voters = []
            }
            break;
          case "NIGHT_TO_DAY":
            if (message.gameStatus.victim) {
              let victimNickname = ''
              for (let i = 0; i < state.subscribers.length; i++) {
                if (state.subscribers[i].playerId === message.victim) {
                  victimNickname = state.subscribers[i].nickname
                  break
                }
              }
              const victimJob = message.gameStatus.victimIsMafia ? '마피아' : '시민'
              state.message = `밤의 투표 결과로 인해, ${ victimNickname }님이 제거되었습니다. \n ${ victimNickname}님의 직업은 ${ victimJob }이였습니다 \n 곧 밤으로 넘어갑니다.`;
            } else {
              state.message = "밤의 투표 결과, 아무도 죽지 않았습니다."
            }
            state.gameStatus = message.gameStatus;
            const keys = Object.keys(state.playerMap);
            for (let i = 0; i < keys.length; i++) {
              const key = keys[i]; // 각각의 키
              const value = obj[key]; // 각각의 키에 해당하는 각각의 값
              state.playerMap[value].alive = message.playerMap[value].alive;
              state.playerMap[value].voters = []
            }
            break;
          case "END":
            const winner = (message.gameStatus.winner === 'mafia') ? '마피아' : '시민'
            state.message = `게임이 종료되었습니다. 최종승자는 ${message.gameStatus.winner}입니다.`

            // 초기화
            state.role = undefined
            state.gameStatus = {
              date: 0,
              phase: "ready",
              timer: 0,
              aliveMafia: 0,
            }
            // state.playerMap = undefined
            state.jobClient = undefineds
            state.mafias = undefined
            state.message = undefined
            state.submessage = ''
            break;
        }
      } else if (message.type === "UPDATE") {
        const keys = Object.keys(state.playerMap);
        for (let i = 0; i < keys.length; i++) {
          const key = keys[i]; // 각각의 키
          const value = obj[key]; // 각각의 키에 해당하는 각각의 값
          state.playerMap[value].voters = message.playerMap[value].voters;
        }
        // } else if (message.type === "REJOIN") {
        //   state.gameStatus = message.gameStatus;
        //   state.players = message.players;
      } else {
        console.log(
          `sorry, unexpected message type. this is what we'v got ${message.type}`
        );
      }
    }

    function onPersonalMessageReceived(payload) {
      const message = JSON.parse(payload.body);
      if (message.type === "ROLE") {
        state.role = message.role;
        state.mafias = message.mafia;
        state.jobClient = state.stompClient.subscribe(
          `/sub/${state.mySessionId}/${state.role}`,
          onJobMessageReceived
        );
        if (state.role === "observer") {
          state.publisher.publishAudio(false);
          state.publisher.publishVideo(false);
        }
      }
    }

    function onJobMessageReceived(payload) {
      const message = JSON.parse(payload.body);
      if (state.role === 'mafia') {
      const keys = Object.keys(state.playerMap);
      for (let i = 0; i < keys.length; i++) {
        const key = keys[i]; // 각각의 키
        const value = obj[key]; // 각각의 키에 해당하는 각각의 값
        state.playerMap[value].voter = message.playerMap[value].voter;
      }
      }  else if (state.role === 'police') {
        let targetNickname = ''
              for (let i = 0; i < state.subscribers.length; i++) {
                if (state.subscribers[i].playerId === message.vote) {
                  targetNickname = state.subscribers[i].nickname
                  break
                }
              }
              const targetJob = message.isMafia ? '마피아' : '시민'
        state.submessage = `당신이 지목한 ${targetNickname}의 직업은 ${targetJob}입니다.`
      }
    }

    function leaveGame() {
      const message = {
        type: "LEAVE",
        id: state.playerId,
      };
      state.stompClient.send(
        `/pub/${state.mySessionId}`,
        {},
        JSON.stringify(Message)
      );
    }

    state.openviduToken = store.getters["token/getOpenviduToken"];
    state.myUserName = store.getters["token/getNickname"];
    state.playerId = store.getters["token/getPlayerId"];
    joinSession();
    // connect();

    window.addEventListener("beforeunload", leaveSession);
    window.addEventListener("beforeunload", leaveGame);
    return {
      state,
      leaveSession,
    };
  },
};
</script>

<style></style>
