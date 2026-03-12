<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-header">
        <img src="@/assets/logo.png" alt="Logo" class="logo" />
        <h1>健康管理平台</h1>
        <p>您的专业健康管理助手</p>
      </div>

      <el-card class="login-card">
        <el-tabs v-model="activeTab" stretch>
          <el-tab-pane label="账号登录" name="account">
            <el-form
              ref="loginFormRef"
              :model="loginForm"
              :rules="loginRules"
              label-width="0"
              @submit.prevent="handleLogin"
            >
              <el-form-item prop="username">
                <el-input
                  v-model="loginForm.username"
                  placeholder="用户名/邮箱/手机号"
                  size="large"
                  clearable
                >
                  <template #prefix>
                    <el-icon><User /></el-icon>
                  </template>
                </el-input>
              </el-form-item>

              <el-form-item prop="password">
                <el-input
                  v-model="loginForm.password"
                  type="password"
                  placeholder="密码"
                  size="large"
                  show-password
                  @keyup.enter="handleLogin"
                >
                  <template #prefix>
                    <el-icon><Lock /></el-icon>
                  </template>
                </el-input>
              </el-form-item>

              <el-form-item>
                <div class="login-options">
                  <el-checkbox v-model="loginForm.remember">
                    记住密码
                  </el-checkbox>
                  <router-link to="/forgot-password" class="forgot-link">
                    忘记密码？
                  </router-link>
                </div>
              </el-form-item>

              <el-form-item>
                <el-button
                  type="primary"
                  size="large"
                  style="width: 100%"
                  :loading="loading"
                  @click="handleLogin"
                >
                  登录
                </el-button>
              </el-form-item>

              <el-form-item>
                <div class="register-link">
                  还没有账号？
                  <router-link to="/register">立即注册</router-link>
                </div>
              </el-form-item>
            </el-form>
          </el-tab-pane>

          <el-tab-pane label="手机登录" name="phone">
            <el-form
              ref="phoneFormRef"
              :model="phoneForm"
              :rules="phoneRules"
              label-width="0"
            >
              <el-form-item prop="phone">
                <el-input
                  v-model="phoneForm.phone"
                  placeholder="手机号"
                  size="large"
                  clearable
                >
                  <template #prefix>
                    <el-icon><Iphone /></el-icon>
                  </template>
                </el-input>
              </el-form-item>

              <el-form-item prop="code">
                <el-input
                  v-model="phoneForm.code"
                  placeholder="验证码"
                  size="large"
                  clearable
                >
                  <template #prefix>
                    <el-icon><ChatLineSquare /></el-icon>
                  </template>
                  <template #append>
                    <el-button
                      :disabled="countdown > 0"
                      @click="sendCode"
                    >
                      {{ countdown > 0 ? `${countdown}秒后重试` : '获取验证码' }}
                    </el-button>
                  </template>
                </el-input>
              </el-form-item>

              <el-form-item>
                <el-button
                  type="primary"
                  size="large"
                  style="width: 100%"
                  :loading="loading"
                  @click="handlePhoneLogin"
                >
                  登录
                </el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>
        </el-tabs>
      </el-card>

      <div class="login-footer">
        <p>© 2024 健康管理平台. All rights reserved.</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Iphone, ChatLineSquare } from '@element-plus/icons-vue'
import { login, sendVerificationCode } from '@/api/auth'
import { useStore } from 'vuex'

const router = useRouter()
const store = useStore()

const activeTab = ref('account')
const loading = ref(false)
const countdown = ref(0)

// 账号登录
const loginFormRef = ref()
const loginForm = reactive({
  username: '',
  password: '',
  remember: false
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ]
}

// 手机登录
const phoneFormRef = ref()
const phoneForm = reactive({
  phone: '',
  code: ''
})

const phoneRules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 6, message: '验证码为6位', trigger: 'blur' }
  ]
}

// 账号登录
const handleLogin = async () => {
  const valid = await loginFormRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await login({
      username: loginForm.username,
      password: loginForm.password
    })

    if (res.success) {
      // 保存 token 和用户信息
      await store.dispatch('user/login', {
        token: res.data.token,
        userInfo: res.data.userInfo
      })

      ElMessage.success('登录成功')
      
      // 跳转到首页或重定向页面
      const redirect = router.currentRoute.value.query.redirect || '/dashboard'
      router.push(redirect)
    }
  } catch (error) {
    ElMessage.error(error.message || '登录失败')
  } finally {
    loading.value = false
  }
}

// 手机登录
const handlePhoneLogin = async () => {
  const valid = await phoneFormRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await login({
      phone: phoneForm.phone,
      code: phoneForm.code,
      loginType: 'phone'
    })

    if (res.success) {
      await store.dispatch('user/login', {
        token: res.data.token,
        userInfo: res.data.userInfo
      })

      ElMessage.success('登录成功')
      router.push('/dashboard')
    }
  } catch (error) {
    ElMessage.error(error.message || '登录失败')
  } finally {
    loading.value = false
  }
}

// 发送验证码
const sendCode = async () => {
  if (!phoneForm.phone) {
    ElMessage.warning('请输入手机号')
    return
  }

  if (!/^1[3-9]\d{9}$/.test(phoneForm.phone)) {
    ElMessage.warning('手机号格式不正确')
    return
  }

  try {
    const res = await sendVerificationCode({
      phone: phoneForm.phone,
      type: 'login'
    })

    if (res.success) {
      ElMessage.success('验证码已发送')
      
      // 倒计时
      countdown.value = 60
      const timer = setInterval(() => {
        countdown.value--
        if (countdown.value <= 0) {
          clearInterval(timer)
        }
      }, 1000)
    }
  } catch (error) {
    ElMessage.error(error.message || '发送失败')
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-container {
  width: 100%;
  max-width: 450px;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
  color: white;
}

.logo {
  width: 80px;
  height: 80px;
  margin-bottom: 20px;
}

.login-header h1 {
  font-size: 32px;
  margin: 0 0 10px;
  font-weight: bold;
}

.login-header p {
  font-size: 16px;
  opacity: 0.9;
}

.login-card {
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border-radius: 16px;
}

.login-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.forgot-link {
  color: #409eff;
  text-decoration: none;
}

.forgot-link:hover {
  text-decoration: underline;
}

.register-link {
  text-align: center;
  width: 100%;
  color: #606266;
}

.register-link a {
  color: #409eff;
  text-decoration: none;
  margin-left: 5px;
}

.register-link a:hover {
  text-decoration: underline;
}

.login-footer {
  text-align: center;
  margin-top: 30px;
  color: white;
  opacity: 0.8;
}
</style>