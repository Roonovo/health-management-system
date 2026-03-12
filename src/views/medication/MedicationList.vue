<template>
  <div class="medication-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的药品</span>
          <el-button type="primary" @click="showAddDialog = true">
            <el-icon><Plus /></el-icon>
            添加药品
          </el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input
          v-model="searchText"
          placeholder="搜索药品名称"
          clearable
          style="width: 300px"
          @input="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>

        <el-select
          v-model="filterCategory"
          placeholder="药品分类"
          clearable
          style="width: 150px"
          @change="handleSearch"
        >
          <el-option label="全部" value="" />
          <el-option label="处方药" value="prescription" />
          <el-option label="非处方药" value="otc" />
          <el-option label="中成药" value="chinese" />
        </el-select>
      </div>

      <!-- 药品列表 -->
      <div class="medication-grid">
        <el-card
          v-for="med in medications"
          :key="med.id"
          class="medication-card"
          shadow="hover"
        >
          <div class="medication-content">
            <div class="medication-header">
              <div class="medication-icon" :style="{ backgroundColor: med.color }">
                <el-icon :size="32">
                  <Medicine />
                </el-icon>
              </div>
              <div class="medication-info">
                <div class="medication-name">{{ med.name }}</div>
                <el-tag :type="getCategoryType(med.category)" size="small">
                  {{ getCategoryLabel(med.category) }}
                </el-tag>
              </div>
            </div>

            <el-descriptions :column="1" size="small" style="margin-top: 15px">
              <el-descriptions-item label="规格">
                {{ med.specification }}
              </el-descriptions-item>
              <el-descriptions-item label="用法用量">
                {{ med.dosage }}
              </el-descriptions-item>
              <el-descriptions-item label="剩余数量">
                <el-tag :type="getStockType(med.stock, med.minStock)">
                  {{ med.stock }} {{ med.unit }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="有效期至">
                <span :class="{ 'expired': isExpired(med.expiryDate) }">
                  {{ med.expiryDate }}
                </span>
              </el-descriptions-item>
            </el-descriptions>

            <div class="medication-actions">
              <el-button type="primary" text @click="viewDetails(med)">
                详情
              </el-button>
              <el-button type="warning" text @click="editMedication(med)">
                编辑
              </el-button>
              <el-button type="danger" text @click="deleteMedication(med.id)">
                删除
              </el-button>
            </div>
          </div>
        </el-card>

        <el-empty v-if="medications.length === 0" description="暂无药品" />
      </div>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        style="margin-top: 20px; justify-content: center"
        @current-change="loadMedications"
      />
    </el-card>

    <!-- 添加/编辑药品对话框 -->
    <el-dialog
      v-model="showAddDialog"
      :title="isEdit ? '编辑药品' : '添加药品'"
      width="600px"
    >
      <el-form
        ref="formRef"
        :model="medicationForm"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="药品名称" prop="name">
          <el-input v-model="medicationForm.name" placeholder="请输入药品名称" />
        </el-form-item>

        <el-form-item label="分类" prop="category">
          <el-select v-model="medicationForm.category" placeholder="请选择分类">
            <el-option label="处方药" value="prescription" />
            <el-option label="非处方药" value="otc" />
            <el-option label="中成药" value="chinese" />
          </el-select>
        </el-form-item>

        <el-form-item label="规格" prop="specification">
          <el-input v-model="medicationForm.specification" placeholder="如：100mg×30片" />
        </el-form-item>

        <el-form-item label="用法用量" prop="dosage">
          <el-input
            v-model="medicationForm.dosage"
            type="textarea"
            :rows="2"
            placeholder="如：口服，一次1片，一日3次"
          />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="当前库存" prop="stock">
              <el-input-number v-model="medicationForm.stock" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单位" prop="unit">
              <el-select v-model="medicationForm.unit">
                <el-option label="片" value="片" />
                <el-option label="粒" value="粒" />
                <el-option label="盒" value="盒" />
                <el-option label="瓶" value="瓶" />
                <el-option label="支" value="支" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="最低库存">
          <el-input-number v-model="medicationForm.minStock" :min="0" style="width: 100%" />
          <span style="margin-left: 10px; color: #909399; font-size: 12px">
            库存低于此值时会提醒补充
          </span>
        </el-form-item>

        <el-form-item label="有效期" prop="expiryDate">
          <el-date-picker
            v-model="medicationForm.expiryDate"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="适应症">
          <el-input
            v-model="medicationForm.indications"
            type="textarea"
            :rows="3"
            placeholder="请输入适应症"
          />
        </el-form-item>

        <el-form-item label="注意事项">
          <el-input
            v-model="medicationForm.precautions"
            type="textarea"
            :rows="3"
            placeholder="请输入注意事项"
          />
        </el-form-item>

        <el-form-item label="备注">
          <el-input
            v-model="medicationForm.remark"
            type="textarea"
            :rows="2"
            placeholder="其他补充说明"
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

    <!-- 药品详情对话框 -->
    <el-dialog
      v-model="showDetailsDialog"
      title="药品详情"
      width="600px"
    >
      <el-descriptions :column="1" border v-if="currentMedication">
        <el-descriptions-item label="药品名称">
          {{ currentMedication.name }}
        </el-descriptions-item>
        <el-descriptions-item label="分类">
          <el-tag :type="getCategoryType(currentMedication.category)">
            {{ getCategoryLabel(currentMedication.category) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="规格">
          {{ currentMedication.specification }}
        </el-descriptions-item>
        <el-descriptions-item label="用法用量">
          {{ currentMedication.dosage }}
        </el-descriptions-item>
        <el-descriptions-item label="当前库存">
          {{ currentMedication.stock }} {{ currentMedication.unit }}
        </el-descriptions-item>
        <el-descriptions-item label="有效期">
          {{ currentMedication.expiryDate }}
        </el-descriptions-item>
        <el-descriptions-item label="适应症">
          {{ currentMedication.indications || '--' }}
        </el-descriptions-item>
        <el-descriptions-item label="注意事项">
          {{ currentMedication.precautions || '--' }}
        </el-descriptions-item>
        <el-descriptions-item label="备注">
          {{ currentMedication.remark || '--' }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Medicine } from '@element-plus/icons-vue'
import {
  getMedications,
  addMedication,
  updateMedication,
  deleteMedication as deleteMedicationApi
} from '@/api/medication'

const showAddDialog = ref(false)
const showDetailsDialog = ref(false)
const isEdit = ref(false)
const searchText = ref('')
const filterCategory = ref('')
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)

// 药品列表
const medications = ref([])

// 当前查看的药品
const currentMedication = ref(null)

// 表单
const formRef = ref()
const medicationForm = reactive({
  id: null,
  name: '',
  category: '',
  specification: '',
  dosage: '',
  stock: 0,
  unit: '片',
  minStock: 10,
  expiryDate: '',
  indications: '',
  precautions: '',
  remark: '',
  color: '#409EFF'
})

const formRules = {
  name: [{ required: true, message: '请输入药品名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  specification: [{ required: true, message: '请输入规格', trigger: 'blur' }],
  dosage: [{ required: true, message: '请输入用法用量', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存', trigger: 'blur' }],
  expiryDate: [{ required: true, message: '请选择有效期', trigger: 'change' }]
}

// 获取分类类型
const getCategoryType = (category) => {
  const types = {
    prescription: 'danger',
    otc: 'success',
    chinese: 'warning'
  }
  return types[category] || 'info'
}
// 获取分类标签
const getCategoryLabel = (category) => {
  const labels = {
    prescription: '处方药',
    otc: '非处方药',
    chinese: '中成药'
  }
  return labels[category] || category
}

// 获取库存类型
const getStockType = (stock, minStock) => {
  if (stock === 0) return 'danger'
  if (stock <= minStock) return 'warning'
  return 'success'
}

// 判断是否过期
const isExpired = (expiryDate) => {
  if (!expiryDate) return false
  return new Date(expiryDate) < new Date()
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  loadMedications()
}

// 查看详情
const viewDetails = (med) => {
  currentMedication.value = med
  showDetailsDialog.value = true
}

// 编辑药品
const editMedication = (med) => {
  isEdit.value = true
  Object.assign(medicationForm, med)
  showAddDialog.value = true
}

// 删除药品
const deleteMedication = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这个药品吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const res = await deleteMedicationApi(id)
    if (res.success) {
      ElMessage.success('删除成功')
      await loadMedications()
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
    const apiFunc = isEdit.value ? updateMedication : addMedication
    const res = await apiFunc(medicationForm.id, medicationForm)

    if (res.success) {
      ElMessage.success(isEdit.value ? '更新成功' : '添加成功')
      showAddDialog.value = false
      await loadMedications()
    }
  } catch (error) {
    ElMessage.error('操作失败：' + error.message)
  }
}

// 加载药品列表
const loadMedications = async () => {
  try {
    const res = await getMedications({
      page: currentPage.value,
      pageSize: pageSize.value,
      search: searchText.value,
      category: filterCategory.value
    })

    if (res.success) {
      medications.value = res.data.list
      total.value = res.data.total
    }
  } catch (error) {
    ElMessage.error('加载失败：' + error.message)
  }
}

onMounted(() => {
  loadMedications()
})
</script>

<style scoped>
.medication-list {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-bar {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
}

/* 药品网格 */
.medication-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.medication-card {
  transition: transform 0.3s;
}

.medication-card:hover {
  transform: translateY(-5px);
}

.medication-content {
  padding: 10px;
}

.medication-header {
  display: flex;
  gap: 15px;
  margin-bottom: 15px;
}

.medication-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.medication-info {
  flex: 1;
}

.medication-name {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 8px;
}

.expired {
  color: #f56c6c;
  font-weight: bold;
}

.medication-actions {
  display: flex;
  justify-content: space-around;
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #e4e7ed;
}

@media (max-width: 768px) {
  .medication-grid {
    grid-template-columns: 1fr;
  }

  .search-bar {
    flex-direction: column;
  }

  .search-bar .el-input,
  .search-bar .el-select {
    width: 100% !important;
  }
}
</style>