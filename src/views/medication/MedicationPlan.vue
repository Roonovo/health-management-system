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
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
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
import { 
  getMedicationPlans, 
  deleteMedicationPlan,
  updateMedicationPlan 
} from '@/api/medication'

const router = useRouter()

const loading = ref(false)
const searchKeyword = ref('')
const filterStatus = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const plans = ref([])

// 过滤后的计划
const filteredPlans = computed(() => {
  let result = [...plans.value]

  if (searchKeyword.value) {
    result = result.filter(plan =>
      plan.name.toLowerCase().includes(searchKeyword.value.toLowerCase())
    )
  }

  if (filterStatus.value) {
    result = result.filter(plan => plan.status === filterStatus.value)
  }

  return result
})

// 使用 watch 监听过滤结果变化，更新总数
watch(filteredPlans, (newVal) => {
  total.value = newVal.length
}, { immediate: true })

const getFrequencyText = (frequency) => {
  const textMap = {
    'once-daily': '每日1次',
    'twice-daily': '每日2次',
    'three-times-daily': '每日3次',
    'four-times-daily': '每日4次'
  }
  return textMap[frequency] || frequency
}

const getStatusColor = (status) => {
  const colorMap = {
    active: '#67C23A',
    paused: '#E6A23C',
    completed: '#909399'
  }
  return colorMap[status] || '#409EFF'
}

const getStatusType = (status) => {
  const typeMap = {
    active: 'success',
    paused: 'warning',
    completed: 'info'
  }
  return typeMap[status] || ''
}

const getStatusText = (status) => {
  const textMap = {
    active: '进行中',
    paused: '已暂停',
    completed: '已完成'
  }
  return textMap[status] || status
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getMedicationPlans({
      status: filterStatus.value,
      keyword: searchKeyword.value
    })
    plans.value = res.data || []
  } catch (error) {
    console.error('加载失败:', error)
    ElMessage.error('加载失败')
    plans.value = []
  } finally {
    loading.value = false
  }
}

const handleCreate = () => {
  router.push('/medication/plan/create')
}

const handleView = (plan) => {
  router.push(`/medication/plan/${plan.id}`)
}

const handleEdit = (plan) => {
  router.push(`/medication/plan/${plan.id}/edit`)
}

const handleCommand = async (command, plan) => {
  try {
    switch (command) {
      case 'pause':
        await updateMedicationPlan(plan.id, { status: 'paused' })
        ElMessage.success('计划已暂停')
        break
      case 'resume':
        await updateMedicationPlan(plan.id, { status: 'active' })
        ElMessage.success('计划已继续')
        break
      case 'complete':
        await updateMedicationPlan(plan.id, { status: 'completed' })
        ElMessage.success('计划已完成')
        break
      case 'delete':
        await handleDelete(plan)
        return
    }
    await loadData()
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败')
  }
}

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
    
    await deleteMedicationPlan(plan.id)
    ElMessage.success('删除成功')
    await loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  loadData()
})
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

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>