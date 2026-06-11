import Cookie, {Token} from "../stores/cookie.ts";
import url from "./config.ts"
import axios from "axios"
import cookie from "../stores/cookie.ts";
import {type Category} from "../stores/category.ts";
import type {FCategoryIdType, FKeyWordType, FPageType, Wall, WallDetail} from "../stores/wall.ts";
import type {FSortType} from "../stores/wall.ts";

export default {
    /**
     * post /auth/register
     * @param username {string} 用户名
     * @param password {string} 密码
     * @param email {string} 邮箱
     */
    async register(username: string, password: string, email: string) {
        try {
            const response = await axios.post(`${url.base}/register`, {
                username: username,
                password: password,
                email: email
            });

            if (response.status !== 200 || !response.data.data) return {
                success: false,
                message: response.data.message as string
            }

            Cookie.setCookie(
                Token,
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
                message: "注册失败，" + err.message as string
            };
        }
    },

    /**
     * post /auth/login
     * @param username {string} 用户名
     * @param password {string} 密码
     */
    async login(username: string, password: string) {
        try {
            const response = await axios.post(`${url.base}/login`, {
                account: username,
                password: password,
            })

            if (response.status !== 200 || !response.data.data) return {
                success: false,
                message: response.data.message as string
            }

            Cookie.setCookie(
                Token,
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
                message: "登录失败，" + err.message as string
            };
        }
    },

    /**
     * get /auth/logout
     */
    async logout() {
        try {
            const response = await axios.get(`${url.base}/logout`, {
                headers: { Authorization: "Bearer " + cookie.getCookie(Token) }
            });

            if (!response.data.data) return {
                success: false,
                message: response.data.message as string
            }

            Cookie.deleteCookie(Token);

            return {
                success: true,
                message: "登出成功",
            };
        } catch (err: any) {
            console.log(err);
            return {
                success: false,
                message: "登出失败，" + err.message as string
            };
        }
    },

    /**
     * get /auth/validate
     */
    async validate() {
        if (Cookie.getCookie(Token) == "") return false;

        try {
            const response = await axios.get(`${url.base}/validate`, {
                headers: { Authorization: "Bearer " + cookie.getCookie(Token) }
            });

            return response.data.data as boolean;
        } catch (err) {
            console.log(err);
            return false;
        }
    },

    /**
     * get /profile/me
     */
    async getMe() {
        try {
            const response = await axios.get(`${url.profile}/me`, {
                headers: { Authorization: "Bearer " + cookie.getCookie(Token) },
            })

            if (response.status !== 200 || !response.data.data) return {
                success: false,
                message: response.data.message as string
            }

            response.data.data.avatarUrl = response.data.data.avatarUrl && url.serverHost + response.data.data.avatarUrl

            return {
                success: true,
                message: "获取用户个人信息成功",
                ...response.data.data,
            };
        } catch (err: any) {
            console.log(err);
            return {
                success: false,
                message: "获取用户个人信息失败，" + err.message as string
            }
        }
    },

    /**
     * put /profile/me
     * @param nickname 昵称
     */
    async putMe(nickname: string) {
        try {
            const response = await axios.put(`${url.profile}/me`, {
                nickname: nickname,
            }, {
                headers: { Authorization: "Bearer " + cookie.getCookie(Token) }
            })

            if (response.status !== 200) return {
                success: false,
                message: response.data.message as string
            }

            return {
                success: true,
                message: "修改昵称成功"
            };
        } catch (err: any) {
            console.log(err);
            return {
                success: false,
                message: "修改昵称失败，" + err.message as string
            }
        }
    },

    /**
     * post /profile/me/password
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    async changePwd(oldPassword: string, newPassword: string) {
        try {
            const response = await axios.post(`${url.profile}/me/password`, {
                oldPassword: oldPassword,
                newPassword: newPassword,
            }, {
                headers: { Authorization: "Bearer " + cookie.getCookie(Token) }
            })

            if (response.status !== 200) return {
                success: false,
                message: response.data.message as string
            }

            return {
                success: true,
                message: "密码修改成功"
            };
        } catch (err: any) {
            console.log(err);
            return {
                success: false,
                message: "密码修改失败，" + err.message as string
            }
        }
    },

    /**
     * post /profile/me/avatar
     * @param img 二进制图片
     */
    async uploadAvatar(img: File) {
        try {
            const formData = new FormData();
            formData.append("file", img);

            const response = await axios.post(`${url.profile}/me/avatar`, formData, {
                headers: {
                    Authorization: "Bearer " + cookie.getCookie(Token),
                    'Content-Type': 'multipart/form-data'
                },
            })

            if (response.status !== 200) return {
                success: false,
                message: response.data.message as string
            }

            return {
                success: true,
                message: "头像上传成功",
                data: url.serverHost + response.data.data as string
            };
        } catch (err: any) {
            console.log(err);
            return {
                success: false,
                message: "头像上传失败，" + err.message as string
            }
        }
    },

    /**
     * get /profile/{userId}
     * @param userId 用户ID
     */
    async getUser(userId: string) {
        try {
            const response = await axios.get(`${url.profile}/${userId}`)

            if (response.status !== 200 || !response.data.data) return {
                success: false,
                message: response.data.message as string
            }

            return {
                success: true,
                message: "获取信息成功",
                ...response.data.data,
            };
        } catch (err: any) {
            console.log(err);
            return {
                success: false,
                message: "获取信息失败，" + err.message as string
            }
        }
    },

    /**
     * get /category/list
     */
    async getCategories() {
        try {
            const response = await axios.get(`${url.category}/list`)

            if (response.status !== 200 || !response.data.data) return {
                success: false,
                message: response.data.message as string
            }

            return {
                success: true,
                message: "获取信息成功",
                data: response.data.data as Array<Category>
            };
        } catch (err: any) {
            console.log(err);
            return {
                success: false,
                message: "获取信息失败，" + err.message as string
            }
        }
    },

    /**
     * get /category/{categoryId}
     */
    async getCategory(categoryId: number) {
        try {
            const response = await axios.get(`${url.category}/${categoryId}`)

            if (response.status !== 200 || !response.data.data) return {
                success: false,
                message: response.data.message as string
            }

            return {
                success: true,
                message: "获取信息成功",
                data: response.data.data as Category
            };
        } catch (err: any) {
            console.log(err);
            return {
                success: false,
                message: "获取信息失败，" + err.message as string
            }
        }
    },

    /**
     * post /category/create
     * need ADMIN
     */
    async createCategory(name: string, description: string) {
        try {
            const response = await axios.post(`${url.category}/create`, {
                name: name,
                description: description
            }, {
                headers: { Authorization: "Bearer " + cookie.getCookie(Token) },
            })

            if (response.status !== 200 || !response.data.data) return {
                success: false,
                message: response.data.message as string
            }

            return {
                success: true,
                message: "获取信息成功",
                categoryId: response.data.data.id as number
            };
        } catch (err: any) {
            console.log(err);
            return {
                success: false,
                message: "获取信息失败，" + err.message as string
            }
        }
    },

    /**
     * delete /category/{categoryId}
     * need ADMIN
     */
    async deleteCategory(categoryId: number) {
        try {
            const response = await axios.delete(`${url.category}/${categoryId}`, {
                headers: { Authorization: "Bearer " + cookie.getCookie(Token) },
            })

            if (response.status !== 200) return {
                success: false,
                message: response.data.message as string
            }

            return {
                success: true,
                message: "获取信息成功"
            };
        } catch (err: any) {
            console.log(err);
            return {
                success: false,
                message: "获取信息失败，" + err.message as string
            }
        }
    },

    /**
     * get /post?keyword={}&category={}&sort={}&page={}&size={}
     */
    async getWalls(categoryId?: FCategoryIdType, keyword?: FKeyWordType, sort?: FSortType, page?: FPageType, size?: number) {
        try {
            const response = await axios.get(`${url.post}`, {
                params: {
                    category: categoryId,
                    keyword: keyword,
                    sort: sort,
                    page: page,
                    size: size
                }
            })

            if (response.status !== 200) return {
                success: false,
                message: response.data.message as string
            }

            return {
                success: true,
                message: "获取信息成功",
                posts: response.data.data.posts as Array<Wall>,
                postCount: response.data.data.postCount as number,
                currentPage: response.data.data.currentPage as number,
                totalPages: response.data.data.totalPages as number
            };
        } catch (err: any) {
            console.log(err);
            return {
                success: false,
                message: "获取信息失败，" + err.message as string
            }
        }
    },

    /**
     * get /post/{postId}
     */
    async getWallDetail(postId: number) {
        try {
            const response = await axios.get(`${url.post}/${postId}`)

            if (response.status !== 200) return {
                success: false,
                message: response.data.message as string
            }

            return {
                success: true,
                message: "获取信息成功",
                data: response.data.data as WallDetail,
            };
        } catch (err: any) {
            console.log(err);
            return {
                success: false,
                message: "获取信息失败，" + err.message as string
            }
        }
    },

    /**
     * post /post/create
     */
    async createWall(categoryId: number, title: string, content: string) {
        try {
            const response = await axios.post(`${url.post}/create`, {
                category: categoryId,
                title: title,
                content: content
            }, {
                headers: { Authorization: "Bearer " + cookie.getCookie(Token) },
            })

            if (response.status !== 200) return {
                success: false,
                message: response.data.message as string
            }

            return {
                success: true,
                message: "获取信息成功",
                postId: response.data.data as number
            };
        } catch (err: any) {
            console.log(err);
            return {
                success: false,
                message: "获取信息失败，" + err.message as string
            }
        }
    },

    /**
     * post /post/{postId}
     */
    async editWall(postId: number, categoryId: number, title: string, content: string) {
        try {
            const response = await axios.post(`${url.post}/${postId}`, {
                categoryId: categoryId,
                title: title,
                content: content
            }, {
                headers: { Authorization: "Bearer " + cookie.getCookie(Token) },
            })

            if (response.status !== 200) return {
                success: false,
                message: response.data.message
            }

            return {
                success: true,
                message: "获取信息成功"
            };
        } catch (err: any) {
            console.log(err);
            return {
                success: false,
                message: "获取信息失败，" + err.message
            }
        }
    },

    /**
     * delete /post/{postId}
     */
    async deleteWall(postId: number) {
        try {
            const response = await axios.delete(`${url.post}/${postId}`, {
                headers: { Authorization: "Bearer " + cookie.getCookie(Token) },
            })

            if (response.status !== 200) return {
                success: false,
                message: response.data.message
            }

            return {
                success: true,
                message: "获取信息成功"
            };
        } catch (err: any) {
            console.log(err);
            return {
                success: false,
                message: "获取信息失败，" + err.message
            }
        }
    },

    /**
     * post /post/{postId}/like
     */
    async likeWall(postId: number) {
        try {
            const response = await axios.delete(`${url.post}/${postId}/like`, {
                headers: { Authorization: "Bearer " + cookie.getCookie(Token) },
            })

            if (response.status !== 200) return {
                success: false,
                message: response.data.message
            }

            return {
                success: true,
                message: "获取信息成功",
                like: response.data.data as boolean
            };
        } catch (err: any) {
            console.log(err);
            return {
                success: false,
                message: "获取信息失败，" + err.message
            }
        }
    },
}