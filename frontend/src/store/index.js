import { createStore } from "vuex";
import token from './modules/token';
import ingame from './modules/ingame';
export default createStore({
    modules: { token, ingame },
});
