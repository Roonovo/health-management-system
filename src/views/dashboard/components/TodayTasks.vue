<template>
  <div class="today-tasks">
    <el-card>
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-icon><Calendar /></el-icon>
            <span>今日任务</span>
            <el-badge :value="uncompletedCount" :max="99" v-if="uncompletedCount > 0" />
          </div>
          <el-button type="primary" text @click="viewAll">
            查看全部
            <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
      </template>

      <el-empty v-if="tasks.length === 0" description="暂无任务" />

      <div v-else class="task-list">
        <div
          v-for="task in tasks"
          :key="task.id"
          class="task-item"
          :class="{ completed: task.completed }"
        >
          <el-checkbox
            v-model="task.completed"
            @change="handleTaskToggle(task)"
          />
          <div class="task-content">
            <div class="task-title">{{ task.title }}</div>
            <div class="task-meta">
              <span class="time">
                <el-icon><Clock /></el-icon>
                {{ task.time }}
              </span>
              <el-tag :type="getTaskType(task.type)" size="small">
                {{ task.typeName }}
              </el-tag>
            </div>
          </div>
                    <div class="task-actions">
            <el-button
              type="primary"
              text
              size="small"
              @click="handleTask(task)"
              v-if="!task.completed"
            >
              {{ getActionText(task.type) }}
            </el-button>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Calendar,
  Clock,
  ArrowRight
} from '@element-plus/icons-vue'

const router = useRouter()

const tasks = ref([
  {
    id: 1,
    title: '早上服用降压药',
    time: '08:00',
    type: 'medication',
    typeName: '用药',
    completed: true
  },
  {
    id: 2,
    title: '测量血压',
    time: '09:00',
    type: 'measurement',
    typeName: '测量',
    completed: false
  },
  {
    id: 3,
    title: '午餐后测血糖',
    time: '13:00',
    type: 'measurement',
    typeName: '测量',
    completed: false
  },
  {
    id: 4,
    title: '下午锻炼30分钟',
    time: '16:00',
    type: 'exercise',
    typeName: '运动',
    completed: false
  },
  {
    id: 5,
    title: '晚上服用降糖药',
    time: '19:00',
    type: 'medication',
    typeName: '用药',
    completed: false
  }
])

// 计算未完成任务数量
const uncompletedCount = computed(() => {
  return tasks.value.filter(task => !task.completed).length
})

// 获取任务类型样式
const getTaskType = (type) => {
  const typeMap = {
    medication: 'danger',
    measurement: 'primary',
    exercise: 'success',
    appointment: 'warning'
  }
  return typeMap[type] || 'info'
}

// 获取操作按钮文本
const getActionText = (type) => {
  const textMap = {
    medication: '去服药',
    measurement: '去测量',
    exercise: '去运动',
    appointment: '去预约'
  }
  return textMap[type] || '去完成'
}

// 处理任务完成切换
const handleTaskToggle = (task) => {
  if (task.completed) {
    ElMessage.success(`已完成：${task.title}`)
  } else {
    ElMessage.info(`已取消完成：${task.title}`)
  }
}

// 处理任务操作
const handleTask = (task) => {
  const routeMap = {
    medication: '/medication/take',
    measurement: '/health-data/add',
    exercise: '/exercise/record',
    appointment: '/appointments/new'
  }
  
  const route = routeMap[task.type]
  if (route) {
    router.push(route)
  } else {
    ElMessage.info('功能开发中')
  }
}

// 查看全部
const viewAll = () => {
  router.push('/tasks')
}
</script>

<style scoped>
.today-tasks {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 500;
}

.task-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.task-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 8px;
  transition: all 0.3s;
}

.task-item:hover {
  background: #e8edf5;
  transform: translateX(4px);
}

.task-item.completed {
  opacity: 0.6;
}

.task-item.completed .task-title {
  text-decoration: line-through;
  color: #909399;
}

.task-content {
  flex: 1;
}

.task-title {
  font-size: 14px;
  color: #303133;
  margin-bottom: 6px;
  font-weight: 500;
}

.task-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 13px;
}

.time {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #909399;
}

.task-actions {
  flex-shrink: 0;
}
</style>