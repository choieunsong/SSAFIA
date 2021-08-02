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
      Host: undefined,
      role: undefined,
      gameStatus: {
        date: 0,
        phase: 'ready',
        timer: 0,
        aliveMafia: 0,
        victim: undefined,
        victimIsMafia: undefined,
        suspects: undefined,
      },
      players: undefined,
      stompClient: undefined,
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
        console.log("subscriber");
        console.log(subscriber);
        const array = subscriber.stream.connection.data.split('"');
        subscriber.playerId = array[3];
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
        .connect(state.openviduToken, { clientData: state.playerId })
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
      state.stompClient.subscribe(`/sub/all`, onMessageReceived);
      state.stompClient.subscribe(
        `/sub/${state.playerId}`,
        onPersonalMessageReceived
      );
      // Tell your username to the server
      state.stompClient.send(
        "/pub",
        {},
        JSON.stringify({ playerId: state.playerId, type: "CONNECT" })
      );
    }

    function onError(error) {
      console.log("websocket connection failed, try agin or change your code");
    }

    function connect() {
      var socket = new SockJS(`/ws/${state.mySessionId}`);
      state.stompClient = Stomp.over(socket);
      state.stompClient.connect({}, onConnected, onError);
    }

    function sendMessageVote(targetPlayerId) {
      if (state.stompClient) {
        var Message = {
          playerId: state.playerId,
          vote: targetPlayerId,
          type: "VOTE",
        };
        state.stompClient.send("/pub", {}, JSON.stringify(Message));
      }
    }

    function sendMessageConfirm() {
      if (state.stompCliesnt) {
        var Message = {
          playerId: state.playerId,
          type: "CONFIRM",
        };
        state.stompClient.send("/pub", {}, JSON.stringify(Message));
      }
    }

    function onMessageReceived(payload) {
      var message = JSON.parse(payload.body);
      if (message.type === "JOIN") {
        console.log(message.type)
      } else if (message.type === "LEAVE") {
        console.log(message.type)
      } else if (message.type === "HOSTCHANGED") {
        state.host = message.host
        if (state.host === state.playerId) {
          state.isHost = true
        } else {
          state.isHost = false
        }
      } else if (message.type === "GAMESESSION") {
        console.log(message.type)
      } 
    }

    function onPersonalMessageReceived(payload) {
      const message = JSON.parse(payload.body);

      if (message.type === "start") {
        state.role = message.role;
        state.stompClient.subscribe(`/sub/${state.role}`, onJobMessageReceived)
      }
    }
    
    function onJobMessageReceived(payload) {
      const message = JSON.parse(payload.body);
    }

    state.openviduToken = store.getters["token/getOpenviduToken"];
    state.myUserName = store.getters["token/getNickname"];
    state.playerId = store.getters["token/getPlayerId"];
    joinSession();
    // connect();

    // window.addEventListener("beforeunload", leaveSession);
    return {
      state,
      leaveSession,
    };
  },
};
</script>

<style></style>
