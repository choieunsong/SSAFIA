import { INGAME } from "../mutation-types";

const state = {
  phase: "READY",

};

const getters = {
  getPhase: (state) => {
    return state.phase;
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
};

const mutations = {
  [INGAME.SET_PHASE](state, phase) {
    state.phase = phase;
  },
};

export default {
  namespaced: true,
  state,
  getters,
  mutations,
  actions,
};
