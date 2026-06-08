
---
# RidgePeak Auth API 文档

**Base URL:** `http://localhost:8080/api/auth`

**通用响应格式：**
| 字段 | 类型 | 说明 |
|------|------|------|
| `code` | int | 200 成功，400 业务错误 |
| `message` | string | `"success"` 或具体错误信息 |
| `data` | any / null | 响应数据，无数据时为 `null` |

---

## 1. 注册

```
 POST /register
 ```

**认证：** 无需

**请求体：**

| 字段         | 类型     | 必填 | 校验规则                         |
|------------|--------|:--:|------------------------------|
| `username` | string | 是  | 3-30 位，只允许数字、大小写字母、下划线       |
| `password` | string | 是  | 8-30 位，只允许数字、大小写字母、`@#$%^&*` |
| `email`    | string | 是  | 合法邮箱格式                       |

**请求示例：**
```json
{
  "username": "zhangsan",
  "password": "Secure@Pass1",
  "email": "zhangsan@example.com"
}
 ```

**成功响应 (200)：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 21600
  }
}
```
> 注册成功即登录，`expiresIn` 单位：秒（21600 = 6 小时）。

**错误响应 (400)：**

| message                                         |
|-------------------------------------------------|
| `用户名必须是：3-30位字符，只允许数字、大小写字母、下划线`                |
| `密码必须是：8-30位字符，只允许数字、大小写字母、以及 @ # $ % ^ & * 字符` |
| `无效的电子邮件格式`                                     |
| `用户名已存在`                                        |
| `邮箱已被注册`                                        |

---

## 2. 登录

```
 POST /login
```

**认证：** 无需

**请求体：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| `account` | string | 是 | 用户名或邮箱（支持 `JsonAlias`：也可传 `username` 或 `email`） |
| `password` | string | 是 | 密码明文 |

**请求示例：**
```json
{
  "account": "zhangsan",
  "password": "Secure@Pass1"
}
 ```
```json
{
  "email": "zhangsan@example.com",
  "password": "Secure@Pass1"
}
```

**成功响应 (200)：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 21600
  }
}
```
> 每次登录会签发新 Token，**旧 Token 立即失效**（单点登录）。

**错误响应 (400)：**

| message      |
|--------------|
| `用户名/邮箱不能为空` |
| `密码不能为空`     |
| `邮箱未注册`      |
| `用户名不存在`     |
| `密码错误`       |

---

## 3. 登出

```
GET /logout
```

**认证：** 需要

**请求头：**
```
Authorization: Bearer <accessToken>
```

**请求体：** 无

**成功响应 (200)：**
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```
> 登出后该用户所有 Token 立即失效。

**错误响应 (400)：**

| message       |
|---------------|
| `未登录或验证失败`    |
| `登录已过期，请重新登录` |

---

## 4. Token 校验

```
GET /validate
```

**认证：** 无需（公开接口，手动处理 Token）

**请求头：**
```
Authorization: Bearer <accessToken>
```

**请求体：** 无

**成功响应 (200)：**
```json
// token 有效
{ "code": 200, "message": "success", "data": true }

// token 无效（无 Header / 格式错误 / 过期 / 已登出）
{ "code": 200, "message": "success", "data": false }
```
> 无论校验是否通过，`code` 始终为 200，结果看 `data`（`true` / `false`）。

---

## 全局说明

- Token 类型为 **JWT (HS256)**，有效期 6 小时
- Token 附带服务端撤销机制，登录或登出会使旧 Token 失效
- 所有业务错误的 `code` 为 `400`，`message` 为人类可读的中文提示
- 请求体 JSON 格式错误时返回 `{ "code": 400, "message": "请求体格式错误，请检查 JSON 结构" }`