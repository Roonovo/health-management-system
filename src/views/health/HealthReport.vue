<template>
  <div class="health-report">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>健康报告</span>
          <div class="header-actions">
            <el-button @click="generateReport">
              <el-icon><Refresh /></el-icon>
              生成报告
            </el-button>
            <el-button type="primary" @click="exportReport">
              <el-icon><Download /></el-icon>
              导出报告
            </el-button>
          </div>
        </div>
      </template>

      <div v-if="!report.id" class="empty-state">
        <el-empty description="暂无健康报告">
          <el-button type="primary" @click="generateReport">
            生成报告
          </el-button>
        </el-empty>
      </div>

      <div v-else class="report-content">
        <!-- 报告头部 -->
        <div class="report-header">
          <h1>健康评估报告</h1>
          <div class="report-info">
            <p><strong>报告编号：</strong>{{ report.reportNo }}</p>
            <p><strong>生成时间：</strong>{{ report.generatedAt }}</p>
            <p><strong>评估周期：</strong>{{ report.periodStart }} 至 {{ report.periodEnd }}</p>
          </div>
        </div>

        <!-- 健康评分 -->
        <el-card class="score-card">
          <h2>综合健康评分</h2>
          <div class="score-content">
            <div class="score-circle">
              <el-progress
                type="circle"
                :percentage="report.totalScore"
                :width="180"
                :stroke-width="15"
                :color="getScoreColor(report.totalScore)"
              >
                <template #default="{ percentage }">
                  <span class="score-value">{{ percentage }}</span>
                  <span class="score-label">分</span>
                </template>
              </el-progress>
            </div>
            <div class="score-level">
              <el-tag :type="getScoreLevelType(report.totalScore)" size="large">
                {{ getScoreLevel(report.totalScore) }}
              </el-tag>
              <p class="score-desc">{{ getScoreLevelDesc(report.totalScore) }}</p>
            </div>
          </div>
        </el-card>

        <!-- 各项指标评分 -->
        <el-card style="margin-top: 20px">
          <h2>各项指标评分</h2>
          <el-row :gutter="20" style="margin-top: 20px">
            <el-col
              v-for="indicator in report.indicators"
              :key="indicator.name"
              :xs="24"
              :sm="12"
              :md="6"
            >
              <div class="indicator-item">
                <div class="indicator-header">
                  <el-icon :size="24" :color="indicator.color">
                    <component :is="indicator.icon" />
                  </el-icon>
                  <span class="indicator-name">{{ indicator.name }}</span>
                </div>
                <el-progress
                  :percentage="indicator.score"
                  :color="indicator.color"
                  :stroke-width="10"
                />
                <div class="indicator-status">
                  <el-tag :type="getStatusType(indicator.status)" size="small">
                    {{ indicator.statusLabel }}
                  </el-tag>
                </div>
              </div>
            </el-col>
          </el-row>
        </el-card>

        <!-- 健康趋势分析 -->
        <el-card style="margin-top: 20px">
          <h2>健康趋势分析</h2>
          <div id="trend-chart" style="width: 100%; height: 400px; margin-top: 20px"></div>
        </el-card>

        <!-- 异常指标 -->
        <el-card v-if="report.abnormalIndicators && report.abnormalIndicators.length > 0" style="margin-top: 20px">
          <h2>异常指标提醒</h2>
          <el-alert
            v-for="item in report.abnormalIndicators"
            :key="item.name"
            :title="item.name"
            :description="item.description"
            :type="item.level === 'danger' ? 'error' : 'warning'"
            show-icon
            style="margin-top: 15px"
          />
        </el-card>

        <!-- 健康建议 -->
        <el-card style="margin-top: 20px">
          <h2>健康建议</h2>
          <div class="suggestions">
            <div
              v-for="(suggestion, index) in report.suggestions"
              :key="index"
              class="suggestion-item"
            >
              <div class="suggestion-icon">
                <el-icon :size="20" color="#409EFF">
                  <Check />
                </el-icon>
              </div>
              <div class="suggestion-content">
                <h4>{{ suggestion.title }}</h4>
                <p>{{ suggestion.content }}</p>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 报告总结 -->
        <el-card style="margin-top: 20px">
          <h2>报告总结</h2>
          <div class="summary">
            <p>{{ report.summary }}</p>
          </div>
        </el-card>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Refresh,
  Download,
  Check,
  Odometer,
  TrendCharts,
  Aim,
  Monitor
} from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getHealthReport, generateHealthReport, exportHealthReport } from '@/api/health'

const report = ref({
  id: null,
  reportNo: '',
  generatedAt: '',
  periodStart: '',
  periodEnd: '',
  totalScore: 0,
  indicators: [],
  abnormalIndicators: [],
  suggestions: [],
  summary: ''
})

let trendChart = null

// 获取评分颜色
const getScoreColor = (score) => {
  if (score >= 80) return '#67C23A'
  if (score >= 60) return '#E6A23C'
  return '#F56C6C'
}

// 获取评分等级
const getScoreLevel = (score) => {
  if (score >= 90) return '优秀'
  if (score >= 80) return '良好'
  if (score >= 60) return '一般'
  return '需改善'
}

// 获取评分等级类型
const getScoreLevelType = (score) => {
  if (score >= 80) return 'success'
  if (score >= 60) return 'warning'
  return 'danger'
}

// 获取评分等级描述
const getScoreLevelDesc = (score) => {
  if (score >= 90) return '您的健康状况非常好，请继续保持'
  if (score >= 80) return '您的健康状况良好，注意保持良好习惯'
  if (score >= 60) return '您的健康状况一般，需要注意改善'
  return '您的健康状况需要改善，建议咨询医生'
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

// 初始化趋势图表
const initTrendChart = () => {
  const chartDom = document.getElementById('trend-chart')
  if (!chartDom) return

  trendChart = echarts.init(chartDom)

  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['综合评分', '血压评分', '血糖评分', '体重评分']
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
      data: ['第1周', '第2周', '第3周', '第4周']
    },
    yAxis: {
      type: 'value',
      max: 100
    },
    series: [
      {
        name: '综合评分',
        type: 'line',
        data: [75, 78, 82, 85],
        smooth: true,
        itemStyle: { color: '#409EFF' }
      },
      {
        name: '血压评分',
        type: 'line',
        data: [70, 75, 80, 82],
        smooth: true,
        itemStyle: { color: '#67C23A' }
      },
      {
        name: '血糖评分',
        type: 'line',
        data: [80, 82, 85, 88],
        smooth: true,
        itemStyle: { color: '#E6A23C' }
      },
      {
        name: '体重评分',
        type: 'line',
        data: [75, 77, 78, 80],
        smooth: true,
        itemStyle: { color: '#F56C6C' }
      }
    ]
  }

  trendChart.setOption(option)
}

// 加载报告
const loadReport = async () => {
  try {
    const res = await getHealthReport()
    if (res.success && res.data) {
      report.value = res.data

      nextTick(() => {
        initTrendChart()
      })
    }
  } catch (error) {
        console.error('加载报告失败：', error)
  }
}

// 生成报告
const generateReport = async () => {
  try {
    ElMessage.info('正在生成报告，请稍候...')
    
    const res = await generateHealthReport()
    if (res.success) {
      ElMessage.success('报告生成成功')
      await loadReport()
    }
  } catch (error) {
    ElMessage.error('生成报告失败：' + error.message)
  }
}

// 导出报告
const exportReport = async () => {
  try {
    const res = await exportHealthReport(report.value.id)
    if (res.success) {
      // 创建下载链接
      const blob = new Blob([res.data], { type: 'application/pdf' })
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = `健康报告_${report.value.reportNo}.pdf`
      link.click()
      window.URL.revokeObjectURL(url)
      
      ElMessage.success('导出成功')
    }
  } catch (error) {
    ElMessage.error('导出失败：' + error.message)
  }
}

onMounted(() => {
  loadReport()

  window.addEventListener('resize', () => {
    trendChart?.resize()
  })
})
</script>

<style scoped>
.health-report {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.empty-state {
  padding: 60px 0;
  text-align: center;
}

.report-content {
  padding: 20px;
}

/* 报告头部 */
.report-header {
  text-align: center;
  padding: 30px 0;
  border-bottom: 2px solid #e4e7ed;
  margin-bottom: 30px;
}

.report-header h1 {
  font-size: 28px;
  color: #303133;
  margin: 0 0 20px;
}

.report-info {
  display: flex;
  justify-content: center;
  gap: 40px;
  color: #606266;
}

.report-info p {
  margin: 5px 0;
}

/* 评分卡片 */
.score-card h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #303133;
}

.score-content {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 60px;
  padding: 20px 0;
}

.score-circle {
  text-align: center;
}

.score-value {
  font-size: 48px;
  font-weight: bold;
  display: block;
}

.score-label {
  font-size: 16px;
  color: #909399;
}

.score-level {
  text-align: center;
}

.score-level .el-tag {
  font-size: 24px;
  padding: 15px 30px;
  margin-bottom: 15px;
}

.score-desc {
  font-size: 16px;
  color: #606266;
  margin: 0;
}

/* 指标项 */
.indicator-item {
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 8px;
  margin-bottom: 20px;
}

.indicator-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
}

.indicator-name {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.indicator-status {
  margin-top: 10px;
  text-align: center;
}

/* 健康建议 */
.suggestions {
  margin-top: 20px;
}

.suggestion-item {
  display: flex;
  gap: 15px;
  padding: 20px;
  background-color: #f0f9ff;
  border-left: 4px solid #409EFF;
  margin-bottom: 15px;
  border-radius: 4px;
}

.suggestion-icon {
  flex-shrink: 0;
  width: 40px;
  height: 40px;
  background-color: #409EFF;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.suggestion-content h4 {
  margin: 0 0 10px;
  color: #303133;
  font-size: 16px;
}

.suggestion-content p {
  margin: 0;
  color: #606266;
  line-height: 1.6;
}

/* 报告总结 */
.summary {
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 8px;
  margin-top: 20px;
}

.summary p {
  margin: 0;
  line-height: 1.8;
  color: #606266;
  font-size: 15px;
}

h2 {
  font-size: 20px;
  color: #303133;
  margin: 0 0 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid #e4e7ed;
}

@media print {
  .card-header,
  .header-actions {
    display: none;
  }
}
</style>