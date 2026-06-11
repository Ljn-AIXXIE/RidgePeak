import {createRouter, createWebHistory} from "vue-router"
import LoginPage from "../components/LoginPage.vue"
import RegisterPage from "../components/RegisterPage.vue"
import HomePage from "../components/HomePage.vue";
import ProfilePage from "../components/ProfilePage.vue";
import {AuthState, UserId} from "../stores/auth.ts";
import AdminPage from "../components/AdminPage.vue";

const routes = [
    {
        path: '/',
        redirect: '/home',
    },
    {
        path: '/home',
        name: 'Home',
        component: HomePage,
    },
    {
        path: '/login',
        name: 'Login',
        component: LoginPage,
    },
    {
        path: '/register',
        name: 'Register',
        component: RegisterPage,
    },
    {
        path: '/profile/:userId(\\d+)',
        name: 'Profile',
        component: ProfilePage,
    },
    {
        path: '/admin',
        name: 'Admin',
        component: AdminPage,
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes,
})

export default router

export function goToProfile() {
    if (AuthState.value) router.push(`/profile/${UserId.value}`)
    else router.push('/login')
}
export function goToUserProfile(userId: string) {
    router.push(`/profile/${userId}`)
}
export function goHome() {
    router.push('/home')
}
export function goLogin() {
    router.push('/login')
}
export function goRegister() {
    router.push('/register')
}
export function goAdmin() {
    router.push('/admin')
}
export function goBack() {
    router.back()
}