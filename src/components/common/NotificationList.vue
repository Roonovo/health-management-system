<template>
  <div class="notification-list">
    <el-empty v-if="!notifications || notifications.length === 0" description="暂无通知" />
    
    <div v-else class="notifications">
      <div
        v-for="notification in notifications"
        :key="notification.id"
        class="notification-item"
        :class="{ unread: !notification.read }"
      >
        <div class="notification-icon">
          <el-icon :size="20">
            <Bell />
          </el-icon>
        </div>
        <div class="notification-content">
          <div class="notification-title">{{ notification.title }}</div>
          <div class="notification-desc">{{ notification.message }}</div>
          <div class="notification-time">{{ notification.time }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Bell } from '@element-plus/icons-vue'

defineProps({
  notifications: {
    type: Array,
    default: () => []
  }
})
</script>

<style scoped>
.notification-list {
  max-height: 400px;
  overflow-y: auto;
}

.notifications {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.notification-item {
  display: flex;
  gap: 12px;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.notification-item:hover {
  background: #e8f0ff;
}

.notification-item.unread {
  background: #fff;
  border-left: 3px solid #409eff;
}

.notification-icon {
  flex-shrink: 0;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #409eff;
  color: white;
  border-radius: 50%;
}

.notification-content {
  flex: 1;
}

.notification-title {
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.notification-desc {
  font-size: 14px;
  color: #606266;
  margin-bottom: 4px;
}

.notification-time {
  font-size: 12px;
  color: #909399;
}
</style>