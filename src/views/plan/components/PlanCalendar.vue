<template>
  <div class="plan-calendar">
    <el-card>
      <template #header>
        <div class="card-header">
          <el-icon><Calendar /></el-icon>
          <span>用药日历</span>
        </div>
      </template>

      <div class="calendar-wrapper">
        <el-calendar v-model="currentDate">
          <template #header="{ date }">
            <div class="calendar-header">
              <el-button-group>
                <el-button size="small" @click="selectDate('prev-month')">
                  <el-icon><ArrowLeft /></el-icon>
                  上月
                </el-button>
                <el-button size="small" @click="selectDate('today')">
                  今天
                </el-button>
                <el-button size="small" @click="selectDate('next-month')">
                  下月
                  <el-icon><ArrowRight /></el-icon>
                </el-button>
              </el-button-group>
              <span class="current-month">{{ formatMonth(date) }}</span>
            </div>
          </template>

          <template #date-cell="{ data }">
            <div class="calendar-day" :class="getDayClass(data.day)">
              <div class="day-number">{{ data.day.split('-').slice(-1)[0] }}</div>
              <div class="day-content">
                <div class="medication-list" v-if="getDayMedications(data.day).length > 0">
                  <div
                    v-for="(med, index) in getDayMedications(data.day).slice(0, 3)"
                    :key="index"
                    class="med-item"
                    :class="getMedStatusClass(data.day, med)"
                    @click="handleMedClick(data.day, med)"
                  >
                    <el-icon :size="12">
                      <component :is="getMedIcon(med.status)" />
                    </el-icon>
                    <span class="med-name">{{ med.name }}</span>
                  </div>
                  <div class="more-meds" v-if="getDayMedications(data.day).length > 3">
                    +{{ getDayMedications(data.day).length - 3 }}
                  </div>
                </div>
                <div class="no-medication" v-else>
                  <span>-</span>
                </div>
              </div>
            </div>
          </template>
        </el-calendar>

        <!-- 图例 -->
        <div class="calendar-legend">
          <div class="legend-item">
            <span class="legend-dot completed"></span>
            <span>已服用</span>
          </div>
          <div class="legend-item">
            <span class="legend-dot pending"></span>
            <span>待服用</span>
          </div>
          <div class="legend-item">
            <span class="legend-dot missed"></span>
            <span>已漏服</span>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 日详情对话框 -->
    <el-dialog
      v-model="showDayDetail"
      :title="`${selectedDate} 用药详情`"
      width="600px"
    >
      <div class="day-detail">
        <el-timeline>
          <el-timeline-item
            v-for="(med, index) in selectedDayMeds"
            :key="index"
            :timestamp="med.time"
            :type="getMedTimelineType(med.status)"
            :icon="getMedIcon(med.status)"
          >
            <el-card>
              <div class="timeline-content">
                <div class="med-header">
                  <span class="med-name">{{ med.name }}</span>
                  <el-tag :type="getMedTagType(med.status)" size="small">
                    {{ getMedStatusText(med.status) }}
                  </el-tag>
                </div>
                <div class="med-info">
                  <p>剂量：{{ med.dosage }} {{ med.unit }}</p>
                  <p>用法：{{ med.usage }}</p>
                </div>
                <div class="med-actions" v-if="med.status === 'pending'">
                  <el-button type="primary" size="small" @click="markAsTaken(med)">
                    标记已服用
                  </el-button>
                </div>
              </div>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Calendar,
  ArrowLeft,
  ArrowRight,
  Check,
  Clock,
  Close
} from '@element-plus/icons-vue'
import { format, parseISO, startOfMonth, endOfMonth, addMonths, subMonths } from 'date-fns'

const currentDate = ref(new Date())
const showDayDetail = ref(false)
const selectedDate = ref('')
const selected DayMeds = ref([])

// 模拟用药数据
const medicationData = ref({
  '2024-01-15': [
    { id: 1, name: '降压药', time: '08:00', dosage: '1', unit: '片', usage: '饭后', status: 'completed' },
    { id: 2, name: '降糖药', time: '12:00', dosage: '1', unit: '片', usage: '饭后', status: 'completed' },
    { id: 3, name: '阿司匹林', time: '20:00', dosage: '1', unit: '片', usage: '睡前', status: 'pending' }
  ],
  '2024-01-16': [
    { id: 4, name: '降压药', time: '08:00', dosage: '1', unit: '片', usage: '饭后', status: 'missed' },
        { id: 5, name: '降糖药', time: '12:00', dosage: '1', unit: '片', usage: '饭后', status: 'pending' }
  ],
  '2024-01-17': [
    { id: 6, name: '降压药', time: '08:00', dosage: '1', unit: '片', usage: '饭后', status: 'pending' },
    { id: 7, name: '降糖药', time: '12:00', dosage: '1', unit: '片', usage: '饭后', status: 'pending' },
    { id: 8, name: '阿司匹林', time: '20:00', dosage: '1', unit: '片', usage: '睡前', status: 'pending' }
  ]
})

// 获取某天的用药记录
const getDayMedications = (day) => {
  return medicationData.value[day] || []
}

// 获取日期样式类
const getDayClass = (day) => {
  const meds = getDayMedications(day)
  if (meds.length === 0) return ''
  
  const hasCompleted = meds.some(m => m.status === 'completed')
  const hasMissed = meds.some(m => m.status === 'missed')
  const hasPending = meds.some(m => m.status === 'pending')
  
  if (hasMissed) return 'has-missed'
  if (hasPending) return 'has-pending'
  if (hasCompleted) return 'has-completed'
  
  return ''
}

// 获取用药状态样式类
const getMedStatusClass = (day, med) => {
  return `status-${med.status}`
}

// 获取用药图标
const getMedIcon = (status) => {
  const iconMap = {
    completed: Check,
    pending: Clock,
    missed: Close
  }
  return iconMap[status] || Clock
}

// 格式化月份
const formatMonth = (date) => {
  return format(date, 'yyyy年 MM月')
}

// 选择日期
const selectDate = (type) => {
  if (type === 'prev-month') {
    currentDate.value = subMonths(currentDate.value, 1)
  } else if (type === 'next-month') {
    currentDate.value = addMonths(currentDate.value, 1)
  } else if (type === 'today') {
    currentDate.value = new Date()
  }
}

// 处理用药点击
const handleMedClick = (day, med) => {
  selectedDate.value = day
  selectedDayMeds.value = getDayMedications(day)
  showDayDetail.value = true
}

// 获取时间线类型
const getMedTimelineType = (status) => {
  const typeMap = {
    completed: 'success',
    pending: 'primary',
    missed: 'danger'
  }
  return typeMap[status] || 'primary'
}

// 获取标签类型
const getMedTagType = (status) => {
  const typeMap = {
    completed: 'success',
    pending: 'warning',
    missed: 'danger'
  }
  return typeMap[status] || 'info'
}

// 获取状态文本
const getMedStatusText = (status) => {
  const textMap = {
    completed: '已服用',
    pending: '待服用',
    missed: '已漏服'
  }
  return textMap[status] || status
}

// 标记为已服用
const markAsTaken = (med) => {
  med.status = 'completed'
  ElMessage.success('已标记为已服用')
}
</script>

<style scoped>
.plan-calendar {
  height: 100%;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 500;
}

.calendar-wrapper {
  width: 100%;
}

.calendar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.current-month {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.calendar-day {
  height: 100%;
  min-height: 80px;
  padding: 4px;
}

.day-number {
  font-size: 14px;
  font-weight: 500;
  color: #606266;
  margin-bottom: 4px;
}

.calendar-day.has-completed {
  background: #f0f9ff;
}

.calendar-day.has-pending {
  background: #fff7e6;
}

.calendar-day.has-missed {
  background: #ffebee;
}

.day-content {
  font-size: 12px;
}

.medication-list {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.med-item {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 2px 4px;
  border-radius: 3px;
  cursor: pointer;
  transition: all 0.2s;
}

.med-item:hover {
  transform: translateX(2px);
}

.med-item.status-completed {
  background: #67C23A;
  color: white;
}

.med-item.status-pending {
  background: #E6A23C;
  color: white;
}

.med-item.status-missed {
  background: #F56C6C;
  color: white;
}

.med-name {
  font-size: 11px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.more-meds {
  text-align: center;
  color: #909399;
  font-size: 11px;
  margin-top: 2px;
}

.no-medication {
  text-align: center;
  color: #DCDFE6;
  padding: 8px 0;
}

.calendar-legend {
  display: flex;
  justify-content: center;
  gap: 24px;
  margin-top: 16px;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 6px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #606266;
}

.legend-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
}

.legend-dot.completed {
  background: #67C23A;
}

.legend-dot.pending {
  background: #E6A23C;
}

.legend-dot.missed {
  background: #F56C6C;
}

.day-detail {
  max-height: 500px;
  overflow-y: auto;
}

.timeline-content {
  padding: 8px;
}

.med-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.med-header .med-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.med-info p {
  margin: 4px 0;
  color: #606266;
  font-size: 14px;
}

.med-actions {
  margin-top: 8px;
}

:deep(.el-calendar-table .el-calendar-day) {
  height: 100px;
  padding: 4px;
}

:deep(.el-calendar-table td) {
  border: 1px solid #EBEEF5;
}

:deep(.el-calendar-table td.is-today) {
  background: #ecf5ff;
}

@media (max-width: 768px) {
  .calendar-header {
    flex-direction: column;
    gap: 12px;
    padding: 0 10px;
  }

  .calendar-day {
    min-height: 60px;
  }

  .med-name {
    font-size: 10px;
  }

  .calendar-legend {
    flex-direction: column;
    gap: 8px;
  }
}
</style>