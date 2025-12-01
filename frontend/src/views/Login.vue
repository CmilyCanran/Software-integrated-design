<template>
    <div class="login-container">
        <div class="login-form">
            <div class="login-header">
                <h2>服装销售系统</h2>
                <p>欢迎登录</p>
            </div>

            <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" size="large"
                @submit.prevent="handleLogin">
                <el-form-item prop="username">
                    <el-input v-model="loginForm.username" placeholder="请输入用户名" prefix-icon="User" clearable
                        @keyup.enter="handleLogin" />
                </el-form-item>

                <el-form-item prop="password">
                    <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" prefix-icon="Lock"
                        show-password clearable @keyup.enter="handleLogin" />
                </el-form-item>

                <el-form-item>
                    <el-button type="primary" size="large" style="width: 100%" :loading="authStore.loading"
                        @click="handleLogin">
                        登录
                    </el-button>
                </el-form-item>

                <el-form-item>
                    <div class="register-link">
                        还没有账号？
                        <el-link type="primary" @click="goToRegister">立即注册</el-link>
                    </div>
                </el-form-item>
            </el-form>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { authAPI } from '@/api/auth'
import { ElMessage } from 'element-plus'
import { handleError } from '@/utils/errorHandler'
import type { LoginRequest, LoginResponse } from '@/types'

const router = useRouter()
const authStore = useAuthStore()
const loginFormRef = ref()

const loginForm = reactive({
    username: '',
    password: ''
})

const loginRules = {
    username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
    ]
}

/**
 * 📝 表单验证函数
 *
 * 💡 学习目标：
 * - 理解表单验证的重要性
 * - 学习如何封装验证逻辑
 * - 掌握异步验证的处理方式
 *
 * @returns 验证是否通过
 */
const validateForm = async (): Promise<boolean> => {
    if (!loginFormRef.value) return false

    try {
        // 触发表单验证
        await loginFormRef.value.validate()
        return true
    } catch (error) {
        // 验证失败，返回false
        console.warn('表单验证失败:', error)
        return false
    }
}

/**
 * 🔐 执行登录API调用
 *
 * 💡 学习目标：
 * - 学习如何封装API调用逻辑
 * - 理解登录请求的数据结构
 * - 掌握异步API调用的错误处理
 *
 * @param credentials - 登录凭据
 * @returns 登录响应数据
 */
const performLogin = async (credentials: LoginRequest): Promise<LoginResponse> => {
    // 调用登录API
    const response = await authAPI.login(credentials)

    // 🔍 调试：打印API响应数据
    console.log('🔍 登录API响应:', response)

    return response
}

/**
 * ✅ 处理登录成功
 *
 * 💡 学习目标：
 * - 学习如何处理成功响应
 * - 理解状态管理的工作流程
 * - 掌握页面导航的方法
 *
 * @param response - 登录响应数据
 */
const handleLoginSuccess = (response: LoginResponse): void => {
    // 🔍 验证数据结构
    // 因为request.ts响应拦截器已经提取了data部分，
    // 所以response直接就是包含token和user的对象
    if (!response || !response.token || !response.user) {
        console.error('❌ 数据结构错误:', {
            hasToken: !!response?.token,
            hasUser: !!response?.user,
            fullData: response
        })
        throw new Error('登录数据格式错误')
    }

    // 更新认证状态
    authStore.login(response)

    // 🔍 验证store状态
    console.log('🔍 登录后store状态:', {
        token: authStore.token,
        userInfo: authStore.userInfo,
        isLoggedIn: authStore.isLoggedIn
    })

    // 显示成功消息并跳转
    ElMessage.success('登录成功')
    router.push('/dashboard')
}

/**
 * ❌ 处理登录错误
 *
 * 💡 学习目标：
 * - 学习如何统一处理错误
 * - 理解不同类型错误的处理方式
 * - 掌握用户友好的错误消息显示
 *
 * @param error - 错误对象
 */
const handleLoginError = (error: unknown): void => {
    // 使用统一的错误处理工具
    const appError = handleError(error, {
        showToast: false,  // 我们自己处理消息显示
        customMessage: '登录失败'
    })

    // 🔍 记录详细的错误信息（调试用）
    console.error('❌ 登录失败详细信息:', {
        code: appError.code,
        message: appError.message,
        details: appError.details
    })

    // 根据错误代码显示不同的用户友好消息
    switch (appError.code) {
        case 'UNAUTHORIZED':
            ElMessage.error('用户名或密码错误')
            break
        case 'SERVER_ERROR':
            ElMessage.error('服务器内部错误，请稍后重试')
            break
        case 'NETWORK_ERROR':
        case 'TIMEOUT_ERROR':
            ElMessage.error('网络连接失败，请检查网络')
            break
        default:
            // 显示统一的错误消息
            ElMessage.error(appError.message || '登录失败，请重试')
    }
}

/**
 * 🎯 主要的登录处理函数
 *
 * 💡 学习目标：
 * - 学习如何将复杂逻辑拆分为小函数
 * - 理解函数职责单一原则
 * - 掌握异步操作的流程控制
 */
const handleLogin = async (): Promise<void> => {
    // 验证表单
    const isValid = await validateForm()
    if (!isValid) return

    try {
        // 设置加载状态
        authStore.setLoading(true)

        // 执行登录
        const response = await performLogin({
            username: loginForm.username,
            password: loginForm.password
        })

        // 处理登录成功
        await handleLoginSuccess(response)

    } catch (error) {
        // 处理登录错误
        handleLoginError(error)
    } finally {
        // 重置加载状态
        authStore.setLoading(false)
    }
}

/**
 * 🔗 跳转到注册页面
 *
 * 💡 学习目标：
 * - 学习Vue Router的基本使用
 * - 理解页面导航的实现方式
 */
const goToRegister = (): void => {
    router.push('/register')
}
</script>

<style scoped>
.login-container {
    min-height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-form {
    width: 400px;
    padding: 40px;
    background: white;
    border-radius: 10px;
    box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
}

.login-header {
    text-align: center;
    margin-bottom: 30px;
}

.login-header h2 {
    color: #333;
    margin-bottom: 10px;
}

.login-header p {
    color: #666;
    font-size: 14px;
}

.register-link {
    text-align: center;
    color: #666;
}
</style>
