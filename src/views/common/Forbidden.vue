<template>
  <div class="forbidden">
    <div class="forbidden-content">
      <div class="error-code">403</div>
      <h1>访问被拒绝</h1>
      <p>抱歉，您没有权限访问此页面</p>
      <div class="actions">
        <el-button type="primary" size="large" @click="goHome">
          <el-icon><HomeFilled /></el-icon>
          返回首页
        </el-button>
        <el-button size="large" @click="goBack">
          <el-icon><Back /></el-icon>
          返回上页
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { HomeFilled, Back } from '@element-plus/icons-vue'

const router = useRouter()

const goHome = () => {
  router.push('/')
}

const goBack = () => {
  router.go(-1)
}
</script>

<style scoped>
.forbidden {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.forbidden-content {
  text-align: center;
  color: white;
}

.error-code {
  font-size: 150px;
  font-weight: bold;
  line-height: 1;
  margin-bottom: 20px;
  text-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.forbidden-content h1 {
  font-size: 32px;
  margin: 0 0 15px;
}

.forbidden-content p {
  font-size: 18px;
  margin: 0 0 40px;
  opacity: 0.9;
}

.actions {
  display: flex;
  gap: 20px;
  justify-content: center;
}

@media (max-width: 768px) {
  .error-code {
    font-size: 100px;
  }

  .forbidden-content h1 {
    font-size: 24px;
  }

  .forbidden-content p {
    font-size: 16px;
  }

  .actions {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>