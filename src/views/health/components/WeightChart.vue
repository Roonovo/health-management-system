<template>
  <div class="weight-chart">
    <el-card>
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-icon><TrendCharts /></el-icon>
            <span>体重趋势</span>
          </div>
          <div class="header-right">
            <el-radio-group v-model="timePeriod" size="small" @change="loadData">
              <el-radio-button label="week">近7天</el-radio-button>
              <el-radio-button label="month">近30天</el-radio-button>
              <el-radio-button label="quarter">近3个月</el-radio-button>
              <el-radio-button label="year">近一年</el-radio-button>
            </el-radio-group>
          </div>
        </div>
      </template>

      <div class="chart-wrapper">
        <!-- 统计信息 -->
        <div class="stats-row">
          <div class="stat-item">
            <div class="stat-label">当前体重</div>
            <div class="stat-value">
              {{ currentWeight }} <span class="unit">kg</span>
            </div>
          </div>
          <div class="stat-item">
            <div class="stat-label">平均体重</div>
            <div class="stat-value">
              {{ avgWeight }} <span class="unit">kg</span>
            </div>
          </div>
          <div class="stat-item">
            <div class="stat-label">体重变化</div>
            <div class="stat-value" :class="weightChange > 0 ? 'danger' : 'normal'">
              {{ weightChange > 0 ? '+' : '' }}{{ weightChange }} <span class="unit">kg</span>
            </div>
          </div>
          <div class="stat-item">
            <div class="stat-label">BMI指数</div>
            <div class="stat-value">
              <el-tag :type="bmiStatus.type" size="large">
                {{ bmiValue }} - {{ bmiStatus.text }}
              </el-tag>
            </div>
          </div>
        </div>

        <!-- 图表区域 -->
        <div ref="chartRef" class="chart-container" v-loading="loading"></div>

        <!-- 参考信息 -->
        <div class="reference-info">
          <el-alert type="info" :closable="false">
            <template #title>
              <div class="reference-content">
                <span class="reference-title">BMI参考范围：</span>
                <el-tag type="warning" size="small" effect="plain">偏瘦: <18.5</el-tag>
                <el-tag type="success" size="small" effect="plain">正常: 18.5-24</el-tag>
                                <el-tag type="warning" size="small" effect="plain">超重: 24-28</el-tag>
                <el-tag type="danger" size="small" effect="plain">肥胖: ≥28</el-tag>
              </div>
            </template>
          </el-alert>
        </div>

        <!-- 数据列表 -->
        <div class="data-list" v-if="showDataList">
          <el-divider>详细数据</el-divider>
          <el-table :data="chartData" stripe max-height="300">
            <el-table-column prop="date" label="日期" width="180" />
            <el-table-column label="体重" width="120" align="center">
              <template #default="{ row }">
                <span class="weight-value">{{ row.weight }} kg</span>
              </template>
            </el-table-column>
            <el-table-column label="BMI" width="100" align="center">
              <template #default="{ row }">
                <span :class="getBMIClass(row.bmi)">{{ row.bmi }}</span>
              </template>
            </el-table-column>
            <el-table-column label="体脂率" width="100" align="center">
              <template #default="{ row }">
                {{ row.bodyFat || '-' }} %
              </template>
            </el-table-column>
            <el-table-column label="变化" width="100" align="center">
              <template #default="{ row }">
                <span :class="row.change > 0 ? 'change-up' : 'change-down'" v-if="row.change">
                  {{ row.change > 0 ? '+' : '' }}{{ row.change }} kg
                </span>
                <span v-else>-</span>
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

const timePeriod = ref('month')
const loading = ref(false)
const chartRef = ref(null)
const showDataList = ref(false)
let chartInstance = null

// 用户身高（实际应该从用户资料获取）
const userHeight = ref(170) // cm

// 模拟数据
const chartData = ref([
  {
    date: '2024-01-01',
    weight: 65.5,
    bmi: 22.7,
    bodyFat: 18.5,
    change: null,
    notes: ''
  },
  {
    date: '2024-01-03',
    weight: 65.3,
    bmi: 22.6,
    bodyFat: 18.3,
    change: -0.2,
    notes: '控制饮食'
  },
  {
    date: '2024-01-05',
    weight: 65.1,
    bmi: 22.5,
    bodyFat: 18.2,
    change: -0.2,
    notes: '运动1小时'
  },
  {
    date: '2024-01-07',
    weight: 65.0,
    bmi: 22.5,
    bodyFat: 18.0,
    change: -0.1,
    notes: ''
  },
  {
    date: '2024-01-09',
    weight: 64.8,
    bmi: 22.4,
    bodyFat: 17.9,
    change: -0.2,
    notes: '坚持运动'
  },
  {
    date: '2024-01-11',
    weight: 64.9,
    bmi: 22.4,
    bodyFat: 18.0,
    change: 0.1,
    notes: '周末聚餐'
  },
  {
    date: '2024-01-13',
    weight: 64.7,
    bmi: 22.4,
    bodyFat: 17.8,
    change: -0.2,
    notes: '恢复运动'
  }
])

// 当前体重
const currentWeight = computed(() => {
  if (chartData.value.length === 0) return 0
  return chartData.value[chartData.value.length - 1].weight
})

// 平均体重
const avgWeight = computed(() => {
  if (chartData.value.length === 0) return 0
  const sum = chartData.value.reduce((acc, item) => acc + item.weight, 0)
  return (sum / chartData.value.length).toFixed(1)
})

// 体重变化
const weightChange = computed(() => {
  if (chartData.value.length < 2) return 0
  const first = chartData.value[0].weight
  const last = chartData.value[chartData.value.length - 1].weight
  return (last - first).toFixed(1)
})

// BMI值
const bmiValue = computed(() => {
  if (!currentWeight.value || !userHeight.value) return 0
  const heightInMeters = userHeight.value / 100
  return (currentWeight.value / (heightInMeters * heightInMeters)).toFixed(1)
})

// BMI状态
const bmiStatus = computed(() => {
  const value = parseFloat(bmiValue.value)
  if (value < 18.5) {
    return { type: 'warning', text: '偏瘦' }
  } else if (value < 24) {
    return { type: 'success', text: '正常' }
  } else if (value < 28) {
    return { type: 'warning', text: '超重' }
  } else {
    return { type: 'danger', text: '肥胖' }
  }
})

// 获取BMI样式类
const getBMIClass = (bmi) => {
  if (bmi < 18.5) return 'bmi-low'
  if (bmi < 24) return 'bmi-normal'
  if (bmi < 28) return 'bmi-high'
  return 'bmi-danger'
}

// 初始化图表
const initChart = () => {
  if (!chartRef.value) return

  if (chartInstance) {
    chartInstance.dispose()
  }

  chartInstance = echarts.init(chartRef.value)

  const dates = chartData.value.map(item => item.date)
  const weights = chartData.value.map(item => item.weight)
  const bmis = chartData.value.map(item => item.bmi)
  const bodyFats = chartData.value.map(item => item.bodyFat || null)

  // 计算目标体重（正常BMI范围的中值）
  const heightInMeters = userHeight.value / 100
  const targetBMI = 21.5 // 正常范围中值
  const targetWeight = (targetBMI * heightInMeters * heightInMeters).toFixed(1)

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross'
      },
      formatter: (params) => {
        let result = `<div style="font-weight: bold; margin-bottom: 5px;">${params[0].axisValue}</div>`
        params.forEach(param => {
          if (param.value !== null) {
            result += `
              <div style="display: flex; align-items: center; margin: 3px 0;">
                <span style="display: inline-block; width: 10px; height: 10px; border-radius: 50%; background-color: ${param.color}; margin-right: 5px;"></span>
                <span>${param.seriesName}: <strong>${param.value}</strong> ${param.seriesName === 'BMI' ? '' : param.seriesName === '体脂率' ? '%' : 'kg'}</span>
              </div>
            `
          }
        })
        return result
      }
    },
    legend: {
      data: ['体重', 'BMI', '体脂率', '目标体重'],
      top: 10
    },
    grid: {
      left: '3%',
      right: '8%',
      bottom: '3%',
      top: '15%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: dates,
      axisLabel: {
        rotate: 45
      }
    },
    yAxis: [
      {
        type: 'value',
        name: '体重 (kg)',
        position: 'left',
        axisLabel: {
          formatter: '{value} kg'
        }
      },
      {
        type: 'value',
        name: 'BMI / 体脂率',
        position: 'right',
        axisLabel: {
          formatter: '{value}'
        }
      }
    ],
    series: [
      {
        name: '体重',
        type: 'line',
        smooth: true,
        data: weights,
        yAxisIndex: 0,
        itemStyle: {
          color: '#409EFF'
        },
        lineStyle: {
          width: 3
        },
        symbol: 'circle',
        symbolSize: 8,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
          ])
        }
      },
      {
        name: 'BMI',
        type: 'line',
        smooth: true,
        data: bmis,
        yAxisIndex: 1,
        itemStyle: {
          color: '#67C23A'
        },
        lineStyle: {
          width: 2,
          type: 'dashed'
        },
        symbol: 'circle',
        symbolSize: 6
      },
      {
        name: '体脂率',
        type: 'line',
        smooth: true,
        data: bodyFats,
        yAxisIndex: 1,
        itemStyle: {
          color: '#E6A23C'
        },
        lineStyle: {
          width: 2,
          type: 'dashed'
        },
        symbol: 'circle',
        symbolSize: 6
      },
      {
        name: '目标体重',
        type: 'line',
        data: dates.map(() => targetWeight),
        yAxisIndex: 0,
        lineStyle: {
          type: 'dotted',
          color: '#F56C6C',
          width: 2
        },
        symbol: 'none',
        silent: true,
        markLine: {
          silent: true,
          label: {
            formatter: '目标: {c} kg'
          }
        }
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
.weight-chart {
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

.weight-value {
  font-weight: 500;
  color: #409EFF;
}

.bmi-low {
  color: #E6A23C;
  font-weight: 500;
}

.bmi-normal {
  color: #67C23A;
  font-weight: 500;
}

.bmi-high {
  color: #E6A23C;
  font-weight: 500;
}

.bmi-danger {
  color: #F56C6C;
  font-weight: 500;
}

.change-up {
  color: #F56C6C;
}

.change-down {
  color: #67C23A;
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