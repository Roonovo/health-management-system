<template>
  <div class="health-plan">
    <el-card>
      <template #header>
        <div class="card-header">
          <el-icon><Notebook /></el-icon>
          <span>我的健康计划</span>
          <el-button type="primary" @click="showCreateDialog = true">
            <el-icon><Plus /></el-icon>
            新建计划
          </el-button>
        </div>
      </template>

      <el-tabs v-model="activeTab">
        <el-tab-pane label="进行中" name="active">
          <div class="plan-list">
            <div
              v-for="plan in activePlans"
              :key="plan.id"
              class="plan-card"
              @click="viewPlan(plan)"
            >
              <div class="plan-header">
                <div class="plan-title">
                  <el-icon :size="20" :color="plan.color">
                    <component :is="getPlanIcon(plan.type)" />
                  </el-icon>
                  <span>{{ plan.title }}</span>
                </div>
                <el-tag :type="getPlanTagType(plan.type)" size="small">
                  {{ getPlanTypeText(plan.type) }}
                </el-tag>
              </div>
              
              <div class="plan-content">
                <p class="plan-desc">{{ plan.description }}</p>
                
                <div class="plan-progress">
                  <el-progress
                    :percentage="plan.progress"
                    :color="getProgressColor(plan.progress)"
                                        :stroke-width="8"
                  />
                  <span class="progress-text">{{ plan.progress }}% 完成</span>
                </div>

                <div class="plan-meta">
                  <span class="meta-item">
                    <el-icon><Calendar /></el-icon>
                    {{ formatDateRange(plan.startDate, plan.endDate) }}
                  </span>
                  <span class="meta-item">
                    <el-icon><Clock /></el-icon>
                    剩余 {{ getRemainingDays(plan.endDate) }} 天
                  </span>
                </div>
              </div>

              <div class="plan-actions">
                <el-button type="primary" size="small" @click.stop="checkIn(plan)">
                  <el-icon><Check /></el-icon>
                  打卡
                </el-button>
                <el-button size="small" @click.stop="editPlan(plan)">
                  <el-icon><Edit /></el-icon>
                  编辑
                </el-button>
              </div>
            </div>
          </div>

          <el-empty v-if="activePlans.length === 0" description="暂无进行中的计划" />
        </el-tab-pane>

        <el-tab-pane label="已完成" name="completed">
          <div class="plan-list">
            <div
              v-for="plan in completedPlans"
              :key="plan.id"
              class="plan-card completed"
            >
              <div class="plan-header">
                <div class="plan-title">
                  <el-icon :size="20" color="#67C23A">
                    <component :is="getPlanIcon(plan.type)" />
                  </el-icon>
                  <span>{{ plan.title }}</span>
                </div>
                <el-tag type="success" size="small">已完成</el-tag>
              </div>
              
              <div class="plan-content">
                <p class="plan-desc">{{ plan.description }}</p>
                
                <div class="completion-info">
                  <el-icon color="#67C23A" :size="48"><SuccessFilled /></el-icon>
                  <div class="completion-text">
                    <p class="completion-title">恭喜完成目标！</p>
                    <p class="completion-date">完成于 {{ formatDate(plan.completedDate) }}</p>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <el-empty v-if="completedPlans.length === 0" description="暂无完成的计划" />
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 创建计划对话框 -->
    <el-dialog
      v-model="showCreateDialog"
      title="新建健康计划"
      width="600px"
    >
      <el-form
        ref="formRef"
        :model="planForm"
        :rules="planRules"
        label-width="100px"
      >
        <el-form-item label="计划名称" prop="title">
          <el-input v-model="planForm.title" placeholder="如：每日步行10000步" />
        </el-form-item>

        <el-form-item label="计划类型" prop="type">
          <el-select v-model="planForm.type" placeholder="选择计划类型" style="width: 100%">
            <el-option label="运动健身" value="exercise" />
            <el-option label="饮食控制" value="diet" />
            <el-option label="作息调整" value="sleep" />
            <el-option label="体重管理" value="weight" />
            <el-option label="血压控制" value="bloodPressure" />
            <el-option label="血糖控制" value="bloodSugar" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>

        <el-form-item label="目标描述" prop="description">
          <el-input
            v-model="planForm.description"
            type="textarea"
            :rows="3"
            placeholder="描述您的健康目标"
          />
        </el-form-item>

        <el-form-item label="开始日期" prop="startDate">
          <el-date-picker
            v-model="planForm.startDate"
            type="date"
            placeholder="选择开始日期"
            style="width: 100%"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>

        <el-form-item label="结束日期" prop="endDate">
          <el-date-picker
            v-model="planForm.endDate"
            type="date"
            placeholder="选择结束日期"
            style="width: 100%"
            value-format="YYYY-MM-DD"
            :disabled-date="disabledEndDate"
          />
        </el-form-item>

        <el-form-item label="每日目标">
          <el-input v-model="planForm.dailyGoal" placeholder="如：步行10000步">
            <template #append>
              <el-select v-model="planForm.goalUnit" style="width: 100px">
                <el-option label="步" value="步" />
                <el-option label="分钟" value="分钟" />
                <el-option label="次" value="次" />
                <el-option label="kg" value="kg" />
                <el-option label="卡路里" value="卡路里" />
              </el-select>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="提醒设置">
          <el-switch v-model="planForm.reminderEnabled" />
          <span style="margin-left: 12px; font-size: 13px; color: #909399">
            启用每日提醒
          </span>
        </el-form-item>

        <el-form-item label="提醒时间" v-if="planForm.reminderEnabled">
          <el-time-picker
            v-model="planForm.reminderTime"
            placeholder="选择提醒时间"
            format="HH:mm"
            value-format="HH:mm"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="handleCreatePlan" :loading="creating">
          创建
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted} from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Notebook,
  Plus,
  Calendar,
  Clock,
  Check,
  Edit,
  SuccessFilled,
  Walk,
  Food,
  Moon,
  TrendCharts,
  Aim
} from '@element-plus/icons-vue'
import { format, parseISO, differenceInDays } from 'date-fns'

const activeTab = ref('active')
const showCreateDialog = ref(false)
const formRef = ref(null)
const creating = ref(false)

// 进行中的计划
const activePlans = ref([
  {
    id: 1,
    title: '每日步行10000步',
    type: 'exercise',
    description: '每天坚持步行10000步，提升心肺功能',
    startDate: '2024-01-01',
    endDate: '2024-03-31',
    progress: 65,
    color: '#409EFF'
  },
  {
    id: 2,
    title: '控制每日热量摄入',
    type: 'diet',
    description: '每日热量控制在1800卡以内',
    startDate: '2024-01-01',
    endDate: '2024-02-29',
    progress: 45,
    color: '#67C23A'
  }
])

// 已完成的计划
const completedPlans = ref([
  {
    id: 3,
    title: '戒烟计划',
    type: 'other',
    description: '30天戒烟挑战',
    startDate: '2023-12-01',
    endDate: '2023-12-31',
    completedDate: '2023-12-31'
  }
])

// 计划表单
const planForm = ref({
  title: '',
  type: '',
  description: '',
  startDate: '',
  endDate: '',
  dailyGoal: '',
  goalUnit: '步',
  reminderEnabled: true,
  reminderTime: '09:00'
})

const planRules = {
  title: [
    { required: true, message: '请输入计划名称', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择计划类型', trigger: 'change' }
  ],
  description: [
    { required: true, message: '请输入目标描述', trigger: 'blur' }
  ],
  startDate: [
    { required: true, message: '请选择开始日期', trigger: 'change' }
  ],
  endDate: [
    { required: true, message: '请选择结束日期', trigger: 'change' }
  ]
}

// 获取计划图标
const getPlanIcon = (type) => {
  const iconMap = {
    exercise: Walk,
    diet: Food,
    sleep: Moon,
    weight: TrendCharts,
    bloodPressure: TrendCharts,
    bloodSugar: TrendCharts,
    other: Aim
  }
  return iconMap[type] || Aim
}

// 获取计划标签类型
const getPlanTagType = (type) => {
  const typeMap = {
    exercise: 'primary',
    diet: 'success',
    sleep: 'info',
    weight: 'warning',
    bloodPressure: 'danger',
    bloodSugar: 'warning',
    other: ''
  }
  return typeMap[type] || ''
}

// 获取计划类型文本
const getPlanTypeText = (type) => {
  const textMap = {
    exercise: '运动健身',
    diet: '饮食控制',
    sleep: '作息调整',
    weight: '体重管理',
    bloodPressure: '血压控制',
    bloodSugar: '血糖控制',
    other: '其他'
  }
  return textMap[type] || type
}

// 获取进度颜色
const getProgressColor = (progress) => {
  if (progress >= 80) return '#67C23A'
  if (progress >= 50) return '#409EFF'
  if (progress >= 30) return '#E6A23C'
  return '#F56C6C'
}

// 格式化日期范围
const formatDateRange = (start, end) => {
  try {
    return `${format(parseISO(start), 'MM/dd')} - ${format(parseISO(end), 'MM/dd')}`
  } catch {
    return `${start} - ${end}`
  }
}

// 格式化日期
const formatDate = (dateStr) => {
  try {
    return format(parseISO(dateStr), 'yyyy-MM-dd')
  } catch {
    return dateStr
  }
}

// 获取剩余天数
const getRemainingDays = (endDate) => {
  try {
    const days = differenceInDays(parseISO(endDate), new Date())
    return days > 0 ? days : 0
  } catch {
    return 0
  }
}

// 禁用结束日期
const disabledEndDate = (date) => {
  if (!planForm.value.startDate) return false
  return date < new Date(planForm.value.startDate)
}

// 查看计划详情
const viewPlan = (plan) => {
  console.log('查看计划:', plan)
  // 跳转到计划详情页
}

// 打卡
const checkIn = (plan) => {
  ElMessage.success(`${plan.title} 打卡成功！`)
  // 更新进度
  plan.progress = Math.min(plan.progress + 5, 100)
}

// 编辑计划
const editPlan = (plan) => {
  console.log('编辑计划:', plan)
  // 打开编辑对话框
}

// 创建计划
const handleCreatePlan = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      creating.value = true
      try {
        await new Promise(resolve => setTimeout(resolve, 1000))
        ElMessage.success('计划创建成功')
        showCreateDialog.value = false
        formRef.value.resetFields()
      } catch (error) {
        ElMessage.error('创建失败：' + error.message)
      } finally {
        creating.value = false
      }
    }
  })
}
</script>

<style scoped>
.health-plan {
  height: 100%;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 500;
}

.card-header > span {
  flex: 1;
}

.plan-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
}

.plan-card {
  padding: 20px;
  background: #fff;
  border: 1px solid #EBEEF5;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.plan-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border-color: #409EFF;
}

.plan-card.completed {
  opacity: 0.9;
}

.plan-card.completed:hover {
  border-color: #67C23A;
}

.plan-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.plan-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.plan-content {
  margin-bottom: 16px;
}

.plan-desc {
  color: #606266;
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 16px;
}

.plan-progress {
  margin-bottom: 12px;
}

.progress-text {
  display: block;
  margin-top: 8px;
  font-size: 13px;
  color: #909399;
  text-align: right;
}

.plan-meta {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: #909399;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.plan-actions {
  display: flex;
  gap: 8px;
  padding-top: 16px;
  border-top: 1px solid #EBEEF5;
}

.completion-info {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: #f0f9ff;
  border-radius: 8px;
  margin-top: 16px;
}

.completion-text {
  flex: 1;
}

.completion-title {
  font-size: 16px;
  font-weight: 600;
  color: #67C23A;
  margin-bottom: 4px;
}

.completion-date {
  font-size: 13px;
  color: #909399;
}

@media (max-width: 768px) {
  .plan-list {
    grid-template-columns: 1fr;
  }
}
</style>