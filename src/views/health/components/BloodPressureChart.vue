<template>
  <div class="blood-pressure-chart">
    <el-card>
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-icon><TrendCharts /></el-icon>
            <span>血压趋势</span>
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
            <div class="stat-label">平均收缩压</div>
            <div class="stat-value" :class="getStatusClass(avgSystolic)">
              {{ avgSystolic }} <span class="unit">mmHg</span>
            </div>
          </div>
          <div class="stat-item">
            <div class="stat-label">平均舒张压</div>
            <div class="stat-value" :class="getStatusClass(avgDiastolic, 'diastolic')">
              {{ avgDiastolic }} <span class="unit">mmHg</span>
            </div>
          </div>
          <div class="stat-item">
            <div class="stat-label">测量次数</div>
            <div class="stat-value">{{ totalRecords }} <span class="unit">次</span></div>
          </div>
          <div class="stat-item">
            <div class="stat-label">健康评估</div>
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
                <el-tag type="success" size="small" effect="plain">正常: 90-140/60-90 mmHg</el-tag>
                <el-tag type="warning" size="small" effect="plain">偏高: 140-160/90-100 mmHg</el-tag>
                <el-tag type="danger" size="small" effect="plain">高血压: >160/>100 mmHg</el-tag>
              </div>
            </template>
          </el-alert>
        </div>

        <!-- 数据列表 -->
        <div class="data-list" v-if="showDataList">
          <el-divider>详细数据</el-divider>
          <el-table :data="chartData" stripe max-height="300">
            <el-table-column prop="date" label="日期" width="180" />
            <el-table-column label="收缩压" width="120" align="center">
              <template #default="{ row }">
                <span :class="getStatusClass(row.systolic)">
                  {{ row.systolic }} mmHg
                </span>
              </template>
            </el-table-column>
            <el-table-column label="舒张压" width="120" align="center">
              <template #default="{ row }">
                <span :class="getStatusClass(row.diastolic, 'diastolic')">
                  {{ row.diastolic }} mmHg
                </span>
              </template>
            </el-table-column>
            <el-table-column label="心率" width="100" align="center">
              <template #default="{ row }">
                {{ row.pulse || '-' }} bpm
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

const timePeriod = ref('month')
const loading = ref(false)
const chartRef = ref(null)
const showDataList = ref(false)
let chartInstance = null

// 模拟数据
const chartData = ref([
  {
    date: '2024-01-01',
    systolic: 120,
    diastolic: 80,
    pulse: 72,
    notes: '早晨测量'
  },
  {
    date: '2024-01-02',
    systolic: 125,
    diastolic: 82,
    pulse: 75,
    notes: '饭后测量'
  },
  {
    date: '2024-01-03',
    systolic: 118,
    diastolic: 78,
    pulse: 70,
    notes: '运动后'
  },
  {
    date: '2024-01-04',
    systolic: 122,
    diastolic: 81,
    pulse: 73,
    notes: ''
  },
  {
    date: '2024-01-05',
    systolic: 119,
    diastolic: 79,
    pulse: 71,
    notes: '睡前测量'
  },
  {
    date: '2024-01-06',
    systolic: 121,
    diastolic: 80,
    pulse: 72,
    notes: ''
  },
  {
    date: '2024-01-07',
    systolic: 123,
    diastolic: 82,
    pulse: 74,
    notes: '早晨测量'
  }
])

// 计算平均值
const avgSystolic = computed(() => {
  if (chartData.value.length === 0) return 0
  const sum = chartData.value.reduce((acc, item) => acc + item.systolic, 0)
  return Math.round(sum / chartData.value.length)
})

const avgDiastolic = computed(() => {
  if (chartData.value.length === 0) return 0
  const sum = chartData.value.reduce((acc, item) => acc + item.diastolic, 0)
  return Math.round(sum / chartData.value.length)
})

const totalRecords = computed(() => chartData.value.length)

// 健康状态评估
const healthStatus = computed(() => {
  const sys = avgSystolic.value
  const dia = avgDiastolic.value

  if (sys <= 140 && dia <= 90) {
    return { type: 'success', text: '正常' }
  } else if (sys <= 160 && dia <= 100) {
    return { type: 'warning', text: '偏高' }
  } else {
    return { type: 'danger', text: '高血压' }
  }
})

// 获取状态样式类
const getStatusClass = (value, type = 'systolic') => {
  if (type === 'systolic') {
    if (value <= 140) return 'normal'
    if (value <= 160) return 'warning'
    return 'danger'
  } else {
    if (value <= 90) return 'normal'
    if (value <= 100) return 'warning'
    return 'danger'
  }
}

// 获取记录状态
const getRecordStatus = (row) => {
  if (row.systolic <= 140 && row.diastolic <= 90) {
    return { type: 'success', text: '正常' }
  } else if (row.systolic <= 160 && row.diastolic <= 100) {
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

  const dates = chartData.value.map(item => item.date)
  const systolicData = chartData.value.map(item => item.systolic)
  const diastolicData = chartData.value.map(item => item.diastolic)

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross',
        label: {
          backgroundColor: '#6a7985'
        }
      },
      formatter: (params) => {
        let result = `<div style="font-weight: bold; margin-bottom: 5px;">${params[0].axisValue}</div>`
        params.forEach(param => {
          const status = param.seriesName === '收缩压' 
            ? getStatusClass(param.value)
            : getStatusClass(param.value, 'diastolic')
          const color = status === 'normal' ? '#67C23A' : status === 'warning' ? '#E6A23C' : '#F56C6C'
          result += `
            <div style="display: flex; align-items: center; margin: 3px 0;">
              <span style="display: inline-block; width: 10px; height: 10px; border-radius: 50%; background-color: ${param.color}; margin-right: 5px;"></span>
              <span>${param.seriesName}: <strong style="color: ${color}">${param.value}</strong> mmHg</span>
            </div>
          `
        })
        return result
      }
    },
    legend: {
      data: ['收缩压', '舒张压', '正常范围(上限)', '正常范围(下限)'],
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
      data: dates,
      axisLabel: {
        rotate: 45
      }
    },
    yAxis: {
      type: 'value',
      name: 'mmHg',
      min: 50,
      max: 180,
      splitLine: {
        lineStyle: {
          type: 'dashed'
        }
      }
    },
    series: [
      {
        name: '收缩压',
        type: 'line',
        smooth: true,
        data: systolicData,
        itemStyle: {
          color: '#409EFF'
        },
        lineStyle: {
          width: 3
        },
        symbol: 'circle',
        symbolSize: 8,
        markLine: {
          silent: true,
          lineStyle: {
            color: '#E6A23C',
            type: 'dashed'
          },
          data: [
            { yAxis: 140, name: '收缩压警戒线' }
          ],
          label: {
            formatter: '警戒线: {c} mmHg'
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
        name: '舒张压',
        type: 'line',
        smooth: true,
        data: diastolicData,
        itemStyle: {
          color: '#67C23A'
        },
        lineStyle: {
          width: 3
        },
        symbol: 'circle',
        symbolSize: 8,
        markLine: {
          silent: true,
          lineStyle: {
            color: '#E6A23C',
            type: 'dashed'
          },
          data: [
            { yAxis: 90, name: '舒张压警戒线' }
          ],
          label: {
            formatter: '警戒线: {c} mmHg'
          }
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(103, 194, 58, 0.3)' },
            { offset: 1, color: 'rgba(103, 194, 58, 0.05)' }
          ])
        }
      },
      {
        name: '正常范围(上限)',
        type: 'line',
        data: dates.map(() => 140),
        lineStyle: {
          type: 'dotted',
          color: '#909399',
          width: 1
        },
        symbol: 'none',
        silent: true
      },
      {
        name: '正常范围(下限)',
        type: 'line',
        data: dates.map(() => 90),
        lineStyle: {
          type: 'dotted',
          color: '#909399',
          width: 1
        },
        symbol: 'none',
        silent: true
      }
    ]
  }

  chartInstance.setOption(option)

  // 响应式调整
  window.addEventListener('resize', () => {
    chartInstance?.resize()
  })
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // 这里应该根据 timePeriod 加载不同时间段的数据
    ElMessage.success('数据加载成功')
    
    await nextTick()
    initChart()
  } catch (error) {
    ElMessage.error('数据加载失败：' + error.message)
  } finally {
    loading.value = false
  }
}

// 监听时间周期变化
watch(timePeriod, () => {
  loadData()
})

onMounted(async () => {
  await nextTick()
  initChart()
})
</script>

<style scoped>
.blood-pressure-chart {
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