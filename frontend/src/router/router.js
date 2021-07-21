import {createWebHistory, createRouter} from 'vue-router';

const routes = [
    {
        path: '/',
        redirect: '/home'
    },
    {
        path: '/home',
        name: 'Home',
        component: () => import('@/views/home/components/Home'),
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
        path: "/:catchAll(.*)",
        name: 'NotFound',
        component: () => import('@/views/error/NotFound')
    }
];

export const router = createRouter({
    history: createWebHistory(),
    routes
});