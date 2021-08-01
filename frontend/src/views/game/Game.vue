<template>
  <div id="main-container" class="container">
		<div id="join" v-if="!state.session">
			<div id="img-div"><img src="resources/images/openvidu_grey_bg_transp_cropped.png" /></div>
			<div id="join-dialog" class="jumbotron vertical-center">
				<h1>Join a video session</h1>
				<div class="form-group">
					<p>
						<label>Participant</label>
						<input v-model="state.myUserName" class="form-control" type="text" required>
					</p>
					<p>
						<label>Session</label>
						<input v-model="state.mySessionId" class="form-control" type="text" required>
					</p>
					<p class="text-center">
						<button class="btn btn-lg btn-success" @click="joinSession">Join!</button>
					</p>
				</div>
			</div>
		</div>

		<div id="session" v-if="state.session">
			<div id="session-header">
				<h1 id="session-title">{{ state.mySessionId }}</h1>
				<input class="btn btn-large btn-danger" type="button" id="buttonLeaveSession" @click="leaveSession" value="Leave session">
			</div>
			<div id="main-video" class="col-md-6">
				<user-video :stream-manager="state.mainStreamManager"/>
			</div>
			<div id="video-container" class="col-md-6">
				<user-video :stream-manager="state.publisher" @click="updateMainVideoStreamManager(state.publisher)"/>
				<user-video v-for="sub in state.subscribers" :key="sub.stream.connection.connectionId" :stream-manager="sub" @click="updateMainVideoStreamManager(sub)"/>
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
import { API_BASE_URL } from "@/constant/index";

axios.defaults.headers.post["Content-Type"] = "application/json";
const OPENVIDU_SERVER_URL = API_BASE_URL + ":4443";

export default {
  name: "Game",
  components: {
    UserVideo,
  },
  setup() {
    const route = useRoute();
    const store = useStore();
    const state = reactive({
      OV: undefined,
      session: undefined,
      mainStreamManager: undefined,
      publisher: undefined,
      subscribers: [],

      mySessionId: route.params.roomId,
      myUserName: undefined,
      openviduToken: undefined,
      playerId: undefined,
    });

    var leaveSession = function() {
      // --- Leave the session by calling 'disconnect' method over the Session object ---
      if (state.session) state.session.disconnect();

      state.session = undefined;
      state.mainStreamManager = undefined;
      state.publisher = undefined;
      state.subscribers = [];
      state.OV = undefined;
    };

    var updateMainVideoStreamManager = function(stream) {
      if (state.mainStreamManager === stream) return;
      state.mainStreamManager = stream;
    };
    // See https://docs.openvidu.io/en/stable/reference-docs/REST-API/#post-openviduapisessions

    /**
     * --------------------------
     * SERVER-SIDE RESPONSIBILITY
     * --------------------------
     * These methods retrieve the mandatory user token from OpenVidu Server.
     * This behavior MUST BE IN YOUR SERVER-SIDE IN PRODUCTION (by using
     * the API REST, openvidu-java-client or openvidu-node-client):
     *   1) Initialize a Session in OpenVidu Server	(POST /openvidu/api/sessions)
     *   2) Create a Connection in OpenVidu Server (POST /openvidu/api/sessions/<SESSION_ID>/connection)
     *   3) The Connection.token must be consumed in Session.connect() method
     */

    // See https://docs.openvidu.io/en/stable/reference-docs/REST-API/#post-openviduapisessionsltsession_idgtconnection
    var joinSession = function() {
      // --- Get an OpenVidu object ---
      state.OV = new OpenVidu();

      // --- Init a session ---
      state.session = state.OV.initSession();

      // --- Specify the actions when events take place in the session ---

      // On every new Stream received...
      state.session.on("streamCreated", ({ stream }) => {
        const subscriber = state.session.subscribe(stream);
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

      // --- Connect to the session with a valid user token ---

      // 'getToken' method is simulating what your server-side should do.
      // 'token' parameter should be retrieved and returned by your own backend
      console.log(state.openviduToken)
      state.session
        .connect(state.openviduToken, { clientData: state.myUserName })
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
    state.openviduToken = store.getters['token/getOpenviduToken']
    state.myUserName = store.getters['token/getNickname']
    joinSession()
    window.addEventListener("beforeunload", leaveSession);
    return {
      state,
      joinSession,
      updateMainVideoStreamManager,
      leaveSession,
    };
  },
};
</script>

<style></style>
