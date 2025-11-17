# Thymeleaf模板引擎基础

## 📋 学习目标

- 理解Thymeleaf模板引擎的概念和优势
- 掌握Thymeleaf基础语法和表达式
- 学会使用Thymeleaf常用标签和属性
- 了解Thymeleaf与Spring Boot的集成

## 🏗️ Thymeleaf基础概念

### 什么是Thymeleaf？
Thymeleaf是一个现代的服务器端Java模板引擎，能够处理HTML、XML、JavaScript、CSS等模板文件。

### Thymeleaf的优势
- **自然模板**: 可以直接在浏览器中打开查看
- **Spring集成**: 与Spring Boot完美集成
- **标准方言**: 基于HTML5标准
- **国际化支持**: 内置国际化功能
- **无依赖**: 可以独立于Web容器运行

## 📝 Thymeleaf基础语法

### 1. 命名空间声明

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Thymeleaf示例</title>
</head>
<body>
    <!-- 页面内容 -->
</body>
</html>
```

### 2. 标准表达式语法

#### 变量表达式 ${...}
```html
<!-- 显示变量值 -->
<p th:text="${user.username}">默认用户名</p>

<!-- 访问对象属性 -->
<p th:text="${user.email}">默认邮箱</p>

<!-- 访问Map值 -->
<p th:text="${map['key']}">默认值</p>

<!-- 方法调用 -->
<p th:text="${user.getFullName()}">默认全名</p>
```

#### 选择变量表达式 *{...}
```html
<div th:object="${user}">
    <p th:text="*{username}">用户名</p>
    <p th:text="*{email}">邮箱</p>
    <p th:text="*{getFullName()}">全名</p>
</div>

<!-- 等价于 -->
<div>
    <p th:text="${user.username}">用户名</p>
    <p th:text="${user.email}">邮箱</p>
    <p th:text="${user.getFullName()}">全名</p>
</div>
```

#### 消息表达式 #{...}
```html
<!-- 国际化消息 -->
<p th:text="#{welcome.message}">欢迎</p>

<!-- 带参数的消息 -->
<p th:text="#{welcome.user(${user.username})}">欢迎用户</p>
```

#### 链接表达式 @{...}
```html
<!-- 基础链接 -->
<a th:href="@{/users}">用户列表</a>

<!-- 带参数链接 -->
<a th:href="@{/users/{id}(id=${user.id})}">用户详情</a>

<!-- 多参数链接 -->
<a th:href="@{/users(id=${user.id}, action='edit')}">编辑用户</a>

<!-- 查询参数 -->
<a th:href="@{/users(page=2, size=10)}">第二页</a>

<!-- 绝对URL -->
<a th:href="@{https://www.example.com}">外部链接</a>
```

#### 片段表达式 ~{...}
```html
<!-- 引用模板片段 -->
<div th:replace="~{fragments/header :: header}"></div>

<!-- 带参数的片段 -->
<div th:replace="~{fragments/user :: userCard(${user})}"></div>
```

#### 字面量
```html
<!-- 字符串 -->
<p th:text="'Hello, ' + ${user.username}">问候</p>

<!-- 数字 -->
<p th:text="${user.age} + 1">年龄+1</p>

<!-- 布尔值 -->
<div th:if="${user.enabled}" th:text="true">启用</div>

<!-- null值 -->
<p th:text="${user.phone ?: '未设置'}">电话</p>
```

### 3. 运算符

#### 算术运算符
```html
<p th:text="${user.age + 1}">年龄+1</p>
<p th:text="${user.age * 2}">年龄*2</p>
<p th:text="${user.age / 2}">年龄/2</p>
<p th:text="${user.age % 2}">年龄%2</p>
```

#### 比较运算符
```html
<p th:if="${user.age > 18}">成年用户</p>
<p th:if="${user.age >= 18}">成年或以上</p>
<p th:if="${user.age < 65}">未退休用户</p>
<p th:if="${user.age <= 65}">退休或以下</p>
```

#### 逻辑运算符
```html
<div th:if="${user.enabled and user.role == 'ADMIN'}">
    管理员用户
</div>

<div th:if="${user.enabled or user.role == 'VIP'}">
    活跃用户
</div>

<div th:if="${not user.enabled}">
    未启用用户
</div>
```

#### 三元运算符
```html
<p th:text="${user.enabled ? '启用' : '禁用'}">状态</p>

<!-- Elvis运算符 -->
<p th:text="${user.phone ?: '未设置'}">电话</p>
```

## 🎨 Thymeleaf常用属性

### 1. th:text 和 th:utext

```html
<!-- th:text: 转义HTML -->
<p th:text="${user.description}">描述（HTML会被转义）</p>

<!-- th:utext: 不转义HTML -->
<p th:utext="${user.description}">描述（HTML不会被转义）</p>
```

### 2. 条件属性

```html
<!-- th:if: 条件为真时显示 -->
<div th:if="${user.enabled}">
    用户已启用
</div>

<!-- th:unless: 条件为假时显示 -->
<div th:unless="${user.enabled}">
    用户未启用
</div>

<!-- th:switch / th:case: 多条件判断 -->
<div th:switch="${user.role}">
    <p th:case="'ADMIN'">管理员</p>
    <p th:case="'USER'">普通用户</p>
    <p th:case="*">其他角色</p>
</div>
```

### 3. 循环属性

```html
<!-- th:each: 循环遍历 -->
<table>
    <tr th:each="user : ${users}">
        <td th:text="${user.id}">ID</td>
        <td th:text="${user.username}">用户名</td>
        <td th:text="${user.email}">邮箱</td>
    </tr>
</table>

<!-- 带状态的循环 -->
<table>
    <tr th:each="user, iterStat : ${users}">
        <td th:text="${iterStat.count}">序号</td>
        <td th:text="${user.username}">用户名</td>
        <td th:text="${iterStat.first ? '第一个' : ''}">标记</td>
        <td th:text="${iterStat.last ? '最后一个' : ''}">标记</td>
    </tr>
</table>
```

### 4. 表单属性

```html
<form th:action="@{/users}" th:object="${user}" method="post">
    <!-- th:field: 自动绑定表单字段 -->
    <input type="text" th:field="*{username}" />
    <input type="email" th:field="*{email}" />

    <!-- th:errors: 显示验证错误 -->
    <span th:if="${#fields.hasErrors('username')}" th:errors="*{username}">错误信息</span>

    <!-- th:action: 动态表单动作 -->
    <button type="submit">提交</button>
</form>
```

## 📋 实际应用示例

### 1. 用户列表页面

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>用户列表</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <div class="container mt-4">
        <h1>用户列表</h1>

        <!-- 搜索表单 -->
        <form th:action="@{/users}" method="get" class="mb-3">
            <div class="row">
                <div class="col-md-8">
                    <input type="text" name="keyword" th:value="${keyword}"
                           class="form-control" placeholder="搜索用户">
                </div>
                <div class="col-md-4">
                    <button type="submit" class="btn btn-primary">搜索</button>
                    <a th:href="@{/users/new}" class="btn btn-success">添加用户</a>
                </div>
            </div>
        </form>

        <!-- 用户表格 -->
        <table class="table table-striped">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>用户名</th>
                    <th>邮箱</th>
                    <th>角色</th>
                    <th>状态</th>
                    <th>操作</th>
                </tr>
            </thead>
            <tbody>
                <tr th:each="user : ${users.content}">
                    <td th:text="${user.id}">1</td>
                    <td th:text="${user.username}">admin</td>
                    <td th:text="${user.email}">admin@example.com</td>
                    <td>
                        <span class="badge"
                              th:classappend="${user.role.name() == 'ADMIN' ? 'bg-danger' : 'bg-primary'}"
                              th:text="${user.role.displayName}">管理员</span>
                    </td>
                    <td>
                        <span class="badge"
                              th:classappend="${user.enabled} ? 'bg-success' : 'bg-secondary'"
                              th:text="${user.enabled} ? '启用' : '禁用'">启用</span>
                    </td>
                    <td>
                        <a th:href="@{/users/{id}(id=${user.id})}" class="btn btn-sm btn-info">查看</a>
                        <a th:href="@{/users/{id}/edit(id=${user.id})}" class="btn btn-sm btn-warning">编辑</a>
                        <form th:action="@{/users/{id}(id=${user.id})}" method="post"
                              style="display: inline;">
                            <input type="hidden" name="_method" value="delete">
                            <button type="submit" class="btn btn-sm btn-danger"
                                    onclick="return confirm('确定要删除吗？')">删除</button>
                        </form>
                    </td>
                </tr>
            </tbody>
        </table>

        <!-- 分页导航 -->
        <nav th:if="${users.totalPages > 1}">
            <ul class="pagination">
                <!-- 首页 -->
                <li class="page-item" th:class="${users.first} ? 'disabled'">
                    <a class="page-link" th:href="@{/users(page=0)}">首页</a>
                </li>

                <!-- 上一页 -->
                <li class="page-item" th:class="${users.first} ? 'disabled'">
                    <a class="page-link" th:href="@{/users(page=${users.number - 1})}">上一页</a>
                </li>

                <!-- 页码 -->
                <li class="page-item" th:each="i : ${#numbers.sequence(0, users.totalPages - 1)}"
                    th:class="${i == users.number} ? 'active'">
                    <a class="page-link" th:href="@{/users(page=${i})}" th:text="${i + 1}">1</a>
                </li>

                <!-- 下一页 -->
                <li class="page-item" th:class="${users.last} ? 'disabled'">
                    <a class="page-link" th:href="@{/users(page=${users.number + 1})}">下一页</a>
                </li>

                <!-- 末页 -->
                <li class="page-item" th:class="${users.last} ? 'disabled'">
                    <a class="page-link" th:href="@{/users(page=${users.totalPages - 1})}">末页</a>
                </li>
            </ul>
        </nav>
    </div>
</body>
</html>
```

### 2. 用户表单页面

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title th:text="${title}">用户表单</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <div class="container mt-4">
        <div class="row justify-content-center">
            <div class="col-md-8">
                <div class="card">
                    <div class="card-header">
                        <h3 th:text="${title}">用户表单</h3>
                    </div>
                    <div class="card-body">
                        <!-- 错误消息 -->
                        <div th:if="${errorMessage}" class="alert alert-danger">
                            <span th:text="${errorMessage}"></span>
                        </div>

                        <form th:action="${action}" method="post" th:object="${user}">
                            <!-- 用户名 -->
                            <div class="mb-3">
                                <label for="username" class="form-label">用户名</label>
                                <input type="text" class="form-control" th:field="*{username}" id="username"
                                       th:classappend="${#fields.hasErrors('username')} ? 'is-invalid' : ''">
                                <div th:if="${#fields.hasErrors('username')}" class="invalid-feedback">
                                    <span th:each="error : ${#fields.errors('username')}" th:text="${error}">错误</span>
                                </div>
                            </div>

                            <!-- 邮箱 -->
                            <div class="mb-3">
                                <label for="email" class="form-label">邮箱</label>
                                <input type="email" class="form-control" th:field="*{email}" id="email"
                                       th:classappend="${#fields.hasErrors('email')} ? 'is-invalid' : ''">
                                <div th:if="${#fields.hasErrors('email')}" class="invalid-feedback">
                                    <span th:each="error : ${#fields.errors('email')}" th:text="${error}">错误</span>
                                </div>
                            </div>

                            <!-- 密码（仅创建时） -->
                            <div th:if="${user.id == null}" class="mb-3">
                                <label for="password" class="form-label">密码</label>
                                <input type="password" class="form-control" th:field="*{password}" id="password"
                                       th:classappend="${#fields.hasErrors('password')} ? 'is-invalid' : ''">
                                <div th:if="${#fields.hasErrors('password')}" class="invalid-feedback">
                                    <span th:each="error : ${#fields.errors('password')}" th:text="${error}">错误</span>
                                </div>
                            </div>

                            <!-- 性别单选 -->
                            <div class="mb-3">
                                <label class="form-label">性别</label>
                                <div th:each="gender : ${genders}" class="form-check">
                                    <input class="form-check-input" type="radio" th:field="*{gender}"
                                           th:value="${gender}" th:id="'gender-' + ${gender.name()}">
                                    <label class="form-check-label" th:for="'gender-' + ${gender.name()}"
                                           th:text="${gender.displayName}">性别</label>
                                </div>
                            </div>

                            <!-- 角色下拉 -->
                            <div class="mb-3">
                                <label for="role" class="form-label">角色</label>
                                <select class="form-select" th:field="*{role}" id="role"
                                        th:classappend="${#fields.hasErrors('role')} ? 'is-invalid' : ''">
                                    <option th:each="role : ${roles}" th:value="${role}"
                                            th:text="${role.displayName}">角色</option>
                                </select>
                                <div th:if="${#fields.hasErrors('role')}" class="invalid-feedback">
                                    <span th:each="error : ${#fields.errors('role')}" th:text="${error}">错误</span>
                                </div>
                            </div>

                            <!-- 启用状态复选框 -->
                            <div class="mb-3 form-check">
                                <input class="form-check-input" type="checkbox" th:field="*{enabled}" id="enabled">
                                <label class="form-check-label" for="enabled">启用用户</label>
                            </div>

                            <!-- 提交按钮 -->
                            <div class="d-flex justify-content-end">
                                <a th:href="@{/users}" class="btn btn-secondary me-2">取消</a>
                                <button type="submit" class="btn btn-primary">保存</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
```

## 🎨 模板片段和布局

### 1. 创建模板片段

```html
<!-- fragments/header.html -->
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head th:fragment="header">
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title th:text="${title}">默认标题</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
</html>

<!-- fragments/navbar.html -->
<nav th:fragment="navbar" class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container">
        <a class="navbar-brand" th:href="@{/}">用户管理系统</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav me-auto">
                <li class="nav-item">
                    <a class="nav-link" th:href="@{/users}">用户管理</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" th:href="@{/roles}">角色管理</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" th:href="@{/statistics}">统计报表</a>
                </li>
            </ul>
            <ul class="navbar-nav">
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-bs-toggle="dropdown">
                        <span th:text="${#authentication.name}">用户</span>
                    </a>
                    <ul class="dropdown-menu">
                        <li><a class="dropdown-item" th:href="@{/profile}">个人资料</a></li>
                        <li><a class="dropdown-item" th:href="@{/logout}">退出登录</a></li>
                    </ul>
                </li>
            </ul>
        </div>
    </div>
</nav>

<!-- fragments/footer.html -->
<footer th:fragment="footer" class="bg-light text-center py-3 mt-5">
    <p>&copy; <span th:text="${#temporals.format(#temporals.createNow(), 'yyyy')}">2024</span>
       用户管理系统. All rights reserved.</p>
</footer>

<!-- fragments/alerts.html -->
<div th:fragment="alerts" class="container mt-3">
    <!-- 成功消息 -->
    <div th:if="${successMessage}" class="alert alert-success alert-dismissible fade show" role="alert">
        <span th:text="${successMessage}"></span>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>

    <!-- 错误消息 -->
    <div th:if="${errorMessage}" class="alert alert-danger alert-dismissible fade show" role="alert">
        <span th:text="${errorMessage}"></span>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>

    <!-- 警告消息 -->
    <div th:if="${warningMessage}" class="alert alert-warning alert-dismissible fade show" role="alert">
        <span th:text="${warningMessage}"></span>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>

    <!-- 信息消息 -->
    <div th:if="${infoMessage}" class="alert alert-info alert-dismissible fade show" role="alert">
        <span th:text="${infoMessage}"></span>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</div>
```

### 2. 布局模板

```html
<!-- layout/main.html -->
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout">

<head th:replace="~{fragments/header :: header}">
    <!-- 默认头部内容 -->
</head>

<body>
    <!-- 导航栏 -->
    <nav th:replace="~{fragments/navbar :: navbar}"></nav>

    <!-- 消息提示 -->
    <div th:replace="~{fragments/alerts :: alerts}"></div>

    <!-- 主要内容 -->
    <main layout:fragment="content">
        <!-- 默认内容 -->
    </main>

    <!-- 页脚 -->
    <footer th:replace="~{fragments/footer :: footer}"></footer>

    <!-- JavaScript -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <th:block layout:fragment="scripts">
        <!-- 默认脚本 -->
    </th:block>
</body>
</html>
```

### 3. 使用布局模板

```html
<!-- users/list.html -->
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="~{layout/main}">

<head>
    <title th:text="'用户列表 - 第' + ${users.number + 1} + '页'">用户列表</title>
</head>

<body>
<div layout:fragment="content">
    <div class="container mt-4">
        <h1>用户列表</h1>

        <!-- 页面内容 -->
        <table class="table">
            <!-- 表格内容 -->
        </table>
    </div>
</div>

<th:block layout:fragment="scripts">
    <script>
        // 页面特定的JavaScript
        $(document).ready(function() {
            console.log('用户列表页面加载完成');
        });
    </script>
</th:block>
</body>
</html>
```

## 🔧 实用工具对象

### 1. #dates - 日期处理

```html
<!-- 格式化日期 -->
<span th:text="${#dates.format(user.createdAt, 'yyyy-MM-dd HH:mm:ss')}">2024-01-01 12:00:00</span>

<!-- 创建日期 -->
<span th:text="${#dates.createNow()}">当前日期</span>

<!-- 日期计算 -->
<span th:text="${#dates.month(user.birthDate)}">月份</span>
<span th:text="${#dates.year(user.birthDate)}">年份</span>
```

### 2. #numbers - 数字处理

```html
<!-- 格式化数字 -->
<span th:text="${#numbers.formatDecimal(user.salary, 2, 2)}">1,234.56</span>

<!-- 整数序列 -->
<div th:each="i : ${#numbers.sequence(1, 5)}">
    <span th:text="${i}">数字</span>
</div>

<!-- 百分比 -->
<span th:text="${#numbers.formatPercent(completionRate, 1, 1)}">85.5%</span>
```

### 3. #strings - 字符串处理

```html
<!-- 字符串长度 -->
<span th:text="${#strings.length(user.username)}">长度</span>

<!-- 字符串截取 -->
<span th:text="${#strings.abbreviate(user.description, 50)}">截取的文本</span>

<!-- 大小写转换 -->
<span th:text="${#strings.toUpperCase(user.username)}">大写</span>

<!-- 判断是否为空 -->
<div th:if="${#strings.isEmpty(user.phone)}">未设置电话</div>
```

### 4. #arrays 和 #lists - 集合处理

```html
<!-- 数组大小 -->
<span th:text="${#arrays.length(user.roles)}">角色数量</span>

<!-- 列表包含 -->
<div th:if="${#lists.contains(user.roles, 'ADMIN')}">是管理员</div>

<!-- 连接字符串 -->
<span th:text="${#strings.listJoin(user.permissions, ', ')}">权限列表</span>
```

## ✅ 检查点

完成本节学习后，您应该能够：

- [ ] 理解Thymeleaf模板引擎的概念
- [ ] 掌握Thymeleaf基础语法和表达式
- [ ] 使用Thymeleaf常用属性和标签
- [ ] 创建模板片段和布局
- [ ] 使用Thymeleaf实用工具对象

## 🚀 下一步

Thymeleaf基础学习完成后，接下来我们将学习：
[页面布局设计](02-页面布局.md)

---

**提示**: Thymeleaf的强大之处在于其自然模板特性，可以在没有后端环境的情况下直接在浏览器中预览页面效果。