<template>
  <div class="admin-dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20">
      <el-col 
        v-for="stat in stats" 
        :key="stat.key"
        :xs="12" 
        :sm="6"
      >
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" :style="{ backgroundColor: stat.color }">
              <el-icon :size="32">
                <component :is="stat.iconComponent" />
              </el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stat.value }}</div>
              <div class="stat-label">{{ stat.label }}</div>
            </div>
          </div>
          <div class="stat-footer">
            <span :class="stat.trend > 0 ? 'trend-up' : 'trend-down'">
              <el-icon>
                <Top v-if="stat.trend > 0" />
                <Bottom v-else />
              </el-icon>
              {{ Math.abs(stat.trend) }}%
            </span>
            <span class="stat-desc">较上周</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :xs="24" :lg="16">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>用户增长趋势</span>
              <el-radio-group v-model="userChartType" size="small">
                <el-radio-button label="week">周</el-radio-button>
                <el-radio-button label="month">月</el-radio-button>
                <el-radio-button label="year">年</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div id="user-trend-chart" style="width: 100%; height: 350px"></div>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="8">
        <el-card>
          <template #header>
            <span>数据分布</span>
          </template>
          <div id="data-distribution-chart" style="width: 100%; height: 350px"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快捷操作 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :xs="24" :sm="12" :md="6" v-for="action in quickActions" :key="action.key">
        <el-card shadow="hover" class="action-card" @click="handleQuickAction(action.key)">
          <div class="action-content">
            <el-icon :size="40" :color="action.color">
              <component :is="action.icon" />
            </el-icon>
            <div class="action-info">
              <div class="action-title">{{ action.title }}</div>
              <div class="action-desc">{{ action.desc }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 活跃用户和最新数据 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :xs="24" :lg="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>活跃用户 TOP 5</span>
              <el-button type="text" @click="$router.push('/admin/users')">
                查看更多
                <el-icon><ArrowRight /></el-icon>
              </el-button>
            </div>
          </template>
          <el-table :data="activeUsers" stripe>
            <el-table-column type="index" label="#" width="50" />
            <el-table-column prop="username" label="用户名" />
            <el-table-column prop="realName" label="姓名" />
            <el-table-column prop="loginCount" label="登录次数" align="center" width="100" />
            <el-table-column prop="lastLogin" label="最后登录" width="180" />
          </el-table>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>最新健康数据</span>
              <el-button type="text" @click="$router.push('/admin/data-statistics')">
                查看更多
                <el-icon><ArrowRight /></el-icon>
              </el-button>
            </div>
          </template>
          <el-table :data="latestData" stripe>
            <el-table-column prop="username" label="用户" width="100" />
            <el-table-column prop="type" label="类型" width="100">
              <template #default="{ row }">
                <el-tag :type="getDataTypeTag(row.type)" size="small">
                  {{ getDataTypeLabel(row.type) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="value" label="数值" align="center" width="100" />
            <el-table-column prop="time" label="时间" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 系统状态 -->
    <el-row style="margin-top: 20px">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>系统状态</span>
              <el-button type="text" @click="refreshSystemStatus">
                <el-icon><Refresh /></el-icon>
                刷新
              </el-button>
            </div>
          </template>

          <el-row :gutter="20">
            <el-col :xs="24" :sm="12" :md="6">
              <div class="system-stat">
                <div class="system-label">
                  <el-icon><Cpu /></el-icon>
                  CPU使用率
                </div>
                <el-progress
                  :percentage="systemStatus.cpu"
                  :color="getProgressColor(systemStatus.cpu)"
                  :stroke-width="10"
                />
                <div class="system-value">{{ systemStatus.cpu }}%</div>
              </div>
            </el-col>
            <el-col :xs="24" :sm="12" :md="6">
              <div class="system-stat">
                <div class="system-label">
                  <el-icon><Memory /></el-icon>
                  内存使用率
                </div>
                <el-progress
                  :percentage="systemStatus.memory"
                  :color="getProgressColor(systemStatus.memory)"
                  :stroke-width="10"
                />
                <div class="system-value">{{ systemStatus.memory }}%</div>
              </div>
            </el-col>
            <el-col :xs="24" :sm="12" :md="6">
              <div class="system-stat">
                <div class="system-label">
                  <el-icon><Coin /></el-icon>
                  磁盘使用率
                </div>
                <el-progress
                  :percentage="systemStatus.disk"
                  :color="getProgressColor(systemStatus.disk)"
                  :stroke-width="10"
                />
                <div class="system-value">{{ systemStatus.disk }}%</div>
              </div>
            </el-col>
            <el-col :xs="24" :sm="12" :md="6">
              <div class="system-stat">
                <div class="system-label">
                  <el-icon><Connection /></el-icon>
                  数据库连接
                </div>
                <el-progress
                  :percentage="systemStatus.database"
                  :color="getProgressColor(systemStatus.database)"
                  :stroke-width="10"
                />
                <div class="system-value">{{ systemStatus.database }}%</div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>

    <!-- 系统公告 -->
    <el-row style="margin-top: 20px">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>系统公告</span>
              <el-button type="primary" size="small" @click="showAnnouncementDialog = true">
                <el-icon><Plus /></el-icon>
                发布公告
              </el-button>
            </div>
          </template>

          <el-timeline>
            <el-timeline-item
              v-for="announcement in announcements"
              :key="announcement.id"
              :timestamp="announcement.time"
              placement="top"
            >
              <el-card>
                <h4>{{ announcement.title }}</h4>
                <p>{{ announcement.content }}</p>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>

    <!-- 发布公告对话框 -->
    <el-dialog
      v-model="showAnnouncementDialog"
      title="发布系统公告"
      width="600px"
    >
      <el-form :model="announcementForm" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="announcementForm.title" placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input
            v-model="announcementForm.content"
            type="textarea"
            :rows="5"
            placeholder="请输入公告内容"
          />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="announcementForm.type" placeholder="请选择公告类型">
            <el-option label="系统通知" value="system" />
            <el-option label="功能更新" value="feature" />
            <el-option label="维护通知" value="maintenance" />
            <el-option label="紧急公告" value="urgent" />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showAnnouncementDialog = false">取消</el-button>
        <el-button type="primary" @click="publishAnnouncement">
          发布
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  User,
  DataLine,
  Document,
  Bell,
  Top,
  Bottom,
  ArrowRight,
  Refresh,
  Plus,
  UserFilled,
  Setting,
  DataAnalysis,
  Management,
  Cpu,
  Memory,
  Coin,
  Connection
} from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import {
  getDashboardStats,
  getUserTrend,
  getActiveUsers,
  getLatestData,
  getSystemStatus,
  getAnnouncements,
  publishAnnouncement as publishAnnouncementApi
} from '@/api/admin'

const router = useRouter()

const userChartType = ref('week')
const showAnnouncementDialog = ref(false)

// 统计数据
const stats = ref([
  {
    key: 'users',
    label: '总用户数',
    value: 0,
    trend: 0,
    icon: 'User',
    iconComponent: User,
    color: '#409EFF'
  },
  {
    key: 'data',
    label: '健康数据',
    value: 0,
    trend: 0,
    icon: 'DataLine',
    iconComponent: DataLine,
    color: '#67C23A'
  },
  {
    key: 'records',
    label: '健康档案',
    value: 0,
    trend: 0,
    icon: 'Document',
    iconComponent: Document,
    color: '#E6A23C'
  },
  {
    key: 'alerts',
    label: '健康预警',
    value: 0,
    trend: 0,
    icon: 'Bell',
    iconComponent: Bell,
    color: '#F56C6C'
  }
])

// 快捷操作
const quickActions = ref([
  {
    key: 'users',
    title: '用户管理',
    desc: '查看和管理系统用户',
    icon: UserFilled,
    color: '#409EFF'
  },
  {
    key: 'data',
    title: '数据统计',
    desc: '查看详细数据报表',
    icon: DataAnalysis,
    color: '#67C23A'
  },
  {
    key: 'system',
    title: '系统设置',
    desc: '配置系统参数',
    icon: Setting,
    color: '#E6A23C'
  },
  {
    key: 'logs',
    title: '操作日志',
    desc: '查看系统日志',
    icon: Management,
    color: '#F56C6C'
  }
])

// 活跃用户
const activeUsers = ref([])

// 最新数据
const latestData = ref([])

// 系统状态
const systemStatus = reactive({
  cpu: 0,
  memory: 0,
  disk: 0,
  database: 0
})

// 公告列表
const announcements = ref([])

// 公告表单
const announcementForm = reactive({
  title: '',
  content: '',
  type: 'system'
})

let userTrendChart = null
let dataDistributionChart = null

// 获取数据类型标签
const getDataTypeTag = (type) => {
  const typeMap = {
    bloodPressure: 'danger',
    bloodSugar: 'warning',
    weight: 'success',
    heartRate: 'primary'
  }
  return typeMap[type] || 'info'
}

// 获取数据类型标签文本
const getDataTypeLabel = (type) => {
  const labelMap = {
    bloodPressure: '血压',
    bloodSugar: '血糖',
    weight: '体重',
    heartRate: '心率'
  }
  return labelMap[type] || type
}

// 获取进度条颜色
const getProgressColor = (percentage) => {
  if (percentage >= 90) return '#F56C6C'
  if (percentage >= 70) return '#E6A23C'
  return '#67C23A'
}

// 处理快捷操作
const handleQuickAction = (key) => {
  const routeMap = {
    users: '/admin/users',
    data: '/admin/data-statistics',
    system: '/admin/settings',
    logs: '/admin/logs'
  }

  const route = routeMap[key]
  if (route) {
    router.push(route)
  }
}

// 初始化用户趋势图表
const initUserTrendChart = (data) => {
  const chartDom = document.getElementById('user-trend-chart')
  if (!chartDom) return

  userTrendChart = echarts.init(chartDom)

  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['新增用户', '活跃用户']
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
      data: data.dates
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '新增用户',
        type: 'line',
        data: data.newUsers,
        smooth: true,
        itemStyle: { color: '#409EFF' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
          ])
        }
      },
      {
        name: '活跃用户',
        type: 'line',
        data: data.activeUsers,
        smooth: true,
        itemStyle: { color: '#67C23A' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(103, 194, 58, 0.3)' },
            { offset: 1, color: 'rgba(103, 194, 58, 0.05)' }
          ])
        }
      }
    ]
  }

  userTrendChart.setOption(option)
}

// 初始化数据分布图表
const initDataDistributionChart = (data) => {
  const chartDom = document.getElementById('data-distribution-chart')
  if (!chartDom) return

  dataDistributionChart = echarts.init(chartDom)

  const option = {
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center'
    },
    series: [
      {
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
            fontSize: 20,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: data
      }
    ]
  }

  dataDistributionChart.setOption(option)
}

// 加载数据
const loadDashboardStats = async () => {
  try {
    const res = await getDashboardStats()
    if (res.success) {
      stats.value.forEach(stat => {
        const data = res.data[stat.key]
        if (data) {
          stat.value = data.value
          stat.trend = data.trend
        }
      })
    }
  } catch (error) {
    console.error('加载统计数据失败：', error)
  }
}

const loadUserTrend = async () => {
  try {
    const res = await getUserTrend({ type: userChartType.value })
    if (res.success) {
      initUserTrendChart(res.data)
    }
  } catch (error) {
    console.error('加载用户趋势失败：', error)
  }
}

const loadActiveUsers = async () => {
  try {
    const res = await getActiveUsers({ limit: 5 })
    if (res.success) {
      activeUsers.value = res.data
    }
  } catch (error) {
    console.error('加载活跃用户失败：', error)
  }
}

const loadLatestData = async () => {
  try {
    const res = await getLatestData({ limit: 5 })
    if (res.success) {
      latestData.value = res.data
    }
  } catch (error) {
    console.error('加载最新数据失败：', error)
  }
}

const loadSystemStatus = async () => {
  try {
    const res = await getSystemStatus()
    if (res.success) {
      Object.assign(systemStatus, res.data)
    }
  } catch (error) {
    console.error('加载系统状态失败：', error)
  }
}

const loadAnnouncements = async () => {
  try {
    const res = await getAnnouncements({ limit: 5 })
    if (res.success) {
      announcements.value = res.data
    }
  } catch (error) {
    console.error('加载公告失败：', error)
  }
}

// 刷新系统状态
const refreshSystemStatus = () => {
  loadSystemStatus()
  ElMessage.success('已刷新系统状态')
}

// 发布公告
const publishAnnouncement = async () => {
  if (!announcementForm.title || !announcementForm.content) {
    ElMessage.warning('请填写完整的公告信息')
    return
  }

  try {
    const res = await publishAnnouncementApi(announcementForm)
    if (res.success) {
      ElMessage.success('公告发布成功')
      showAnnouncementDialog.value = false
      
      // 重置表单
      announcementForm.title = ''
      announcementForm.content = ''
      announcementForm.type = 'system'
      
      // 重新加载公告列表
      loadAnnouncements()
    }
  } catch (error) {
    ElMessage.error('发布失败：' + error.message)
  }
}

// 监听图表类型变化
watch(userChartType, () => {
  loadUserTrend()
})

onMounted(() => {
  loadDashboardStats()
  loadActiveUsers()
  loadLatestData()
  loadSystemStatus()
  loadAnnouncements()

  nextTick(() => {
    loadUserTrend()
    
    // 初始化数据分布图表
    initDataDistributionChart([
      { value: 335, name: '血压数据', itemStyle: { color: '#F56C6C' } },
      { value: 310, name: '血糖数据', itemStyle: { color: '#E6A23C' } },
      { value: 234, name: '体重数据', itemStyle: { color: '#67C23A' } },
      { value: 135, name: '心率数据', itemStyle: { color: '#409EFF' } }
    ])
  })

  window.addEventListener('resize', () => {
    userTrendChart?.resize()
    dataDistributionChart?.resize()
  })
})
</script>

<style scoped>
.admin-dashboard {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 统计卡片 */
.stat-card {
  transition: transform 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 15px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.stat-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 15px;
  border-top: 1px solid #e4e7ed;
  font-size: 13px;
}

.trend-up {
  color: #67C23A;
  display: flex;
  align-items: center;
  gap: 3px;
}

.trend-down {
  color: #F56C6C;
  display: flex;
  align-items: center;
  gap: 3px;
}

.stat-desc {
  color: #909399;
}

/* 快捷操作卡片 */
.action-card {
  cursor: pointer;
  transition: all 0.3s;
}

.action-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.action-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 20px 10px;
}

.action-info {
  margin-top: 15px;
}

.action-title {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.action-desc {
  font-size: 13px;
  color: #909399;
}

/* 系统状态 */
.system-stat {
  padding: 20px 0;
  text-align: center;
}

.system-label {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
  font-size: 14px;
  color: #606266;
  margin-bottom: 15px;
}

.system-value {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
  margin-top: 10px;
}

@media (max-width: 768px) {
  .stat-value {
    font-size: 24px;
  }

  .stat-icon {
    width: 50px;
    height: 50px;
  }

  .action-content {
    padding: 15px 5px;
  }

  .action-title {
    font-size: 14px;
  }
}
</style>