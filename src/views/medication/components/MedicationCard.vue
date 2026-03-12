<template>
  <div class="medication-card">
    <el-card :class="['med-card', statusClass]" shadow="hover">
      <div class="card-header">
        <div class="med-info">
          <div class="med-name">
            <el-icon :size="20" :color="iconColor">
              <component :is="getTypeIcon(medication.type)" />
            </el-icon>
            <span class="name-text">{{ medication.name }}</span>
          </div>
          <el-tag :type="getStatusType(medication.status)" size="small">
            {{ getStatusText(medication.status) }}
          </el-tag>
        </div>
        
        <div class="med-actions">
          <el-dropdown trigger="click" @command="handleCommand">
            <el-button type="primary" text circle>
              <el-icon><MoreFilled /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="take">
                  <el-icon><Check /></el-icon>
                  标记已服用
                </el-dropdown-item>
                <el-dropdown-item command="edit">
                  <el-icon><Edit /></el-icon>
                  编辑
                </el-dropdown-item>
                <el-dropdown-item command="history">
                  <el-icon><Document /></el-icon>
                  服用记录
                </el-dropdown-item>
                <el-dropdown-item command="delete" divided>
                  <el-icon><Delete /></el-icon>
                  删除
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>

      <div class="card-content">
        <div class="dosage-info">
          <div class="info-item">
            <span class="label">剂量：</span>
            <span class="value">{{ medication.dosage }} {{ medication.unit }}</span>
          </div>
          <div class="info-item">
            <span class="label">频次：</span>
            <span class="value">{{ getFrequencyText(medication.frequency) }}</span>
          </div>
          <div class="info-item">
            <span class="label">用法：</span>
            <span class="value">{{ getUsageText(medication.usage) }}</span>
          </div>
        </div>

        <div class="time-info" v-if="medication.times && medication.times.length > 0">
          <div class="time-label">服药时间：</div>
          <div class="time-list">
            <el-tag
              v-for="(time, index) in medication.times"
              :key="index"
              :type="isTimePassed(time) ? 'info' : 'primary'"
              size="small"
              effect="plain"
            >
              {{ time }}
            </el-tag>
          </div>
        </div>

        <div class="duration-info">
          <el-progress
            :percentage="getDurationPercentage()"
            :color="progressColor"
            :stroke-width="8"
          >
            <template #default="{ percentage }">
              <span class="progress-text">{{ percentage }}%</span>
            </template>
          </el-progress>
          <div class="duration-text">
            <span>开始：{{ formatDate(medication.startDate) }}</span>
            <span>结束：{{ formatDate(medication.endDate) }}</span>
          </div>
        </div>

        <div class="notes" v-if="medication.notes">
          <el-icon><Warning /></el-icon>
          <span>{{ medication.notes }}</span>
        </div>
      </div>

      <div class="card-footer">
        <div class="stock-info">
          <el-icon :color="getStockColor()"><Box /></el-icon>
          <span :class="['stock-text', getStockClass()]">
            剩余 {{ medication.remainingQuantity }} {{ medication.unit }}
          </span>
        </div>
        
        <el-button
          v-if="medication.status === 'active'"
          type="primary"
          size="small"
          @click="handleTake"
        >
          <el-icon><Check /></el-icon>
          已服用
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  MoreFilled,
  Check,
  Edit,
  Document,
  Delete,
  Warning,
  Box,
  Medicine,
  Coffee,
  Sunrise,
  Moon
} from '@element-plus/icons-vue'
import { format, parseISO, differenceInDays, isBefore } from 'date-fns'

const props = defineProps({
  medication: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['take', 'edit', 'delete', 'view-history'])

// 获取类型图标
const getTypeIcon = (type) => {
  const iconMap = {
    tablet: Medicine,
    capsule: Coffee,
    injection: Sunrise,
    liquid: Moon
  }
  return iconMap[type] || Medicine
}

// 图标颜色
const iconColor = computed(() => {
  const colorMap = {
    active: '#409EFF',
    paused: '#E6A23C',
    completed: '#67C23A',
    expired: '#909399'
  }
  return colorMap[props.medication.status] || '#409EFF'
})

// 状态样式类
const statusClass = computed(() => {
  return `status-${props.medication.status}`
})

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    active: 'success',
    paused: 'warning',
    completed: 'info',
    expired: 'danger'
  }
  return typeMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const textMap = {
    active: '进行中',
    paused: '已暂停',
    completed: '已完成',
    expired: '已过期'
  }
  return textMap[status] || status
}

// 获取频次文本
const getFrequencyText = (frequency) => {
  const textMap = {
    'once-daily': '每日一次',
    'twice-daily': '每日两次',
    'three-times-daily': '每日三次',
    'four-times-daily': '每日四次',
    'as-needed': '按需服用'
  }
  return textMap[frequency] || frequency
}

// 获取用法文本
const getUsageText = (usage) => {
  const textMap = {
    'before-meal': '饭前',
    'after-meal': '饭后',
    'with-meal': '随餐',
    'before-sleep': '睡前',
    'any-time': '任意时间'
  }
  return textMap[usage] || usage
}

// 判断时间是否已过
const isTimePassed = (time) => {
  const now = new Date()
  const [hours, minutes] = time.split(':')
  const timeDate = new Date()
  timeDate.setHours(parseInt(hours), parseInt(minutes), 0)
  return isBefore(timeDate, now)
}

// 获取疗程进度
const getDurationPercentage = () => {
  const start = parseISO(props.medication.startDate)
  const end = parseISO(props.medication.endDate)
  const now = new Date()
  
  const total = differenceInDays(end, start)
  const passed = differenceInDays(now, start)
  
  if (passed < 0) return 0
  if (passed > total) return 100
  
  return Math.round((passed / total) * 100)
}

// 进度条颜色
const progressColor = computed(() => {
  const percentage = getDurationPercentage()
  if (percentage < 30) return '#409EFF'
  if (percentage < 70) return '#67C23A'
  if (percentage < 90) return '#E6A23C'
  return '#F56C6C'
})

// 获取库存颜色
const getStockColor = () => {
  const quantity = props.medication.remainingQuantity
  if (quantity <= 5) return '#F56C6C'
  if (quantity <= 10) return '#E6A23C'
  return '#67C23A'
}

// 获取库存样式类
const getStockClass = () => {
  const quantity = props.medication.remainingQuantity
  if (quantity <= 5) return 'stock-low'
  if (quantity <= 10) return 'stock-medium'
  return 'stock-high'
}

// 格式化日期
const formatDate = (dateStr) => {
  try {
    return format(parseISO(dateStr), 'yyyy-MM-dd')
  } catch {
    return dateStr
  }
}

// 处理命令
const handleCommand = (command) => {
  switch (command) {
    case 'take':
      handleTake()
      break
    case 'edit':
      emit('edit', props.medication)
      break
    case 'history':
      emit('view-history', props.medication)
      break
    case 'delete':
      handleDelete()
      break
  }
}

// 处理服药
const handleTake = () => {
  emit('take', props.medication)
  ElMessage.success('已记录服药')
}

// 处理删除
const handleDelete = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这个用药记录吗？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    emit('delete', props.medication)
  } catch {
    // 取消删除
  }
}
</script>

<style scoped>
.medication-card {
  margin-bottom: 16px;
}

.med-card {
  transition: all 0.3s;
  border-left: 4px solid #409EFF;
}

.med-card.status-active {
  border-left-color: #67C23A;
}

.med-card.status-paused {
  border-left-color: #E6A23C;
}

.med-card.status-completed {
  border-left-color: #909399;
  opacity: 0.8;
}

.med-card.status-expired {
  border-left-color: #F56C6C;
  opacity: 0.7;
}

.med-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.med-info {
  flex: 1;
}

.med-name {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.name-text {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.card-content {
  margin-bottom: 16px;
}

.dosage-info {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 12px;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 6px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.info-item .label {
  color: #909399;
  font-size: 13px;
}

.info-item .value {
  color: #303133;
  font-weight: 500;
  font-size: 14px;
}

.time-info {
  margin-bottom: 12px;
}

.time-label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 8px;
}

.time-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.duration-info {
  margin-bottom: 12px;
}

.progress-text {
  font-size: 12px;
  font-weight: 500;
}

.duration-text {
  display: flex;
  justify-content: space-between;
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
}

.notes {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px;
  background: #fff9e6;
  border-radius: 4px;
  color: #E6A23C;
  font-size: 13px;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.stock-info {
  display: flex;
  align-items: center;
  gap: 6px;
}

.stock-text {
  font-size: 13px;
  font-weight: 500;
}

.stock-text.stock-low {
  color: #F56C6C;
}

.stock-text.stock-medium {
  color: #E6A23C;
}

.stock-text.stock-high {
  color: #67C23A;
}

@media (max-width: 768px) {
  .dosage-info {
    flex-direction: column;
    gap: 8px;
  }

  .name-text {
    font-size: 16px;
  }
}
</style>