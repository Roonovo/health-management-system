<template>
  <div class="medication-plan">
    <el-card>
      <template #header>
        <div class="card-header">
          <el-icon><Management /></el-icon>
          <span>用药计划管理</span>
          <el-button type="primary" @click="handleCreate">
            <el-icon><Plus /></el-icon>
            新建计划
          </el-button>
        </div>
      </template>

      <div class="filter-bar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索药品名称"
          clearable
          style="width: 240px"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>

        <el-select
          v-model="filterStatus"
          placeholder="状态筛选"
          clearable
          style="width: 150px"
        >
          <el-option label="全部" value="" />
          <el-option label="进行中" value="active" />
          <el-option label="已暂停" value="paused" />
          <el-option label="已完成" value="completed" />
        </el-select>

        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          style="width: 280px"
        />
      </div>

      <el-table
        :data="filteredPlans"
        stripe
        style="width: 100%"
        v-loading="loading"
      >
        <el-table-column prop="name" label="药品名称" width="180">
          <template #default="{ row }">
            <div class="med-name-cell">
              <el-icon :size="18" :color="getStatusColor(row.status)">
                <Medicine />
              </el-icon>
              <span>{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="剂量" width="120">
          <template #default="{ row }">
            {{ row.dosage }} {{ row.unit }}
          </template>
        </el-table-column>

        <el-table-column label="频次" width="120">
          <template #default="{ row }">
            {{ getFrequencyText(row.frequency) }}
          </template>
        </el-table-column>

        <el-table-column label="时间" width="200">
          <template #default="{ row }">
            <div class="time-tags">
              <el-tag
                v-for="(time, index) in row.times.slice(0, 2)"
                :key="index"
                size="small"
                type="primary"
                effect="plain"
              >
                {{ time }}
              </el-tag>
              <span v-if="row.times.length > 2" class="more-times">
                +{{ row.times.length - 2 }}
              </span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="疗程" width="200">
          <template #default="{ row }">
            <div class="duration-cell">
              <span>{{ formatDate(row.startDate) }}</span>
              <span style="margin: 0 4px">~</span>
              <span>{{ formatDate(row.endDate) }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="进度" width="150">
          <template #default="{ row }">
            <el-progress
              :percentage="getDurationPercentage(row)"
              :color="getProgressColor(getDurationPercentage(row))"
              :stroke-width="6"
            />
          </template>
        </el-table-column>

        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text size="small" @click="handleView(row)">
              查看
            </el-button>
            <el-button type="primary" text size="small" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-dropdown trigger="click" @command="(cmd) => handleCommand(cmd, row)">
              <el-button type="primary" text size="small">
                更多
                <el-icon><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="pause" v-if="row.status === 'active'">
                    <el-icon><VideoPause /></el-icon>
                    暂停
                  </el-dropdown-item>
                  <el-dropdown-item command="resume" v-if="row.status === 'paused'">
                    <el-icon><VideoPlay /></el-icon>
                    继续
                  </el-dropdown-item>
                  <el-dropdown-item command="complete">
                    <el-icon><CircleCheck /></el-icon>
                    标记完成
                  </el-dropdown-item>
                  <el-dropdown-item command="delete" divided>
                    <el-icon><Delete /></el-icon>
                    删除
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="loadData"
          @size-change="loadData"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Management,
  Plus,
  Search,
  Medicine,
  ArrowDown,
  VideoPause,
  VideoPlay,
  CircleCheck,
  Delete
} from '@element-plus/icons-vue'
import { format, parseISO, differenceInDays } from 'date-fns'

const router = useRouter()

const loading = ref(false)
const searchKeyword = ref('')
const filterStatus = ref('')
const dateRange = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 模拟数据
const plans = ref([
  {
    id: 1,
    name: '降压药',
    dosage: 1,
    unit: '片',
    frequency: 'twice-daily',
    times: ['08:00', '20:00'],
    startDate: '2024-01-01',
    endDate: '2024-03-31',
    status: 'active'
  },
  {
    id: 2,
    name: '降糖药',
    dosage: 1,
    unit: '片',
    frequency: 'three-times-daily',
    times: ['08:00', '13:00', '19:00'],
    startDate: '2024-01-01',
    endDate: '2024-02-29',
    status: 'active'
  },
  {
    id: 3,
    name: '阿司匹林',
    dosage: 1,
    unit: '片',
    frequency: 'once-daily',
    times: ['20:00'],
    startDate: '2023-12-01',
    endDate: '2024-01-31',
    status: 'paused'
  }
])

// 过滤后的计划
const filteredPlans = computed(() => {
  let result = plans.value

  // 关键词搜索
  if (searchKeyword.value) {
    result = result.filter(plan =>
      plan.name.toLowerCase().includes(searchKeyword.value.toLowerCase())
    )
  }

  // 状态筛选
  if (filterStatus.value) {
    result = result.filter(plan => plan.status === filterStatus.value)
  }

  total.value = result.length
  return result
})

// 获取频次文本
const getFrequencyText = (frequency) => {
  const textMap = {
    'once-daily': '每日1次',
    'twice-daily': '每日2次',
    'three-times-daily': '每日3次',
    'four-times-daily': '每日4次'
  }
  return textMap[frequency] || frequency
}

// 格式化日期
const formatDate = (dateStr) => {
  try {
    return format(parseISO(dateStr), 'yyyy-MM-dd')
  } catch {
    return dateStr
  }
}

// 获取疗程进度
const getDurationPercentage = (plan) => {
  const start = parseISO(plan.startDate)
  const end = parseISO(plan.endDate)
  const now = new Date()
  
  const total = differenceInDays(end, start)
  const passed = differenceInDays(now, start)
  
  if (passed < 0) return 0
  if (passed > total) return 100
  
  return Math.round((passed / total) * 100)
}

// 获取进度颜色
const getProgressColor = (percentage) => {
  if (percentage >= 80) return '#E6A23C'
  if (percentage >= 50) return '#409EFF'
  return '#67C23A'
}

// 获取状态颜色
const getStatusColor = (status) => {
  const colorMap = {
    active: '#67C23A',
    paused: '#E6A23C',
    completed: '#909399'
  }
  return colorMap[status] || '#409EFF'
}

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    active: 'success',
    paused: 'warning',
    completed: 'info'
  }
  return typeMap[status] || ''
}

// 获取状态文本
const getStatusText = (status) => {
  const textMap = {
    active: '进行中',
    paused: '已暂停',
    completed: '已完成'
  }
  return textMap[status] || status
}

// 加载数据
const loadData = () => {
  // 实际项目中这里应该调用API
  console.log('加载数据')
}

// 新建计划
const handleCreate = () => {
  router.push('/medication/plan/create')
}

// 查看详情
const handleView = (plan) => {
  router.push(`/medication/plan/${plan.id}`)
}

// 编辑计划
const handleEdit = (plan) => {
  router.push(`/medication/plan/${plan.id}/edit`)
}

// 处理命令
const handleCommand = async (command, plan) => {
  switch (command) {
    case 'pause':
      plan.status = 'paused'
      ElMessage.success('计划已暂停')
      break
    case 'resume':
      plan.status = 'active'
      ElMessage.success('计划已继续')
      break
    case 'complete':
      plan.status = 'completed'
      ElMessage.success('计划已完成')
      break
    case 'delete':
      await handleDelete(plan)
      break
  }
}

// 删除计划
const handleDelete = async (plan) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除 "${plan.name}" 的用药计划吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const index = plans.value.findIndex(p => p.id === plan.id)
    if (index > -1) {
      plans.value.splice(index, 1)
      ElMessage.success('删除成功')
    }
  } catch {
    // 取消删除
  }
}
</script>

<style scoped>
.medication-plan {
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

.filter-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.med-name-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.time-tags {
  display: flex;
  gap: 4px;
  align-items: center;
  flex-wrap: wrap;
}

.more-times {
  font-size: 12px;
  color: #909399;
}

.duration-cell {
  font-size: 13px;
  color: #606266;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>