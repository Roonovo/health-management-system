<template>
  <div class="reset-password-page">
    <div class="reset-password-container">
      <div class="reset-password-header">
        <div class="logo">
          <img src="@/assets/logo.png" alt="Logo">
        </div>
        <h2>重置密码</h2>
        <p class="subtitle">请设置您的新密码</p>
      </div>

      <el-steps :active="currentStep" finish-status="success" align-center>
        <el-step title="验证身份" />
        <el-step title="设置密码" />
        <el-step title="完成" />
      </el-steps>

      <!-- 步骤1: 验证身份 -->
      <div v-show="currentStep === 0" class="step-content">
        <el-form
          ref="verifyFormRef"
          :model="verifyForm"
          :rules="verifyRules"
          label-width="0"
        >
          <el-form-item prop="account">
            <el-input
              v-model="verifyForm.account"
              placeholder="请输入手机号或邮箱"
              size="large"
              clearable
            >
              <template #prefix>
                <el-icon><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="verifyCode">
            <el-input
              v-model="verifyForm.verifyCode"
              placeholder="请输入验证码"
              size="large"
              clearable
            >
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
              <template #append>
                <el-button
                  :disabled="countdown > 0"
                  @click="sendVerifyCode"
                >
                  {{ countdown > 0 ? `${countdown}秒后重试` : '获取验证码' }}
                </el-button>
              </template>
            </el-input>
          </el-form-item>

          <el-button
            type="primary"
            size="large"
            style="width: 100%"
            :loading="verifying"
            @click="verifyIdentity"
          >
            下一步
          </el-button>
        </el-form>
      </div>

      <!-- 步骤2: 设置密码 -->
      <div v-show="currentStep === 1" class="step-content">
        <el-form
          ref="passwordFormRef"
          :model="passwordForm"
          :rules="passwordRules"
          label-width="0"
        >
          <el-form-item prop="password">
            <el-input
              v-model="passwordForm.password"
              type="password"
              placeholder="请输入新密码"
              size="large"
              show-password
              clearable
            >
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
            <div class="password-strength">
              <div class="strength-bar">
                <div
                  class="strength-level"
                  :class="passwordStrength.class"
                  :style="{ width: passwordStrength.width }"
                ></div>
              </div>
              <span class="strength-text">{{ passwordStrength.text }}</span>
            </div>
          </el-form-item>

          <el-form-item prop="confirmPassword">
            <el-input
              v-model="passwordForm.confirmPassword"
              type="password"
              placeholder="请确认新密码"
              size="large"
              show-password
              clearable
            >
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <div class="password-tips">
            <p>密码要求：</p>
            <ul>
              <li :class="{ active: passwordValidation.length }">
                <el-icon>
                  <Check v-if="passwordValidation.length" />
                  <Close v-else />
                </el-icon>
                长度至少8个字符
              </li>
              <li :class="{ active: passwordValidation.lowercase }">
                <el-icon>
                  <Check v-if="passwordValidation.lowercase" />
                  <Close v-else />
                </el-icon>
                包含小写字母
              </li>
              <li :class="{ active: passwordValidation.uppercase }">
                <el-icon>
                  <Check v-if="passwordValidation.uppercase" />
                  <Close v-else />
                </el-icon>
                包含大写字母
              </li>
              <li :class="{ active: passwordValidation.number }">
                <el-icon>
                  <Check v-if="passwordValidation.number" />
                  <Close v-else />
                </el-icon>
                包含数字
              </li>
            </ul>
          </div>

          <el-button
            type="primary"
            size="large"
            style="width: 100%; margin-top: 20px"
            :loading="resetting"
            @click="resetPassword"
          >
            重置密码
          </el-button>

          <el-button
            size="large"
            style="width: 100%; margin-top: 10px"
            @click="currentStep = 0"
          >
            上一步
          </el-button>
        </el-form>
      </div>

      <!-- 步骤3: 完成 -->
      <div v-show="currentStep === 2" class="step-content complete-step">
        <el-result
          icon="success"
          title="密码重置成功"
          sub-title="您的密码已成功重置，请使用新密码登录"
        >
          <template #extra>
            <el-button type="primary" size="large" @click="goToLogin">
              返回登录
            </el-button>
          </template>
        </el-result>
      </div>

      <div class="reset-password-footer">
        <router-link to="/login">返回登录</router-link>
        <span class="divider">|</span>
        <router-link to="/register">注册账号</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Check, Close } from '@element-plus/icons-vue'
import {
  sendResetPasswordCode,
  verifyResetCode,
  resetPasswordByCode
} from '@/api/auth'

const router = useRouter()

const currentStep = ref(0)
const verifying = ref(false)
const resetting = ref(false)
const countdown = ref(0)
let countdownTimer = null

// 验证表单
const verifyFormRef = ref()
const verifyForm = reactive({
  account: '',
  verifyCode: ''
})

const verifyRules = {
  account: [
    { required: true, message: '请输入手机号或邮箱', trigger: 'blur' }
  ],
  verifyCode: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 6, message: '验证码长度为6位', trigger: 'blur' }
  ]
}

// 密码表单
const passwordFormRef = ref()
const passwordForm = reactive({
  password: '',
  confirmPassword: ''
})

const validatePassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入密码'))
  } else if (value.length < 8) {
    callback(new Error('密码长度至少8个字符'))
  } else if (!/[a-z]/.test(value)) {
    callback(new Error('密码必须包含小写字母'))
  } else if (!/[A-Z]/.test(value)) {
    callback(new Error('密码必须包含大写字母'))
  } else if (!/\d/.test(value)) {
    callback(new Error('密码必须包含数字'))
  } else {
    callback()
  }
}

const validateConfirmPassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请确认密码'))
  } else if (value !== passwordForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = {
  password: [
    { validator: validatePassword, trigger: 'blur' }
  ],
  confirmPassword: [
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

// 密码强度
const passwordStrength = computed(() => {
  const password = passwordForm.password
  if (!password) {
    return { width: '0%', class: '', text: '' }
  }

  let strength = 0
  if (password.length >= 8) strength++
  if (/[a-z]/.test(password)) strength++
  if (/[A-Z]/.test(password)) strength++
  if (/\d/.test(password)) strength++
  if (/[^a-zA-Z0-9]/.test(password)) strength++

  if (strength <= 2) {
    return { width: '33%', class: 'weak', text: '弱' }
  } else if (strength <= 3) {
    return { width: '66%', class: 'medium', text: '中' }
  } else {
    return { width: '100%', class: 'strong', text: '强' }
  }
})

// 密码验证状态
const passwordValidation = computed(() => {
  const password = passwordForm.password
  return {
    length: password.length >= 8,
    lowercase: /[a-z]/.test(password),
    uppercase: /[A-Z]/.test(password),
    number: /\d/.test(password)
  }
})

// 发送验证码
const sendVerifyCode = async () => {
  if (!verifyForm.account) {
    ElMessage.warning('请先输入手机号或邮箱')
    return
  }

  try {
    const res = await sendResetPasswordCode({
      account: verifyForm.account
    })

    if (res.success) {
      ElMessage.success('验证码已发送')
      
      // 开始倒计时
      countdown.value = 60
      countdownTimer = setInterval(() => {
        countdown.value--
        if (countdown.value <= 0) {
          clearInterval(countdownTimer)
        }
      }, 1000)
    }
  } catch (error) {
    ElMessage.error('发送失败：' + error.message)
  }
}

// 验证身份
const verifyIdentity = async () => {
  const valid = await verifyFormRef.value.validate().catch(() => false)
  if (!valid) return

  verifying.value = true

  try {
    const res = await verifyResetCode({
      account: verifyForm.account,
      code: verifyForm.verifyCode
    })

    if (res.success) {
      ElMessage.success('验证成功')
      currentStep.value = 1
    }
  } catch (error) {
    ElMessage.error('验证失败：' + error.message)
  } finally {
    verifying.value = false
  }
}

// 重置密码
const resetPassword = async () => {
  const valid = await passwordFormRef.value.validate().catch(() => false)
  if (!valid) return

  resetting.value = true

  try {
    const res = await resetPasswordByCode({
      account: verifyForm.account,
      code: verifyForm.verifyCode,
      password: passwordForm.password
    })

    if (res.success) {
      ElMessage.success('密码重置成功')
      currentStep.value = 2
    }
  } catch (error) {
    ElMessage.error('重置失败：' + error.message)
  } finally {
    resetting.value = false
  }
}

// 返回登录
const goToLogin = () => {
  router.push('/login')
}

// 清理定时器
watch(() => currentStep.value, () => {
  if (currentStep.value !== 0 && countdownTimer) {
    clearInterval(countdownTimer)
    countdown.value = 0
  }
})
</script>

<style scoped>
.reset-password-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.reset-password-container {
  width: 100%;
  max-width: 500px;
  background: white;
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.reset-password-header {
  text-align: center;
  margin-bottom: 30px;
}

.logo {
  margin-bottom: 20px;
}

.logo img {
  height: 50px;
}

.reset-password-header h2 {
  margin: 0 0 10px;
  font-size: 28px;
  color: #303133;
}

.subtitle {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.step-content {
  margin-top: 30px;
}

.password-strength {
  margin-top: 10px;
}

.strength-bar {
  height: 4px;
  background-color: #e4e7ed;
  border-radius: 2px;
  overflow: hidden;
  margin-bottom: 5px;
}

.strength-level {
  height: 100%;
  transition: all 0.3s;
}

.strength-level.weak {
  background-color: #F56C6C;
}

.strength-level.medium {
  background-color: #E6A23C;
}

.strength-level.strong {
  background-color: #67C23A;
}

.strength-text {
  font-size: 12px;
  color: #909399;
}

.password-tips {
  background-color: #f5f7fa;
  border-radius: 8px;
  padding: 15px;
  margin-top: 15px;
}

.password-tips p {
  margin: 0 0 10px;
  font-size: 13px;
  color: #606266;
  font-weight: 500;
}

.password-tips ul {
  margin: 0;
  padding: 0;
  list-style: none;
}

.password-tips li {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #909399;
  margin-bottom: 8px;
}

.password-tips li:last-child {
  margin-bottom: 0;
}

.password-tips li.active {
  color: #67C23A;
}

.password-tips li .el-icon {
  font-size: 16px;
}

.complete-step {
  text-align: center;
  padding: 20px 0;
}

.reset-password-footer {
  text-align: center;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #e4e7ed;
  font-size: 14px;
}

.reset-password-footer a {
  color: #409EFF;
  text-decoration: none;
  transition: color 0.3s;
}

.reset-password-footer a:hover {
  color: #66b1ff;
}

.divider {
  margin: 0 15px;
  color: #dcdfe6;
}

@media (max-width: 576px) {
  .reset-password-container {
    padding: 30px 20px;
  }

  .reset-password-header h2 {
    font-size: 24px;
  }
}
</style>