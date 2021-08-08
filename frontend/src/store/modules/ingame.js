import { INGAME } from "../mutation-types";

const state = {
  phase: "READY",
<<<<<<< HEAD
  date: 0,
=======

>>>>>>> f94bc87c4414e253cf2dd13da8b99869bb589d22
};

const getters = {
  getPhase: (state) => {
    return state.phase;
<<<<<<< HEAD
  },
  getDate: (state) => {
    return state.date
=======
>>>>>>> f94bc87c4414e253cf2dd13da8b99869bb589d22
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
  setPhase({ commit }, date) {
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
  [INGAEM.SET_DATE](state, date) {
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
