<template>
  <div class="health-plan">
    <el-row :gutter="20">
      <!-- 左侧：目标设置 -->
      <el-col :xs="24" :md="8">
        <el-card>
          <template #header>
            <span>健康目标</span>
          </template>

          <el-form :model="goalForm" label-width="100px">
            <el-form-item label="每日运动">
              <el-input-number
                v-model="goalForm.dailyExercise"
                :min="0"
                :max="180"
                style="width: 100%"
              />
              <span style="margin-left: 10px">分钟</span>
            </el-form-item>

            <el-form-item label="睡眠时长">
              <el-input-number
                v-model="goalForm.sleepHours"
                :min="4"
                :max="12"
                style="width: 100%"
              />
              <span style="margin-left: 10px">小时</span>
            </el-form-item>

            <el-form-item label="饮水量">
              <el-input-number
                v-model="goalForm.waterIntake"
                :min="1"
                :max="10"
                style="width: 100%"
              />
              <span style="margin-left: 10px">升</span>
            </el-form-item>

            <el-form-item label="服药提醒">
              <el-switch v-model="goalForm.medicationReminder" />
            </el-form-item>

            <el-form-item label="复诊提醒">
              <el-switch v-model="goalForm.checkupReminder" />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="saveGoals" style="width: 100%">
                保存目标
              </el-button>
            </el-form-item>
          </el-form>

          <el-divider />

          <div class="smart-suggestions">
            <h4>智能建议</h4>
            <el-button
              type="success"
              size="small"
              @click="generateSmartPlan"
              style="width: 100%"
            >
              <el-icon><MagicStick /></el-icon>
              生成智能日程
            </el-button>
          </div>
        </el-card>

        <!-- 今日完成情况 -->
        <el-card style="margin-top: 20px">
          <template #header>
            <span>今日完成情况</span>
          </template>

          <div class="daily-progress">
            <div class="progress-item">
              <div class="progress-header">
                <span>运动时长</span>
                <span class="progress-value">{{ todayProgress.exercise }}/{{ goalForm.dailyExercise }} 分钟</span>
              </div>
              <el-progress
                :percentage="calculatePercentage(todayProgress.exercise, goalForm.dailyExercise)"
                :color="getProgressColor(todayProgress.exercise, goalForm.dailyExercise)"
              />
            </div>

            <div class="progress-item">
              <div class="progress-header">
                <span>饮水量</span>
                <span class="progress-value">{{ todayProgress.water }}/{{ goalForm.waterIntake }} 升</span>
              </div>
              <el-progress
                :percentage="calculatePercentage(todayProgress.water, goalForm.waterIntake)"
                :color="getProgressColor(todayProgress.water, goalForm.waterIntake)"
              />
            </div>

            <div class="progress-item">
              <div class="progress-header">
                <span>完成任务</span>
                <span class="progress-value">{{ todayProgress.tasks }}/{{ totalTasks }} 项</span>
              </div>
              <el-progress
                :percentage="calculatePercentage(todayProgress.tasks, totalTasks)"
                :color="getProgressColor(todayProgress.tasks, totalTasks)"
              />
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧：日历和日程 -->
      <el-col :xs="24" :md="16">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>健康日历</span>
              <div class="date-range">
                <el-button-group>
                  <el-button @click="changeWeek(-1)">
                    <el-icon><ArrowLeft /></el-icon>
                  </el-button>
                  <el-button @click="goToday">今天</el-button>
                  <el-button @click="changeWeek(1)">
                    <el-icon><ArrowRight /></el-icon>
                  </el-button>
                </el-button-group>
                <span class="current-week">{{ currentWeekLabel }}</span>
              </div>
            </div>
          </template>

          <!-- 周视图 -->
          <div class="week-calendar">
            <div class="week-header">
              <div class="time-column">时间</div>
              <div
                v-for="day in weekDays"
                :key="day.date"
                class="day-column"
                :class="{ today: day.isToday }"
              >
                <div class="day-name">{{ day.name }}</div>
                <div class="day-date">{{ day.date }}</div>
              </div>
            </div>

            <div class="week-body">
              <div
                v-for="hour in displayHours"
                :key="hour"
                class="hour-row"
              >
                <div class="time-column">{{ formatHour(hour) }}</div>
                <div
                  v-for="day in weekDays"
                  :key="day.date"
                  class="day-column"
                  @click="handleCellClick(day, hour)"
                >
                  <!-- 事件块 -->
                  <div
                    v-for="event in getEventsAtTime(day.date, hour)"
                    :key="event.id"
                    class="event-block"
                    :style="{
                      backgroundColor: event.color,
                      height: `${event.duration * 60}px`
                    }"
                    @click.stop="handleEventClick(event)"
                  >
                    <div class="event-title">{{ event.title }}</div>
                    <div class="event-time">{{ event.time }}</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 固定日程列表 -->
        <el-card style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>固定日程</span>
              <el-button type="primary" size="small" @click="showAddEventDialog = true">
                <el-icon><Plus /></el-icon>
                添加
              </el-button>
            </div>
          </template>

          <div class="fixed-events">
            <div
              v-for="event in fixedEvents"
              :key="event.id"
              class="fixed-event-item"
            >
              <div class="event-dot" :style="{ backgroundColor: event.color }"></div>
              <div class="event-info">
                <div class="event-title">{{ event.title }}</div>
                <div class="event-time">{{ event.time }}</div>
              </div>
              <el-button type="text" @click="deleteEvent(event.id)">
                删除
              </el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 添加事件对话框 -->
    <el-dialog
      v-model="showAddEventDialog"
      title="添加日程"
      width="500px"
    >
      <el-form
        ref="eventFormRef"
        :model="eventForm"
        :rules="eventRules"
        label-width="100px"
      >
        <el-form-item label="标题" prop="title">
          <el-input v-model="eventForm.title" placeholder="请输入日程标题" />
        </el-form-item>

        <el-form-item label="类型" prop="type">
          <el-select v-model="eventForm.type" placeholder="请选择类型">
            <el-option label="运动" value="exercise" />
            <el-option label="用药" value="medication" />
            <el-option label="复诊" value="checkup" />
            <el-option label="饮食" value="diet" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>

        <el-form-item label="日期" prop="date">
          <el-date-picker
            v-model="eventForm.date"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="时间" prop="time">
          <el-time-picker
            v-model="eventForm.time"
            placeholder="选择时间"
            value-format="HH:mm"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="时长">
          <el-input-number
            v-model="eventForm.duration"
            :min="15"
            :max="180"
            :step="15"
            style="width: 100%"
          />
          <span style="margin-left: 10px">分钟</span>
        </el-form-item>

        <el-form-item label="重复">
          <el-select v-model="eventForm.repeat" placeholder="请选择重复规则">
            <el-option label="不重复" value="none" />
            <el-option label="每天" value="daily" />
            <el-option label="每周" value="weekly" />
            <el-option label="每月" value="monthly" />
          </el-select>
        </el-form-item>

        <el-form-item label="提醒">
          <el-checkbox v-model="eventForm.reminder">提前提醒</el-checkbox>
        </el-form-item>

        <el-form-item v-if="eventForm.reminder" label="提前时间">
          <el-select v-model="eventForm.reminderTime">
            <el-option label="5分钟前" :value="5" />
            <el-option label="10分钟前" :value="10" />
            <el-option label="15分钟前" :value="15" />
            <el-option label="30分钟前" :value="30" />
            <el-option label="1小时前" :value="60" />
          </el-select>
        </el-form-item>

        <el-form-item label="备注">
          <el-input
            v-model="eventForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showAddEventDialog = false">取消</el-button>
        <el-button type="primary" @click="handleAddEvent">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ArrowLeft,
  ArrowRight,
  Plus,
  MagicStick
} from '@element-plus/icons-vue'
import {
  getHealthGoals,
  updateHealthGoals,
  getHealthEvents,
  addHealthEvent,
  deleteHealthEvent,
  generateSmartSchedule
} from '@/api/plan'

const showAddEventDialog = ref(false)

// 目标设置
const goalForm = reactive({
  dailyExercise: 30,
  sleepHours: 8,
  waterIntake: 2,
  medicationReminder: true,
  checkupReminder: true
})

// 今日进度
const todayProgress = reactive({
  exercise: 25,
  water: 1.5,
  tasks: 3
})

const totalTasks = ref(5)

// 当前周
const currentWeek = ref(new Date())

// 事件表单
const eventFormRef = ref()
const eventForm = reactive({
  title: '',
  type: '',
  date: '',
  time: '',
  duration: 30,
  repeat: 'none',
  reminder: true,
  reminderTime: 15,
  remark: ''
})

const eventRules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  date: [{ required: true, message: '请选择日期', trigger: 'change' }],
  time: [{ required: true, message: '请选择时间', trigger: 'change' }]
}

// 固定事件
const fixedEvents = ref([])

// 所有事件
const allEvents = ref([])

// 显示的小时范围
const displayHours = Array.from({ length: 24 }, (_, i) => i)

// 当前周标签
const currentWeekLabel = computed(() => {
  const start = getWeekStart(currentWeek.value)
  const end = getWeekEnd(currentWeek.value)
  return `${formatDate(start)} - ${formatDate(end)}`
})

// 获取周开始日期
const getWeekStart = (date) => {
  const d = new Date(date)
  const day = d.getDay()
  const diff = d.getDate() - day + (day === 0 ? -6 : 1)
  return new Date(d.setDate(diff))
}

// 获取周结束日期
const getWeekEnd = (date) => {
  const start = getWeekStart(date)
  return new Date(start.getTime() + 6 * 24 * 60 * 60 * 1000)
}

// 周天数据
const weekDays = computed(() => {
  const start = getWeekStart(currentWeek.value)
  const today = new Date().toDateString()
  
  return Array.from({ length: 7 }, (_, i) => {
    const date = new Date(start)
    date.setDate(start.getDate() + i)
    
    return {
      name: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'][i],
      date: formatDate(date),
      isToday: date.toDateString() === today
    }
  })
})

// 格式化日期
const formatDate = (date) => {
  const d = new Date(date)
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${month}-${day}`
}

// 格式化小时
const formatHour = (hour) => {
  return `${String(hour).padStart(2, '0')}:00`
}

// 计算百分比
const calculatePercentage = (current, target) => {
  if (!target) return 0
  return Math.min(Math.round((current / target) * 100), 100)
}

// 获取进度条颜色
const getProgressColor = (current, target) => {
  const percentage = (current / target) * 100
  if (percentage >= 100) return '#67C23A'
  if (percentage >= 50) return '#E6A23C'
  return '#F56C6C'
}

// 获取指定时间的事件
const getEventsAtTime = (date, hour) => {
  return allEvents.value.filter(event => {
    if (event.date !== date) return false
    const eventHour = parseInt(event.time.split(':')[0])
    return eventHour === hour
  })
}

// 切换周
const changeWeek = (offset) => {
  const newDate = new Date(currentWeek.value)
  newDate.setDate(newDate.getDate() + offset * 7)
  currentWeek.value = newDate
  loadEvents()
}

// 回到今天
const goToday = () => {
  currentWeek.value = new Date()
  loadEvents()
}

// 单元格点击
const handleCellClick = (day, hour) => {
  eventForm.date = `2024-${day.date}`
  eventForm.time = formatHour(hour)
  showAddEventDialog.value = true
}

// 事件点击
const handleEventClick = (event) => {
  ElMessageBox.confirm(
    `${event.title}\n${event.time}\n${event.remark || ''}`,
    '日程详情',
    {
      confirmButtonText: '编辑',
      cancelButtonText: '关闭',
      type: 'info'
    }
  ).then(() => {
    // TODO: 实现编辑功能
  }).catch(() => {})
}

// 保存目标
const saveGoals = async () => {
  try {
    const res = await updateHealthGoals(goalForm)
    if (res.success) {
      ElMessage.success('保存成功')
    }
  } catch (error) {
    ElMessage.error('保存失败：' + error.message)
  }
}

// 生成智能计划
const generateSmartPlan = async () => {
  try {
    ElMessage.info('正在生成智能日程...')
    
    const res = await generateSmartSchedule(goalForm)
    if (res.success) {
      ElMessage.success('智能日程生成成功')
      await loadEvents()
    }
  } catch (error) {
    ElMessage.error('生成失败：' + error.message)
  }
}

// 添加事件
const handleAddEvent = async () => {
  const valid = await eventFormRef.value.validate().catch(() => false)
  if (!valid) return

  try {
    const res = await addHealthEvent(eventForm)
    if (res.success) {
      ElMessage.success('添加成功')
      showAddEventDialog.value = false
      await loadEvents()
    }
  } catch (error) {
    ElMessage.error('添加失败：' + error.message)
  }
}

// 删除事件
const deleteEvent = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这个日程吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const res = await deleteHealthEvent(id)
    if (res.success) {
      ElMessage.success('删除成功')
      await loadEvents()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败：' + error.message)
    }
  }
}

// 加载数据
const loadGoals = async () => {
  try {
    const res = await getHealthGoals()
    if (res.success) {
      Object.assign(goalForm, res.data)
    }
  } catch (error) {
    console.error('加载目标失败：', error)
  }
}

const loadEvents = async () => {
  try {
    const start = getWeekStart(currentWeek.value)
    const end = getWeekEnd(currentWeek.value)
    
    const res = await getHealthEvents({
      startDate: start.toISOString().split('T')[0],
      endDate: end.toISOString().split('T')[0]
    })
    
    if (res.success) {
      allEvents.value = res.data.events || []
      fixedEvents.value = res.data.fixedEvents || []
    }
  } catch (error) {
    console.error('加载事件失败：', error)
  }
}

onMounted(() => {
  loadGoals()
  loadEvents()
})
</script>

<style scoped>
.health-plan {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.date-range {
  display: flex;
  align-items: center;
  gap: 15px;
}

.current-week {
  font-weight: bold;
  color: #303133;
}

/* 每日进度 */
.daily-progress {
  padding: 10px 0;
}

.progress-item {
  margin-bottom: 20px;
}

.progress-item:last-child {
  margin-bottom: 0;
}

.progress-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 14px;
  color: #606266;
}

.progress-value {
  font-weight: bold;
  color: #303133;
}

/* 智能建议 */
.smart-suggestions {
  margin-top: 20px;
}

.smart-suggestions h4 {
  margin: 0 0 15px;
  color: #303133;
}

/* 周日历 */
.week-calendar {
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  overflow: hidden;
}

.week-header {
  display: grid;
  grid-template-columns: 60px repeat(7, 1fr);
  background-color: #f5f7fa;
  border-bottom: 2px solid #e4e7ed;
}

.time-column {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 10px;
  font-size: 12px;
  color: #909399;
  border-right: 1px solid #e4e7ed;
}

.day-column {
  padding: 10px;
  text-align: center;
  border-right: 1px solid #e4e7ed;
  position: relative;
}

.day-column:last-child {
  border-right: none;
}

.day-column.today {
  background-color: #ecf5ff;
}

.day-name {
  font-size: 14px;
  color: #606266;
  margin-bottom: 5px;
}

.day-date {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.week-body {
  max-height: 600px;
  overflow-y: auto;
}

.hour-row {
  display: grid;
  grid-template-columns: 60px repeat(7, 1fr);
  border-bottom: 1px solid #e4e7ed;
  min-height: 60px;
}

.hour-row .day-column {
  cursor: pointer;
  transition: background-color 0.3s;
}

.hour-row .day-column:hover {
  background-color: #f5f7fa;
}

/* 事件块 */
.event-block {
  padding: 5px;
  border-radius: 4px;
  color: white;
  font-size: 12px;
  margin-bottom: 2px;
  cursor: pointer;
  transition: opacity 0.3s;
}

.event-block:hover {
  opacity: 0.8;
}

.event-title {
  font-weight: bold;
  margin-bottom: 2px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.event-time {
  font-size: 11px;
  opacity: 0.9;
}

/* 固定事件列表 */
.fixed-events {
  padding: 10px 0;
}

.fixed-event-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
  margin-bottom: 10px;
}

.event-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  flex-shrink: 0;
}

.event-info {
  flex: 1;
}

.fixed-event-item .event-title {
  font-size: 14px;
  color: #303133;
  margin-bottom: 5px;
}

.fixed-event-item .event-time {
  font-size: 12px;
  color: #909399;
}

@media (max-width: 768px) {
  .week-calendar {
    overflow-x: auto;
  }

  .week-header,
  .hour-row {
    min-width: 800px;
  }
}
</style>