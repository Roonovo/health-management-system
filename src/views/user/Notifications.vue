<template>
  <div class="user-notifications">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>消息通知</span>
          <div class="header-actions">
            <el-button type="primary" text @click="markAllRead">
              <el-icon><Check /></el-icon>
              全部已读
            </el-button>
            <el-button type="danger" text @click="clearAll">
              <el-icon><Delete /></el-icon>
              清空消息
            </el-button>
          </div>
        </div>
      </template>

      <el-tabs v-model="activeTab">
        <el-tab-pane label="全部" name="all">
          <el-badge :value="unreadCount.all" :hidden="unreadCount.all === 0" />
        </el-tab-pane>
        <el-tab-pane label="系统通知" name="system">
          <el-badge :value="unreadCount.system" :hidden="unreadCount.system === 0" />
        </el-tab-pane>
        <el-tab-pane label="健康提醒" name="health">
          <el-badge :value="unreadCount.health" :hidden="unreadCount.health === 0" />
        </el-tab-pane>
        <el-tab-pane label="用药提醒" name="medication">
          <el-badge :value="unreadCount.medication" :hidden="unreadCount.medication === 0" />
        </el-tab-pane>
      </el-tabs>

      <div class="notifications-list">
        <div
          v-for="notification in filteredNotifications"
          :key="notification.id"
          class="notification-item"
          :class="{ unread: !notification.read }"
          @click="handleNotificationClick(notification)"
        >
          <div 
            class="notification-icon" 
            :style="{ backgroundColor: getIconColor(notification.type) }"
          >
            <el-icon :size="24">
              <component :is="getIconComponent(notification.type)" />
            </el-icon>
          </div>

          <div class="notification-content">
            <div class="notification-header">
              <h4>{{ notification.title }}</h4>
              <span class="notification-time">{{ formatTime(notification.time) }}</span>
            </div>
            <p class="notification-message">{{ notification.message }}</p>
          </div>

          <div class="notification-actions">
            <el-button
              v-if="!notification.read"
              type="primary"
              text
              @click.stop="markRead(notification)"
            >
              标为已读
            </el-button>
            <el-button
              type="danger"
              text
              @click.stop="deleteNotification(notification)"
            >
              删除
            </el-button>
          </div>
        </div>

        <el-empty
          v-if="filteredNotifications.length === 0"
          description="暂无消息"
        />
      </div>

      <el-pagination
        v-if="total > pageSize"
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        style="margin-top: 20px; justify-content: center"
        @current-change="loadNotifications"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Check,
  Delete,
  Bell,
  Warning,
  InfoFilled,
  SuccessFilled,
  Medicine
} from '@element-plus/icons-vue'
import {
  getNotifications,
  markNotificationRead,
  markAllNotificationsRead,
  deleteNotification as deleteNotificationApi,
  clearAllNotifications
} from '@/api/user'

const router = useRouter()

const activeTab = ref('all')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 通知列表
const notifications = ref([])

// 未读数量
const unreadCount = ref({
  all: 0,
  system: 0,
  health: 0,
  medication: 0
})

// 图标组件映射
const iconComponents = {
  system: InfoFilled,
  health: SuccessFilled,
  medication: Medicine,
  warning: Warning,
  default: Bell
}

// 过滤后的通知
const filteredNotifications = computed(() => {
  if (activeTab.value === 'all') {
    return notifications.value
  }
  return notifications.value.filter(n => n.type === activeTab.value)
})

// 获取图标组件
const getIconComponent = (type) => {
  return iconComponents[type] || iconComponents.default
}

// 获取图标颜色
const getIconColor = (type) => {
  const colorMap = {
    system: '#409EFF',
    health: '#67C23A',
    medication: '#E6A23C',
    warning: '#F56C6C'
  }
  return colorMap[type] || '#909399'
}

// 格式化时间
const formatTime = (time) => {
  const now = new Date()
  const notificationTime = new Date(time)
  const diff = now - notificationTime

  const minute = 60 * 1000
  const hour = 60 * minute
  const day = 24 * hour

  if (diff < minute) {
    return '刚刚'
  } else if (diff < hour) {
    return `${Math.floor(diff / minute)}分钟前`
  } else if (diff < day) {
    return `${Math.floor(diff / hour)}小时前`
  } else if (diff < 7 * day) {
    return `${Math.floor(diff / day)}天前`
  } else {
    return notificationTime.toLocaleDateString()
  }
}

// 处理通知点击
const handleNotificationClick = async (notification) => {
  // 标记为已读
  if (!notification.read) {
    await markRead(notification)
  }

  // 如果有跳转链接，则跳转
  if (notification.link) {
    router.push(notification.link)
  }
}

// 标记为已读
const markRead = async (notification) => {
  try {
    const res = await markNotificationRead(notification.id)
    if (res.success) {
      notification.read = true
      updateUnreadCount()
    }
  } catch (error) {
    ElMessage.error('操作失败：' + error.message)
  }
}

// 全部已读
const markAllRead = async () => {
  try {
    const res = await markAllNotificationsRead()
    if (res.success) {
      notifications.value.forEach(n => {
        n.read = true
      })
      updateUnreadCount()
      ElMessage.success('已全部标记为已读')
    }
  } catch (error) {
    ElMessage.error('操作失败：' + error.message)
  }
}

// 删除通知
const deleteNotification = async (notification) => {
  try {
    await ElMessageBox.confirm('确定要删除这条通知吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const res = await deleteNotificationApi(notification.id)
    if (res.success) {
      ElMessage.success('已删除')
      await loadNotifications()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败：' + error.message)
    }
  }
}

// 清空所有
const clearAll = async () => {
  try {
    await ElMessageBox.confirm('确定要清空所有消息吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const res = await clearAllNotifications()
    if (res.success) {
      ElMessage.success('已清空')
      notifications.value = []
      updateUnreadCount()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败：' + error.message)
    }
  }
}

// 更新未读数量
const updateUnreadCount = () => {
  unreadCount.value.all = notifications.value.filter(n => !n.read).length
  unreadCount.value.system = notifications.value.filter(
    n => !n.read && n.type === 'system'
  ).length
  unreadCount.value.health = notifications.value.filter(
    n => !n.read && n.type === 'health'
  ).length
  unreadCount.value.medication = notifications.value.filter(
    n => !n.read && n.type === 'medication'
  ).length
}

// 加载通知
const loadNotifications = async () => {
  try {
    const res = await getNotifications({
      page: currentPage.value,
      pageSize: pageSize.value
    })

    if (res.success) {
      notifications.value = res.data.list
      total.value = res.data.total
      updateUnreadCount()
    }
  } catch (error) {
    console.error('加载通知失败：', error)
  }
}

onMounted(() => {
  loadNotifications()
})
</script>

<style scoped>
.user-notifications {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.notifications-list {
  padding: 20px 0;
}

.notification-item {
  display: flex;
  gap: 15px;
  padding: 20px;
  background-color: #fafafa;
  border-radius: 8px;
  margin-bottom: 15px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.notification-item:hover {
  background-color: #f0f2f5;
}

.notification-item.unread {
  background-color: #ecf5ff;
  border-left: 4px solid #409EFF;
}

.notification-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.notification-content {
  flex: 1;
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.notification-header h4 {
  margin: 0;
  font-size: 16px;
  color: #303133;
}

.notification-time {
  font-size: 13px;
  color: #909399;
}

.notification-message {
  margin: 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
}

.notification-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
  align-items: flex-end;
}

@media (max-width: 768px) {
  .notification-item {
    flex-direction: column;
  }

  .notification-actions {
    flex-direction: row;
    justify-content: flex-end;
  }
}
</style>