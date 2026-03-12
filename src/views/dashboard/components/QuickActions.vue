<template>
  <div class="quick-actions">
    <el-card>
      <template #header>
        <div class="card-header">
          <el-icon><Operation /></el-icon>
          <span>快捷操作</span>
        </div>
      </template>

      <el-row :gutter="16">
        <el-col :xs="12" :sm="8" :md="6" v-for="action in actions" :key="action.name">
          <div class="action-item" @click="handleAction(action)">
            <div class="icon-wrapper" :style="{ backgroundColor: action.color }">
              <el-icon :size="28">
                <component :is="action.icon" />
              </el-icon>
            </div>
            <div class="name">{{ action.name }}</div>
          </div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Operation,
  Edit,
  Document,
  Bell,
  ChatDotRound,
  TrendCharts,
  Calendar,
  Setting
} from '@element-plus/icons-vue'

const router = useRouter()

const actions = ref([
  {
    name: '记录数据',
    icon: Edit,
    color: '#409EFF',
    path: '/health-data/add'
  },
  {
    name: '查看档案',
    icon: Document,
    color: '#67C23A',
    path: '/health-records'
  },
  {
    name: '用药提醒',
    icon: Bell,
    color: '#E6A23C',
    path: '/medication/reminders'
  },
  {
    name: 'AI咨询',
    icon: ChatDotRound,
    color: '#F56C6C',
    path: '/consultation/new'
  },
  {
    name: '健康报告',
    icon: TrendCharts,
    color: '#909399',
    path: '/health-reports'
  },
  {
    name: '预约挂号',
    icon: Calendar,
    color: '#5470C6',
    path: '/appointments/new'
  },
  {
    name: '数据分析',
    icon: TrendCharts,
    color: '#91CC75',
    path: '/analytics'
  },
  {
    name: '系统设置',
    icon: Setting,
    color: '#FAC858',
    path: '/settings'
  }
])

const handleAction = (action) => {
  if (action.path) {
    router.push(action.path)
  } else {
    ElMessage.info(`功能开发中：${action.name}`)
  }
}
</script>

<style scoped>
.quick-actions {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 500;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 20px;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.3s;
}

.action-item:hover {
  background: #f5f7fa;
  transform: translateY(-3px);
}

.icon-wrapper {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.name {
  font-size: 14px;
  color: #606266;
  text-align: center;
}
</style>