import { createStore } from "vuex";
import token from './modules/token';
import ingame from './modules/ingame';
import createPersistedState from "vuex-persistedstate";

export default createStore({
    modules: { token, ingame },
    plugins: [createPersistedState({
        paths: ['token', 'ingame']
    })],
});
