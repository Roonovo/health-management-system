<template>
  <div class="blood-sugar-chart">
    <el-card>
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-icon><TrendCharts /></el-icon>
            <span>血糖趋势</span>
          </div>
          <div class="header-right">
            <el-radio-group v-model="timePeriod" size="small" @change="loadData">
              <el-radio-button label="week">近7天</el-radio-button>
              <el-radio-button label="month">近30天</el-radio-button>
              <el-radio-button label="quarter">近3个月</el-radio-button>
            </el-radio-group>
          </div>
        </div>
      </template>

      <div class="chart-wrapper">
        <!-- 统计信息 -->
        <div class="stats-row">
          <div class="stat-item">
            <div class="stat-label">平均空腹血糖</div>
            <div class="stat-value" :class="getStatusClass(avgFasting)">
              {{ avgFasting }} <span class="unit">mmol/L</span>
            </div>
          </div>
          <div class="stat-item">
            <div class="stat-label">平均餐后血糖</div>
            <div class="stat-value" :class="getStatusClass(avgAfterMeal, 'afterMeal')">
              {{ avgAfterMeal }} <span class="unit">mmol/L</span>
            </div>
          </div>
          <div class="stat-item">
            <div class="stat-label">测量次数</div>
            <div class="stat-value">{{ totalRecords }} <span class="unit">次</span></div>
          </div>
          <div class="stat-item">
            <div class="stat-label">控制情况</div>
            <div class="stat-value">
              <el-tag :type="healthStatus.type" size="large">
                {{ healthStatus.text }}
              </el-tag>
            </div>
          </div>
        </div>

        <!-- 图表区域 -->
        <div ref="chartRef" class="chart-container" v-loading="loading"></div>

        <!-- 参考范围说明 -->
        <div class="reference-info">
          <el-alert type="info" :closable="false">
            <template #title>
              <div class="reference-content">
                <span class="reference-title">参考范围：</span>
                <el-tag type="success" size="small" effect="plain">
                  空腹: 3.9-6.1 mmol/L
                </el-tag>
                <el-tag type="primary" size="small" effect="plain">
                  餐后2小时: <7.8 mmol/L
                </el-tag>
                <el-tag type="warning" size="small" effect="plain">
                  糖尿病前期: 6.1-7.0 (空腹)
                </el-tag>
                <el-tag type="danger" size="small" effect="plain">
                  糖尿病: ≥7.0 (空腹)
                </el-tag>
              </div>
            </template>
          </el-alert>
        </div>

        <!-- 数据列表 -->
        <div class="data-list" v-if="showDataList">
          <el-divider>详细数据</el-divider>
          <el-table :data="chartData" stripe max-height="300">
            <el-table-column prop="date" label="日期时间" width="180" />
            <el-table-column label="测量时间" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="getMealTimeType(row.mealTime)" size="small">
                  {{ getMealTimeText(row.mealTime) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="血糖值" width="120" align="center">
              <template #default="{ row }">
                <span :class="getValueClass(row.value, row.mealTime)">
                  {{ row.value }} mmol/L
                </span>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="getRecordStatus(row).type" size="small">
                  {{ getRecordStatus(row).text }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="notes" label="备注" show-overflow-tooltip />
          </el-table>
        </div>

        <!-- 切换数据列表显示 -->
        <div class="toggle-list">
          <el-button type="primary" text @click="showDataList = !showDataList">
            {{ showDataList ? '隐藏' : '查看' }}详细数据
            <el-icon>
              <component :is="showDataList ? ArrowUp : ArrowDown" />
            </el-icon>
          </el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { TrendCharts, ArrowUp, ArrowDown } from '@element-plus/icons-vue'
import * as echarts from 'echarts'

const timePeriod = ref('week')
const loading = ref(false)
const chartRef = ref(null)
const showDataList = ref(false)
let chartInstance = null

// 模拟数据
const chartData = ref([
  {
    date: '2024-01-01 07:00',
    value: 5.6,
    mealTime: 'fasting',
    notes: '空腹'
  },
  {
    date: '2024-01-01 09:30',
    value: 7.2,
    mealTime: 'afterBreakfast',
    notes: '早餐后2小时'
  },
  {
    date: '2024-01-01 14:00',
    value: 7.5,
    mealTime: 'afterLunch',
    notes: '午餐后2小时'
  },
  {
    date: '2024-01-02 07:00',
    value: 5.8,
    mealTime: 'fasting',
    notes: '空腹'
  },
  {
    date: '2024-01-02 09:30',
    value: 7.0,
    mealTime: 'afterBreakfast',
    notes: '早餐后2小时'
  },
  {
    date: '2024-01-03 07:00',
    value: 5.5,
    mealTime: 'fasting',
    notes: '空腹'
  },
  {
    date: '2024-01-03 14:00',
    value: 7.3,
    mealTime: 'afterLunch',
    notes: '午餐后2小时'
  }
])

// 计算平均值
const avgFasting = computed(() => {
  const fastingData = chartData.value.filter(item => item.mealTime === 'fasting')
  if (fastingData.length === 0) return 0
  const sum = fastingData.reduce((acc, item) => acc + item.value, 0)
  return (sum / fastingData.length).toFixed(1)
})

const avgAfterMeal = computed(() => {
  const afterMealData = chartData.value.filter(item => item.mealTime !== 'fasting')
  if (afterMealData.length === 0) return 0
  const sum = afterMealData.reduce((acc, item) => acc + item.value, 0)
  return (sum / afterMealData.length).toFixed(1)
})

const totalRecords = computed(() => chartData.value.length)

// 健康状态评估
const healthStatus = computed(() => {
  const fasting = parseFloat(avgFasting.value)
  
  if (fasting < 6.1) {
    return { type: 'success', text: '正常' }
  } else if (fasting < 7.0) {
    return { type: 'warning', text: '糖尿病前期' }
  } else {
    return { type: 'danger', text: '需要关注' }
  }
})

// 获取状态样式类
const getStatusClass = (value, type = 'fasting') => {
  const val = parseFloat(value)
  if (type === 'fasting') {
    if (val < 6.1) return 'normal'
    if (val < 7.0) return 'warning'
    return 'danger'
  } else {
    if (val < 7.8) return 'normal'
    if (val < 11.1) return 'warning'
    return 'danger'
  }
}

// 获取值的样式类
const getValueClass = (value, mealTime) => {
  return getStatusClass(value, mealTime === 'fasting' ? 'fasting' : 'afterMeal')
}

// 获取用餐时间类型
const getMealTimeType = (mealTime) => {
  const typeMap = {
    fasting: 'warning',
    afterBreakfast: 'success',
    afterLunch: 'primary',
    afterDinner: 'info'
  }
  return typeMap[mealTime] || 'info'
}

// 获取用餐时间文本
const getMealTimeText = (mealTime) => {
  const textMap = {
    fasting: '空腹',
    afterBreakfast: '早餐后',
    afterLunch: '午餐后',
    afterDinner: '晚餐后'
  }
  return textMap[mealTime] || mealTime
}

// 获取记录状态
const getRecordStatus = (row) => {
  const type = row.mealTime === 'fasting' ? 'fasting' : 'afterMeal'
  const statusClass = getStatusClass(row.value, type)
  
  if (statusClass === 'normal') {
    return { type: 'success', text: '正常' }
  } else if (statusClass === 'warning') {
    return { type: 'warning', text: '偏高' }
  } else {
    return { type: 'danger', text: '异常' }
  }
}

// 初始化图表
const initChart = () => {
  if (!chartRef.value) return

  if (chartInstance) {
    chartInstance.dispose()
  }

  chartInstance = echarts.init(chartRef.value)

  const dates = chartData.value.map(item => item.date.split(' ')[0])
  const times = chartData.value.map(item => item.date.split(' ')[1])
  const values = chartData.value.map(item => item.value)
  const mealTimes = chartData.value.map(item => getMealTimeText(item.mealTime))

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross'
      },
      formatter: (params) => {
        const param = params[0]
        const index = param.dataIndex
        const mealTime = mealTimes[index]
        const status = getRecordStatus(chartData.value[index])
        const color = status.type === 'success' ? '#67C23A' : status.type === 'warning' ? '#E6A23C' : '#F56C6C'
        
        return `
          <div style="font-weight: bold; margin-bottom: 5px;">${dates[index]} ${times[index]}</div>
          <div style="margin: 3px 0;">
            <span style="display: inline-block; width: 10px; height: 10px; border-radius: 50%; background-color: ${param.color}; margin-right: 5px;"></span>
            <span>血糖值: <strong style="color: ${color}">${param.value}</strong> mmol/L</span>
          </div>
                    <div style="margin: 3px 0;">
            <span>状态: <strong style="color: ${color}">${status.text}</strong></span>
          </div>
        `
      }
    },
    legend: {
      data: ['血糖值', '空腹正常上限', '餐后正常上限'],
      top: 10
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '15%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: chartData.value.map(item => `${item.date.split(' ')[0]}\n${item.date.split(' ')[1]}`),
      axisLabel: {
        rotate: 45,
        fontSize: 11
      }
    },
    yAxis: {
      type: 'value',
      name: 'mmol/L',
      min: 3,
      max: 12,
      splitLine: {
        lineStyle: {
          type: 'dashed'
        }
      }
    },
    series: [
      {
        name: '血糖值',
        type: 'line',
        smooth: true,
        data: values,
        itemStyle: {
          color: (params) => {
            const item = chartData.value[params.dataIndex]
            const statusClass = getValueClass(item.value, item.mealTime)
            return statusClass === 'normal' ? '#67C23A' : statusClass === 'warning' ? '#E6A23C' : '#F56C6C'
          }
        },
        lineStyle: {
          width: 3,
          color: '#409EFF'
        },
        symbol: 'circle',
        symbolSize: 10,
        markLine: {
          silent: true,
          lineStyle: {
            color: '#E6A23C',
            type: 'dashed'
          },
          data: [
            { yAxis: 6.1, name: '空腹上限' },
            { yAxis: 7.8, name: '餐后上限' }
          ],
          label: {
            formatter: '{b}: {c}'
          }
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
          ])
        }
      },
      {
        name: '空腹正常上限',
        type: 'line',
        data: chartData.value.map(() => 6.1),
        lineStyle: {
          type: 'dotted',
          color: '#67C23A',
          width: 2
        },
        symbol: 'none',
        silent: true
      },
      {
        name: '餐后正常上限',
        type: 'line',
        data: chartData.value.map(() => 7.8),
        lineStyle: {
          type: 'dotted',
          color: '#409EFF',
          width: 2
        },
        symbol: 'none',
        silent: true
      }
    ]
  }

  chartInstance.setOption(option)

  window.addEventListener('resize', () => {
    chartInstance?.resize()
  })
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 500))
    ElMessage.success('数据加载成功')
    await nextTick()
    initChart()
  } catch (error) {
    ElMessage.error('数据加载失败：' + error.message)
  } finally {
    loading.value = false
  }
}

watch(timePeriod, () => {
  loadData()
})

onMounted(async () => {
  await nextTick()
  initChart()
})
</script>

<style scoped>
.blood-sugar-chart {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 500;
}

.chart-wrapper {
  width: 100%;
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 20px;
  padding: 16px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8edf5 100%);
  border-radius: 8px;
}

.stat-item {
  text-align: center;
}

.stat-label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.stat-value .unit {
  font-size: 14px;
  font-weight: normal;
  color: #909399;
  margin-left: 4px;
}

.stat-value.normal {
  color: #67C23A;
}

.stat-value.warning {
  color: #E6A23C;
}

.stat-value.danger {
  color: #F56C6C;
}

.chart-container {
  width: 100%;
  height: 400px;
  margin: 20px 0;
}

.reference-info {
  margin: 20px 0;
}

.reference-content {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.reference-title {
  font-weight: 500;
  color: #606266;
}

.data-list {
  margin-top: 20px;
}

.toggle-list {
  text-align: center;
  margin-top: 16px;
}

@media (max-width: 768px) {
  .card-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .stats-row {
    grid-template-columns: repeat(2, 1fr);
  }

  .chart-container {
    height: 300px;
  }
}
</style>