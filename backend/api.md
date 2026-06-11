
---
# RidgePeak Auth API 文档

**Base URL:** `http://localhost:8080/api/auth`

**通用响应格式：**

| 字段        | 类型         | 说明                                |
|-----------|------------|-----------------------------------|
| `code`    | int        | 200 成功，400 业务错误，401 认证错误，403 权限不足 |
| `message` | string     | `"success"` 或具体错误信息               |
| `data`    | any / null | 响应数据，无数据时为 `null`                 |

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

| 字段         | 类型     | 必填 | 说明                                              |
|------------|--------|:--:|-------------------------------------------------|
| `account`  | string | 是  | 用户名或邮箱（支持 `JsonAlias`：也可传 `username` 或 `email`） |
| `password` | string | 是  | 密码明文                                            |

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

**错误响应：**

| code | message       |
|------|---------------|
| 401  | `未登录或验证失败`    |
| 401  | `登录已过期，请重新登录` |

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
- 业务错误的 `code` 为 `400`，认证错误的 `code` 为 `401`
- `message` 为人类可读的中文提示
- 请求体 JSON 格式错误时返回 `{ "code": 400, "message": "请求体格式错误，请检查 JSON 结构" }`

---

# RidgePeak Profile API 文档

**Base URL:** `http://localhost:8080/api/profile`

---

## 1. 获取自己的个人信息

```
GET /me
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
    "data": {
        "userId": 1,
        "username": "zhangsan",
        "email": "zhangsan@example.com",
        "nickname": "张三",
        "avatarUrl": "/uploads/avatars/a1b2c3d4.png",
        "role": "USER",
        "createdAt": "2026-06-08T12:00:00",
        "updatedAt": "2026-06-08T18:30:00",
        "lastLoginTime": "2026-06-08T18:00:00"
    }
}
```

**错误响应：**

| code | message     |
|------|-------------|
| 401  | `未登录或验证失败`  |
| 401  | `登录已过期，请重新登录` |

---

## 2. 修改个人信息

```
PUT /me
```

**认证：** 需要

**请求头：**
```
Authorization: Bearer <accessToken>
```

**请求体：**

| 字段         | 类型     | 必填 | 校验规则              |
|------------|--------|:--:|-------------------|
| `nickname` | string | 是  | 1-30 位字符，不能为空     |

**请求示例：**
```json
{
    "nickname": "张三"
}
```

**成功响应 (200)：**
```json
{
    "code": 200,
    "message": "success",
    "data": null
}
```

**错误响应：**

| code | message         |
|------|-----------------|
| 400  | `昵称不能为空`        |
| 400  | `昵称必须是：1-30位字符` |
| 401  | `未登录或验证失败`      |
| 401  | `登录已过期，请重新登录`   |

---

## 3. 获取他人公开信息

```
GET /{userId}
```

**认证：** 无需

**路径参数：**

| 参数       | 类型   | 说明    |
|----------|------|-------|
| `userId` | Long | 用户 ID |

**请求体：** 无

**成功响应 (200)：**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "userId": 1,
        "username": "zhangsan",
        "nickname": "张三",
        "avatarUrl": "/uploads/avatars/a1b2c3d4.png",
        "role": "USER"
    }
}
```
> 公开接口，不返回 `email`、`lastLoginTime` 等私密字段。

**错误响应：**

| code | message  |
|------|----------|
| 400  | `用户不存在`  |

---

## 4. 修改密码

```
POST /me/password
```

**认证：** 需要

**请求头：**
```
Authorization: Bearer <accessToken>
```

**请求体：**

| 字段            | 类型     | 必填 | 校验规则                         |
|---------------|--------|:--:|------------------------------|
| `oldPassword` | string | 是  | 不能为空                         |
| `newPassword` | string | 是  | 8-30 位，只允许数字、大小写字母、`@#$%^&*` |

**请求示例：**
```json
{
    "oldPassword": "Old@Pass1",
    "newPassword": "New@Pass2"
}
```

**成功响应 (200)：**
```json
{
    "code": 200,
    "message": "success",
    "data": null
}
```

**错误响应：**

| code | message                                         |
|------|-------------------------------------------------|
| 400  | `旧密码不能为空`                                       |
| 400  | `密码必须是：8-30位字符，只允许数字、大小写字母、以及 @ # $ % ^ & * 字符` |
| 400  | `旧密码不正确`                                        |
| 400  | `新密码不能与旧密码相同`                                   |
| 401  | `未登录或验证失败`                                      |
| 401  | `登录已过期，请重新登录`                                   |

---

## 5. 上传头像

```
POST /me/avatar
Content-Type: multipart/form-data
```

**认证：** 需要

**请求头：**
```
Authorization: Bearer <accessToken>
```

**表单字段：**

| 字段     | 类型   | 必填 | 校验规则              |
|--------|------|:--:|-------------------|
| `file` | file | 是  | 图片类型，大小不超过 2MB    |

**请求示例（curl）：**
```bash
curl -X POST http://localhost:8080/api/profile/me/avatar \
  -H "Authorization: Bearer <accessToken>" \
  -F "file=@avatar.png"
```

**成功响应 (200)：**
```json
{
    "code": 200,
    "message": "success",
    "data": "/uploads/avatars/e4f5g6h7.png"
}
```
> 返回值为头像的相对路径，拼上 Base URL 即可访问：`http://localhost:8080/uploads/avatars/e4f5g6h7.png`

**错误响应：**

| code | message          |
|------|------------------|
| 400  | `文件为空`           |
| 400  | `只允许上传图片文件`      |
| 400  | `图片大小不能超过 2MB`   |
| 400  | `文件上传失败`         |
| 401  | `未登录或验证失败`       |
| 401  | `登录已过期，请重新登录`    |

---

## 全局说明

- 静态文件 `/uploads/**` 已映射为公开访问，头像等资源无需登录
- 文件上传限制：单文件最大 2MB

---

# RidgePeak Category API 文档

**Base URL:** `http://localhost:8080/api/category`

---

## 1. 板块列表

```
GET /list
```

**认证：** 无需

**请求体：** 无

**成功响应 (200)：**
```json
{
    "code": 200,
    "message": "success",
    "data": [
        {
            "id": 1,
            "name": "技术交流",
            "description": "编程、架构相关讨论"
        }
    ]
}
```
> `data` 为数组，无板块时返回空数组 `[]`。

---

## 2. 板块详情

```
GET /{categoryId}
```

**认证：** 无需

**路径参数：**

| 参数           | 类型   | 说明     |
|--------------|------|--------|
| `categoryId` | Long | 板块 ID  |

**请求体：** 无

**成功响应 (200)：**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": 1,
        "name": "技术交流",
        "description": "编程、架构相关讨论"
    }
}
```

**错误响应：**

| code | message  |
|------|----------|
| 400  | `板块不存在`  |

---

## 3. 创建板块

```
POST /create
```

**认证：** 需要（ADMIN 角色）

**请求头：**
```
Authorization: Bearer <accessToken>
```

**请求体：**

| 字段            | 类型     | 必填 | 校验规则         |
|---------------|--------|:--:|--------------|
| `name`        | string | 是  | 1-20 位字符     |
| `description` | string | 否  | —            |

**请求示例：**
```json
{
    "name": "技术交流",
    "description": "编程、架构相关讨论"
}
```

**成功响应 (200)：**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": 1,
        "name": "技术交流",
        "description": "编程、架构相关讨论"
    }
}
```

**错误响应：**

| code | message       |
|------|---------------|
| 400  | `板块名不能为空`     |
| 400  | `该板块已存在`      |
| 401  | `未登录或验证失败`    |
| 401  | `登录已过期，请重新登录` |
| 403  | `无权操作`        |

---

## 4. 删除板块

```
DELETE /{categoryId}
```

**认证：** 需要（ADMIN 角色）

**请求头：**
```
Authorization: Bearer <accessToken>
```

**路径参数：**

| 参数           | 类型   | 说明     |
|--------------|------|--------|
| `categoryId` | Long | 板块 ID  |

**请求体：** 无

**成功响应 (200)：**
```json
{
    "code": 200,
    "message": "success",
    "data": null
}
```

**错误响应：**

| code | message       |
|------|---------------|
| 400  | `板块不存在`       |
| 401  | `未登录或验证失败`    |
| 401  | `登录已过期，请重新登录` |
| 403  | `无权操作`        |

---

# RidgePeak Post API 文档

**Base URL:** `http://localhost:8080/api/post`

---

## 1. 帖子列表 / 搜索

```
GET /
```

**认证：** 无需

**查询参数：**

| 参数         | 类型     | 必填 | 说明                                      |
|------------|--------|:--:|-----------------------------------------|
| `keyword`  | string | 否  | 标题关键词，模糊匹配                             |
| `category` | long   | 否  | 板块 ID，不传 = 全板块                          |
| `sort`     | string | 否  | `latest`（最新，默认）/ `popular`（最多浏览）        |
| `page`     | int    | 否  | 页码，从 0 开始，默认 0                          |
| `size`     | int    | 否  | 每页条数，默认 10                              |

**请求示例：**
```
GET /api/post
GET /api/post?category=1
GET /api/post?keyword=Spring
GET /api/post?keyword=Spring&category=1&sort=popular&page=1&size=10
```

**请求体：** 无

**成功响应 (200)：**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "posts": [
            {
                "postId": 1,
                "title": "Spring Boot 入门教程",
                "trimmedContent": "这是一篇入门教程...",
                "categoryName": "技术交流",
                "authorName": "zhangsan",
                "viewCount": 128,
                "createdAt": "2026-06-08T12:00:00"
            }
        ],
        "postCount": 42,
        "currentPage": 0,
        "totalPages": 5
    }
}
```

**错误响应：**

| code | message  |
|------|----------|
| 400  | `板块不存在`  |

---

## 2. 帖子详情

```
GET /{postId}
```

**认证：** 无需

**路径参数：**

| 参数       | 类型   | 说明     |
|----------|------|--------|
| `postId` | Long | 帖子 ID  |

**请求体：** 无

**成功响应 (200)：**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "title": "Spring Boot 入门教程",
        "content": "这是一篇入门教程...",
        "viewCount": 129,
        "category": {
            "id": 1,
            "name": "技术交流"
        },
        "author": {
            "userId": 1,
            "username": "zhangsan",
            "nickname": "张三",
            "avatarUrl": "/uploads/avatars/xxx.png",
            "role": "USER"
        },
        "createdAt": "2026-06-08T12:00:00",
        "updatedAt": "2026-06-08T18:30:00"
    }
}
```
> 每次访问详情 `viewCount` 自动 +1。

**错误响应：**

| code | message  |
|------|----------|
| 400  | `帖子不存在`  |

---

## 3. 发帖

```
POST /create
```

**认证：** 需要

**请求头：**
```
Authorization: Bearer <accessToken>
```

**请求体：**

| 字段           | 类型     | 必填 | 校验规则            |
|--------------|--------|:--:|-----------------|
| `categoryId` | long   | 是  | 不能为空            |
| `title`      | string | 是  | 1-100 位字符       |
| `content`    | string | 是  | 不能为空            |

**请求示例：**
```json
{
    "categoryId": 1,
    "title": "Spring Boot 入门教程",
    "content": "这是一篇入门教程..."
}
```

**成功响应 (200)：**
```json
{
    "code": 200,
    "message": "success",
    "data": 1
}
```
> `data` 为新帖子的 ID。

**错误响应：**

| code | message            |
|------|--------------------|
| 400  | `板块不能为空`           |
| 400  | `板块不存在`            |
| 400  | `帖子标题不能为空`         |
| 400  | `帖子标题必须是：1-100位字符` |
| 400  | `帖子正文不能为空`         |
| 401  | `未登录或验证失败`         |
| 401  | `登录已过期，请重新登录`      |

---

## 4. 编辑帖子

```
POST /{postId}
```

**认证：** 需要（作者本人或 ADMIN）

**请求头：**
```
Authorization: Bearer <accessToken>
```

**路径参数：**

| 参数       | 类型   | 说明     |
|----------|------|--------|
| `postId` | Long | 帖子 ID  |

**请求体：** 同 [3. 发帖](#3-发帖)

**请求示例：**
```json
{
    "categoryId": 1,
    "title": "Spring Boot 进阶教程（修订版）",
    "content": "这是修订后的内容..."
}
```

**成功响应 (200)：**
```json
{
    "code": 200,
    "message": "success",
    "data": null
}
```

**错误响应：**

| code | message            |
|------|--------------------|
| 400  | `帖子不存在`            |
| 400  | `板块不存在`            |
| 400  | `帖子标题不能为空`         |
| 400  | `帖子标题必须是：1-100位字符` |
| 400  | `帖子正文不能为空`         |
| 401  | `未登录或验证失败`         |
| 401  | `登录已过期，请重新登录`      |
| 403  | `无权操作`             |

---

## 5. 删除帖子

```
DELETE /{postId}
```

**认证：** 需要（作者本人或 ADMIN）

**请求头：**
```
Authorization: Bearer <accessToken>
```

**路径参数：**

| 参数       | 类型   | 说明     |
|----------|------|--------|
| `postId` | Long | 帖子 ID  |

**请求体：** 无

**成功响应 (200)：**
```json
{
    "code": 200,
    "message": "success",
    "data": null
}
```

**错误响应：**

| code | message       |
|------|---------------|
| 400  | `帖子不存在`       |
| 401  | `未登录或验证失败`    |
| 401  | `登录已过期，请重新登录` |
| 403  | `无权操作`        |

---

## 6. 点赞 / 取消赞

```
POST /{postId}/like
```

**认证：** 需要

**请求头：**
```
Authorization: Bearer <accessToken>
```

**路径参数：**

| 参数       | 类型   | 说明     |
|----------|------|--------|
| `postId` | Long | 帖子 ID  |

**请求体：** 无

**成功响应 (200)：**
```json
// 点赞成功
{ "code": 200, "message": "success", "data": true }

// 已点赞 → 取消赞
{ "code": 200, "message": "success", "data": false }
```
> 同一接口自动切换赞/取消赞。`data` 为 `true` 表示当前已点赞，`false` 表示已取消。

**错误响应：**

| code | message        |
|------|----------------|
| 400  | `帖子不存在`       |
| 401  | `未登录或验证失败`    |
| 401  | `登录已过期，请重新登录` |

---

# RidgePeak Comment API 文档

**Base URL:** `http://localhost:8080/api/comment`

---

## 1. 评论列表

```
GET /?postId={postId}
```

**认证：** 无需

**查询参数：**

| 参数       | 类型   | 必填 | 说明     |
|----------|------|:--:|--------|
| `postId` | long | 是  | 帖子 ID  |

**请求体：** 无

**成功响应 (200)：**
```json
{
    "code": 200,
    "message": "success",
    "data": [
        {
            "commentId": 1,
            "content": "好帖",
            "author": {
                "userId": 1,
                "username": "zhangsan",
                "nickname": "张三",
                "avatarUrl": "/uploads/avatars/xxx.png",
                "role": "USER"
            },
            "createdAt": "2026-06-08T12:30:00",
            "children": [
                {
                    "commentId": 2,
                    "content": "赞同",
                    "author": {
                        "userId": 2,
                        "username": "lisi",
                        "nickname": "李四",
                        "avatarUrl": null,
                        "role": "USER"
                    },
                    "createdAt": "2026-06-08T13:00:00",
                    "children": []
                }
            ]
        }
    ]
}
```
> 返回树形结构，`children` 为子评论列表，嵌套递归。无评论时返回空数组 `[]`。

**错误响应：**

| code | message  |
|------|----------|
| 400  | `帖子不存在`  |

---

## 2. 发表评论

```
POST /create
```

**认证：** 需要

**请求头：**
```
Authorization: Bearer <accessToken>
```

**请求体：**

| 字段         | 类型     | 必填 | 校验规则                     |
|------------|--------|:--:|--------------------------|
| `postId`   | long   | 是  | 不能为空                     |
| `content`  | string | 是  | 1-300 位字符                |
| `parentId` | long   | 否  | 父评论 ID，回复子评论时传入，不传则为一级评论 |

**请求示例：**
```json
// 一级评论
{
    "postId": 1,
    "content": "好帖"
}

// 回复别人的评论
{
    "postId": 1,
    "content": "赞同",
    "parentId": 1
}
```

**成功响应 (200)：**
```json
{
    "code": 200,
    "message": "success",
    "data": 2
}
```
> `data` 为新评论的 ID。

**错误响应：**

| code | message              |
|------|----------------------|
| 400  | `帖子不能为空`            |
| 400  | `帖子不存在`              |
| 400  | `评论内容不能为空`          |
| 400  | `评论内容必须是：1-300位字符`  |
| 400  | `评论不存在`              |
| 401  | `未登录或验证失败`          |
| 401  | `登录已过期，请重新登录`       |

---

## 3. 删除评论

```
DELETE /{commentId}
```

**认证：** 需要（作者本人或 ADMIN）

**请求头：**
```
Authorization: Bearer <accessToken>
```

**路径参数：**

| 参数          | 类型   | 说明     |
|-------------|------|--------|
| `commentId` | Long | 评论 ID  |

**请求体：** 无

**成功响应 (200)：**
```json
{
    "code": 200,
    "message": "success",
    "data": null
}
```
> 删除评论时，其下所有子评论会被级联删除。

**错误响应：**

| code | message       |
|------|---------------|
| 400  | `评论不存在`       |
| 401  | `未登录或验证失败`    |
| 401  | `登录已过期，请重新登录` |
| 403  | `无权操作`        |

---

## 全局说明（更新）

- 所有接口的 `code` 含义：200 成功，400 业务错误，401 认证错误，403 权限不足
- 需要认证的接口不带 Token 或 Token 过期时返回 401
- ADMIN 接口被普通用户调用时返回 403 `无权操作`