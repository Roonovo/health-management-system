<template>
  <div class="medication-reminder">
    <el-row :gutter="20">
      <!-- 左侧：即将到来的提醒 -->
      <el-col :xs="24" :md="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>即将到来的提醒</span>
              <el-tag type="warning">{{ upcomingReminders.length }}</el-tag>
            </div>
          </template>

          <el-timeline>
            <el-timeline-item
              v-for="reminder in upcomingReminders"
              :key="reminder.id"
              :timestamp="reminder.time"
              :type="getReminderType(reminder)"
              placement="top"
            >
              <el-card shadow="hover" class="reminder-card">
                <div class="reminder-content">
                  <div class="reminder-header">
                    <el-icon :size="24" :color="reminder.color">
                      <Bell />
                    </el-icon>
                    <div class="reminder-info">
                      <div class="reminder-title">{{ reminder.title }}</div>
                      <div class="reminder-subtitle">{{ reminder.medicationName }}</div>
                    </div>
                  </div>
                  <div class="reminder-details">
                    <p><strong>用法用量：</strong>{{ reminder.dosage }}</p>
                    <p v-if="reminder.note"><strong>备注：</strong>{{ reminder.note }}</p>
                  </div>
                  <div class="reminder-actions">
                    <el-button type="success" size="small" @click="confirmTaken(reminder)">
                      <el-icon><Check /></el-icon>
                      已服用
                    </el-button>
                    <el-button size="small" @click="snoozeReminder(reminder)">
                      <el-icon><Clock /></el-icon>
                      稍后提醒
                    </el-button>
                    <el-button type="danger" size="small" @click="dismissReminder(reminder)">
                      <el-icon><Close /></el-icon>
                      忽略
                    </el-button>
                  </div>
                </div>
              </el-card>
            </el-timeline-item>
          </el-timeline>

          <el-empty v-if="upcomingReminders.length === 0" description="暂无提醒" />
        </el-card>
      </el-col>

      <!-- 右侧：提醒设置 -->
      <el-col :xs="24" :md="12">
        <el-card>
          <template #header>
            <span>提醒设置</span>
          </template>

          <el-form :model="settings" label-width="120px">
            <el-form-item label="启用提醒">
              <el-switch v-model="settings.enabled" @change="saveSettings" />
            </el-form-item>

            <el-form-item label="提前提醒">
              <el-input-number
                v-model="settings.advanceMinutes"
                :min="0"
                :max="60"
                :disabled="!settings.enabled"
              />
              <span style="margin-left: 10px">分钟</span>
            </el-form-item>

            <el-form-item label="重复提醒">
              <el-switch
                v-model="settings.repeatReminder"
                :disabled="!settings.enabled"
                @change="saveSettings"
              />
            </el-form-item>

            <el-form-item
              v-if="settings.repeatReminder"
              label="重复间隔"
            >
              <el-input-number
                v-model="settings.repeatInterval"
                :min="5"
                :max="30"
                :disabled="!settings.enabled"
              />
              <span style="margin-left: 10px">分钟</span>
            </el-form-item>

            <el-form-item label="提醒方式">
              <el-checkbox-group
                v-model="settings.methods"
                :disabled="!settings.enabled"
              >
                <el-checkbox label="notification">系统通知</el-checkbox>
                <el-checkbox label="sound">声音提醒</el-checkbox>
                <el-checkbox label="vibration">震动提醒</el-checkbox>
              </el-checkbox-group>
            </el-form-item>

            <el-form-item label="免打扰时间">
              <el-switch
                v-model="settings.doNotDisturb"
                :disabled="!settings.enabled"
                @change="saveSettings"
              />
            </el-form-item>

            <el-form-item
              v-if="settings.doNotDisturb"
              label="时间段"
            >
              <el-time-picker
                v-model="settings.dndStart"
                placeholder="开始时间"
                value-format="HH:mm"
                :disabled="!settings.enabled"
                style="width: 45%"
              />
              <span style="margin: 0 10px">至</span>
              <el-time-picker
                v-model="settings.dndEnd"
                placeholder="结束时间"
                value-format="HH:mm"
                :disabled="!settings.enabled"
                style="width: 45%"
              />
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                @click="saveSettings"
                :disabled="!settings.enabled"
              >
                保存设置
              </el-button>
              <el-button @click="testReminder" :disabled="!settings.enabled">
                测试提醒
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 历史提醒记录 -->
        <el-card style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>历史记录</span>
              <el-button type="text" @click="clearHistory">
                清空记录
              </el-button>
            </div>
          </template>

          <el-table :data="historyReminders" stripe max-height="400">
            <el-table-column prop="date" label="日期" width="120" />
            <el-table-column prop="time" label="时间" width="100" />
            <el-table-column prop="medicationName" label="药品" />
            <el-table-column label="状态" align="center" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)" size="small">
                  {{ getStatusLabel(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>

          <el-empty v-if="historyReminders.length === 0" description="暂无记录" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox, ElNotification } from 'element-plus'
import { Bell, Check, Clock, Close } from '@element-plus/icons-vue'
import {
  getUpcomingReminders,
  getReminderSettings,
  updateReminderSettings,
  confirmMedicationTaken,
  snoozeReminder as snoozeReminderApi,
  dismissReminder as dismissReminderApi,
  getReminderHistory,
  clearReminderHistory
} from '@/api/medication'

// 即将到来的提醒
const upcomingReminders = ref([])

// 历史提醒
const historyReminders = ref([])

// 提醒设置
const settings = reactive({
  enabled: true,
  advanceMinutes: 15,
  repeatReminder: false,
  repeatInterval: 10,
  methods: ['notification', 'sound'],
  doNotDisturb: false,
  dndStart: '22:00',
  dndEnd: '07:00'
})

let reminderCheckInterval = null

// 获取提醒类型
const getReminderType = (reminder) => {
  const now = new Date()
  const reminderTime = new Date(`${now.toDateString()} ${reminder.time}`)
  const diff = reminderTime - now

  if (diff < 0) return 'danger'
  if (diff < 15 * 60 * 1000) return 'warning'
  return 'primary'
}

// 获取状态类型
const getStatusType = (status) => {
  const types = {
    confirmed: 'success',
    snoozed: 'warning',
    dismissed: 'info',
    missed: 'danger'
  }
  return types[status] || 'info'
}

// 获取状态标签
const getStatusLabel = (status) => {
  const labels = {
    confirmed: '已服用',
    snoozed: '已推迟',
    dismissed: '已忽略',
    missed: '已错过'
  }
  return labels[status] || status
}

// 确认服用
const confirmTaken = async (reminder) => {
  try {
    const res = await confirmMedicationTaken(reminder.id)
    if (res.success) {
      ElMessage.success('已记录服药')
      await loadUpcomingReminders()
      await loadHistory()
    }
  } catch (error) {
    ElMessage.error('操作失败：' + error.message)
  }
}

// 稍后提醒
const snoozeReminder = async (reminder) => {
  try {
    await ElMessageBox.prompt('请选择推迟时间', '稍后提醒', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputType: 'number',
      inputValue: 10,
      inputValidator: (value) => {
        if (!value || value <= 0) {
          return '请输入有效的分钟数'
        }
        return true
      }
    }).then(async ({ value }) => {
      const res = await snoozeReminderApi(reminder.id, parseInt(value))
      if (res.success) {
        ElMessage.success(`已推迟${value}分钟`)
        await loadUpcomingReminders()
      }
    })
  } catch (error) {
    // 用户取消
  }
}

// 忽略提醒
const dismissReminder = async (reminder) => {
  try {
    await ElMessageBox.confirm('确定要忽略这条提醒吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const res = await dismissReminderApi(reminder.id)
    if (res.success) {
      ElMessage.info('已忽略提醒')
      await loadUpcomingReminders()
      await loadHistory()
    }
  } catch (error) {
    // 用户取消
  }
}

// 保存设置
const saveSettings = async () => {
  try {
    const res = await updateReminderSettings(settings)
    if (res.success) {
      ElMessage.success('设置已保存')
      
      // 如果禁用了提醒，清除定时检查
      if (!settings.enabled && reminderCheckInterval) {
        clearInterval(reminderCheckInterval)
        reminderCheckInterval = null
      } else if (settings.enabled && !reminderCheckInterval) {
        startReminderCheck()
      }
    }
  } catch (error) {
    ElMessage.error('保存失败：' + error.message)
  }
}

// 测试提醒
const testReminder = () => {
  const hasNotificationPermission = settings.methods.includes('notification')
  
  if (hasNotificationPermission) {
    ElNotification({
      title: '测试提醒',
      message: '这是一条测试提醒消息',
      type: 'info',
      duration: 5000
    })
  }

  if (settings.methods.includes('sound')) {
    // 播放提醒声音
    const audio = new Audio('/sounds/reminder.mp3')
    audio.play().catch(() => {
      ElMessage.warning('无法播放提醒声音')
    })
  }

  ElMessage.success('测试提醒已发送')
}

// 清空历史记录
const clearHistory = async () => {
  try {
    await ElMessageBox.confirm('确定要清空所有历史记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const res = await clearReminderHistory()
    if (res.success) {
      ElMessage.success('已清空历史记录')
      historyReminders.value = []
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败：' + error.message)
    }
  }
}

// 检查提醒
const checkReminders = async () => {
  try {
    const res = await getUpcomingReminders()
    if (res.success && res.data.length > 0) {
      // 检查是否有新的即将到来的提醒
      const newReminders = res.data.filter(reminder => {
        const now = new Date()
        const reminderTime = new Date(`${now.toDateString()} ${reminder.time}`)
        const diff = reminderTime - now
        
        // 在提醒时间前的设定分钟数内触发
        return diff > 0 && diff <= settings.advanceMinutes * 60 * 1000
      })

      // 发送通知
      newReminders.forEach(reminder => {
        if (settings.methods.includes('notification')) {
          ElNotification({
            title: '用药提醒',
            message: `${reminder.medicationName} - ${reminder.dosage}`,
            type: 'warning',
            duration: 0,
            onClick: () => {
              confirmTaken(reminder)
            }
          })
        }

        if (settings.methods.includes('sound')) {
          const audio = new Audio('/sounds/reminder.mp3')
          audio.play().catch(() => {})
        }
      })

      upcomingReminders.value = res.data
    }
  } catch (error) {
    console.error('检查提醒失败：', error)
  }
}

// 启动提醒检查
const startReminderCheck = () => {
  // 每分钟检查一次
  reminderCheckInterval = setInterval(() => {
    checkReminders()
  }, 60 * 1000)
  
  // 立即检查一次
  checkReminders()
}

// 加载数据
const loadUpcomingReminders = async () => {
  try {
    const res = await getUpcomingReminders()
    if (res.success) {
      upcomingReminders.value = res.data
    }
  } catch (error) {
    console.error('加载提醒失败：', error)
  }
}

const loadSettings = async () => {
  try {
    const res = await getReminderSettings()
    if (res.success) {
      Object.assign(settings, res.data)
    }
  } catch (error) {
    console.error('加载设置失败：', error)
  }
}

const loadHistory = async () => {
  try {
    const res = await getReminderHistory({ limit: 20 })
    if (res.success) {
      historyReminders.value = res.data
    }
  } catch (error) {
    console.error('加载历史记录失败：', error)
  }
}

// 请求通知权限
const requestNotificationPermission = async () => {
  if ('Notification' in window && Notification.permission === 'default') {
    const permission = await Notification.requestPermission()
    if (permission !== 'granted') {
      ElMessage.warning('未授予通知权限，将无法收到系统通知')
      settings.methods = settings.methods.filter(m => m !== 'notification')
    }
  }
}

onMounted(() => {
  loadUpcomingReminders()
  loadSettings()
  loadHistory()
  requestNotificationPermission()
  
  if (settings.enabled) {
    startReminderCheck()
  }
})

onUnmounted(() => {
  if (reminderCheckInterval) {
    clearInterval(reminderCheckInterval)
  }
})
</script>

<style scoped>
.medication-reminder {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 提醒卡片 */
.reminder-card {
  margin: 0;
}

.reminder-content {
  padding: 10px;
}

.reminder-header {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 15px;
}

.reminder-info {
  flex: 1;
}

.reminder-title {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.reminder-subtitle {
  font-size: 14px;
  color: #909399;
}

.reminder-details {
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
  margin-bottom: 15px;
}

.reminder-details p {
  margin: 5px 0;
  font-size: 14px;
  color: #606266;
}

.reminder-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>