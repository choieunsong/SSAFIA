import { TOKEN } from "../mutation-types";

const state = {
  token: "",
  name: "",
  imageUrl: "",
  email: "",
  isLogin: false,
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
    };
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
};

export default {
  namespaced: true,
  state,
  getters,
  mutations,
  actions,
};
