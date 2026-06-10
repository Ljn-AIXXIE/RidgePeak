import { createApp } from 'vue'
import { Message } from "@arco-design/web-vue"
import './style/style.css'
import App from './App.vue'
import router from "./route/router.ts"
import {createPinia} from "pinia"
import {ClearUserInfo, RefreshUserInfo, useAuthStore} from "./stores/auth.ts";
import api from "./api";
import Cookie, {Token} from "./stores/cookie.ts";
import UIUtils from "./utils/UIUtils.ts";

const app = createApp(App)
const pinia = createPinia()

Message._context = app._context

app.use(router)
app.use(pinia)

const token = Cookie.getCookie(Token);

const path = router.currentRoute.value.fullPath
const routerLoggedInPage = !(path === '/' || path === '/login' || path === '/register');

if (token === "" && routerLoggedInPage) {
    router.push("/login");
}
else if (token !== "" && routerLoggedInPage) {
    const authStore = useAuthStore()
    const tokenValid = await api.validate()
    authStore.setAuthState(tokenValid)

    if (tokenValid) await RefreshUserInfo();
    else ClearUserInfo();

    if (!tokenValid) {
        UIUtils.info("登录已失效")
        router.push("/login");
    }
}

app.config.errorHandler = (err) => {
    console.log(err)
}

app.mount('#app')