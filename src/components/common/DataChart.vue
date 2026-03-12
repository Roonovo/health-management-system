<template>
  <div :id="chartId" :style="{ width: '100%', height: height }"></div>
</template>

<script setup>
import { onMounted, watch, nextTick } from 'vue'
import * as echarts from 'echarts'

const props = defineProps({
  data: {
    type: Array,
    default: () => []
  },
  chartType: {
    type: String,
    default: 'line'
  },
  title: {
    type: String,
    default: ''
  },
  height: {
    type: String,
    default: '400px'
  }
})

const chartId = 'chart-' + Math.random().toString(36).substr(2, 9)
let chartInstance = null

const initChart = () => {
  nextTick(() => {
    const chartDom = document.getElementById(chartId)
    if (!chartDom) return

    chartInstance = echarts.init(chartDom)

    const option = {
      title: {
        text: props.title,
        left: 'center'
      },
      tooltip: {
        trigger: 'axis'
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
        data: props.data.map(item => item.measureTime?.substring(5, 10) || '')
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          name: props.title,
          type: props.chartType,
          data: props.data.map(item => item.value || item.systolic),
          smooth: true,
          lineStyle: {
            width: 2
          },
          itemStyle: {
            color: '#409EFF'
          }
        }
      ]
    }

    chartInstance.setOption(option)
  })
}

watch(() => props.data, () => {
  if (chartInstance) {
    initChart()
  }
}, { deep: true })

onMounted(() => {
  initChart()
  window.addEventListener('resize', () => {
    chartInstance?.resize()
  })
})
</script>