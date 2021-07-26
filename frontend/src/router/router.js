import {createWebHistory, createRouter} from 'vue-router';
import { computed} from 'vue'

const routes = [
    {
        path: '/',
        redirect: '/home'
    },
    {
        path: '/home',
        name: 'Home',
        component: () => import('@/views/home/Home'),
    },
    {
        path: '/login',
        name: 'Login',
        component: () => import('@/views/home/components/Login')
    },
    {
        path: '/oauth2/redirect',
        name: 'OauthHandler',
        component: () => import('@/views/home/components/OauthHandler'),
    },
    {
        path: '/room-setting',
        name: 'RoomSetting',
        component: () => import('@/views/room-setting/Room-setting'),
    },
    {
        path: "/nickname/:roomId",
        name: "Nickname",
        props: true,
        component: () => import('@/views/nickname/Nickname')
    },
    {
        path: "/:catchAll(.*)",
        name: 'NotFound',
        component: () => import('@/views/error/NotFound')
    },
    {
        path: "/game",
        name: 'Game',
        component: () => import('@/views/game/Game')
    }
];

export const router = createRouter({
    history: createWebHistory(),
    routes
})
router.beforeEach((to, from, next) => { 
    const isLogin = (localStorage.getItem('token') === null ? false : true)
    console.log(isLogin)
    if (to.name === "RoomSetting" && isLogin) {
        next()
    } else if (to.name === "RoomSetting" && !isLogin) {
        next({ name: "Home"})
    } else {
        next()
    }
})

