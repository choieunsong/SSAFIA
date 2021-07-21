import { createStore } from "vuex";
import token from './modules/token';

export default createStore({
    modules: { token },
});
