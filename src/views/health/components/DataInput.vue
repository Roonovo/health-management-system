<template>
  <div class="data-input">
    <el-card>
      <template #header>
        <div class="card-header">
          <el-icon><Edit /></el-icon>
          <span>健康数据录入</span>
        </div>
      </template>

      <el-tabs v-model="activeTab" type="border-card">
        <!-- 血压录入 -->
        <el-tab-pane label="血压" name="bloodPressure">
          <el-form
            ref="bloodPressureFormRef"
            :model="bloodPressureForm"
            :rules="bloodPressureRules"
            label-width="120px"
          >
            <el-form-item label="测量时间" prop="measureTime">
              <el-date-picker
                v-model="bloodPressureForm.measureTime"
                type="datetime"
                placeholder="选择测量时间"
                style="width: 100%"
                :disabled-date="disabledDate"
              />
            </el-form-item>

            <el-form-item label="收缩压" prop="systolic">
              <el-input-number
                v-model="bloodPressureForm.systolic"
                :min="60"
                :max="250"
                :step="1"
                style="width: 100%"
              />
              <span class="unit-text">mmHg</span>
            </el-form-item>

            <el-form-item label="舒张压" prop="diastolic">
              <el-input-number
                v-model="bloodPressureForm.diastolic"
                :min="40"
                :max="150"
                :step="1"
                style="width: 100%"
              />
              <span class="unit-text">mmHg</span>
            </el-form-item>

            <el-form-item label="心率" prop="pulse">
              <el-input-number
                v-model="bloodPressureForm.pulse"
                :min="40"
                :max="200"
                :step="1"
                style="width: 100%"
              />
              <span class="unit-text">次/分</span>
            </el-form-item>

            <el-form-item label="测量位置">
              <el-radio-group v-model="bloodPressureForm.position">
                <el-radio label="left">左臂</el-radio>
                <el-radio label="right">右臂</el-radio>
              </el-radio-group>
            </el-form-item>

            <el-form-item label="测量姿势">
              <el-radio-group v-model="bloodPressureForm.posture">
                <el-radio label="sitting">坐位</el-radio>
                <el-radio label="lying">卧位</el-radio>
                <el-radio label="standing">站位</el-radio>
              </el-radio-group>
            </el-form-item>

            <el-form-item label="备注">
              <el-input
                v-model="bloodPressureForm.notes"
                type="textarea"
                :rows="3"
                placeholder="记录测量时的特殊情况、症状等"
              />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="submitBloodPressure" :loading="submitting">
                <el-icon><Check /></el-icon>
                保存数据
              </el-button>
              <el-button @click="resetBloodPressureForm">
                <el-icon><Refresh /></el-icon>
                重置
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 血糖录入 -->
        <el-tab-pane label="血糖" name="bloodSugar">
          <el-form
            ref="bloodSugarFormRef"
            :model="bloodSugarForm"
            :rules="bloodSugarRules"
            label-width="120px"
          >
            <el-form-item label="测量时间" prop="measureTime">
              <el-date-picker
                v-model="bloodSugarForm.measureTime"
                type="datetime"
                placeholder="选择测量时间"
                style="width: 100%"
                :disabled-date="disabledDate"
              />
            </el-form-item>

            <el-form-item label="血糖值" prop="value">
              <el-input-number
                v-model="bloodSugarForm.value"
                :min="2"
                :max="30"
                :step="0.1"
                :precision="1"
                style="width: 100%"
              />
              <span class="unit-text">mmol/L</span>
            </el-form-item>

            <el-form-item label="测量时段" prop="mealTime">
              <el-select v-model="bloodSugarForm.mealTime" style="width: 100%">
                <el-option label="空腹" value="fasting" />
                <el-option label="早餐后" value="afterBreakfast" />
                <el-option label="午餐前" value="beforeLunch" />
                <el-option label="午餐后" value="afterLunch" />
                <el-option label="晚餐前" value="beforeDinner" />
                <el-option label="晚餐后" value="afterDinner" />
                <el-option label="睡前" value="beforeSleep" />
              </el-select>
            </el-form-item>

            <el-form-item label="用药情况">
              <el-radio-group v-model="bloodSugarForm.medicationTaken">
                <el-radio :label="true">已用药</el-radio>
                <el-radio :label="false">未用药</el-radio>
              </el-radio-group>
            </el-form-item>

            <el-form-item label="备注">
              <el-input
                v-model="bloodSugarForm.notes"
                type="textarea"
                :rows="3"
                placeholder="记录饮食、运动、用药等相关信息"
              />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="submitBloodSugar" :loading="submitting">
                <el-icon><Check /></el-icon>
                保存数据
              </el-button>
              <el-button @click="resetBloodSugarForm">
                <el-icon><Refresh /></el-icon>
                重置
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 体重录入 -->
        <el-tab-pane label="体重" name="weight">
          <el-form
            ref="weightFormRef"
            :model="weightForm"
            :rules="weightRules"
            label-width="120px"
          >
            <el-form-item label="测量时间" prop="measureTime">
              <el-date-picker
                v-model="weightForm.measureTime"
                type="datetime"
                placeholder="选择测量时间"
                style="width: 100%"
                :disabled-date="disabledDate"
              />
            </el-form-item>

            <el-form-item label="体重" prop="weight">
              <el-input-number
                v-model="weightForm.weight"
                :min="20"
                :max="300"
                :step="0.1"
                :precision="1"
                style="width: 100%"
              />
              <span class="unit-text">kg</span>
            </el-form-item>

            <el-form-item label="身高">
              <el-input-number
                v-model="weightForm.height"
                :min="100"
                :max="250"
                :step="0.1"
                :precision="1"
                style="width: 100%"
              />
              <span class="unit-text">cm</span>
            </el-form-item>

            <el-form-item label="BMI" v-if="bmi">
              <div class="bmi-result">
                <span class="bmi-value" :class="bmiStatus.class">{{ bmi }}</span>
                <el-tag :type="bmiStatus.type" size="large">{{ bmiStatus.text }}</el-tag>
              </div>
            </el-form-item>

            <el-form-item label="体脂率">
              <el-input-number
                v-model="weightForm.bodyFat"
                :min="0"
                :max="60"
                :step="0.1"
                :precision="1"
                style="width: 100%"
              />
              <span class="unit-text">%</span>
            </el-form-item>

            <el-form-item label="备注">
              <el-input
                v-model="weightForm.notes"
                type="textarea"
                :rows="3"
                placeholder="记录测量条件、饮食等信息"
              />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="submitWeight" :loading="submitting">
                <el-icon><Check /></el-icon>
                保存数据
              </el-button>
              <el-button @click="resetWeightForm">
                <el-icon><Refresh /></el-icon>
                重置
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 其他健康数据 -->
        <el-tab-pane label="其他数据" name="other">
          <el-form
            ref="otherFormRef"
            :model="otherForm"
            label-width="120px"
          >
            <el-form-item label="测量时间">
              <el-date-picker
                v-model="otherForm.measureTime"
                type="datetime"
                placeholder="选择测量时间"
                style="width: 100%"
                :disabled-date="disabledDate"
              />
            </el-form-item>

            <el-form-item label="体温">
              <el-input-number
                v-model="otherForm.temperature"
                :min="35"
                :max="42"
                :step="0.1"
                :precision="1"
                style="width: 100%"
              />
              <span class="unit-text">℃</span>
            </el-form-item>

            <el-form-item label="血氧饱和度">
              <el-input-number
                v-model="otherForm.bloodOxygen"
                :min="70"
                :max="100"
                :step="1"
                style="width: 100%"
              />
              <span class="unit-text">%</span>
            </el-form-item>

            <el-form-item label="呼吸频率">
              <el-input-number
                v-model="otherForm.respiratoryRate"
                :min="8"
                :max="40"
                :step="1"
                style="width: 100%"
              />
              <span class="unit-text">次/分</span>
            </el-form-item>

            <el-form-item label="睡眠时长">
              <el-input-number
                v-model="otherForm.sleepDuration"
                :min="0"
                :max="24"
                :step="0.5"
                :precision="1"
                style="width: 100%"
              />
              <span class="unit-text">小时</span>
            </el-form-item>

            <el-form-item label="备注">
              <el-input
                v-model="otherForm.notes"
                type="textarea"
                :rows="3"
                placeholder="记录其他相关信息"
              />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="submitOther" :loading="submitting">
                <el-icon><Check /></el-icon>
                保存数据
              </el-button>
              <el-button @click="resetOtherForm">
                <el-icon><Refresh /></el-icon>
                重置
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Edit, Check, Refresh } from '@element-plus/icons-vue'

const activeTab = ref('bloodPressure')
const submitting = ref(false)

// 表单引用
const bloodPressureFormRef = ref(null)
const bloodSugarFormRef = ref(null)
const weightFormRef = ref(null)
const otherFormRef = ref(null)

// 血压表单
const bloodPressureForm = ref({
  measureTime: new Date(),
  systolic: null,
  diastolic: null,
  pulse: null,
  position: 'left',
  posture: 'sitting',
  notes: ''
})

const bloodPressureRules = {
  measureTime: [
    { required: true, message: '请选择测量时间', trigger: 'change' }
  ],
  systolic: [
    { required: true, message: '请输入收缩压', trigger: 'blur' },
    { type: 'number', min: 60, max: 250, message: '收缩压范围: 60-250', trigger: 'blur' }
  ],
  diastolic: [
    { required: true, message: '请输入舒张压', trigger: 'blur' },
    { type: 'number', min: 40, max: 150, message: '舒张压范围: 40-150', trigger: 'blur' }
  ]
}

// 血糖表单
const bloodSugarForm = ref({
  measureTime: new Date(),
  value: null,
  mealTime: 'fasting',
  medicationTaken: false,
  notes: ''
})

const bloodSugarRules = {
  measureTime: [
    { required: true, message: '请选择测量时间', trigger: 'change' }
  ],
  value: [
    { required: true, message: '请输入血糖值', trigger: 'blur' },
    { type: 'number', min: 2, max: 30, message: '血糖值范围: 2-30', trigger: 'blur' }
  ],
  mealTime: [
    { required: true, message: '请选择测量时段', trigger: 'change' }
  ]
}

// 体重表单
const weightForm = ref({
  measureTime: new Date(),
  weight: null,
  height: null,
  bodyFat: null,
  notes: ''
})

const weightRules = {
  measureTime: [
    { required: true, message: '请选择测量时间', trigger: 'change' }
  ],
  weight: [
    { required: true, message: '请输入体重', trigger: 'blur' },
    { type: 'number', min: 20, max: 300, message: '体重范围: 20-300', trigger: 'blur' }
  ]
}

// 其他数据表单
const otherForm = ref({
  measureTime: new Date(),
  temperature: null,
  bloodOxygen: null,
  respiratoryRate: null,
  sleepDuration: null,
  notes: ''
})

// 计算BMI
const bmi = computed(() => {
  if (weightForm.value.weight && weightForm.value.height) {
    const heightInMeters = weightForm.value.height / 100
    return (weightForm.value.weight / (heightInMeters * heightInMeters)).toFixed(1)
  }
  return null
})

// BMI状态
const bmiStatus = computed(() => {
  if (!bmi.value) return { class: '', type: 'info', text: '' }
  
  const value = parseFloat(bmi.value)
  if (value < 18.5) {
    return { class: 'bmi-low', type: 'warning', text: '偏瘦' }
  } else if (value < 24) {
    return { class: 'bmi-normal', type: 'success', text: '正常' }
  } else if (value < 28) {
    return { class: 'bmi-high', type: 'warning', text: '超重' }
  } else {
    return { class: 'bmi-danger', type: 'danger', text: '肥胖' }
  }
})

// 禁用未来日期
const disabledDate = (time) => {
  return time.getTime() > Date.now()
}

// 提交血压数据
const submitBloodPressure = async () => {
  if (!bloodPressureFormRef.value) return
  
  await bloodPressureFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        // 模拟API调用
        await new Promise(resolve => setTimeout(resolve, 1000))
        ElMessage.success('血压数据保存成功')
        resetBloodPressureForm()
      } catch (error) {
        ElMessage.error('保存失败：' + error.message)
      } finally {
        submitting.value = false
      }
    }
  })
}

// 提交血糖数据
const submitBloodSugar = async () => {
  if (!bloodSugarFormRef.value) return
  
  await bloodSugarFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        await new Promise(resolve => setTimeout(resolve, 1000))
        ElMessage.success('血糖数据保存成功')
        resetBloodSugarForm()
      } catch (error) {
        ElMessage.error('保存失败：' + error.message)
      } finally {
        submitting.value = false
      }
    }
  })
}

// 提交体重数据
const submitWeight = async () => {
  if (!weightFormRef.value) return
  
  await weightFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        await new Promise(resolve => setTimeout(resolve, 1000))
        ElMessage.success('体重数据保存成功')
        resetWeightForm()
      } catch (error) {
        ElMessage.error('保存失败：' + error.message)
      } finally {
        submitting.value = false
      }
    }
  })
}

// 提交其他数据
const submitOther = async () => {
  submitting.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 1000))
    ElMessage.success('数据保存成功')
    resetOtherForm()
  } catch (error) {
    ElMessage.error('保存失败：' + error.message)
  } finally {
    submitting.value = false
  }
}

// 重置表单
const resetBloodPressureForm = () => {
  bloodPressureFormRef.value?.resetFields()
  bloodPressureForm.value.measureTime = new Date()
}

const resetBloodSugarForm = () => {
  bloodSugarFormRef.value?.resetFields()
  bloodSugarForm.value.measureTime = new Date()
}

const resetWeightForm = () => {
  weightFormRef.value?.resetFields()
  weightForm.value.measureTime = new Date()
}

const resetOtherForm = () => {
  otherForm.value = {
    measureTime: new Date(),
    temperature: null,
    bloodOxygen: null,
    respiratoryRate: null,
    sleepDuration: null,
    notes: ''
  }
}
</script>

<style scoped>
.data-input {
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

.unit-text {
  margin-left: 10px;
  color: #909399;
  font-size: 14px;
}

.bmi-result {
  display: flex;
  align-items: center;
  gap: 16px;
}

.bmi-value {
  font-size: 32px;
  font-weight: bold;
}

.bmi-value.bmi-low {
  color: #E6A23C;
}

.bmi-value.bmi-normal {
  color: #67C23A;
}

.bmi-value.bmi-high {
  color: #E6A23C;
}

.bmi-value.bmi-danger {
  color: #F56C6C;
}

:deep(.el-form-item) {
  margin-bottom: 24px;
}

:deep(.el-tabs__content) {
  padding: 20px;
}
</style>