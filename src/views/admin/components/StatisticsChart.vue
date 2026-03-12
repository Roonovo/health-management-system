<template>
  <div class="statistics-chart">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ title }}</span>
          <el-radio-group v-model="chartType" size="small" @change="handleTypeChange">
            <el-radio-button label="line">折线图</el-radio-button>
            <el-radio-button label="bar">柱状图</el-radio-button>
            <el-radio-button label="area">面积图</el-radio-button>
          </el-radio-group>
        </div>
      </template>

      <div :id="chartId" :style="{ width: '100%', height: height }"></div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch } from 'vue'
import * as echarts from 'echarts'

const props = defineProps({
  title: {
    type: String,
    default: '数据统计'
  },
  chartId: {
    type: String,
    required: true
  },
  height: {
    type: String,
    default: '400px'
  },
  data: {
    type: Object,
    required: true
  },
  colors: {
    type: Array,
    default: () => ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C']
  }
})

const chartType = ref('line')
let chart = null

// 初始化图表
const initChart = () => {
  const chartDom = document.getElementById(props.chartId)
  if (!chartDom) return

  chart = echarts.init(chartDom)
  updateChart()
}

// 更新图表
const updateChart = () => {
  if (!chart) return

  const { dates, series } = props.data

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross'
      }
    },
    legend: {
      data: series.map(s => s.name),
      bottom: 0
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '10%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: chartType.value === 'bar',
      data: dates
    },
    yAxis: {
      type: 'value'
    },
    series: series.map((s, index) => ({
      name: s.name,
      type: chartType.value === 'area' ? 'line' : chartType.value,
      data: s.data,
      smooth: true,
      itemStyle: { 
        color: props.colors[index % props.colors.length] 
      },
      areaStyle: chartType.value === 'area' ? {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { 
            offset: 0, 
            color: `${props.colors[index % props.colors.length]}80` 
          },
          { 
            offset: 1, 
            color: `${props.colors[index % props.colors.length]}10` 
          }
        ])
      } : undefined
    }))
  }

  chart.setOption(option)
}

// 处理图表类型变化
const handleTypeChange = () => {
  updateChart()
}

// 监听数据变化
watch(() => props.data, () => {
  updateChart()
}, { deep: true })

// 窗口大小变化
const handleResize = () => {
  chart?.resize()
}

onMounted(() => {
  initChart()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  chart?.dispose()
})
</script>

<style scoped>
.statistics-chart {
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>