import { INGAME } from "../mutation-types";

const state = {
  phase: "READY",
  date: 0,
};

const getters = {
  getPhase: (state) => {
    return state.phase;
  },
  getDate: (state) => {
    return state.date
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
  setDate({ commit }, date) {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        commit(INGAME.SET_DATE, date);
        resolve();
      }, 0);
    });
  },
};

const mutations = {
  [INGAME.SET_PHASE](state, phase) {
    state.phase = phase;
  },
  [INGAME.SET_DATE](state, date) {
    state.date = date;
  }
};

export default {
  namespaced: true,
  state,
  getters,
  mutations,
  actions,
};
