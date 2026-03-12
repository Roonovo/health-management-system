<template>
  <div class="error-page">
    <el-result
      icon="error"
      title="500"
      sub-title="抱歉，服务器出错了"
    >
      <template #extra>
        <el-button type="primary" @click="reload">刷新页面</el-button>
        <el-button @click="goHome">返回首页</el-button>
      </template>
    </el-result>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const reload = () => {
  window.location.reload()
}

const goHome = () => {
  router.push('/dashboard')
}
</script>

<style scoped>
.error-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
}
</style>