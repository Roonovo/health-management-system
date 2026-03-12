<template>
  <div class="health-data">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>健康数据</span>
          <el-button type="primary" @click="showAddDialog = true">
            <el-icon><Plus /></el-icon>
            添加数据
          </el-button>
        </div>
      </template>

      <!-- 日期范围选择 -->
      <div class="filters">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
          @change="loadData"
        />
        <el-radio-group v-model="quickDate" @change="handleQuickDate">
          <el-radio-button label="today">今天</el-radio-button>
          <el-radio-button label="week">本周</el-radio-button>
          <el-radio-button label="month">本月</el-radio-button>
        </el-radio-group>
      </div>

      <!-- 数据分类标签页 -->
      <el-tabs v-model="activeTab" @tab-change="loadData">
        <el-tab-pane label="血压" name="blood-pressure">
          <div class="data-content">
            <!-- 图表 -->
            <div id="blood-pressure-chart" style="width: 100%; height: 300px"></div>

            <!-- 数据表格 -->
            <el-table :data="bloodPressureData" stripe style="margin-top: 20px">
              <el-table-column prop="systolic" label="收缩压 (mmHg)" align="center" />
              <el-table-column prop="diastolic" label="舒张压 (mmHg)" align="center" />
              <el-table-column prop="heartRate" label="心率 (bpm)" align="center" />
              <el-table-column prop="measureTime" label="测量时间" align="center" width="180" />
              <el-table-column label="状态" align="center" width="100">
                <template #default="{ row }">
                  <el-tag :type="getStatusType(row.status)">
                    {{ row.statusLabel }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" align="center" width="150">
                <template #default="{ row }">
                  <el-button type="primary" text @click="handleEdit(row)">
                    编辑
                  </el-button>
                  <el-button type="danger" text @click="handleDelete(row.id)">
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>

            <el-empty v-if="bloodPressureData.length === 0" description="暂无数据" />
          </div>
        </el-tab-pane>

        <el-tab-pane label="血糖" name="blood-sugar">
          <div class="data-content">
            <div id="blood-sugar-chart" style="width: 100%; height: 300px"></div>

            <el-table :data="bloodSugarData" stripe style="margin-top: 20px">
              <el-table-column prop="value" label="血糖值 (mmol/L)" align="center" />
              <el-table-column prop="mealType" label="测量时段" align="center" />
              <el-table-column prop="measureTime" label="测量时间" align="center" width="180" />
              <el-table-column label="状态" align="center" width="100">
                <template #default="{ row }">
                  <el-tag :type="getStatusType(row.status)">
                    {{ row.statusLabel }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" align="center" width="150">
                <template #default="{ row }">
                  <el-button type="primary" text @click="handleEdit(row)">
                    编辑
                  </el-button>
                  <el-button type="danger" text @click="handleDelete(row.id)">
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>

            <el-empty v-if="bloodSugarData.length === 0" description="暂无数据" />
          </div>
        </el-tab-pane>

        <el-tab-pane label="体重" name="weight">
          <div class="data-content">
            <div id="weight-chart" style="width: 100%; height: 300px"></div>

            <el-table :data="weightData" stripe style="margin-top: 20px">
              <el-table-column prop="value" label="体重 (kg)" align="center" />
              <el-table-column prop="bmi" label="BMI" align="center" />
              <el-table-column prop="measureTime" label="测量时间" align="center" width="180" />
              <el-table-column label="状态" align="center" width="100">
                <template #default="{ row }">
                  <el-tag :type="getStatusType(row.status)">
                    {{ row.statusLabel }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" align="center" width="150">
                <template #default="{ row }">
                  <el-button type="primary" text @click="handleEdit(row)">
                    编辑
                  </el-button>
                  <el-button type="danger" text @click="handleDelete(row.id)">
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>

            <el-empty v-if="weightData.length === 0" description="暂无数据" />
          </div>
        </el-tab-pane>

        <el-tab-pane label="心率" name="heart-rate">
          <div class="data-content">
            <div id="heart-rate-chart" style="width: 100%; height: 300px"></div>

            <el-table :data="heartRateData" stripe style="margin-top: 20px">
              <el-table-column prop="value" label="心率 (bpm)" align="center" />
              <el-table-column prop="measureTime" label="测量时间" align="center" width="180" />
              <el-table-column label="状态" align="center" width="100">
                <template #default="{ row }">
                  <el-tag :type="getStatusType(row.status)">
                    {{ row.statusLabel }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" align="center" width="150">
                <template #default="{ row }">
                  <el-button type="primary" text @click="handleEdit(row)">
                    编辑
                  </el-button>
                  <el-button type="danger" text @click="handleDelete(row.id)">
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>

            <el-empty v-if="heartRateData.length === 0" description="暂无数据" />
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 添加/编辑数据对话框 -->
    <el-dialog
      v-model="showAddDialog"
      :title="isEdit ? '编辑数据' : '添加数据'"
      width="500px"
    >
      <el-form
        ref="formRef"
        :model="dataForm"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="数据类型" prop="type">
          <el-select v-model="dataForm.type" placeholder="请选择数据类型">
            <el-option label="血压" value="blood-pressure" />
            <el-option label="血糖" value="blood-sugar" />
            <el-option label="体重" value="weight" />
            <el-option label="心率" value="heart-rate" />
          </el-select>
        </el-form-item>

        <!-- 血压 -->
        <template v-if="dataForm.type === 'blood-pressure'">
          <el-form-item label="收缩压" prop="systolic">
            <el-input-number
              v-model="dataForm.systolic"
              :min="60"
              :max="250"
              placeholder="收缩压"
            />
            <span style="margin-left: 10px">mmHg</span>
          </el-form-item>
          <el-form-item label="舒张压" prop="diastolic">
            <el-input-number
              v-model="dataForm.diastolic"
              :min="40"
              :max="150"
              placeholder="舒张压"
            />
            <span style="margin-left: 10px">mmHg</span>
          </el-form-item>
          <el-form-item label="心率" prop="heartRate">
            <el-input-number
              v-model="dataForm.heartRate"
              :min="40"
              :max="200"
              placeholder="心率"
            />
            <span style="margin-left: 10px">bpm</span>
          </el-form-item>
        </template>

        <!-- 血糖 -->
        <template v-if="dataForm.type === 'blood-sugar'">
          <el-form-item label="血糖值" prop="value">
            <el-input-number
              v-model="dataForm.value"
              :min="2"
              :max="30"
              :precision="1"
              placeholder="血糖值"
            />
            <span style="margin-left: 10px">mmol/L</span>
          </el-form-item>
          <el-form-item label="测量时段" prop="mealType">
            <el-select v-model="dataForm.mealType" placeholder="请选择测量时段">
              <el-option label="空腹" value="fasting" />
              <el-option label="早餐后" value="breakfast" />
              <el-option label="午餐前" value="before-lunch" />
              <el-option label="午餐后" value="lunch" />
              <el-option label="晚餐前" value="before-dinner" />
              <el-option label="晚餐后" value="dinner" />
              <el-option label="睡前" value="bedtime" />
            </el-select>
          </el-form-item>
        </template>

        <!-- 体重 -->
        <template v-if="dataForm.type === 'weight'">
          <el-form-item label="体重" prop="value">
            <el-input-number
              v-model="dataForm.value"
              :min="30"
              :max="200"
              :precision="1"
              placeholder="体重"
            />
            <span style="margin-left: 10px">kg</span>
          </el-form-item>
        </template>

        <!-- 心率 -->
        <template v-if="dataForm.type === 'heart-rate'">
          <el-form-item label="心率" prop="value">
            <el-input-number
              v-model="dataForm.value"
              :min="40"
              :max="200"
              placeholder="心率"
            />
            <span style="margin-left: 10px">bpm</span>
          </el-form-item>
        </template>

        <el-form-item label="测量时间" prop="measureTime">
          <el-date-picker
            v-model="dataForm.measureTime"
            type="datetime"
            placeholder="选择测量时间"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>

        <el-form-item label="备注">
          <el-input
            v-model="dataForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
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
import { ref, reactive, onMounted, watch, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import {
  getBloodPressureData,
  getBloodSugarData,
  getWeightData,
  getHeartRateData,
  addHealthData,
  updateHealthData,
  deleteHealthData
} from '@/api/health'

const activeTab = ref('blood-pressure')
const dateRange = ref([])
const quickDate = ref('week')
const showAddDialog = ref(false)
const isEdit = ref(false)

// 数据
const bloodPressureData = ref([])
const bloodSugarData = ref([])
const weightData = ref([])
const heartRateData = ref([])

// 表单
const formRef = ref()
const dataForm = reactive({
  type: 'blood-pressure',
  systolic: null,
  diastolic: null,
  heartRate: null,
  value: null,
  mealType: '',
  measureTime: '',
  remark: ''
})

const formRules = {
  type: [{ required: true, message: '请选择数据类型', trigger: 'change' }],
  systolic: [{ required: true, message: '请输入收缩压', trigger: 'blur' }],
  diastolic: [{ required: true, message: '请输入舒张压', trigger: 'blur' }],
  value: [{ required: true, message: '请输入数值', trigger: 'blur' }],
  measureTime: [{ required: true, message: '请选择测量时间', trigger: 'change' }]
}

// 图表实例
let bloodPressureChart = null
let bloodSugarChart = null
let weightChart = null
let heartRateChart = null

// 获取状态标签类型
const getStatusType = (status) => {
  const typeMap = {
    normal: 'success',
    warning: 'warning',
    danger: 'danger'
  }
  return typeMap[status] || 'info'
}

// 快捷日期选择
const handleQuickDate = (value) => {
  const today = new Date()
  let startDate = new Date()

  switch (value) {
    case 'today':
      startDate = today
      break
    case 'week':
      startDate.setDate(today.getDate() - 7)
      break
    case 'month':
      startDate.setMonth(today.getMonth() - 1)
      break
  }

  dateRange.value = [
    startDate.toISOString().split('T')[0],
    today.toISOString().split('T')[0]
  ]

  loadData()
}

// 加载数据
const loadData = async () => {
  try {
    const params = {
      startDate: dateRange.value[0],
      endDate: dateRange.value[1]
    }

    switch (activeTab.value) {
      case 'blood-pressure':
        await loadBloodPressureData(params)
        break
      case 'blood-sugar':
        await loadBloodSugarData(params)
        break
      case 'weight':
        await loadWeightData(params)
        break
      case 'heart-rate':
        await loadHeartRateData(params)
        break
    }
  } catch (error) {
    ElMessage.error('加载数据失败：' + error.message)
  }
}

// 加载血压数据
const loadBloodPressureData = async (params) => {
  const res = await getBloodPressureData(params)
  if (res.success) {
    bloodPressureData.value = res.data
    nextTick(() => {
      initBloodPressureChart()
    })
  }
}

// 加载血糖数据
const loadBloodSugarData = async (params) => {
  const res = await getBloodSugarData(params)
  if (res.success) {
    bloodSugarData.value = res.data
    nextTick(() => {
      initBloodSugarChart()
    })
  }
}

// 加载体重数据
const loadWeightData = async (params) => {
  const res = await getWeightData(params)
  if (res.success) {
    weightData.value = res.data
    nextTick(() => {
      initWeightChart()
    })
  }
}

// 加载心率数据
const loadHeartRateData = async (params) => {
  const res = await getHeartRateData(params)
  if (res.success) {
    heartRateData.value = res.data
    nextTick(() => {
      initHeartRateChart()
    })
  }
}

// 初始化血压图表
const initBloodPressureChart = () => {
  const chartDom = document.getElementById('blood-pressure-chart')
  if (!chartDom) return

  bloodPressureChart = echarts.init(chartDom)

  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['收缩压', '舒张压']
    },
    xAxis: {
      type: 'category',
      data: bloodPressureData.value.map(item => item.measureTime.substring(5, 16))
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '收缩压',
        type: 'line',
        data: bloodPressureData.value.map(item => item.systolic),
        smooth: true,
        itemStyle: { color: '#409EFF' }
      },
      {
        name: '舒张压',
        type: 'line',
        data: bloodPressureData.value.map(item => item.diastolic),
        smooth: true,
        itemStyle: { color: '#67C23A' }
      }
    ]
  }

  bloodPressureChart.setOption(option)
}

// 初始化血糖图表
const initBloodSugarChart = () => {
  const chartDom = document.getElementById('blood-sugar-chart')
  if (!chartDom) return

  bloodSugarChart = echarts.init(chartDom)

  const option = {
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: bloodSugarData.value.map(item => item.measureTime.substring(5, 16))
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '血糖',
        type: 'line',
        data: bloodSugarData.value.map(item => item.value),
        smooth: true,
        itemStyle: { color: '#E6A23C' }
      }
    ]
  }

  bloodSugarChart.setOption(option)
}

// 初始化体重图表
const initWeightChart = () => {
  const chartDom = document.getElementById('weight-chart')
  if (!chartDom) return

  weightChart = echarts.init(chartDom)

  const option = {
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: weightData.value.map(item => item.measureTime.substring(5, 16))
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '体重',
        type: 'line',
        data: weightData.value.map(item => item.value),
        smooth: true,
        itemStyle: { color: '#F56C6C' }
      }
    ]
  }

  weightChart.setOption(option)
}

// 初始化心率图表
const initHeartRateChart = () => {
  const chartDom = document.getElementById('heart-rate-chart')
  if (!chartDom) return

  heartRateChart = echarts.init(chartDom)

  const option = {
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: heartRateData.value.map(item => item.measureTime.substring(5, 16))
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '心率',
        type: 'line',
        data: heartRateData.value.map(item => item.value),
        smooth: true,
        itemStyle: { color: '#909399' }
      }
    ]
  }

  heartRateChart.setOption(option)
}

// 编辑
const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(dataForm, row)
  showAddDialog.value = true
}

// 删除
const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这条数据吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const res = await deleteHealthData(id, activeTab.value)
    if (res.success) {
      ElMessage.success('删除成功')
      loadData()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败：' + error.message)
    }
  }
}

// 提交
const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  try {
    const apiFunc = isEdit.value ? updateHealthData : addHealthData
    const res = await apiFunc(dataForm)

    if (res.success) {
      ElMessage.success(isEdit.value ? '更新成功' : '添加成功')
      showAddDialog.value = false
      loadData()
    }
  } catch (error) {
    ElMessage.error('操作失败：' + error.message)
  }
}

// 监听标签页切换
watch(activeTab, () => {
  loadData()
})

onMounted(() => {
  handleQuickDate('week')

  // 响应式调整图表
  window.addEventListener('resize', () => {
    bloodPressureChart?.resize()
    bloodSugarChart?.resize()
    weightChart?.resize()
    heartRateChart?.resize()
  })
})
</script>

<style scoped>
.health-data {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.filters {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.data-content {
  margin-top: 20px;
}
</style>