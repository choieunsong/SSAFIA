import {createWebHistory, createRouter} from 'vue-router';

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
        path: "/nickname",
        name: "Nickname",
        component: () => import('@/views/nickname/Nickname')
    },
    {
        path: "/:catchAll(.*)",
        name: 'NotFound',
        component: () => import('@/views/error/NotFound')
    },
    {
        path: "/nickname/{:roomID}",
        name: "Nickname",
        component: () => import('@/views/nickname/Nickname')
    },
    {
        path: "/main/{:roomID}",
        name: 'name',
        component: () => import('@/views/main/Main')
    }
];

export const router = createRouter({
    history: createWebHistory(),
    routes
});