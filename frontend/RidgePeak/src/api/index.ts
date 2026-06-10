import Cookie from "../stores/cookie.ts";
import url from "./config.ts"
import axios from "axios"
import cookie from "../stores/cookie.ts";

export default {
    async register(username: string, password: string, email: string) {
        try {
            const response = await axios.post(`${url.base}/register`, {
                username: username,
                password: password,
                email: email
            });

            if (response.status !== 200 || !response.data.data) return {
                success: false,
                message: response.data.message,
            }

            Cookie.setCookie(
                "token",
                response.data.data.accessToken,
                response.data.data.expiresIn
            )

            return {
                success: true,
                message: "注册成功，欢迎 " + username,
            }
        } catch (err: any) {
            console.log(err);
            return {
                success: false,
                message: "注册失败，" + err.message
            };
        }
    },

    async login(username: string, password: string) {
        try {
            const response = await axios.post(`${url.base}/login`, {
                account: username,
                password: password,
            })

            if (response.status !== 200 || !response.data.data) return {
                success: false,
                message: response.data.message,
            }

            Cookie.setCookie(
                "token",
                response.data.data.accessToken,
                response.data.data.expiresIn
            )

            return {
                success: true,
                message: "登录成功，欢迎回来 " + username,
            }

        } catch (err: any) {
            console.log(err);
            return {
                success: false,
                message: "登录失败，" + err.message
            };
        }
    },

    async logout() {
        try {
            const response = await axios.post(`${url.base}/logout`, {
                headers: { Authorization: "Bearer " + cookie.getCookie("token") }
            });

            if (!response.data.data) return {
                success: false,
                message: response.data.message
            }

            Cookie.deleteCookie("token");

            return {
                success: true,
                message: "登出成功",
            };
        } catch (err: any) {
            console.log(err);
            return {
                success: false,
                message: "登录失败，" + err.message
            };
        }
    },

    async validate() {
        if (Cookie.getCookie("token") == "") return false;

        try {
            const response = await axios.get(`${url.base}/validate`, {
                headers: { Authorization: "Bearer " + cookie.getCookie("token") }
            });

            return response.data.data;
        } catch (err) {
            console.log(err);
            return false;
        }
    },

    async getMe() {
        try {
            const response = await axios.get(`${url.profile}/me`, {
                headers: { Authorization: "Bearer " + cookie.getCookie("token") },
            })

            if (response.status !== 200 || !response.data.data) return {
                success: false,
                message: response.data.message
            }

            return {
                success: true,
                message: "获取用户个人信息成功",
                ...response.data,
            };
        } catch (err: any) {
            console.log(err);
            return {
                success: false,
                message: "登录失败，" + err.message
            }
        }
    }
}