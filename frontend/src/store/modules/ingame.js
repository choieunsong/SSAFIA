import { INGAME } from "../mutation-types";

const state = {
  phase: "READY",
  isREJOIN: false,
};

const getters = {
  getPhase: (state) => {
    return state.phase;
  },
  getIsREJOIN: (state) => {
    return state.isREJOIN
  },
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
};

const mutations = {
  [INGAME.SET_PHASE](state, phase) {
    state.phase = phase;
  },
  [INGAME.SET_ISREJOIN](state, isREJOIN) {
    state.isREJOIN = isREJOIN;
  },
};

export default {
  namespaced: true,
  state,
  getters,
  mutations,
  actions,
};
