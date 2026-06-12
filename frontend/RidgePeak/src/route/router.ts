import {createRouter, createWebHistory} from "vue-router"
import LoginPage from "../components/LoginPage.vue"
import RegisterPage from "../components/RegisterPage.vue"
import HomePage from "../components/HomePage.vue";
import ProfilePage from "../components/ProfilePage.vue";
import AdminPage from "../components/AdminPage.vue";
import PostCreatePage from "../components/PostCreatePage.vue";
import PostDetailPage from "../components/PostDetailPage.vue";
import PostEditPage from "../components/PostEditPage.vue";

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
    },
    {
        path: '/post/create',
        name: 'PostCreate',
        component: PostCreatePage,
    },
    {
        path: '/post/edit/:postId(\\d+)',
        name: 'PostEdit',
        component: PostEditPage,
    },
    {
        path: '/post/:postId(\\d+)',
        name: 'PostDetail',
        component: PostDetailPage,
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes,
})

export default router

export function goUserProfile(userId: number) {
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
export function goPostCreate() {
    router.push('/post/create')
}
export function goPostEdit(postId: number) {
    router.push(`/post/edit/${postId}`)
}
export function goPostDetail(postId: number) {
    router.push(`/post/${postId}`)
}
export function goBack() {
    router.back()
}