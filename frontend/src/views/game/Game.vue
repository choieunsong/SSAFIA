<template>
  <div id="container">
    <div id="wrapper">
      <div id="join" class="animate join">
        <h1>Join a Room</h1>
        <form onsubmit="register" accept-charset="UTF-8">
          <p>
            <input
              type="text"
              name="name"
              value=""
              id="name"
              placeholder="Username"
              required
            />
          </p>
          <p>
            <input
              type="text"
              name="room"
              value=""
              id="roomName"
              placeholder="Room"
              required
            />
          </p>
          <p class="submit">
            <input type="submit" name="commit" value="Join!" />
          </p>
        </form>
      </div>
      <div id="room" style="display: none;">
        <h2 id="room-header"></h2>
        s
        <div id="participants"></div>
        <input
          type="button"
          id="button-leave"
          onmouseup="leaveRoom();"
          value="Leave room"
        />
      </div>
    </div>
  </div>
</template>

<script>
import { onMounted, onDeactivated, reactive } from "vue";
import kurentoUtils from "kurento-utils";
import webRtcPeer from "webrtc-adapter";
import SockJs from "sockjs-client";
import "./Game.css";

export default {
  name: "Game",
  setup() {
    const state = reactive({
      ws: null,
      participants: {},
      name: "",
    });
    function Participant(name) {
      this.name = name;
      var container = document.createElement("div");
      container.className = isPresentMainParticipant()
        ? PARTICIPANT_CLASS
        : PARTICIPANT_MAIN_CLASS;
      container.id = name;
      var span = document.createElement("span");
      var video = document.createElement("video");
      var rtcPeer;

      container.appendChild(video);
      container.appendChild(span);
      container.onclick = switchContainerClass;
      document.getElementById("participants").appendChild(container);

      span.appendChild(document.createTextNode(name));

      video.id = "video-" + name;
      video.autoplay = true;
      video.controls = false;

      this.getElement = function() {
        return container;
      };

      this.getVideoElement = function() {
        return video;
      };

      function switchContainerClass() {
        if (container.className === PARTICIPANT_CLASS) {
          var elements = Array.prototype.slice.call(
            document.getElementsByClassName(PARTICIPANT_MAIN_CLASS)
          );
          elements.forEach(function(item) {
            item.className = PARTICIPANT_CLASS;
          });

          container.className = PARTICIPANT_MAIN_CLASS;
        } else {
          container.className = PARTICIPANT_CLASS;
        }
      }

      function isPresentMainParticipant() {
        return (
          document.getElementsByClassName(PARTICIPANT_MAIN_CLASS).length != 0
        );
      }

      this.offerToReceiveVideo = function(error, offerSdp, wp) {
        if (error) return console.error("sdp offer error");
        console.log("Invoking SDP offer callback function");
        var msg = { id: "receiveVideoFrom", sender: name, sdpOffer: offerSdp };
        sendMessage(msg);
      };

      this.onIceCandidate = function(candidate, wp) {
        console.log("Local candidate" + JSON.stringify(candidate));

        var message = {
          id: "onIceCandidate",
          candidate: candidate,
          name: name,
        };
        sendMessage(message);
      };

      Object.defineProperty(this, "rtcPeer", { writable: true });

      this.dispose = function() {
        console.log("Disposing participant " + this.name);
        this.rtcPeer.dispose();
        container.parentNode.removeChild(container);
      };
    }

    const PARTICIPANT_MAIN_CLASS = "participant main";
    const PARTICIPANT_CLASS = "participant";

    function register() {
      var name = document.getElementById("name").value;
      var room = document.getElementById("roomName").value;

      document.getElementById("room-header").innerText = "ROOM " + room;
      document.getElementById("join").style.display = "none";
      document.getElementById("room").style.display = "block";

      var message = {
        id: "joinRoom",
        name: name,
        room: room,
      };
      sendMessage(message);
    }

    function onNewParticipant(request) {
      receiveVideo(request.name);
    }

    function receiveVideoResponse(result) {
      state.participants[result.name].rtcPeer.processAnswer(
        result.sdpAnswer,
        function(error) {
          if (error) return console.error(error);
        }
      );
    }

    function callResponse(message) {
      if (message.response != "accepted") {
        console.info("Call not accepted by peer. Closing call");
        stop();
      } else {
        webRtcPeer.processAnswer(message.sdpAnswer, function(error) {
          if (error) return console.error(error);
        });
      }
    }

    function onExistingParticipants(msg) {
      var constraints = {
        audio: true,
        video: {
          mandatory: {
            maxWidth: 320,
            maxFrameRate: 15,
            minFrameRate: 15,
          },
        },
      };
      var room = document.getElementById("roomName").value;
      console.log(name + " registered in room " + room);
      var participant = new Participant(name);
      state.participants[name] = participant;
      var video = participant.getVideoElement();

      var options = {
        localVideo: video,
        mediaConstraints: constraints,
        onicecandidate: participant.onIceCandidate.bind(participant),
      };
      participant.rtcPeer = new kurentoUtils.WebRtcPeer.WebRtcPeerSendonly(
        options,
        function(error) {
          if (error) {
            return console.error(error);
          }
          this.generateOffer(participant.offerToReceiveVideo.bind(participant));
        }
      );

      msg.data.forEach(receiveVideo);
    }

    function leaveRoom() {
      sendMessage({
        id: "leaveRoom",
      });

      for (var key in state.participants) {
        state.participants[key].dispose();
      }

      document.getElementById("join").style.display = "block";
      document.getElementById("room").style.display = "none";

      state.ws.close();
    }

    function receiveVideo(sender) {
      var participant = new Participant(sender);
      state.participants[sender] = participant;
      var video = participant.getVideoElement();

      var options = {
        remoteVideo: video,
        onicecandidate: participant.onIceCandidate.bind(participant),
      };

      participant.rtcPeer = new kurentoUtils.WebRtcPeer.WebRtcPeerRecvonly(
        options,
        function(error) {
          if (error) {
            return console.error(error);
          }
          this.generateOffer(participant.offerToReceiveVideo.bind(participant));
        }
      );
    }

    function onParticipantLeft(request) {
      console.log("Participant " + request.name + " left");
      var participant = state.participants[request.name];
      participant.dispose();
      delete state.participants[request.name];
    }

    function sendMessage(message) {
      var jsonMessage = JSON.stringify(message);
      console.log("Sending message: " + jsonMessage);
      state.ws.send(jsonMessage);
    }

    state.socket = new SockJs("https://localhost:8443/groupcall");
    state.socket.onmessage = function(message) {
      const parsedMessage = JSON.parse(message.data);
      console.info("Received message: " + message.data);

      switch (parsedMessage.id) {
        case "existingParticipants":
          onExistingParticipants(parsedMessage);
          break;
        case "newParticipantArrived":
          onNewParticipant(parsedMessage);
          break;
        case "participantLeft":
          onParticipantLeft(parsedMessage);
          break;
        case "receiveVideoAnswer":
          receiveVideoResponse(parsedMessage);
          break;
        case "iceCandidate":
          state.participants[parsedMessage.name].rtcPeer.addIceCandidate(
            parsedMessage.candidate,
            function(error) {
              if (error) {
                console.error("Error adding candidate: " + error);
                return;
              }
            }
          );
          break;
        default:
          console.error("Unrecognized message", parsedMessage);
      }
    };

    onDeactivated(() => {
      state.socket.close();
    });
    return {
      state,
      register,
      leaveRoom,
    };
  },
};
</script>

<style></style>
