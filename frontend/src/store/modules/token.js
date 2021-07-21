import { TOKEN } from '../mutation-types';

const state = {
    token: '',
    isLogin: false,
}

const getters = {
    getToken: (state) => {
        return state.token;
    },
    getIsLogin: (state) => {
        return state.isLogin;
    }
}

const actions = {
    setToken({ commit }, token) {
        commit(TOKEN.SET_TOKEN, token);
    },
    setIsLogin({ commit }, isLogin) {
        commit(TOKEN.SET_IS_LOGIN, isLogin);
    }
}

const mutations = {
    [TOKEN.SET_TOKEN](state, payload) {
        state.token = payload;
    },
    [TOKEN.SET_IS_LOGIN](state) {
        if (localStorage.getItem("token")) {
            state.isLogin = true;
        } else {
            state.isLogin = false;
        }
    }
}

export default {
    namespaced: true,
    state,
    getters,
    mutations,
    actions
}