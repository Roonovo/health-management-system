<template>
  <div class="health-record">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>健康档案</span>
          <el-button type="primary" @click="showEditDialog = true">
            <el-icon><Edit /></el-icon>
            编辑档案
          </el-button>
        </div>
      </template>

      <div class="record-content">
        <!-- 基本信息 -->
        <el-descriptions title="基本信息" :column="2" border>
          <el-descriptions-item label="姓名">
            {{ record.realName || '--' }}
          </el-descriptions-item>
          <el-descriptions-item label="性别">
            {{ record.gender === 'male' ? '男' : record.gender === 'female' ? '女' : '--' }}
          </el-descriptions-item>
          <el-descriptions-item label="出生日期">
            {{ record.birthday || '--' }}
          </el-descriptions-item>
          <el-descriptions-item label="年龄">
            {{ calculateAge(record.birthday) }} 岁
          </el-descriptions-item>
          <el-descriptions-item label="身高">
            {{ record.height || '--' }} cm
          </el-descriptions-item>
          <el-descriptions-item label="血型">
            {{ record.bloodType || '--' }}
          </el-descriptions-item>
          <el-descriptions-item label="婚姻状况">
            {{ getMaritalStatus(record.maritalStatus) }}
          </el-descriptions-item>
          <el-descriptions-item label="职业">
            {{ record.occupation || '--' }}
          </el-descriptions-item>
        </el-descriptions>

        <!-- 联系信息 -->
        <el-descriptions title="联系信息" :column="2" border style="margin-top: 30px">
          <el-descriptions-item label="手机号">
            {{ record.phone || '--' }}
          </el-descriptions-item>
          <el-descriptions-item label="邮箱">
            {{ record.email || '--' }}
          </el-descriptions-item>
          <el-descriptions-item label="居住地址" :span="2">
            {{ record.address || '--' }}
          </el-descriptions-item>
          <el-descriptions-item label="紧急联系人">
            {{ record.emergencyContact || '--' }}
          </el-descriptions-item>
          <el-descriptions-item label="紧急联系电话">
            {{ record.emergencyPhone || '--' }}
          </el-descriptions-item>
        </el-descriptions>

        <!-- 疾病史 -->
        <el-descriptions title="疾病史" :column="1" border style="margin-top: 30px">
          <el-descriptions-item label="既往病史">
            <el-tag
              v-for="disease in record.pastDiseases"
              :key="disease"
              style="margin-right: 10px"
              type="warning"
            >
              {{ disease }}
            </el-tag>
            <span v-if="!record.pastDiseases || record.pastDiseases.length === 0">无</span>
          </el-descriptions-item>
          <el-descriptions-item label="家族病史">
            <el-tag
              v-for="disease in record.familyDiseases"
              :key="disease"
              style="margin-right: 10px"
              type="danger"
            >
              {{ disease }}
            </el-tag>
            <span v-if="!record.familyDiseases || record.familyDiseases.length === 0">无</span>
          </el-descriptions-item>
          <el-descriptions-item label="过敏史">
            <el-tag
              v-for="allergy in record.allergies"
              :key="allergy"
              style="margin-right: 10px"
              type="info"
            >
              {{ allergy }}
            </el-tag>
            <span v-if="!record.allergies || record.allergies.length === 0">无</span>
          </el-descriptions-item>
          <el-descriptions-item label="手术史">
            {{ record.surgeryHistory || '无' }}
          </el-descriptions-item>
        </el-descriptions>

        <!-- 生活习惯 -->
        <el-descriptions title="生活习惯" :column="2" border style="margin-top: 30px">
          <el-descriptions-item label="吸烟">
            {{ getSmokingStatus(record.smoking) }}
          </el-descriptions-item>
          <el-descriptions-item label="饮酒">
            {{ getDrinkingStatus(record.drinking) }}
          </el-descriptions-item>
          <el-descriptions-item label="运动频率">
            {{ getExerciseFrequency(record.exerciseFrequency) }}
          </el-descriptions-item>
          <el-descriptions-item label="睡眠时长">
            {{ record.sleepHours || '--' }} 小时/天
          </el-descriptions-item>
          <el-descriptions-item label="饮食习惯" :span="2">
            {{ record.dietHabits || '--' }}
          </el-descriptions-item>
        </el-descriptions>

        <!-- 备注 -->
        <el-descriptions title="备注信息" :column="1" border style="margin-top: 30px">
          <el-descriptions-item label="备注">
            {{ record.remark || '--' }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-card>

    <!-- 编辑对话框 -->
    <el-dialog
      v-model="showEditDialog"
      title="编辑健康档案"
      width="800px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
      >
        <el-divider content-position="left">基本信息</el-divider>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名" prop="realName">
              <el-input v-model="form.realName" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="form.gender">
                <el-radio label="male">男</el-radio>
                <el-radio label="female">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="出生日期" prop="birthday">
              <el-date-picker
                v-model="form.birthday"
                type="date"
                placeholder="选择日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="身高" prop="height">
              <el-input-number
                v-model="form.height"
                :min="100"
                :max="250"
                placeholder="身高"
                style="width: 100%"
              />
              <span style="margin-left: 10px">cm</span>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="血型">
              <el-select v-model="form.bloodType" placeholder="请选择血型">
                <el-option label="A型" value="A" />
                <el-option label="B型" value="B" />
                <el-option label="AB型" value="AB" />
                <el-option label="O型" value="O" />
                <el-option label="不详" value="unknown" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="婚姻状况">
              <el-select v-model="form.maritalStatus" placeholder="请选择婚姻状况">
                <el-option label="未婚" value="single" />
                <el-option label="已婚" value="married" />
                <el-option label="离异" value="divorced" />
                <el-option label="丧偶" value="widowed" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="职业">
          <el-input v-model="form.occupation" placeholder="请输入职业" />
        </el-form-item>

        <el-divider content-position="left">联系信息</el-divider>

        <el-form-item label="居住地址">
          <el-input v-model="form.address" placeholder="请输入详细地址" />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="紧急联系人">
              <el-input v-model="form.emergencyContact" placeholder="请输入联系人姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="紧急联系电话">
              <el-input v-model="form.emergencyPhone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">疾病史</el-divider>

        <el-form-item label="既往病史">
          <el-select
            v-model="form.pastDiseases"
            multiple
            filterable
            allow-create
            placeholder="请选择或输入既往病史"
            style="width: 100%"
          >
            <el-option label="高血压" value="高血压" />
            <el-option label="糖尿病" value="糖尿病" />
            <el-option label="冠心病" value="冠心病" />
            <el-option label="脑卒中" value="脑卒中" />
            <el-option label="哮喘" value="哮喘" />
          </el-select>
        </el-form-item>

        <el-form-item label="家族病史">
          <el-select
            v-model="form.familyDiseases"
            multiple
            filterable
            allow-create
            placeholder="请选择或输入家族病史"
            style="width: 100%"
          >
            <el-option label="高血压" value="高血压" />
            <el-option label="糖尿病" value="糖尿病" />
            <el-option label="心脏病" value="心脏病" />
            <el-option label="癌症" value="癌症" />
          </el-select>
        </el-form-item>

        <el-form-item label="过敏史">
          <el-select
            v-model="form.allergies"
            multiple
            filterable
            allow-create
            placeholder="请选择或输入过敏史"
            style="width: 100%"
          >
            <el-option label="青霉素" value="青霉素" />
            <el-option label="头孢" value="头孢" />
            <el-option label="海鲜" value="海鲜" />
            <el-option label="花粉" value="花粉" />
          </el-select>
        </el-form-item>

        <el-form-item label="手术史">
          <el-input
            v-model="form.surgeryHistory"
            type="textarea"
            :rows="3"
            placeholder="请输入手术史"
          />
        </el-form-item>

        <el-divider content-position="left">生活习惯</el-divider>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="吸烟">
              <el-select v-model="form.smoking" placeholder="请选择">
                <el-option label="不吸烟" value="never" />
                <el-option label="已戒烟" value="quit" />
                <el-option label="偶尔吸烟" value="occasionally" />
                <el-option label="经常吸烟" value="frequently" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="饮酒">
              <el-select v-model="form.drinking" placeholder="请选择">
                <el-option label="不饮酒" value="never" />
                <el-option label="已戒酒" value="quit" />
                <el-option label="偶尔饮酒" value="occasionally" />
                <el-option label="经常饮酒" value="frequently" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="运动频率">
              <el-select v-model="form.exerciseFrequency" placeholder="请选择">
                <el-option label="从不运动" value="never" />
                <el-option label="偶尔运动" value="occasionally" />
                <el-option label="每周1-2次" value="1-2" />
                <el-option label="每周3-4次" value="3-4" />
                <el-option label="每周5次以上" value="5+" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="睡眠时长">
              <el-input-number
                v-model="form.sleepHours"
                :min="4"
                :max="12"
                placeholder="睡眠时长"
                style="width: 100%"
              />
              <span style="margin-left: 10px">小时/天</span>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="饮食习惯">
          <el-input
            v-model="form.dietHabits"
            type="textarea"
            :rows="3"
            placeholder="请描述您的饮食习惯"
          />
        </el-form-item>

        <el-form-item label="备注">
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="3"
            placeholder="其他需要说明的信息"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="loading">
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Edit } from '@element-plus/icons-vue'
import { getHealthRecord, updateHealthRecord } from '@/api/health'

const showEditDialog = ref(false)
const loading = ref(false)

const record = ref({
  realName: '',
  gender: '',
  birthday: '',
  height: null,
  bloodType: '',
  maritalStatus: '',
  occupation: '',
  phone: '',
  email: '',
  address: '',
  emergencyContact: '',
  emergencyPhone: '',
  pastDiseases: [],
  familyDiseases: [],
  allergies: [],
  surgeryHistory: '',
  smoking: '',
  drinking: '',
  exerciseFrequency: '',
  sleepHours: null,
  dietHabits: '',
  remark: ''
})

const formRef = ref()
const form = reactive({ ...record.value })

const rules = {
  realName: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  birthday: [
    { required: true, message: '请选择出生日期', trigger: 'change' }
  ]
}

// 计算年龄
const calculateAge = (birthday) => {
  if (!birthday) return '--'
  const today = new Date()
  const birthDate = new Date(birthday)
  let age = today.getFullYear() - birthDate.getFullYear()
  const monthDiff = today.getMonth() - birthDate.getMonth()
  if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
    age--
  }
  return age
}

// 获取婚姻状况标签
const getMaritalStatus = (status) => {
  const statusMap = {
    single: '未婚',
    married: '已婚',
    divorced: '离异',
    widowed: '丧偶'
  }
  return statusMap[status] || '--'
}

// 获取吸烟状况
const getSmokingStatus = (status) => {
  const statusMap = {
    never: '不吸烟',
    quit: '已戒烟',
    occasionally: '偶尔吸烟',
    frequently: '经常吸烟'
  }
  return statusMap[status] || '--'
}

// 获取饮酒状况
const getDrinkingStatus = (status) => {
  const statusMap = {
    never: '不饮酒',
    quit: '已戒酒',
    occasionally: '偶尔饮酒',
    frequently: '经常饮酒'
  }
  return statusMap[status] || '--'
}

// 获取运动频率
const getExerciseFrequency = (frequency) => {
  const frequencyMap = {
    never: '从不运动',
    occasionally: '偶尔运动',
    '1-2': '每周1-2次',
    '3-4': '每周3-4次',
    '5+': '每周5次以上'
  }
  return frequencyMap[frequency] || '--'
}

// 加载健康档案
const loadRecord = async () => {
  try {
    const res = await getHealthRecord()
    if (res.success) {
      record.value = res.data
      Object.assign(form, res.data)
    }
  } catch (error) {
    ElMessage.error('加载失败：' + error.message)
  }
}

// 提交
const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await updateHealthRecord(form)
    if (res.success) {
      ElMessage.success('保存成功')
      showEditDialog.value = false
      await loadRecord()
    }
  } catch (error) {
    ElMessage.error('保存失败：' + error.message)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadRecord()
})
</script>

<style scoped>
.health-record {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.record-content {
  padding: 20px 0;
}

:deep(.el-descriptions__title) {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 15px;
}

:deep(.el-descriptions__label) {
  font-weight: 500;
}
</style>