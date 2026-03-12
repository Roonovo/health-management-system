<template>
  <div class="admin-dashboard">
    <div class="dashboard-header">
      <h2>管理员控制台</h2>
      <el-button type="primary" @click="refreshData">
        <el-icon><Refresh /></el-icon>
        刷新数据
      </el-button>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="24" :sm="12" :lg="6" v-for="stat in stats" :key="stat.title">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" :style="{ backgroundColor: stat.color }">
              <el-icon :size="32">
                <component :is="stat.icon" />
              </el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-title">{{ stat.title }}</div>
              <div class="stat-value">{{ stat.value }}</div>
              <div class="stat-desc">
                <span :class="stat.trend">
                  <el-icon>
                    <component :is="stat.trend === 'up' ? TrendCharts : Bottom" />
                  </el-icon>
                  {{ stat.change }}
                </span>
                <span class="stat-period">较昨日</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20">
      <el-col :xs="24" :lg="16">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>用户活跃趋势</span>
              <el-radio-group v-model="activeChartPeriod" size="small">
                <el-radio-button label="week">近7天</el-radio-button>
                <el-radio-button label="month">近30天</el-radio-button>
                <el-radio-button label="year">近一年</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="userActivityChart" class="chart-container"></div>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="8">
        <el-card class="chart-card">
          <template #header>
            <span>系统使用分布</span>
          </template>
          <div ref="systemUsageChart" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 数据表格区域 -->
    <el-row :gutter="20">
      <el-col :xs="24" :lg="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>最近用户注册</span>
              <el-button type="primary" text @click="viewAllUsers">
                查看全部
                <el-icon><ArrowRight /></el-icon>
              </el-button>
            </div>
          </template>
          <el-table :data="recentUsers" stripe>
            <el-table-column prop="username" label="用户名" width="120" />
            <el-table-column prop="email" label="邮箱" show-overflow-tooltip />
            <el-table-column prop="createdAt" label="注册时间" width="180" />
            <el-table-column label="状态" width="80" align="center">
              <template #default="{ row }">
                <el-tag :type="row.status === 'active' ? 'success' : 'info'" size="small">
                  {{ row.status === 'active' ? '正常' : '禁用' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>系统日志</span>
              <el-button type="primary" text @click="viewAllLogs">
                查看全部
                <el-icon><ArrowRight /></el-icon>
              </el-button>
            </div>
          </template>
          <el-table :data="systemLogs" stripe>
            <el-table-column prop="type" label="类型" width="100">
              <template #default="{ row }">
                <el-tag :type="getLogType(row.type)" size="small">
                  {{ row.typeName }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="message" label="消息" show-overflow-tooltip />
            <el-table-column prop="time" label="时间" width="180" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快捷操作 -->
    <el-card class="quick-actions-card">
      <template #header>
        <span>快捷管理</span>
      </template>
      <el-row :gutter="16}>
        <el-col :xs="12" :sm="8" :md="6" :lg="4" v-for="action in adminActions" :key="action.name">
          <div class="admin-action-item" @click="handleAdminAction(action)">
            <div class="action-icon" :style="{ backgroundColor: action.color }">
              <el-icon :size="24">
                <component :is="action.icon" />
              </el-icon>
            </div>
            <div class="action-name">{{ action.name }}</div>
          </div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Refresh,
  User,
  Document,
  ChatDotRound,
  TrendCharts,
  Bottom,
  ArrowRight,
  Setting,
  UserFilled,
  FolderOpened,
  DataAnalysis,
  Notification,
  Files
} from '@element-plus/icons-vue'
import * as echarts from 'echarts'

const router = useRouter()

// 统计数据
const stats = ref([
  {
    title: '总用户数',
    value: '1,234',
    change: '+12%',
    trend: 'up',
    icon: User,
    color: '#409EFF'
  },
  {
    title: '今日活跃',
    value: '456',
    change: '+8%',
    trend: 'up',
    icon: UserFilled,
    color: '#67C23A'
  },
  {
    title: '咨询总数',
    value: '3,456',
    change: '+15%',
    trend: 'up',
    icon: ChatDotRound,
    color: '#E6A23C'
  },
  {
    title: '健康档案',
    value: '2,345',
    change: '+5%',
    trend: 'up',
    icon: Document,
    color: '#F56C6C'
  }
])

// 图表周期
const activeChartPeriod = ref('week')

// 图表引用
const userActivityChart = ref(null)
const systemUsageChart = ref(null)

// 最近注册用户
const recentUsers = ref([
  {
    username: '张三',
    email: 'zhangsan@example.com',
    createdAt: '2024-01-15 10:30:00',
    status: 'active'
  },
  {
    username: '李四',
    email: 'lisi@example.com',
    createdAt: '2024-01-15 09:15:00',
    status: 'active'
  },
  {
    username: '王五',
    email: 'wangwu@example.com',
    createdAt: '2024-01-14 16:20:00',
    status: 'active'
  },
  {
    username: '赵六',
    email: 'zhaoliu@example.com',
    createdAt: '2024-01-14 14:10:00',
    status: 'inactive'
  }
])

// 系统日志
const systemLogs = ref([
  {
    type: 'info',
    typeName: '信息',
    message: '系统定时备份完成',
    time: '2024-01-15 10:00:00'
  },
  {
    type: 'warning',
    typeName: '警告',
    message: '磁盘空间使用率超过80%',
    time: '2024-01-15 09:30:00'
  },
  {
    type: 'success',
    typeName: '成功',
    message: '数据库优化完成',
    time: '2024-01-15 08:00:00'
  },
  {
    type: 'error',
    typeName: '错误',
    message: '邮件发送失败',
    time: '2024-01-14 18:20:00'
  }
])

// 管理员快捷操作
const adminActions = ref([
  {
    name: '用户管理',
    icon: UserFilled,
    color: '#409EFF',
    path: '/admin/users'
  },
  {
    name: '内容管理',
    icon: Document,
    color: '#67C23A',
    path: '/admin/content'
  },
  {
    name: '数据分析',
    icon: DataAnalysis,
    color: '#E6A23C',
    path: '/admin/analytics'
  },
  {
    name: '系统设置',
    icon: Setting,
    color: '#F56C6C',
    path: '/admin/settings'
  },
  {
    name: '日志管理',
    icon: FolderOpened,
    color: '#909399',
    path: '/admin/logs'
  },
  {
    name: '通知管理',
    icon: Notification,
    color: '#5470C6',
    path: '/admin/notifications'
  }
])

// 初始化用户活跃趋势图表
const initUserActivityChart = () => {
  if (!userActivityChart.value) return

  const chart = echarts.init(userActivityChart.value)

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross'
      }
    },
    legend: {
      data: ['活跃用户', '新增用户', '咨询次数']
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
        name: '活跃用户',
        type: 'line',
        smooth: true,
        data: [120, 132, 101, 134, 90, 230, 210],
        itemStyle: { color: '#409EFF' }
      },
      {
        name: '新增用户',
        type: 'line',
        smooth: true,
        data: [20, 32, 21, 34, 30, 40, 35],
        itemStyle: { color: '#67C23A' }
      },
      {
        name: '咨询次数',
        type: 'line',
        smooth: true,
        data: [150, 180, 191, 234, 290, 330, 310],
        itemStyle: { color: '#E6A23C' }
      }
    ]
  }

  chart.setOption(option)

  window.addEventListener('resize', () => {
    chart.resize()
  })
}

// 初始化系统使用分布图表
const initSystemUsageChart = () => {
  if (!systemUsageChart.value) return

  const chart = echarts.init(systemUsageChart.value)

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '功能使用',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 16,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: [
          { value: 1048, name: '健康数据', itemStyle: { color: '#409EFF' } },
          { value: 735, name: 'AI咨询', itemStyle: { color: '#67C23A' } },
          { value: 580, name: '用药提醒', itemStyle: { color: '#E6A23C' } },
          { value: 484, name: '健康档案', itemStyle: { color: '#F56C6C' } },
          { value: 300, name: '其他', itemStyle: { color: '#909399' } }
        ]
      }
    ]
  }

  chart.setOption(option)

  window.addEventListener('resize', () => {
    chart.resize()
  })
}

// 获取日志类型
const getLogType = (type) => {
  const typeMap = {
    info: 'primary',
    warning: 'warning',
    error: 'danger',
    success: 'success'
  }
  return typeMap[type] || 'info'
}

// 刷新数据
const refreshData = () => {
  ElMessage.success('数据已刷新')
  // 实际项目中这里应该重新加载数据
}

// 查看所有用户
const viewAllUsers = () => {
  router.push('/admin/users')
}

// 查看所有日志
const viewAllLogs = () => {
  router.push('/admin/logs')
}

// 处理管理员操作
const handleAdminAction = (action) => {
  if (action.path) {
    router.push(action.path)
  } else {
    ElMessage.info(`功能开发中：${action.name}`)
  }
}

onMounted(async () => {
  await nextTick()
  initUserActivityChart()
  initSystemUsageChart()
})
</script>

<style scoped>
.admin-dashboard {
  padding: 0;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.dashboard-header h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  cursor: pointer;
  transition: all 0.3s;
  border: none;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.stat-info {
  flex: 1;
}

.stat-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 8px;
}

.stat-desc {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
}

.stat-desc .up {
  color: #67C23A;
}

.stat-desc .down {
  color: #F56C6C;
}

.stat-period {
  color: #909399;
}

.chart-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  font-weight: 500;
}

.chart-container {
  width: 100%;
  height: 350px;
}

.quick-actions-card {
  margin-top: 20px;
}

.admin-action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 20px;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.3s;
}

.admin-action-item:hover {
  background: #f5f7fa;
  transform: translateY(-3px);
}

.action-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.action-name {
  font-size: 14px;
  color: #606266;
  text-align: center;
}

@media (max-width: 768px) {
  .dashboard-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .chart-container {
    height: 250px;
  }
}
</style>