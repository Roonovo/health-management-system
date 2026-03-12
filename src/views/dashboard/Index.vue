<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <!-- 欢迎卡片 -->
      <el-col :span="24">
        <el-card class="welcome-card">
          <div class="welcome-content">
            <div class="welcome-text">
              <h2>{{ greeting }}，{{ userInfo.realName || userInfo.username }}！</h2>
              <p>今天是 {{ currentDate }}，祝您身体健康！</p>
            </div>
            <div class="welcome-image">
              <img src="@/assets/images/dashboard-welcome.png" alt="Welcome" />
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 健康数据卡片 -->
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card blood-pressure">
          <div class="stat-header">
            <el-icon class="stat-icon"><Odometer /></el-icon>
            <span class="stat-title">血压</span>
          </div>
          <div class="stat-value">
            {{ healthStats.bloodPressure || '--/--' }}
            <span class="stat-unit">mmHg</span>
          </div>
          <div class="stat-footer">
            <span :class="['stat-status', healthStats.bloodPressureStatus]">
              {{ healthStats.bloodPressureLabel || '正常' }}
            </span>
            <span class="stat-time">{{ healthStats.bloodPressureTime }}</span>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card blood-sugar">
          <div class="stat-header">
            <el-icon class="stat-icon"><TrendCharts /></el-icon>
            <span class="stat-title">血糖</span>
          </div>
          <div class="stat-value">
            {{ healthStats.bloodSugar || '--' }}
            <span class="stat-unit">mmol/L</span>
          </div>
          <div class="stat-footer">
            <span :class="['stat-status', healthStats.bloodSugarStatus]">
              {{ healthStats.bloodSugarLabel || '正常' }}
            </span>
            <span class="stat-time">{{ healthStats.bloodSugarTime }}</span>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card weight">
          <div class="stat-header">
            <el-icon class="stat-icon"><Aim /></el-icon>
            <span class="stat-title">体重</span>
          </div>
          <div class="stat-value">
            {{ healthStats.weight || '--' }}
            <span class="stat-unit">kg</span>
          </div>
          <div class="stat-footer">
            <span :class="['stat-status', healthStats.weightStatus]">
              {{ healthStats.weightLabel || '正常' }}
            </span>
            <span class="stat-time">{{ healthStats.weightTime }}</span>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card heart-rate">
          <div class="stat-header">
            <el-icon class="stat-icon"><Monitor /></el-icon>
            <span class="stat-title">心率</span>
          </div>
          <div class="stat-value">
            {{ healthStats.heartRate || '--' }}
            <span class="stat-unit">bpm</span>
          </div>
          <div class="stat-footer">
            <span :class="['stat-status', healthStats.heartRateStatus]">
              {{ healthStats.heartRateLabel || '正常' }}
            </span>
            <span class="stat-time">{{ healthStats.heartRateTime }}</span>
          </div>
        </el-card>
      </el-col>

      <!-- 健康趋势图表 -->
      <el-col :xs="24" :md="16">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>健康趋势</span>
              <el-radio-group v-model="trendPeriod" size="small">
                <el-radio-button label="week">最近7天</el-radio-button>
                <el-radio-button label="month">最近30天</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div id="health-trend-chart" style="width: 100%; height: 300px"></div>
        </el-card>
      </el-col>

      <!-- 今日任务 -->
      <el-col :xs="24" :md="8">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>今日任务</span>
              <el-tag size="small" type="primary">{{ todayTasks.length }}</el-tag>
            </div>
          </template>
          <el-scrollbar height="300px">
            <div class="task-list">
              <div
                v-for="task in todayTasks"
                :key="task.id"
                class="task-item"
                :class="{ completed: task.completed }"
              >
                <el-checkbox
                  v-model="task.completed"
                  @change="toggleTask(task)"
                >
                  <div class="task-content">
                    <div class="task-title">{{ task.title }}</div>
                    <div class="task-time">{{ task.time }}</div>
                  </div>
                </el-checkbox>
              </div>
              <el-empty v-if="todayTasks.length === 0" description="暂无任务" />
            </div>
          </el-scrollbar>
        </el-card>
      </el-col>

      <!-- 用药提醒 -->
      <el-col :xs="24" :md="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>用药提醒</span>
              <router-link to="/medication/plan">
                <el-button type="text" size="small">查看全部</el-button>
              </router-link>
            </div>
          </template>
          <el-scrollbar height="250px">
            <div class="medication-list">
              <div
                v-for="med in medications"
                :key="med.id"
                class="medication-item"
              >
                <el-icon class="medication-icon" :color="med.color">
                  <Medicine />
                </el-icon>
                <div class="medication-info">
                  <div class="medication-name">{{ med.name }}</div>
                  <div class="medication-dosage">{{ med.dosage }}</div>
                </div>
                <div class="medication-time">{{ med.time }}</div>
              </div>
              <el-empty v-if="medications.length === 0" description="暂无用药计划" />
            </div>
          </el-scrollbar>
        </el-card>
      </el-col>

      <!-- 快捷操作 -->
      <el-col :xs="24" :md="12">
        <el-card>
          <template #header>
            <span>快捷操作</span>
          </template>
          <div class="quick-actions">
            <el-button
              v-for="action in quickActions"
              :key="action.path"
              :type="action.type"
              :icon="action.icon"
              @click="router.push(action.path)"
            >
              {{ action.label }}
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'
import {
  Odometer,
  TrendCharts,
  Aim,
  Monitor,
  Medicine,
  Plus,
  ChatDotSquare,
  Calendar,
  Document
} from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getHealthStatistics, getTodayTasks, getMedicationReminders } from '@/api/dashboard'

const router = useRouter()
const store = useStore()

const userInfo = computed(() => store.getters.userInfo || {})
const trendPeriod = ref('week')

// 问候语
const greeting = computed(() => {
  const hour = new Date().getHours()
  if (hour < 6) return '凌晨好'
  if (hour < 9) return '早上好'
  if (hour < 12) return '上午好'
  if (hour < 14) return '中午好'
  if (hour < 17) return '下午好'
  if (hour < 19) return '傍晚好'
  if (hour < 22) return '晚上好'
  return '夜深了'
})

// 当前日期
const currentDate = computed(() => {
  const date = new Date()
  const options = { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' }
  return date.toLocaleDateString('zh-CN', options)
})

// 健康统计数据
const healthStats = ref({
  bloodPressure: '120/80',
  bloodPressureStatus: 'normal',
  bloodPressureLabel: '正常',
  bloodPressureTime: '2小时前',
  
  bloodSugar: '5.6',
  bloodSugarStatus: 'normal',
  bloodSugarLabel: '正常',
  bloodSugarTime: '3小时前',
  
  weight: '70.5',
  weightStatus: 'normal',
  weightLabel: '标准',
  weightTime: '今天',
  
  heartRate: '75',
  heartRateStatus: 'normal',
  heartRateLabel: '正常',
  heartRateTime: '1小时前'
})

// 今日任务
const todayTasks = ref([
  {
    id: 1,
    title: '早餐后测血糖',
    time: '09:00',
    completed: true
  },
  {
    id: 2,
    title: '服用降压药',
    time: '10:00',
    completed: true
  },
  {
    id: 3,
    title: '午餐后散步30分钟',
    time: '14:00',
    completed: false
  },
  {
    id: 4,
    title: '晚餐后测血压',
    time: '19:00',
    completed: false
  }
])

// 用药提醒
const medications = ref([
  {
    id: 1,
    name: '阿司匹林肠溶片',
    dosage: '100mg × 1片',
    time: '早餐后',
    color: '#409EFF'
  },
  {
    id: 2,
    name: '二甲双胍片',
    dosage: '0.5g × 1片',
    time: '午餐后',
    color: '#67C23A'
  },
  {
    id: 3,
    name: '降压药',
    dosage: '5mg × 1片',
    time: '晚餐后',
    color: '#E6A23C'
  }
])

// 快捷操作
const quickActions = [
  {
    label: '添加数据',
    path: '/health/data',
    type: 'primary',
    icon: Plus
  },
  {
    label: 'AI 咨询',
    path: '/consultation',
    type: 'success',
    icon: ChatDotSquare
  },
  {
    label: '健康计划',
    path: '/plan/health',
    type: 'warning',
    icon: Calendar
  },
  {
    label: '健康档案',
    path: '/health/record',
    type: 'info',
    icon: Document
  }
]

// 切换任务状态
const toggleTask = (task) => {
  ElMessage.success(task.completed ? '任务已完成' : '任务未完成')
}

// 初始化健康趋势图表
let trendChart = null
const initTrendChart = () => {
  const chartDom = document.getElementById('health-trend-chart')
  if (!chartDom) return

  trendChart = echarts.init(chartDom)

  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['收缩压', '舒张压', '血糖', '心率']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '收缩压',
        type: 'line',
        data: [120, 118, 122, 119, 121, 120, 118],
        smooth: true,
        itemStyle: { color: '#409EFF' }
      },
      {
        name: '舒张压',
        type: 'line',
        data: [80, 78, 82, 79, 81, 80, 78],
        smooth: true,
        itemStyle: { color: '#67C23A' }
      },
      {
        name: '血糖',
        type: 'line',
        data: [5.6, 5.8, 5.7, 5.9, 5.6, 5.7, 5.8],
        smooth: true,
        itemStyle: { color: '#E6A23C' },
        yAxisIndex: 0
      },
      {
        name: '心率',
        type: 'line',
        data: [75, 72, 76, 74, 73, 75, 74],
        smooth: true,
        itemStyle: { color: '#F56C6C' }
      }
    ]
  }

  trendChart.setOption(option)
}

// 加载数据
const loadData = async () => {
  try {
    // 加载健康统计
    const statsRes = await getHealthStatistics()
    if (statsRes.success) {
      healthStats.value = statsRes.data
    }

    // 加载今日任务
    const tasksRes = await getTodayTasks()
    if (tasksRes.success) {
      todayTasks.value = tasksRes.data
    }

    // 加载用药提醒
    const medsRes = await getMedicationReminders()
    if (medsRes.success) {
      medications.value = medsRes.data
    }
  } catch (error) {
    console.error('加载数据失败：', error)
  }
}

// 监听趋势周期变化
watch(trendPeriod, () => {
  // TODO: 重新加载数据并更新图表
  initTrendChart()
})

onMounted(() => {
  loadData()
  initTrendChart()

  // 响应式调整图表
  window.addEventListener('resize', () => {
    if (trendChart) {
      trendChart.resize()
    }
  })
})
</script>

<style scoped>
.dashboard {
  padding: 0;
}

/* 欢迎卡片 */
.welcome-card {
  margin-bottom: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.welcome-card :deep(.el-card__body) {
  padding: 30px;
}

.welcome-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.welcome-text h2 {
  font-size: 28px;
  margin: 0 0 10px;
}

.welcome-text p {
  font-size: 16px;
  opacity: 0.9;
  margin: 0;
}

.welcome-image img {
  width: 150px;
  height: 150px;
}

/* 统计卡片 */
.stat-card {
  margin-bottom: 20px;
  transition: transform 0.3s, box-shadow 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.stat-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
}

.stat-icon {
  font-size: 24px;
}

.stat-title {
  font-size: 14px;
  color: #909399;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 10px;
}

.stat-unit {
  font-size: 14px;
  color: #909399;
  margin-left: 5px;
}

.stat-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-status {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.stat-status.normal {
  background-color: #f0f9ff;
  color: #67c23a;
}

.stat-status.warning {
  background-color: #fef0f0;
  color: #e6a23c;
}

.stat-status.danger {
  background-color: #fef0f0;
  color: #f56c6c;
}

.stat-time {
  font-size: 12px;
  color: #c0c4cc;
}

.blood-pressure .stat-icon {
  color: #409eff;
}

.blood-sugar .stat-icon {
  color: #67c23a;
}

.weight .stat-icon {
  color: #e6a23c;
}

.heart-rate .stat-icon {
  color: #f56c6c;
}

/* 卡片头部 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 任务列表 */
.task-list {
  padding: 10px 0;
}

.task-item {
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.task-item:last-child {
  border-bottom: none;
}

.task-item.completed .task-title {
  text-decoration: line-through;
  color: #c0c4cc;
}

.task-content {
  flex: 1;
}

.task-title {
  font-size: 14px;
  color: #303133;
  margin-bottom: 5px;
}

.task-time {
  font-size: 12px;
  color: #909399;
}

/* 用药列表 */
.medication-list {
  padding: 10px 0;
}

.medication-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.medication-item:last-child {
  border-bottom: none;
}

.medication-icon {
  font-size: 32px;
}

.medication-info {
  flex: 1;
}

.medication-name {
  font-size: 14px;
  color: #303133;
  margin-bottom: 5px;
}

.medication-dosage {
  font-size: 12px;
  color: #909399;
}

.medication-time {
  font-size: 12px;
  color: #409eff;
  font-weight: bold;
}

/* 快捷操作 */
.quick-actions {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}

.quick-actions .el-button {
  height: 60px;
  font-size: 16px;
}

/* 响应式 */
@media (max-width: 768px) {
  .welcome-image {
    display: none;
  }

  .quick-actions {
    grid-template-columns: 1fr;
  }
}
</style>