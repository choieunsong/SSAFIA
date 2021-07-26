import { createWebHistory, createRouter } from "vue-router";

const routes = [
  {
    path: "/",
    redirect: "/home",
  },
  {
    path: "/home",
    name: "Home",
    component: () => import("@/views/home/Home"),
  },
  {
    path: "/login",
    name: "Login",
    component: () => import("@/views/home/components/Login"),
  },
  {
    path: "/oauth2/redirect",
    name: "OauthHandler",
    component: () => import("@/views/home/components/OauthHandler"),
  },
  {
    path: "/room-setting",
    name: "RoomSetting",
    component: () => import("@/views/room-setting/Room-setting"),
  },
  {
    path: "/nickname/:roomId",
    name: "Nickname",
    component: () => import("@/views/nickname/Nickname"),
  },
  {
    path: "/:catchAll(.*)",
    name: "NotFound",
    props: true,
    component: () => import("@/views/error/NotFound"),
  },
  {
    path: "/game",
    name: "Game",
    component: () => import("@/views/game/Game"),
  },
];

export const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to, from, next) => {
  let vuex = localStorage.getItem("vuex");
  vuex = JSON.parse(vuex);
  if (to.name === "RoomSetting") {
    if (vuex === null) {
      next({ name: "Home" });
    } else if (vuex.token.isLogin === false) {
      next({ name: "Home" });
    } else {
      next();
    }
  } else {
    next();
  }
});
