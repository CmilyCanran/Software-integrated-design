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

const router = useRouter()
const authStore = useAuthStore()
const loginFormRef = ref()

const loginForm = reactive({
    username: 'user',
    password: '123456'
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

const handleLogin = async () => {
    if (!loginFormRef.value) return

    try {
        await loginFormRef.value.validate()

        authStore.setLoading(true)

        const response = await authAPI.login({
            username: loginForm.username,
            password: loginForm.password
        })

        // 🔍 调试：打印API响应数据
        console.log('🔍 登录API响应:', response)
        console.log('🔍 response.data:', response.data)

        // 🔍 验证数据结构
        // 因为request.js响应拦截器已经提取了data部分，
        // 所以response直接就是包含token和user的对象
        if (!response || !response.token || !response.user) {
            console.error('❌ 数据结构错误:', {
                hasToken: !!response?.token,
                hasUser: !!response?.user,
                fullData: response
            })
            ElMessage.error('登录数据格式错误，请联系管理员')
            return
        }

        authStore.login(response)

        // 🔍 验证store状态
        console.log('🔍 登录后store状态:', {
            token: authStore.token,
            userInfo: authStore.userInfo,
            isLoggedIn: authStore.isLoggedIn
        })

        ElMessage.success('登录成功')
        router.push('/dashboard')

    } catch (error) {
        console.error('❌ 登录失败详细信息:', {
            message: error.message,
            response: error.response?.data,
            status: error.response?.status,
            config: error.config
        })

        // 🔍 根据不同错误类型显示不同信息
        if (error.response?.status === 401) {
            ElMessage.error('用户名或密码错误')
        } else if (error.response?.status === 500) {
            ElMessage.error('服务器内部错误，请稍后重试')
        } else if (error.message.includes('Network Error')) {
            ElMessage.error('网络连接失败，请检查网络')
        } else {
            ElMessage.error(error.response?.data?.message || '登录失败，请重试')
        }
    } finally {
        authStore.setLoading(false)
    }
}

const goToRegister = () => {
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
