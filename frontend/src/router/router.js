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
        path: "/:catchAll(.*)",
        name: 'NotFound',
        component: () => import('@/views/error/NotFound')
    },
    {
        path: "/nickname",
        name: "Nickname",
        component: () => import('@/views/nickname/Nickname')
    }
];

export const router = createRouter({
    history: createWebHistory(),
    routes
});