import { INGAME } from "../mutation-types";

const state = {
  phase: "READY",
  isREJOIN: false,
  color: "",
};

const getters = {
  getPhase: (state) => {
    return state.phase;
  },
  getIsREJOIN: (state) => {
    return state.isREJOIN
  },
  getColor: (state) => {
    return state.color
  }
};

const actions = {
  setPhase({ commit }, phase) {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        commit(INGAME.SET_PHASE, phase);
        resolve();
      }, 0);
    });
  },
  setIsREJOIN({ commit }, isREJOIN) {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        commit(INGAME.SET_ISREJOIN, isREJOIN);
        resolve();
      }, 0);
    });
  },
  setColor({ commit }, color) {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        commit(INGAME.SET_COLOR, color);
        resolve();
      }, 0);
    });
  }
};

const mutations = {
  [INGAME.SET_PHASE](state, phase) {
    state.phase = phase;
  },
  [INGAME.SET_ISREJOIN](state, isREJOIN) {
    state.isREJOIN = isREJOIN;
  },
  [INGAME.SET_COLOR](state, color) {
    state.color = color
  }
};

export default {
  namespaced: true,
  state,
  getters,
  mutations,
  actions,
};
