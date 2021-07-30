import { TOKEN } from "../mutation-types";

const state = {
  token: "",
  name: "",
  imageUrl: "",
  email: "",
  isLogin: false,
  roomId: "",
  playerId: "",
  nickname: ""
};

const getters = {
  getToken: (state) => {
    return state.token;
  },
  getProfile: (state) => {
    return {
      name: state.name,
      imageUrl: state.imageUrl,
      email: state.email,
    };
  },
  getIsLogin: (state) => {
    return state.isLogin;
  },
  getHeaders: (state) => {
    return {
      "Content-Type": "application/json",
      Authorization: "Bearer " + state.token,
      RoomId: state.roomId,
      PlayerId: state.playerId,
    };
  },
  getRoomId: (state) => {
    return state.roomId
  },
  getPlayerId: (state) => {
    return state.playerId
  },
  getNickname: (state) => {
    return state.nickname
  },
};

const actions = {
  setToken({ commit }, token) {
    commit(TOKEN.SET_TOKEN, token);
  },
  setProfile({ commit }, profile) {
    commit(TOKEN.SET_PROFILE, profile);
  },
  setLogin({ commit }) {
    commit(TOKEN.SET_LOGIN);
  },
  setLogout({ commit }) {
    commit(TOKEN.SET_LOGOUT);
  },
  setRoomId({ commit }) {
    commit(TOKEN.SET_ROOMID)
  },
  setPlayerId({ commit }) {
    commit(TOKEN.SET_PLAYERID)
  },
  setNickname({ commit }, nickname) {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        console.log(nickname);
        commit(TOKEN.SET_NICKNAME, nickname);
        resolve();
      }, 100)
    })
  }
};

const mutations = {
  [TOKEN.SET_TOKEN](state, token) {
    state.token = token;
  },
  [TOKEN.SET_PROFILE](state, profile) {
    state.name = profile.name;
    state.imageUrl = profile.imageUrl;
    state.email = profile.email;
  },
  [TOKEN.SET_LOGIN](state) {
    state.isLogin = true;
  },
  [TOKEN.SET_LOGOUT](state) {
    state.isLogin = false;
  },
  [TOKEN.SET_ROOMID](state, roomId) {
    state.roomId = roomId
  },
  [TOKEN.SET_PLAYERID](state, playerId) {
    state.playerId = playerId
  },
  [TOKEN.SET_NICKNAME](state, nickname) {
    state.nickname = nickname;
  },
};

export default {
  namespaced: true,
  state,
  getters,
  mutations,
  actions,
};
