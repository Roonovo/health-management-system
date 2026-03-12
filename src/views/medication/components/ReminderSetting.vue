<template>
  <div class="reminder-setting">
    <el-card>
      <template #header>
        <div class="card-header">
          <el-icon><Bell /></el-icon>
          <span>用药提醒设置</span>
        </div>
      </template>

      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="启用提醒" prop="enabled">
          <el-switch v-model="form.enabled" />
          <span class="form-tip">关闭后将不再接收任何用药提醒</span>
        </el-form-item>

        <template v-if="form.enabled">
          <el-divider content-position="left">提醒方式</el-divider>

          <el-form-item label="推送通知">
            <el-switch v-model="form.pushNotification" />
            <span class="form-tip">在应用内显示推送通知</span>
          </el-form-item>

          <el-form-item label="短信提醒">
            <el-switch v-model="form.smsReminder" />
            <span class="form-tip">通过短信发送提醒（可能产生费用）</span>
          </el-form-item>

          <el-form-item label="邮件提醒">
            <el-switch v-model="form.emailReminder" />
            <span class="form-tip">通过邮件发送提醒</span>
          </el-form-item>

          <el-form-item label="语音提醒">
            <el-switch v-model="form.voiceReminder" />
            <span class="form-tip">播放语音提醒</span>
          </el-form-item>

          <el-divider content-position="left">提醒时间</el-divider>

          <el-form-item label="提前提醒" prop="advanceTime">
            <el-select v-model="form.advanceTime" placeholder="选择提前时间">
              <el-option label="准时" :value="0" />
              <el-option label="提前5分钟" :value="5" />
              <el-option label="提前10分钟" :value="10" />
              <el-option label="提前15分钟" :value="15" />
              <el-option label="提前30分钟" :value="30" />
            </el-select>
          </el-form-item>

          <el-form-item label="重复提醒">
            <el-switch v-model="form.repeatReminder" />
          </el-form-item>

          <el-form-item label="重复间隔" v-if="form.repeatReminder" prop="repeatInterval">
            <el-input-number
              v-model="form.repeatInterval"
              :min="1"
              :max="60"
              :step="5"
            />
            <span class="unit-text">分钟</span>
            <span class="form-tip">未确认服药时，每隔指定时间重复提醒</span>
          </el-form-item>

          <el-form-item label="重复次数" v-if="form.repeatReminder" prop="repeatTimes">
            <el-input-number
              v-model="form.repeatTimes"
              :min="1"
              :max="10"
            />
            <span class="unit-text">次</span>
          </el-form-item>

          <el-divider content-position="left">提醒内容</el-divider>

          <el-form-item label="显示剂量">
            <el-switch v-model="form.showDosage" />
          </el-form-item>

          <el-form-item label="显示用法">
            <el-switch v-model="form.showUsage" />
          </el-form-item>

          <el-form-item label="显示注意事项">
            <el-switch v-model="form.showNotes" />
          </el-form-item>

          <el-divider content-position="left">静音时段</el-divider>

          <el-form-item label="启用静音">
            <el-switch v-model="form.silentMode" />
          </el-form-item>

          <el-form-item label="静音时段" v-if="form.silentMode">
            <el-time-picker
              v-model="form.silentStartTime"
              placeholder="开始时间"
              format="HH:mm"
              value-format="HH:mm"
              style="width: 140px"
            />
            <span style="margin: 0 8px">至</span>
            <el-time-picker
              v-model="form.silentEndTime"
              placeholder="结束时间"
              format="HH:mm"
              value-format="HH:mm"
              style="width: 140px"
            />
          </el-form-item>

          <el-divider content-position="left">紧急联系人</el-divider>

          <el-form-item label="启用紧急通知">
            <el-switch v-model="form.emergencyContact" />
            <span class="form-tip">连续未服药时通知紧急联系人</span>
          </el-form-item>

          <el-form-item label="联系人姓名" v-if="form.emergencyContact">
            <el-input v-model="form.contactName" placeholder="请输入联系人姓名" />
          </el-form-item>

          <el-form-item label="联系人电话" v-if="form.emergencyContact">
            <el-input v-model="form.contactPhone" placeholder="请输入联系人电话" />
          </el-form-item>

          <el-form-item label="未服药次数" v-if="form.emergencyContact">
            <el-input-number
              v-model="form.missedDosesThreshold"
              :min="1"
              :max="10"
            />
            <span class="unit-text">次后通知</span>
          </el-form-item>
        </template>

        <el-form-item>
          <el-button type="primary" @click="handleSave" :loading="saving">
            <el-icon><Check /></el-icon>
            保存设置
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
          <el-button type="success" @click="handleTest" v-if="form.enabled">
            <el-icon><Bell /></el-icon>
            测试提醒
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Bell, Check, Refresh } from '@element-plus/icons-vue'

const formRef = ref(null)
const saving = ref(false)

const form = ref({
  enabled: true,
  pushNotification: true,
  smsReminder: false,
  emailReminder: false,
  voiceReminder: false,
  advanceTime: 10,
  repeatReminder: true,
  repeatInterval: 10,
  repeatTimes: 3,
  showDosage: true,
  showUsage: true,
  showNotes: true,
  silentMode: true,
  silentStartTime: '22:00',
  silentEndTime: '07:00',
  emergencyContact: false,
  contactName: '',
  contactPhone: '',
  missedDosesThreshold: 3
})

const rules = {
  advanceTime: [
    { required: true, message: '请选择提前时间', trigger: 'change' }
  ],
  repeatInterval: [
    { required: true, message: '请输入重复间隔', trigger: 'blur' }
  ],
  repeatTimes: [
    { required: true, message: '请输入重复次数', trigger: 'blur' }
  ]
}

// 加载设置
const loadSettings = async () => {
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    ElMessage.success('设置加载成功')
  } catch (error) {
    ElMessage.error('加载失败：' + error.message)
  }
}

// 保存设置
const handleSave = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      saving.value = true
      try {
        // 模拟API调用
        await new Promise(resolve => setTimeout(resolve, 1000))
        ElMessage.success('设置保存成功')
      } catch (error) {
        ElMessage.error('保存失败：' + error.message)
      } finally {
        saving.value = false
      }
    }
  })
}

// 重置设置
const handleReset = () => {
  formRef.value?.resetFields()
  ElMessage.info('已重置为默认设置')
}

// 测试提醒
const handleTest = () => {
  ElMessage({
    message: '这是一条测试提醒：您该服用降压药了',
    type: 'info',
    duration: 5000,
    showClose: true
  })
}

onMounted(() => {
  loadSettings()
})
</script>

<style scoped>
.reminder-setting {
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

.form-tip {
  margin-left: 12px;
  font-size: 12px;
  color: #909399;
}

.unit-text {
  margin-left: 8px;
  color: #909399;
}

:deep(.el-divider__text) {
  font-weight: 500;
  color: #606266;
}
</style>