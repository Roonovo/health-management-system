<template>
  <div class="user-table">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ title }}</span>
          <div class="header-actions">
            <el-input
              v-model="searchText"
              placeholder="搜索用户"
              clearable
              style="width: 200px"
              @input="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-button type="primary" @click="$emit('add')">
              <el-icon><Plus /></el-icon>
              添加用户
            </el-button>
          </div>
        </div>
      </template>

      <el-table
        :data="filteredData"
        stripe
        v-loading="loading"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        
        <el-table-column label="用户" min-width="200">
          <template #default="{ row }">
            <div class="user-cell">
              <el-avatar :src="row.avatar" :size="40">
                <el-icon><User /></el-icon>
              </el-avatar>
              <div class="user-info">
                <div class="user-name">{{ row.username }}</div>
                <div class="user-real-name">{{ row.realName || '--' }}</div>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="email" label="邮箱" min-width="150" />
        
        <el-table-column label="角色" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getRoleType(row.role)">
              {{ getRoleLabel(row.role) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 'active' ? 'success' : 'danger'">
              {{ row.status === 'active' ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column 
          prop="createdAt" 
          label="注册时间" 
          width="180" 
        />

        <el-table-column 
          label="操作" 
          width="200" 
          align="center" 
          fixed="right"
        >
          <template #default="{ row }">
            <el-button 
              type="primary" 
              text 
              @click="$emit('view', row)"
            >
              查看
            </el-button>
            <el-button 
              type="warning" 
              text 
              @click="$emit('edit', row)"
            >
              编辑
            </el-button>
            <el-button 
              type="danger" 
              text 
              @click="$emit('delete', row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-if="total > pageSize"
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 20px; justify-content: center"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { Search, Plus, User } from '@element-plus/icons-vue'

const props = defineProps({
  title: {
    type: String,
    default: '用户列表'
  },
  data: {
    type: Array,
    required: true
  },
  loading: {
    type: Boolean,
    default: false
  },
  total: {
    type: Number,
    default: 0
  }
})

const emit = defineEmits([
  'add',
  'view',
  'edit',
  'delete',
  'selection-change',
  'page-change',
  'size-change',
  'search'
])

const searchText = ref('')
const currentPage = ref(1)
const pageSize = ref(10)

// 过滤后的数据
const filteredData = computed(() => {
  if (!searchText.value) return props.data

  const keyword = searchText.value.toLowerCase()
  return props.data.filter(item => 
    item.username?.toLowerCase().includes(keyword) ||
    item.realName?.toLowerCase().includes(keyword) ||
    item.phone?.includes(keyword) ||
    item.email?.toLowerCase().includes(keyword)
  )
})

// 获取角色类型
const getRoleType = (role) => {
  return role === 'admin' ? 'danger' : 'primary'
}

// 获取角色标签
const getRoleLabel = (role) => {
  return role === 'admin' ? '管理员' : '普通用户'
}

// 处理搜索
const handleSearch = () => {
  emit('search', searchText.value)
}

// 处理选择变化
const handleSelectionChange = (selection) => {
  emit('selection-change', selection)
}

// 处理页码变化
const handlePageChange = (page) => {
  emit('page-change', page)
}

// 处理每页数量变化
const handleSizeChange = (size) => {
  emit('size-change', size)
}
</script>

<style scoped>
.user-table {
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 15px;
  align-items: center;
}

.user-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-info {
  flex: 1;
}

.user-name {
  font-size: 14px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 3px;
}

.user-real-name {
  font-size: 12px;
  color: #909399;
}

@media (max-width: 768px) {
  .header-actions {
    flex-direction: column;
    align-items: stretch;
  }

  .header-actions > * {
    width: 100%;
  }
}
</style>