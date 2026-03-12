<template>
  <div class="forgot-password-page">
    <div class="forgot-container">
      <div class="forgot-header">
        <img src="@/assets/logo.png" alt="Logo" class="logo" />
        <h1>忘记密码</h1>
        <p>通过手机验证码重置密码</p>
      </div>

      <el-card class="forgot-card">
        <el-steps :active="currentStep" finish-status="success" align-center>
          <el-step title="验证身份" />
          <el-step title="重置密码" />
          <el-step title="完成" />
        </el-steps>

        <!-- 步骤1：验证身份 -->
        <el-form
          v-if="currentStep === 0"
          ref="verifyFormRef"
          :model="verifyForm"
          :rules="verifyRules"
          label-width="0"
          style="margin-top: 30px"
        >
          <el-form-item prop="phone">
            <el-input
              v-model="verifyForm.phone"
              placeholder="请输入手机号"
              size="large"
            >
              <template #prefix>
                <el-icon><Iphone /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="code">
            <el-input
              v-model="verifyForm.code"
              placeholder="请输入验证码"
              size="large"
            >
              <template #prefix>
                <el-icon><ChatLineSquare /></el-icon>
              </template>
              <template #append>
                <el-button
                  :disabled="countdown > 0"
                  @click="sendCode"
                >
                  {{ countdown > 0 ? `${countdown}秒` : '获取验证码' }}
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
              @click="verifyPhone"
            >
              下一步
            </el-button>
          </el-form-item>
        </el-form>

        <!-- 步骤2：重置密码 -->
        <el-form
          v-if="currentStep === 1"
          ref="resetFormRef"
          :model="resetForm"
          :rules="resetRules"
          label-width="0"
          style="margin-top: 30px"
        >
          <el-form-item prop="password">
            <el-input
              v-model="resetForm.password"
              type="password"
              placeholder="请输入新密码"
              size="large"
              show-password
            >
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="confirmPassword">
            <el-input
              v-model="resetForm.confirmPassword"
              type="password"
              placeholder="请再次输入新密码"
              size="large"
              show-password
            >
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              style="width: 100%"
              :loading="loading"
              @click="resetPassword"
            >
              重置密码
            </el-button>
          </el-form-item>
        </el-form>

        <!-- 步骤3：完成 -->
        <div v-if="currentStep === 2" class="success-content">
          <el-result
            icon="success"
            title="密码重置成功"
            sub-title="您可以使用新密码登录了"
          >
            <template #extra>
              <el-button type="primary" @click="goLogin">
                去登录
              </el-button>
            </template>
          </el-result>
        </div>

        <div class="back-link" v-if="currentStep < 2">
          <router-link to="/login">返回登录</router-link>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Iphone, ChatLineSquare, Lock } from '@element-plus/icons-vue'
import { sendVerificationCode, resetPassword as resetPasswordApi } from '@/api/auth'

const router = useRouter()

const currentStep = ref(0)
const loading = ref(false)
const countdown = ref(0)

// 验证表单
const verifyFormRef = ref()
const verifyForm = reactive({
  phone: '',
  code: ''
})

const verifyRules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' }
  ]
}

// 重置密码表单
const resetFormRef = ref()
const resetForm = reactive({
  password: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== resetForm.password) {
    callback(new Error('两次密码不一致'))
  } else {
    callback()
  }
}

const resetRules = {
  password: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

// 发送验证码
const sendCode = async () => {
  if (!verifyForm.phone) {
    ElMessage.warning('请输入手机号')
    return
  }

  try {
    const res = await sendVerificationCode({
      phone: verifyForm.phone,
      type: 'reset'
    })

    if (res.success) {
      ElMessage.success('验证码已发送')
      
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

// 验证手机号
const verifyPhone = async () => {
  const valid = await verifyFormRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    // TODO: 调用验证接口
    await new Promise(resolve => setTimeout(resolve, 1000))
    currentStep.value = 1
  } catch (error) {
    ElMessage.error(error.message || '验证失败')
  } finally {
    loading.value = false
  }
}

// 重置密码
const resetPassword = async () => {
  const valid = await resetFormRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await resetPasswordApi({
      phone: verifyForm.phone,
      code: verifyForm.code,
      newPassword: resetForm.password
    })

    if (res.success) {
      currentStep.value = 2
    }
  } catch (error) {
    ElMessage.error(error.message || '重置失败')
  } finally {
    loading.value = false
  }
}

// 去登录
const goLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.forgot-password-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.forgot-container {
  width: 100%;
  max-width: 500px;
}

.forgot-header {
  text-align: center;
  margin-bottom: 30px;
  color: white;
}

.logo {
  width: 80px;
  height: 80px;
  margin-bottom: 20px;
}

.forgot-header h1 {
  font-size: 32px;
  margin: 0 0 10px;
  font-weight: bold;
}

.forgot-header p {
  font-size: 16px;
  opacity: 0.9;
}

.forgot-card {
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border-radius: 16px;
  padding: 30px;
}

.success-content {
  margin-top: 30px;
}

.back-link {
  text-align: center;
  margin-top: 20px;
}

.back-link a {
  color: #409eff;
  text-decoration: none;
}

.back-link a:hover {
  text-decoration: underline;
}
</style>