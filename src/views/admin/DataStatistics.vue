<template>
  <div class="data-statistics">
    <!-- 概览卡片 -->
    <el-row :gutter="20">
      <el-col 
        v-for="stat in overviewStats" 
        :key="stat.key"
        :xs="12" 
        :sm="6"
      >
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" :style="{ backgroundColor: stat.color }">
              <el-icon :size="28">
                <component :is="stat.icon" />
              </el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stat.value }}</div>
              <div class="stat-label">{{ stat.label }}</div>
            </div>
          </div>
          <div class="stat-trend">
            <span :class="stat.trend >= 0 ? 'trend-up' : 'trend-down'">
              <el-icon>
                <Top v-if="stat.trend >= 0" />
                <Bottom v-else />
              </el-icon>
              {{ Math.abs(stat.trend) }}%
            </span>
            <span class="trend-label">较上月</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 数据趋势图表 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :xs="24" :lg="12">
        <StatisticsChart
          title="用户数据趋势"
          chart-id="user-data-trend"
          :data="userDataTrend"
          :colors="['#409EFF', '#67C23A']"
        />
      </el-col>

      <el-col :xs="24" :lg="12">
        <StatisticsChart
          title="健康数据趋势"
          chart-id="health-data-trend"
          :data="healthDataTrend"
          :colors="['#F56C6C', '#E6A23C', '#67C23A', '#409EFF']"
        />
      </el-col>
    </el-row>

    <!-- 数据分类统计 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :xs="24" :lg="8">
        <el-card>
          <template #header>
            <span>数据类型分布</span>
          </template>
          <div id="data-type-chart" style="width: 100%; height: 350px"></div>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="8">
        <el-card>
          <template #header>
            <span>用户活跃度</span>
          </template>
          <div id="user-activity-chart" style="width: 100%; height: 350px"></div>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="8">
        <el-card>
          <template #header>
            <span>设备使用情况</span>
          </template>
          <div id="device-usage-chart" style="width: 100%; height: 350px"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 详细数据表格 -->
    <el-row style="margin-top: 20px">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>数据详情</span>
              <div class="header-actions">
                <el-select 
                  v-model="filterType" 
                  placeholder="数据类型" 
                  style="width: 150px"
                  @change="loadDetailData"
                >
                  <el-option label="全部" value="" />
                  <el-option label="血压" value="bloodPressure" />
                  <el-option label="血糖" value="bloodSugar" />
                  <el-option label="体重" value="weight" />
                  <el-option label="心率" value="heartRate" />
                </el-select>

                <el-date-picker
                  v-model="dateRange"
                  type="daterange"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  value-format="YYYY-MM-DD"
                  @change="loadDetailData"
                />

                <el-button type="primary" @click="exportData">
                  <el-icon><Download /></el-icon>
                  导出数据
                </el-button>
              </div>
            </div>
          </template>

          <el-table :data="detailData" stripe v-loading="loading">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="username" label="用户" width="120" />
            <el-table-column label="数据类型" width="120">
              <template #default="{ row }">
                <el-tag :type="getDataTypeTag(row.type)" size="small">
                  {{ getDataTypeLabel(row.type) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="value" label="数值" align="center" />
            <el-table-column prop="unit" label="单位" width="80" align="center" />
            <el-table-column prop="recordTime" label="记录时间" width="180" />
            <el-table-column label="状态" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">
                  {{ getStatusLabel(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="remark" label="备注" min-width="150" />
          </el-table>

          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="total"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            style="margin-top: 20px; justify-content: center"
            @current-change="loadDetailData"
            @size-change="loadDetailData"
          />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import {
  User,
  DataLine,
  Document,
  Bell,
  Top,
  Bottom,
  Download
} from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import StatisticsChart from './components/StatisticsChart.vue'
import {
  getDataStatistics,
  getUserDataTrend,
  getHealthDataTrend,
  getDataTypeDistribution,
  getUserActivityDistribution,
  getDeviceUsageDistribution,
  getDetailData as getDetailDataApi,
  exportStatisticsData
} from '@/api/admin'

const loading = ref(false)
const filterType = ref('')
const dateRange = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 概览统计
const overviewStats = ref([
  {
    key: 'totalUsers',
    label: '总用户数',
    value: 0,
    trend: 0,
    icon: User,
    color: '#409EFF'
  },
  {
    key: 'totalData',
    label: '总数据量',
    value: 0,
    trend: 0,
    icon: DataLine,
    color: '#67C23A'
  },
  {
    key: 'totalRecords',
    label: '健康档案',
    value: 0,
    trend: 0,
    icon: Document,
    color: '#E6A23C'
  },
  {
    key: 'totalAlerts',
    label: '健康预警',
    value: 0,
    trend: 0,
    icon: Bell,
    color: '#F56C6C'
  }
])

// 用户数据趋势
const userDataTrend = ref({
  dates: [],
  series: [
    { name: '新增用户', data: [] },
    { name: '活跃用户', data: [] }
  ]
})

// 健康数据趋势
const healthDataTrend = ref({
  dates: [],
  series: [
    { name: '血压', data: [] },
    { name: '血糖', data: [] },
    { name: '体重', data: [] },
    { name: '心率', data: [] }
  ]
})

// 详细数据
const detailData = ref([])

let dataTypeChart = null
let userActivityChart = null
let deviceUsageChart = null

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

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    normal: 'success',
    warning: 'warning',
    danger: 'danger'
  }
  return typeMap[status] || 'info'
}

// 获取状态标签
const getStatusLabel = (status) => {
  const labelMap = {
    normal: '正常',
    warning: '预警',
    danger: '异常'
  }
  return labelMap[status] || status
}

// 初始化数据类型分布图表
const initDataTypeChart = async () => {
  const chartDom = document.getElementById('data-type-chart')
  if (!chartDom) return

  dataTypeChart = echarts.init(chartDom)

  try {
    const res = await getDataTypeDistribution()
    if (res.success) {
      const option = {
        tooltip: {
          trigger: 'item'
        },
        legend: {
          bottom: 0
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
            data: res.data
          }
        ]
      }

      dataTypeChart.setOption(option)
    }
  } catch (error) {
    console.error('加载数据类型分布失败：', error)
  }
}

// 初始化用户活跃度图表
const initUserActivityChart = async () => {
  const chartDom = document.getElementById('user-activity-chart')
  if (!chartDom) return

  userActivityChart = echarts.init(chartDom)

  try {
    const res = await getUserActivityDistribution()
    if (res.success) {
      const option = {
        tooltip: {
          trigger: 'item'
        },
        legend: {
          bottom: 0
        },
        series: [
          {
            type: 'pie',
            radius: '70%',
            data: res.data,
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      }

      userActivityChart.setOption(option)
    }
  } catch (error) {
    console.error('加载用户活跃度失败：', error)
  }
}

// 初始化设备使用情况图表
const initDeviceUsageChart = async () => {
  const chartDom = document.getElementById('device-usage-chart')
  if (!chartDom) return

  deviceUsageChart = echarts.init(chartDom)

  try {
    const res = await getDeviceUsageDistribution()
    if (res.success) {
      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: res.data.map(item => item.name)
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            type: 'bar',
            data: res.data.map(item => ({
              value: item.value,
              itemStyle: { color: item.color }
            })),
            barWidth: '50%'
          }
        ]
      }

      deviceUsageChart.setOption(option)
    }
  } catch (error) {
    console.error('加载设备使用情况失败：', error)
  }
}

// 加载概览统计
const loadOverviewStats = async () => {
  try {
    const res = await getDataStatistics()
    if (res.success) {
      overviewStats.value.forEach(stat => {
        const data = res.data[stat.key]
        if (data) {
          stat.value = data.value
          stat.trend = data.trend
        }
      })
    }
  } catch (error) {
    console.error('加载概览统计失败：', error)
  }
}

// 加载用户数据趋势
const loadUserDataTrend = async () => {
  try {
    const res = await getUserDataTrend({ days: 30 })
    if (res.success) {
      userDataTrend.value = res.data
    }
  } catch (error) {
    console.error('加载用户数据趋势失败：', error)
  }
}

// 加载健康数据趋势
const loadHealthDataTrend = async () => {
  try {
    const res = await getHealthDataTrend({ days: 30 })
    if (res.success) {
      healthDataTrend.value = res.data
    }
  } catch (error) {
    console.error('加载健康数据趋势失败：', error)
  }
}

// 加载详细数据
const loadDetailData = async () => {
  loading.value = true

  try {
    const res = await getDetailDataApi({
      page: currentPage.value,
      pageSize: pageSize.value,
      type: filterType.value,
      startDate: dateRange.value[0],
      endDate: dateRange.value[1]
    })

    if (res.success) {
      detailData.value = res.data.list
      total.value = res.data.total
    }
  } catch (error) {
    ElMessage.error('加载数据失败：' + error.message)
  } finally {
    loading.value = false
  }
}

// 导出数据
const exportData = async () => {
  try {
    ElMessage.info('正在准备导出数据...')

    const res = await exportStatisticsData({
      type: filterType.value,
      startDate: dateRange.value[0],
      endDate: dateRange.value[1]
    })

    if (res.success) {
      // 创建下载链接
      const blob = new Blob([JSON.stringify(res.data, null, 2)], {
        type: 'application/json'
      })
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = `statistics_data_${Date.now()}.json`
      link.click()
      window.URL.revokeObjectURL(url)

      ElMessage.success('数据导出成功')
    }
  } catch (error) {
    ElMessage.error('导出失败：' + error.message)
  }
}

// 窗口大小变化
const handleResize = () => {
  dataTypeChart?.resize()
  userActivityChart?.resize()
  deviceUsageChart?.resize()
}

onMounted(() => {
  loadOverviewStats()
  loadUserDataTrend()
  loadHealthDataTrend()

  // 设置默认日期范围（最近30天）
  const today = new Date()
  const lastMonth = new Date(today.getTime() - 30 * 24 * 60 * 60 * 1000)
  dateRange.value = [
    lastMonth.toISOString().split('T')[0],
    today.toISOString().split('T')[0]
  ]

  loadDetailData()

  nextTick(() => {
    initDataTypeChart()
    initUserActivityChart()
    initDeviceUsageChart()
  })

  window.addEventListener('resize', handleResize)
})
</script>

<style scoped>
.data-statistics {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 15px;
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
  width: 56px;
  height: 56px;
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
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 13px;
  color: #909399;
}

.stat-trend {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #e4e7ed;
  font-size: 12px；
}
.trend-up {
color: #67C23A;
display: flex;
align-items: center;
gap: 3px;
font-weight: 500;
}

.trend-down {
color: #F56C6C;
display: flex;
align-items: center;
gap: 3px;
font-weight: 500;
}

.trend-label {
color: #909399;
}

@media (max-width: 768px) {
.header-actions {
flex-direction: column;
align-items: stretch;
}

.header-actions > * {
width: 100% !important;
}

.stat-value {
font-size: 20px;
}

.stat-icon {
width: 48px;
height: 48px;
}
}
</style>