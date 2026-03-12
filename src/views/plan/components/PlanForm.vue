<template>
  <div class="plan-form">
    <el-card>
      <template #header>
        <div class="card-header">
          <el-icon><Edit /></el-icon>
          <span>{{ isEdit ? '编辑' : '新建' }}用药计划</span>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
      >
        <el-form-item label="药品名称" prop="name">
          <el-input
            v-model="form.name"
            placeholder="请输入药品名称"
            clearable
          />
        </el-form-item>

        <el-form-item label="药品类型" prop="type">
          <el-select v-model="form.type" placeholder="选择药品类型" style="width: 100%">
            <el-option label="片剂" value="tablet" />
            <el-option label="胶囊" value="capsule" />
            <el-option label="注射液" value="injection" />
            <el-option label="口服液" value="liquid" />
            <el-option label="软膏" value="ointment" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>

        <el-form-item label="单次剂量" prop="dosage">
          <el-input-number
            v-model="form.dosage"
            :min="0.5"
            :max="100"
            :step="0.5"
            :precision="1"
            style="width: 200px"
          />
          <el-select
            v-model="form.unit"
            placeholder="单位"
            style="width: 100px; margin-left: 12px"
          >
            <el-option label="片" value="片" />
            <el-option label="粒" value="粒" />
            <el-option label="ml" value="ml" />
            <el-option label="mg" value="mg" />
            <el-option label="g" value="g" />
          </el-select>
        </el-form-item>

        <el-form-item label="服药频次" prop="frequency">
          <el-select v-model="form.frequency" placeholder="选择服药频次" style="width: 100%">
            <el-option label="每日一次" value="once-daily" />
            <el-option label="每日两次" value="twice-daily" />
            <el-option label="每日三次" value="three-times-daily" />
            <el-option label="每日四次" value="four-times-daily" />
            <el-option label="隔日一次" value="every-other-day" />
            <el-option label="按需服用" value="as-needed" />
            <el-option label="自定义" value="custom" />
          </el-select>
        </el-form-item>

        <el-form-item label="服药时间" prop="times" v-if="form.frequency !== 'as-needed'">
          <div class="time-picker-list">
            <el-time-picker
              v-for="(time, index) in form.times"
              :key="index"
              v-model="form.times[index]"
              format="HH:mm"
              value-format="HH:mm"
              placeholder="选择时间"
              style="width: 140px; margin-right: 8px; margin-bottom: 8px"
            />
            <el-button
              type="primary"
              circle
              size="small"
              @click="addTime"
              v-if="form.times.length < 8"
            >
              <el-icon><Plus /></el-icon>
            </el-button>
            <el-button
              type="danger"
              circle
              size="small"
              @click="removeTime"
              v-if="form.times.length > 1"
            >
              <el-icon><Minus /></el-icon>
            </el-button>
          </div>
        </el-form-item>

        <el-form-item label="服用方式" prop="usage">
          <el-select v-model="form.usage" placeholder="选择服用方式" style="width: 100%">
            <el-option label="饭前" value="before-meal" />
            <el-option label="饭后" value="after-meal" />
            <el-option label="随餐" value="with-meal" />
            <el-option label="空腹" value="empty-stomach" />
            <el-option label="睡前" value="before-sleep" />
            <el-option label="任意时间" value="any-time" />
          </el-select>
        </el-form-item>

        <el-form-item label="开始日期" prop="startDate">
          <el-date-picker
            v-model="form.startDate"
            type="date"
            placeholder="选择开始日期"
            style="width: 100%"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>

        <el-form-item label="结束日期" prop="endDate">
          <el-date-picker
            v-model="form.endDate"
            type="date"
            placeholder="选择结束日期"
            style="width: 100%"
            value-format="YYYY-MM-DD"
            :disabled-date="disabledEndDate"
          />
        </el-form-item>

        <el-form-item label="总数量" prop="totalQuantity">
          <el-input-number
            v-model="form.totalQuantity"
            :min="1"
            :max="1000"
            style="width: 200px"
          />
          <span class="unit-text">{{ form.unit }}</span>
        </el-form-item>

        <el-form-item label="注意事项">
          <el-input
            v-model="form.notes"
            type="textarea"
            :rows="4"
            placeholder="请输入注意事项，如禁忌、副作用等"
          />
        </el-form-item>

        <el-form-item label="启用提醒">
          <el-switch v-model="form.reminderEnabled" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            <el-icon><Check /></el-icon>
            {{ isEdit ? '保存' : '创建' }}计划
          </el-button>
          <el-button @click="handleCancel">
            <el-icon><Close /></el-icon>
            取消
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Edit, Check, Close, Plus, Minus } from '@element-plus/icons-vue'

const router = useRouter()
const formRef = ref(null)
const submitting = ref(false)

const props = defineProps({
  planId: {
    type: String,
    default: null
  }
})

const isEdit = computed(() => !!props.planId)

const form = ref({
  name: '',
  type: 'tablet',
  dosage: 1,
  unit: '片',
  frequency: 'once-daily',
  times: ['08:00'],
  usage: 'after-meal',
  startDate: '',
  endDate: '',
  totalQuantity: 30,
  notes: '',
  reminderEnabled: true
})

const rules = {
  name: [
    { required: true, message: '请输入药品名称', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择药品类型', trigger: 'change' }
  ],
  dosage: [
    { required: true, message: '请输入单次剂量', trigger: 'blur' }
  ],
  frequency: [
    { required: true, message: '请选择服药频次', trigger: 'change' }
  ],
  times: [
    { required: true, message: '请选择服药时间', trigger: 'change' }
  ],
  usage: [
    { required: true, message: '请选择服用方式', trigger: 'change' }
  ],
  startDate: [
    { required: true, message: '请选择开始日期', trigger: 'change' }
  ],
  endDate: [
    { required: true, message: '请选择结束日期', trigger: 'change' }
  ],
  totalQuantity: [
    { required: true, message: '请输入总数量', trigger: 'blur' }
  ]
}

// 添加时间
const addTime = () => {
  if (form.value.times.length < 8) {
    form.value.times.push('12:00')
  }
}

// 删除时间
const removeTime = () => {
  if (form.value.times.length > 1) {
    form.value.times.pop()
  }
}

// 禁用结束日期
const disabledEndDate = (date) => {
  if (!form.value.startDate) return false
  return date < new Date(form.value.startDate)
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        // 模拟API调用
        await new Promise(resolve => setTimeout(resolve, 1000))
        ElMessage.success(isEdit.value ? '计划保存成功' : '计划创建成功')
        router.push('/medication/plans')
      } catch (error) {
        ElMessage.error('操作失败：' + error.message)
      } finally {
        submitting.value = false
      }
    }
  })
}

// 取消
const handleCancel = () => {
  router.back()
}
</script>

<style scoped>
.plan-form {
  max-width: 800px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 500;
}

.time-picker-list {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
}

.unit-text {
  margin-left: 12px;
  color: #909399;
  font-size: 14px;
}

:deep(.el-form-item) {
  margin-bottom: 24px;
}
</style>