import { INGAME } from "../mutation-types";

const state = {
  role: undefined,
  gameStatus: {
    date: 0,
    phase: "ready",
    timer: 0,
    aliveMafia: 0,
    victim: undefined,
    victimIsMafia: undefined,
    playerNum: 1,
  },
};

const getters = {
  getGameStatus: (state) => {
    return state.gameStatus;
  },
  getRole: (state) => {
    return state.role;
  },
  getPlayerNum: (state) => {
    return state.gameStatus.playerNum;
  },
};

const actions = {
  setGameStatus({ commit }, gameStatus) {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        console.log(gameStatus);
        commit(INGAME.SER_GAMESTATUS, gameStatus);
        resolve();
      }, 0);
    });
  },
  setRole({ commit }, role) {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        console.log(role);
        commit(INGAME.SET_ROLE, role);
        resolve();
      }, 0);
    });
  },
  joinPlayer({ commit }) {
    commit(INGAME.JOIN_PLAYER);
  },
  leavePlayer({ commit }) {
    commit(INGAME.LEAVE_PLAYER);
  },
};

const mutations = {
  [INGAME.SET_GAMESTATUS](state, gameStatus) {
    state.gameStatus = gameStatus;
  },
  [INGAME.SET_ROLE](state, role) {
    state.rolel = role;
  },
  [INGAME.JOIN_PLAYER](state) {
    state.gameStatus.playerNum += 1;
  },
  [INGAME.LEAVE_PLAYER](state) {
    state.gameStatus.playerNum -= 1;
  },
};

export default {
  namespaced: true,
  state,
  getters,
  mutations,
  actions,
};
