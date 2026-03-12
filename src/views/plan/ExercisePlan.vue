<template>
  <div class="exercise-plan">
    <el-row :gutter="20">
      <!-- 左侧：运动统计 -->
      <el-col :xs="24" :md="8">
        <el-card>
          <template #header>
            <span>本周运动统计</span>
          </template>

          <div class="stats">
            <div class="stat-item">
              <div class="stat-label">总时长</div>
              <div class="stat-value">{{ weekStats.totalDuration }}</div>
              <div class="stat-unit">分钟</div>
            </div>

            <div class="stat-item">
              <div class="stat-label">总次数</div>
              <div class="stat-value">{{ weekStats.totalCount }}</div>
              <div class="stat-unit">次</div>
            </div>

            <div class="stat-item">
              <div class="stat-label">消耗热量</div>
              <div class="stat-value">{{ weekStats.totalCalories }}</div>
              <div class="stat-unit">千卡</div>
            </div>
          </div>

          <el-divider />

          <div class="progress-section">
            <div class="progress-header">
              <span>目标完成度</span>
              <span class="progress-percent">{{ weekStats.completionRate }}%</span>
            </div>
            <el-progress
              :percentage="weekStats.completionRate"
              :color="getProgressColor(weekStats.completionRate)"
              :stroke-width="15"
            />
          </div>
        </el-card>

        <el-card style="margin-top: 20px">
          <template #header>
            <span>推荐运动</span>
          </template>

          <div class="recommended-exercises">
            <div
              v-for="exercise in recommendedExercises"
              :key="exercise.id"
              class="exercise-card"
              @click="addExercise(exercise)"
            >
              <el-icon :size="32" :color="exercise.color">
                <component :is="exercise.icon" />
              </el-icon>
              <div class="exercise-info">
                <div class="exercise-name">{{ exercise.name }}</div>
                <div class="exercise-desc">{{ exercise.description }}</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧：运动计划 -->
      <el-col :xs="24" :md="16">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>运动计划</span>
              <el-button type="primary" @click="showAddDialog = true">
                <el-icon><Plus /></el-icon>
                添加运动
              </el-button>
            </div>
          </template>

          <el-tabs v-model="activeTab">
            <el-tab-pane label="本周计划" name="week">
              <div class="plan-list">
                <div
                  v-for="plan in weekPlans"
                  :key="plan.id"
                  class="plan-item"
                  :class="{ completed: plan.completed }"
                >
                  <el-checkbox
                    v-model="plan.completed"
                    @change="togglePlan(plan)"
                    class="plan-checkbox"
                  />
                  <div class="plan-content">
                    <div class="plan-header">
                      <span class="plan-name">{{ plan.name }}</span>
                      <el-tag :type="getTypeTag(plan.type)" size="small">
                        {{ getTypeLabel(plan.type) }}
                      </el-tag>
                    </div>
                    <div class="plan-details">
                      <span><el-icon><Clock /></el-icon> {{ plan.date }} {{ plan.time }}</span>
                      <span><el-icon><Timer /></el-icon> {{ plan.duration }}分钟</span>
                      <span><el-icon><TrendCharts /></el-icon> {{ plan.calories }}千卡</span>
                    </div>
                    <div v-if="plan.note" class="plan-note">
                      {{ plan.note }}
                    </div>
                  </div>
                  <div class="plan-actions">
                    <el-button type="primary" text @click="editPlan(plan)">
                      编辑
                    </el-button>
                    <el-button type="danger" text @click="deletePlan(plan.id)">
                      删除
                    </el-button>
                  </div>
                </div>

                <el-empty v-if="weekPlans.length === 0" description="暂无计划" />
              </div>
            </el-tab-pane>

            <el-tab-pane label="运动记录" name="records">
              <el-table :data="exerciseRecords" stripe>
                <el-table-column prop="name" label="运动名称" />
                <el-table-column prop="type" label="类型" align="center">
                  <template #default="{ row }">
                    <el-tag :type="getTypeTag(row.type)" size="small">
                      {{ getTypeLabel(row.type) }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="duration" label="时长(分钟)" align="center" />
                <el-table-column prop="calories" label="消耗(千卡)" align="center" />
                <el-table-column prop="date" label="日期" align="center" width="180" />
              </el-table>

              <el-pagination
                v-model:current-page="currentPage"
                v-model:page-size="pageSize"
                :total="totalRecords"
                layout="total, prev, pager, next"
                style="margin-top: 20px; justify-content: center"
                @current-change="loadRecords"
              />
            </el-tab-pane>

            <el-tab-pane label="统计图表" name="charts">
              <div id="exercise-chart" style="width: 100%; height: 400px"></div>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>

    <!-- 添加/编辑运动对话框 -->
    <el-dialog
      v-model="showAddDialog"
      :title="isEdit ? '编辑运动' : '添加运动'"
      width="500px"
    >
      <el-form
        ref="formRef"
        :model="exerciseForm"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="运动名称" prop="name">
          <el-input v-model="exerciseForm.name" placeholder="请输入运动名称" />
        </el-form-item>

        <el-form-item label="运动类型" prop="type">
          <el-select v-model="exerciseForm.type" placeholder="请选择类型">
            <el-option label="有氧运动" value="aerobic" />
            <el-option label="力量训练" value="strength" />
            <el-option label="柔韧性" value="flexibility" />
            <el-option label="平衡训练" value="balance" />
          </el-select>
        </el-form-item>

        <el-form-item label="日期" prop="date">
          <el-date-picker
            v-model="exerciseForm.date"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="时间" prop="time">
          <el-time-picker
            v-model="exerciseForm.time"
            placeholder="选择时间"
            value-format="HH:mm"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="时长" prop="duration">
          <el-input-number
            v-model="exerciseForm.duration"
            :min="5"
            :max="180"
            :step="5"
            style="width: 100%"
          />
          <span style="margin-left: 10px">分钟</span>
        </el-form-item>

        <el-form-item label="强度">
          <el-radio-group v-model="exerciseForm.intensity">
            <el-radio label="low">低强度</el-radio>
            <el-radio label="medium">中强度</el-radio>
            <el-radio label="high">高强度</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="备注">
          <el-input
            v-model="exerciseForm.note"
            type="textarea"
            :rows="3"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus,
  Clock,
  Timer,
  TrendCharts,
  Walking,
  Basketball,
  Bicycle,
  Swimming
} from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import {
  getExerciseStats,
  getExercisePlans,
  getExerciseRecords,
  addExercisePlan,
  updateExercisePlan,
  deleteExercisePlan
} from '@/api/plan'

const activeTab = ref('week')
const showAddDialog = ref(false)
const isEdit = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const totalRecords = ref(0)

// 周统计
const weekStats = ref({
  totalDuration: 0,
  totalCount: 0,
  totalCalories: 0,
  completionRate: 0
})

// 推荐运动
const recommendedExercises = [
  {
    id: 1,
    name: '快走',
    description: '30分钟 · 150千卡',
    icon: 'Walking',
    color: '#67C23A',
    type: 'aerobic'
  },
  {
    id: 2,
    name: '慢跑',
    description: '30分钟 · 300千卡',
    icon: 'Basketball',
    color: '#409EFF',
    type: 'aerobic'
  },
  {
    id: 3,
    name: '骑行',
    description: '30分钟 · 200千卡',
    icon: 'Bicycle',
    color: '#E6A23C',
    type: 'aerobic'
  },
  {
    id: 4,
    name: '游泳',
    description: '30分钟 · 350千卡',
    icon: 'Swimming',
    color: '#F56C6C',
    type: 'aerobic'
  }
]

// 周计划
const weekPlans = ref([])

// 运动记录
const exerciseRecords = ref([])

// 表单
const formRef = ref()
const exerciseForm = reactive({
  id: null,
  name: '',
  type: '',
  date: '',
  time: '',
  duration: 30,
  intensity: 'medium',
  note: ''
})

const formRules = {
  name: [{ required: true, message: '请输入运动名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  date: [{ required: true, message: '请选择日期', trigger: 'change' }],
  time: [{ required: true, message: '请选择时间', trigger: 'change' }],
  duration: [{ required: true, message: '请输入时长', trigger: 'blur' }]
}

let exerciseChart = null

// 获取类型标签
const getTypeTag = (type) => {
  const typeMap = {
    aerobic: 'success',
    strength: 'warning',
    flexibility: 'info',
    balance: 'primary'
  }
  return typeMap[type] || 'info'
}

// 获取类型标签文本
const getTypeLabel = (type) => {
  const labelMap = {
    aerobic: '有氧',
    strength: '力量',
    flexibility: '柔韧',
    balance: '平衡'
  }
  return labelMap[type] || type
}

// 获取进度颜色
const getProgressColor = (percent) => {
  if (percent >= 100) return '#67C23A'
  if (percent >= 80) return '#409EFF'
  if (percent >= 60) return '#E6A23C'
  return '#F56C6C'
}

// 切换计划完成状态
const togglePlan = async (plan) => {
  try {
    await updateExercisePlan(plan.id, { completed: plan.completed })
    ElMessage.success(plan.completed ? '已完成' : '取消完成')
    await loadStats()
  } catch (error) {
    ElMessage.error('操作失败：' + error.message)
  }
}

// 添加运动
const addExercise = (exercise) => {
  Object.assign(exerciseForm, {
    name: exercise.name,
    type: exercise.type,
    date: new Date().toISOString().split('T')[0],
    time: new Date().toTimeString().split(' ')[0].substring(0, 5),
    duration: 30
  })
  showAddDialog.value = true
}

// 编辑计划
const editPlan = (plan) => {
  isEdit.value = true
  Object.assign(exerciseForm, plan)
  showAddDialog.value = true
}

// 删除计划
const deletePlan = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这个计划吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const res = await deleteExercisePlan(id)
    if (res.success) {
      ElMessage.success('删除成功')
      await loadPlans()
      await loadStats()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败：' + error.message)
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  try {
    const apiFunc = isEdit.value ? updateExercisePlan : addExercisePlan
    const res = await apiFunc(exerciseForm.id, exerciseForm)

    if (res.success) {
      ElMessage.success(isEdit.value ? '更新成功' : '添加成功')
      showAddDialog.value = false
      await loadPlans()
      await loadStats()
    }
  } catch (error) {
    ElMessage.error('操作失败：' + error.message)
  }
}

// 初始化图表
const initChart = () => {
  const chartDom = document.getElementById('exercise-chart')
  if (!chartDom) return

  exerciseChart = echarts.init(chartDom)

  const option = {
    title: {
      text: '本周运动时长统计',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    },
    yAxis: {
      type: 'value',
      name: '分钟'
    },
    series: [
      {
        name: '运动时长',
        type: 'bar',
        data: [30, 45, 0, 60, 30, 0, 40],
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#409EFF' },
            { offset: 1, color: '#67C23A' }
          ])
        }
      }
    ]
  }

  exerciseChart.setOption(option)
}

// 加载数据
const loadStats = async () => {
  try {
    const res = await getExerciseStats()
    if (res.success) {
      weekStats.value = res.data
    }
  } catch (error) {
    console.error('加载统计失败：', error)
  }
}

const loadPlans = async () => {
  try {
    const res = await getExercisePlans()
    if (res.success) {
      weekPlans.value = res.data
    }
  } catch (error) {
    console.error('加载计划失败：', error)
  }
}

const loadRecords = async () => {
  try {
    const res = await getExerciseRecords({
      page: currentPage.value,
      pageSize: pageSize.value
    })
    
    if (res.success) {
      exerciseRecords.value = res.data.records
      totalRecords.value = res.data.total
    }
  } catch (error) {
    console.error('加载记录失败：', error)
  }
}

onMounted(() => {
  loadStats()
  loadPlans()
  loadRecords()

  nextTick(() => {
    initChart()
  })

  window.addEventListener('resize', () => {
    exerciseChart?.resize()
  })
})
</script>

<style scoped>
.exercise-plan {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 统计卡片 */
.stats {
  display: flex;
  justify-content: space-around;
  padding: 20px 0;
}

.stat-item {
  text-align: center;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 10px;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 5px;
}

.stat-unit {
  font-size: 14px;
  color: #909399;
}

.progress-section {
  padding: 20px 0;
}

.progress-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 14px;
  color: #606266;
}

.progress-percent {
  font-weight: bold;
  color: #409EFF;
}

/* 推荐运动 */
.recommended-exercises {
  padding: 10px 0;
}

.exercise-card {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 8px;
  margin-bottom: 10px;
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
}

.exercise-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.exercise-info {
  flex: 1;
}

.exercise-name {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.exercise-desc {
  font-size: 14px;
  color: #909399;
}

/* 计划列表 */
.plan-list {
  padding: 10px 0;
}

.plan-item {
  display: flex;
  align-items: flex-start;
  gap: 15px;
  padding: 20px;
  background-color: #f5f7fa;
  border-left: 4px solid #409EFF;
  border-radius: 4px;
  margin-bottom: 15px;
  transition: opacity 0.3s;
}

.plan-item.completed {
  opacity: 0.6;
}

.plan-item.completed .plan-name {
  text-decoration: line-through;
}

.plan-checkbox {
  margin-top: 5px;
}

.plan-content {
  flex: 1;
}

.plan-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.plan-name {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.plan-details {
  display: flex;
  gap: 20px;
  font-size: 14px;
  color: #606266;
  margin-bottom: 10px;
}

.plan-details span {
  display: flex;
  align-items: center;
  gap: 5px;
}

.plan-note {
  font-size: 14px;
  color: #909399;
  padding: 10px;
  background-color: white;
  border-radius: 4px;
}

.plan-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
</style>