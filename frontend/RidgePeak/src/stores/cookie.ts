export const Token: string = 'token'
/**
 * set new cookie by name and value
 * @param name
 * @param value
 * @param outTime time by (s)
 */
function setCookie(name: string, value: any, outTime: number) {
    const d = new Date()
    d.setTime(d.getTime() + outTime * 1000)
    const expires = `expires=${d.toUTCString()}`
    document.cookie = `${name}=${value}; expires=${expires}; path=/;`
}

/**
 * get exist cookie by name
 * @param name
 */
function getCookie(name: string) {
    const _name = name + "="
    const ca = document.cookie.split(";")
    for (let i = 0; i < ca.length; i++) {
        const c = ca[i].trim()
        if (c.indexOf(_name) === 0) return c.substring(_name.length, c.length)
    }
    return ""
}

/**
 * delete exist cookie by name
 * @param name
 */
function deleteCookie(name: string) {
    // 设置过期时间为过去的日期
    document.cookie = name + "= expires=Thu, 01 Jan 1970 00:00:00 UTC path=/;"
}

export default { setCookie, getCookie, deleteCookie }