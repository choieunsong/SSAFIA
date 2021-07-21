import {createWebHistory, createRouter} from 'vue-router';

const routes = [
    // {
    //     path: '/',
    //     name: 'App',
    //     component: () => import('@/App')
    // },
    {
        path: '/login',
        name: 'Login',
        component: () => import('@/components/Login')
    },
    {
        path: '/oauth2/redirect',
        name: 'OauthHandler',
        component: () => import('@/components/OauthHandler'),
    },
    {
        path: '/profile',
        name: 'Profile',
        component: () => import('@/components/Profile')
    }
];

export const router = createRouter({
    history: createWebHistory(),
    routes
});