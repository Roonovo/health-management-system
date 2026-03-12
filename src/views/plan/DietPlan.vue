<template>
  <div class="diet-plan">
    <el-row :gutter="20">
      <!-- 左侧：营养统计 -->
      <el-col :xs="24" :md="8">
        <el-card>
          <template #header>
            <span>今日营养摄入</span>
          </template>

          <div class="nutrition-stats">
            <div class="nutrition-item">
              <div class="nutrition-icon" style="background-color: #409EFF">
                <el-icon><Food /></el-icon>
              </div>
              <div class="nutrition-info">
                <div class="nutrition-label">热量</div>
                <div class="nutrition-value">
                  {{ todayNutrition.calories }} / {{ goals.calories }}
                </div>
                <div class="nutrition-unit">千卡</div>
              </div>
              <el-progress
                type="circle"
                :percentage="calculatePercentage(todayNutrition.calories, goals.calories)"
                :width="60"
                :stroke-width="6"
              />
            </div>

            <div class="nutrition-item">
              <div class="nutrition-icon" style="background-color: #67C23A">
                <el-icon><Cherry /></el-icon>
              </div>
              <div class="nutrition-info">
                <div class="nutrition-label">碳水化合物</div>
                <div class="nutrition-value">
                  {{ todayNutrition.carbs }}g / {{ goals.carbs }}g
                </div>
              </div>
              <el-progress
                type="circle"
                :percentage="calculatePercentage(todayNutrition.carbs, goals.carbs)"
                :width="60"
                :stroke-width="6"
                color="#67C23A"
              />
            </div>

            <div class="nutrition-item">
              <div class="nutrition-icon" style="background-color: #E6A23C">
                <el-icon><MeatBall /></el-icon>
              </div>
              <div class="nutrition-info">
                <div class="nutrition-label">蛋白质</div>
                <div class="nutrition-value">
                  {{ todayNutrition.protein }}g / {{ goals.protein }}g
                </div>
              </div>
              <el-progress
                type="circle"
                :percentage="calculatePercentage(todayNutrition.protein, goals.protein)"
                :width="60"
                :stroke-width="6"
                color="#E6A23C"
              />
            </div>

            <div class="nutrition-item">
              <div class="nutrition-icon" style="background-color: #F56C6C">
                <el-icon><IceCreamRound /></el-icon>
              </div>
              <div class="nutrition-info">
                <div class="nutrition-label">脂肪</div>
                <div class="nutrition-value">
                  {{ todayNutrition.fat }}g / {{ goals.fat }}g
                </div>
              </div>
              <el-progress
                type="circle"
                :percentage="calculatePercentage(todayNutrition.fat, goals.fat)"
                :width="60"
                :stroke-width="6"
                color="#F56C6C"
              />
            </div>
          </div>
        </el-card>

        <el-card style="margin-top: 20px">
          <template #header>
            <span>饮食目标</span>
          </template>

          <el-form :model="goals" label-width="100px">
            <el-form-item label="每日热量">
              <el-input-number 
                v-model="goals.calories" 
                :min="1000" 
                :max="3000" 
                :step="100" 
              />
              <span style="margin-left: 10px">千卡</span>
            </el-form-item>

            <el-form-item label="碳水">
              <el-input-number 
                v-model="goals.carbs" 
                :min="0" 
                :max="500" 
              />
              <span style="margin-left: 10px">克</span>
            </el-form-item>

            <el-form-item label="蛋白质">
              <el-input-number 
                v-model="goals.protein" 
                :min="0" 
                :max="200" 
              />
              <span style="margin-left: 10px">克</span>
            </el-form-item>

            <el-form-item label="脂肪">
              <el-input-number 
                v-model="goals.fat" 
                :min="0" 
                :max="150" 
              />
              <span style="margin-left: 10px">克</span>
            </el-form-item>

            <el-form-item>
              <el-button type="primary" style="width: 100%" @click="saveGoals">
                保存目标
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <!-- 右侧：饮食记录 -->
      <el-col :xs="24" :md="16">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>饮食记录</span>
              <el-button type="primary" @click="showAddDialog = true">
                <el-icon><Plus /></el-icon>
                添加记录
              </el-button>
            </div>
          </template>

          <el-tabs v-model="activeTab">
            <el-tab-pane label="今日饮食" name="today">
              <div class="meal-records">
                <div
                  v-for="meal in ['breakfast', 'lunch', 'dinner', 'snack']"
                  :key="meal"
                  class="meal-section"
                >
                  <div class="meal-header">
                    <span class="meal-title">{{ getMealTitle(meal) }}</span>
                    <el-button
                      type="primary"
                      size="small"
                      text
                      @click="addMealRecord(meal)"
                    >
                      <el-icon><Plus /></el-icon>
                      添加
                    </el-button>
                  </div>

                  <div class="meal-items">
                    <div
                      v-for="record in getMealRecords(meal)"
                      :key="record.id"
                      class="meal-item"
                    >
                      <div class="meal-item-info">
                        <div class="meal-item-name">{{ record.foodName }}</div>
                        <div class="meal-item-details">
                          {{ record.amount }}{{ record.unit }} · {{ record.calories }}千卡
                        </div>
                      </div>
                      <div class="meal-item-actions">
                        <el-button 
                          type="primary" 
                          text 
                          size="small" 
                          @click="editRecord(record)"
                        >
                          编辑
                        </el-button>
                        <el-button 
                          type="danger" 
                          text 
                          size="small" 
                          @click="deleteRecord(record.id)"
                        >
                          删除
                        </el-button>
                      </div>
                    </div>

                    <el-empty
                      v-if="getMealRecords(meal).length === 0"
                      description="暂无记录"
                      :image-size="80"
                    />
                  </div>
                </div>
              </div>
            </el-tab-pane>

            <el-tab-pane label="历史记录" name="history">
              <div class="history-filters">
                <el-date-picker
                  v-model="dateRange"
                  type="daterange"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  value-format="YYYY-MM-DD"
                  @change="loadHistory"
                />
              </div>

              <el-table :data="historyRecords" stripe style="margin-top: 20px">
                <el-table-column prop="date" label="日期" width="120" />
                <el-table-column prop="mealType" label="餐次" width="100">
                  <template #default="{ row }">
                    {{ getMealTitle(row.mealType) }}
                  </template>
                </el-table-column>
                <el-table-column prop="foodName" label="食物" />
                <el-table-column prop="amount" label="份量" align="center">
                  <template #default="{ row }">
                    {{ row.amount }}{{ row.unit }}
                  </template>
                </el-table-column>
                <el-table-column 
                  prop="calories" 
                  label="热量(千卡)" 
                  align="center" 
                />
                <el-table-column 
                  prop="protein" 
                  label="蛋白质(g)" 
                  align="center" 
                />
                <el-table-column 
                  prop="carbs" 
                  label="碳水(g)" 
                  align="center" 
                />
                <el-table-column 
                  prop="fat" 
                  label="脂肪(g)" 
                  align="center" 
                />
              </el-table>
            </el-tab-pane>

            <el-tab-pane label="营养分析" name="analysis">
              <div id="nutrition-chart" style="width: 100%; height: 400px"></div>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>

    <!-- 添加/编辑饮食记录对话框 -->
    <el-dialog
      v-model="showAddDialog"
      :title="isEdit ? '编辑记录' : '添加饮食记录'"
      width="600px"
    >
      <el-form
        ref="formRef"
        :model="dietForm"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="餐次" prop="mealType">
          <el-select v-model="dietForm.mealType" placeholder="请选择餐次">
            <el-option label="早餐" value="breakfast" />
            <el-option label="午餐" value="lunch" />
            <el-option label="晚餐" value="dinner" />
            <el-option label="加餐" value="snack" />
          </el-select>
        </el-form-item>

        <el-form-item label="食物名称" prop="foodName">
          <el-autocomplete
            v-model="dietForm.foodName"
            :fetch-suggestions="searchFood"
            placeholder="请输入食物名称"
            style="width: 100%"
            @select="handleFoodSelect"
          />
        </el-form-item>

        <el-form-item label="份量" prop="amount">
          <el-input-number 
            v-model="dietForm.amount" 
            :min="1" 
            :max="1000" 
          />
          <el-select 
            v-model="dietForm.unit" 
            style="width: 100px; margin-left: 10px"
          >
            <el-option label="克" value="g" />
            <el-option label="毫升" value="ml" />
            <el-option label="个" value="个" />
            <el-option label="碗" value="碗" />
            <el-option label="份" value="份" />
          </el-select>
        </el-form-item>

        <el-form-item label="热量" prop="calories">
          <el-input-number 
            v-model="dietForm.calories" 
            :min="0" 
            :max="2000" 
          />
          <span style="margin-left: 10px">千卡</span>
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="碳水" prop="carbs">
              <el-input-number 
                v-model="dietForm.carbs" 
                :min="0" 
                :max="500" 
              />
              <span style="margin-left: 10px">克</span>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="蛋白质" prop="protein">
              <el-input-number 
                v-model="dietForm.protein" 
                :min="0" 
                :max="200" 
              />
              <span style="margin-left: 10px">克</span>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="脂肪" prop="fat">
              <el-input-number 
                v-model="dietForm.fat" 
                :min="0" 
                :max="150" 
              />
              <span style="margin-left: 10px">克</span>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="日期" prop="date">
          <el-date-picker
            v-model="dietForm.date"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="备注">
          <el-input
            v-model="dietForm.remark"
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
  Food,
  Cherry,
  IceCreamRound
} from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import {
  getDietGoals,
  updateDietGoals,
  getTodayNutrition,
  getDietRecords,
  addDietRecord,
  updateDietRecord,
  deleteDietRecord,
  searchFoodDatabase
} from '@/api/plan'

const activeTab = ref('today')
const showAddDialog = ref(false)
const isEdit = ref(false)
const dateRange = ref([])

// 今日营养
const todayNutrition = reactive({
  calories: 0,
  carbs: 0,
  protein: 0,
  fat: 0
})

// 目标
const goals = reactive({
  calories: 2000,
  carbs: 250,
  protein: 80,
  fat: 60
})

// 今日饮食记录
const todayRecords = ref([])

// 历史记录
const historyRecords = ref([])

// 表单
const formRef = ref()
const dietForm = reactive({
  id: null,
  mealType: '',
  foodName: '',
  amount: 100,
  unit: 'g',
  calories: 0,
  carbs: 0,
  protein: 0,
  fat: 0,
  date: '',
  remark: ''
})

const formRules = {
  mealType: [{ required: true, message: '请选择餐次', trigger: 'change' }],
  foodName: [{ required: true, message: '请输入食物名称', trigger: 'blur' }],
  amount: [{ required: true, message: '请输入份量', trigger: 'blur' }],
  calories: [{ required: true, message: '请输入热量', trigger: 'blur' }],
  date: [{ required: true, message: '请选择日期', trigger: 'change' }]
}

let nutritionChart = null

// 获取餐次标题
const getMealTitle = (mealType) => {
  const titles = {
    breakfast: '早餐',
    lunch: '午餐',
    dinner: '晚餐',
    snack: '加餐'
  }
  return titles[mealType] || mealType
}

// 获取指定餐次的记录
const getMealRecords = (mealType) => {
  return todayRecords.value.filter(record => record.mealType === mealType)
}

// 计算百分比
const calculatePercentage = (current, target) => {
  if (!target) return 0
  return Math.min(Math.round((current / target) * 100), 100)
}

// 搜索食物
const searchFood = async (queryString, callback) => {
  if (!queryString) {
    callback([])
    return
  }

  try {
    const res = await searchFoodDatabase(queryString)
    if (res.success) {
      const suggestions = res.data.map(item => ({
        value: item.name,
        ...item
      }))
      callback(suggestions)
    } else {
      callback([])
    }
  } catch (error) {
    callback([])
  }
}

// 选择食物
const handleFoodSelect = (item) => {
  dietForm.calories = item.calories || 0
  dietForm.carbs = item.carbs || 0
  dietForm.protein = item.protein || 0
  dietForm.fat = item.fat || 0
}

// 添加指定餐次记录
const addMealRecord = (mealType) => {
  isEdit.value = false
  Object.assign(dietForm, {
    id: null,
    mealType,
    foodName: '',
    amount: 100,
    unit: 'g',
    calories: 0,
    carbs: 0,
    protein: 0,
    fat: 0,
    date: new Date().toISOString().split('T')[0],
    remark: ''
  })
  showAddDialog.value = true
}

// 编辑记录
const editRecord = (record) => {
  isEdit.value = true
  Object.assign(dietForm, record)
  showAddDialog.value = true
}

// 删除记录
const deleteRecord = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这条记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const res = await deleteDietRecord(id)
    if (res.success) {
      ElMessage.success('删除成功')
      await loadTodayRecords()
      await loadTodayNutrition()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败：' + error.message)
    }
  }
}

// 保存目标
const saveGoals = async () => {
  try {
    const res = await updateDietGoals(goals)
    if (res.success) {
      ElMessage.success('保存成功')
    }
  } catch (error) {
    ElMessage.error('保存失败：' + error.message)
  }
}

// 提交表单
const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  try {
    const apiFunc = isEdit.value ? updateDietRecord : addDietRecord
    const res = await apiFunc(dietForm.id, dietForm)

    if (res.success) {
      ElMessage.success(isEdit.value ? '更新成功' : '添加成功')
      showAddDialog.value = false
      await loadTodayRecords()
      await loadTodayNutrition()
    }
  } catch (error) {
    ElMessage.error('操作失败：' + error.message)
  }
}

// 初始化图表
const initChart = () => {
  const chartDom = document.getElementById('nutrition-chart')
  if (!chartDom) return

  nutritionChart = echarts.init(chartDom)

  const option = {
    title: {
      text: '近7天营养摄入趋势',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['热量', '碳水', '蛋白质', '脂肪'],
      bottom: 0
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '10%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '热量',
        type: 'line',
        data: [1800, 2000, 1900, 2100, 1850, 2050, 1950],
        smooth: true,
        itemStyle: { color: '#409EFF' }
      },
      {
        name: '碳水',
        type: 'line',
        data: [220, 250, 230, 260, 225, 255, 240],
        smooth: true,
        itemStyle: { color: '#67C23A' }
      },
      {
        name: '蛋白质',
        type: 'line',
        data: [70, 80, 75, 85, 72, 82, 78],
        smooth: true,
        itemStyle: { color: '#E6A23C' }
      },
      {
        name: '脂肪',
        type: 'line',
        data: [55, 60, 58, 62, 56, 61, 59],
        smooth: true,
        itemStyle: { color: '#F56C6C' }
      }
    ]
  }

  nutritionChart.setOption(option)
}

// 加载数据
const loadGoals = async () => {
  try {
    const res = await getDietGoals()
    if (res.success) {
      Object.assign(goals, res.data)
    }
  } catch (error) {
    console.error('加载目标失败：', error)
  }
}

const loadTodayNutrition = async () => {
  try {
    const res = await getTodayNutrition()
    if (res.success) {
      Object.assign(todayNutrition, res.data)
    }
  } catch (error) {
    console.error('加载营养数据失败：', error)
  }
}

const loadTodayRecords = async () => {
  try {
    const today = new Date().toISOString().split('T')[0]
    const res = await getDietRecords({ date: today })
    
    if (res.success) {
      todayRecords.value = res.data
    }
  } catch (error) {
    console.error('加载记录失败：', error)
  }
}

const loadHistory = async () => {
  try {
    const res = await getDietRecords({
      startDate: dateRange.value[0],
      endDate: dateRange.value[1]
    })
    
    if (res.success) {
      historyRecords.value = res.data
    }
  } catch (error) {
    console.error('加载历史记录失败：', error)
  }
}

onMounted(() => {
  loadGoals()
  loadTodayNutrition()
  loadTodayRecords()

  // 设置默认日期范围（最近7天）
  const today = new Date()
  const lastWeek = new Date(today.getTime() - 7 * 24 * 60 * 60 * 1000)
  dateRange.value = [
    lastWeek.toISOString().split('T')[0],
    today.toISOString().split('T')[0]
  ]
  loadHistory()

  nextTick(() => {
    initChart()
  })

  window.addEventListener('resize', () => {
    nutritionChart?.resize()
  })
})
</script>

<style scoped>
.diet-plan {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 营养统计 */
.nutrition-stats {
  padding: 10px 0;
}

.nutrition-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 8px;
  margin-bottom: 15px;
}

.nutrition-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  flex-shrink: 0;
}

.nutrition-info {
  flex: 1;
}

.nutrition-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 5px;
}

.nutrition-value {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 3px;
}

.nutrition-unit {
  font-size: 12px;
  color: #909399;
}

/* 餐次记录 */
.meal-records {
  padding: 10px 0;
}

.meal-section {
  margin-bottom: 30px;
}

.meal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  background-color: #f0f2f5;
  border-left: 4px solid #409EFF;
  border-radius: 4px;
  margin-bottom: 15px;
}

.meal-title {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.meal-items {
  padding-left: 20px;
}

.meal-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  background-color: #fafafa;
  border-radius: 4px;
  margin-bottom: 10px;
  transition: background-color 0.3s;
}

.meal-item:hover {
  background-color: #f0f2f5;
}

.meal-item-info {
  flex: 1;
}

.meal-item-name {
  font-size: 15px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.meal-item-details {
  font-size: 13px;
  color: #909399;
}

.meal-item-actions {
  display: flex;
  gap: 10px;
}

/* 历史记录筛选 */
.history-filters {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
}
</style>