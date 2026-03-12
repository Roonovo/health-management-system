<template>
  <div class="plan-progress">
    <el-card>
      <template #header>
        <div class="card-header">
          <el-icon><TrendCharts /></el-icon>
          <span>用药进度统计</span>
        </div>
      </template>

      <div class="progress-wrapper">
        <!-- 总体进度 -->
        <div class="overall-progress">
          <h3>总体服药率</h3>
          <el-progress
            type="dashboard"
            :percentage="overallRate"
            :color="getProgressColor(overallRate)"
            :width="200"
          >
            <template #default="{ percentage }">
              <span class="percentage-value">{{ percentage }}%</span>
              <span class="percentage-label">服药率</span>
            </template>
          </el-progress>
          <div class="overall-stats">
            <div class="stat-item">
              <span class="stat-label">总计划数</span>
              <span class="stat-value">{{ stats.total }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">已完成</span>
              <span class="stat-value completed">{{ stats.completed }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">已漏服</span>
              <span class="stat-value missed">{{ stats.missed }}</span>
            </div>
          </div>
        </div>

        <!-- 各药品进度 -->
        <div class="medication-progress-list">
          <h3>各药品服药情况</h3>
          <div
            v-for="item in medicationProgress"
            :key="item.id"
            class="progress-item"
          >
            <div class="item-header">
              <span class="med-name">{{ item.name }}</span>
              <span class="rate-text" :class="getRateClass(item.rate)">
                {{ item.rate }}%
              </span>
            </div>
            <el-progress
              :percentage="item.rate"
              :color="getProgressColor(item.rate)"
              :stroke-width="12"
            />
            <div class="item-stats">
              <span>应服 {{ item.total }} 次</span>
              <span>已服 {{ item.taken }} 次</span>
              <span>漏服 {{ item.missed }} 次</span>
            </div>
          </div>
        </div>

        <!-- 图表 -->
        <div class="chart-section">
          <h3>近30天服药趋势</h3>
          <div ref="chartRef" class="chart-container"></div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { TrendCharts } from '@element-plus/icons-vue'
import * as echarts from 'echarts'

const chartRef = ref(null)
let chartInstance = null

// 统计数据
const stats = ref({
  total: 90,
  completed: 78,
  missed: 12
})

// 总体服药率
const overallRate = computed(() => {
  if (stats.value.total === 0) return 0
  return Math.round((stats.value.completed / stats.value.total) * 100)
})

// 各药品进度
const medicationProgress = ref([
  {
    id: 1,
    name: '降压药',
    total: 30,
    taken: 28,
    missed: 2,
    rate: 93
  },
  {
    id: 2,
    name: '降糖药',
    total: 30,
    taken: 26,
    missed: 4,
    rate: 87
  },
  {
    id: 3,
    name: '阿司匹林',
    total: 30,
    taken: 24,
    missed: 6,
    rate: 80
  }
])

// 获取进度颜色
const getProgressColor = (rate) => {
  if (rate >= 90) return '#67C23A'
  if (rate >= 70) return '#E6A23C'
  return '#F56C6C'
}

// 获取服药率样式类
const getRateClass = (rate) => {
  if (rate >= 90) return 'rate-high'
  if (rate >= 70) return 'rate-medium'
  return 'rate-low'
}

// 初始化图表
const initChart = () => {
  if (!chartRef.value) return

  chartInstance = echarts.init(chartRef.value)

  const dates = Array.from({ length: 30 }, (_, i) => {
    const date = new Date()
    date.setDate(date.getDate() - (29 - i))
    return `${date.getMonth() + 1}/${date.getDate()}`
  })

  const completedData = Array.from({ length: 30 }, () => Math.floor(Math.random() * 2) + 2)
  const missedData = Array.from({ length: 30 }, () => Math.floor(Math.random() * 2))

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    legend: {
      data: ['已服用', '已漏服']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: dates,
      axisLabel: {
        rotate: 45
      }
    },
    yAxis: {
      type: 'value',
      name: '次数'
    },
    series: [
      {
        name: '已服用',
        type: 'bar',
        stack: 'total',
        data: completedData,
        itemStyle: {
          color: '#67C23A'
        }
      },
      {
        name: '已漏服',
        type: 'bar',
        stack: 'total',
        data: missedData,
        itemStyle: {
          color: '#F56C6C'
        }
      }
    ]
  }

  chartInstance.setOption(option)

  window.addEventListener('resize', () => {
    chartInstance?.resize()
  })
}

onMounted(async () => {
  await nextTick()
  initChart()
})
</script>

<style scoped>
.plan-progress {
  height: 100%;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 500;
}

.progress-wrapper {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.overall-progress {
  text-align: center;
}

.overall-progress h3 {
  margin-bottom: 20px;
  color: #303133;
}

.percentage-value {
  display: block;
  font-size: 32px;
  font-weight: bold;
  color: #303133;
}

.percentage-label {
  display: block;
  font-size: 14px;
  color: #909399;
  margin-top: 8px;
}

.overall-stats {
  display: flex;
  justify-content: center;
  gap: 40px;
  margin-top: 24px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.stat-label {
  font-size: 13px;
  color: #909399;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.stat-value.completed {
  color: #67C23A;
}

.stat-value.missed {
  color: #F56C6C;
}

.medication-progress-list h3 {
  margin-bottom: 20px;
  color: #303133;
}

.progress-item {
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
  margin-bottom: 16px;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.med-name {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.rate-text {
  font-size: 18px;
  font-weight: bold;
}

.rate-text.rate-high {
  color: #67C23A;
}

.rate-text.rate-medium {
  color: #E6A23C;
}

.rate-text.rate-low {
  color: #F56C6C;
}

.item-stats {
  display: flex;
  justify-content: space-around;
  margin-top: 12px;
  font-size: 13px;
  color: #606266;
}

.chart-section h3 {
  margin-bottom: 20px;
  color: #303133;
}

.chart-container {
  width: 100%;
  height: 300px;
}

@media (max-width: 768px) {
  .overall-stats {
    flex-direction: column;
    gap: 16px;
  }

  .chart-container {
    height: 250px;
  }
}
</style>