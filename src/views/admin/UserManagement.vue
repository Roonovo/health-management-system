<template>
  <div class="user-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <el-button type="primary" @click="showAddDialog = true">
            <el-icon><Plus /></el-icon>
            添加用户
          </el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input
          v-model="searchText"
          placeholder="搜索用户名、姓名或手机号"
          clearable
          style="width: 300px"
          @input="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>

        <el-select
          v-model="filterRole"
          placeholder="用户角色"
          clearable
          style="width: 150px"
          @change="handleSearch"
        >
          <el-option label="全部" value="" />
          <el-option label="管理员" value="admin" />
          <el-option label="普通用户" value="user" />
        </el-select>

        <el-select
          v-model="filterStatus"
          placeholder="账号状态"
          clearable
          style="width: 150px"
          @change="handleSearch"
        >
          <el-option label="全部" value="" />
          <el-option label="正常" value="active" />
          <el-option label="禁用" value="disabled" />
        </el-select>

        <el-button type="primary" @click="handleSearch">
          <el-icon><Search /></el-icon>
          搜索
        </el-button>

        <el-button @click="resetSearch">
          <el-icon><Refresh /></el-icon>
          重置
        </el-button>
      </div>

      <!-- 用户表格 -->
      <el-table
        :data="users"
        stripe
        style="margin-top: 20px"
        v-loading="loading"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="用户" width="200">
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
            <el-switch
              v-model="row.status"
              active-value="active"
              inactive-value="disabled"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="注册时间" width="180" />
        <el-table-column prop="lastLogin" label="最后登录" width="180" />
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text @click="viewUser(row)">
              查看
            </el-button>
            <el-button type="warning" text @click="editUser(row)">
              编辑
            </el-button>
            <el-button type="danger" text @click="deleteUser(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 20px; justify-content: center"
        @current-change="loadUsers"
        @size-change="loadUsers"
      />
    </el-card>

    <!-- 添加/编辑用户对话框 -->
    <el-dialog
      v-model="showAddDialog"
      :title="isEdit ? '编辑用户' : '添加用户'"
      width="600px"
    >
      <el-form
        ref="formRef"
        :model="userForm"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="userForm.username"
            placeholder="请输入用户名"
            :disabled="isEdit"
          />
        </el-form-item>

        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="userForm.realName" placeholder="请输入真实姓名" />
        </el-form-item>

        <el-form-item label="手机号" prop="phone">
          <el-input v-model="userForm.phone" placeholder="请输入手机号" />
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" placeholder="请输入邮箱" />
        </el-form-item>

        <el-form-item v-if="!isEdit" label="密码" prop="password">
          <el-input
            v-model="userForm.password"
            type="password"
            placeholder="请输入密码"
            show-password
          />
        </el-form-item>

        <el-form-item label="角色" prop="role">
          <el-select v-model="userForm.role" placeholder="请选择角色">
            <el-option label="管理员" value="admin" />
            <el-option label="普通用户" value="user" />
          </el-select>
        </el-form-item>

        <el-form-item label="状态">
          <el-radio-group v-model="userForm.status">
            <el-radio label="active">正常</el-radio>
            <el-radio label="disabled">禁用</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="备注">
          <el-input
            v-model="userForm.remark"
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

    <!-- 用户详情对话框 -->
    <el-dialog
      v-model="showDetailsDialog"
      title="用户详情"
      width="800px"
    >
      <el-descriptions :column="2" border v-if="currentUser">
        <el-descriptions-item label="用户名">
          {{ currentUser.username }}
        </el-descriptions-item>
        <el-descriptions-item label="真实姓名">
          {{ currentUser.realName || '--' }}
        </el-descriptions-item>
        <el-descriptions-item label="手机号">
          {{ currentUser.phone || '--' }}
        </el-descriptions-item>
        <el-descriptions-item label="邮箱">
          {{ currentUser.email || '--' }}
        </el-descriptions-item>
        <el-descriptions-item label="角色">
          <el-tag :type="getRoleType(currentUser.role)">
            {{ getRoleLabel(currentUser.role) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentUser.status === 'active' ? 'success' : 'danger'">
            {{ currentUser.status === 'active' ? '正常' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="注册时间">
          {{ currentUser.createdAt }}
        </el-descriptions-item>
        <el-descriptions-item label="最后登录">
          {{ currentUser.lastLogin }}
        </el-descriptions-item>
        <el-descriptions-item label="登录次数">
          {{ currentUser.loginCount }}
        </el-descriptions-item>
        <el-descriptions-item label="健康数据">
          {{ currentUser.healthDataCount }}
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">
          {{ currentUser.remark || '--' }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, User } from '@element-plus/icons-vue'
import {
  getUsers,
  createUser,
  updateUser,
  deleteUser as deleteUserApi,
  updateUserStatus
} from '@/api/admin'

const loading = ref(false)
const showAddDialog = ref(false)
const showDetailsDialog = ref(false)
const isEdit = ref(false)

const searchText = ref('')
const filterRole = ref('')
const filterStatus = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 用户列表
const users = ref([])

// 当前用户
const currentUser = ref(null)

// 表单
const formRef = ref()
const userForm = reactive({
  id: null,
  username: '',
  realName: '',
  phone: '',
  email: '',
  password: '',
  role: 'user',
  status: 'active',
  remark: ''
})

const formRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
}

// 获取角色类型
const getRoleType = (role) => {
  return role === 'admin' ? 'danger' : 'primary'
}

// 获取角色标签
const getRoleLabel = (role) => {
  return role === 'admin' ? '管理员' : '普通用户'
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  loadUsers()
}

// 重置搜索
const resetSearch = () => {
  searchText.value = ''
  filterRole.value = ''
  filterStatus.value = ''
  currentPage.value = 1
  loadUsers()
}

// 状态变更
const handleStatusChange = async (row) => {
  try {
    const res = await updateUserStatus(row.id, row.status)
    if (res.success) {
      ElMessage.success('状态更新成功')
    } else {
      // 恢复原状态
      row.status = row.status === 'active' ? 'disabled' : 'active'
    }
  } catch (error) {
    ElMessage.error('操作失败：' + error.message)
    row.status = row.status === 'active' ? 'disabled' : 'active'
  }
}

// 查看用户
const viewUser = (user) => {
  currentUser.value = user
  showDetailsDialog.value = true
}

// 编辑用户
const editUser = (user) => {
  isEdit.value = true
  Object.assign(userForm, user)
  showAddDialog.value = true
}

// 删除用户
const deleteUser = async (user) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除用户"${user.username}"吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const res = await deleteUserApi(user.id)
    if (res.success) {
      ElMessage.success('删除成功')
      await loadUsers()
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
    const apiFunc = isEdit.value ? updateUser : createUser
    const res = await apiFunc(userForm.id, userForm)

    if (res.success) {
      ElMessage.success(isEdit.value ? '更新成功' : '添加成功')
      showAddDialog.value = false
      
      // 重置表单
      Object.keys(userForm).forEach(key => {
        if (key !== 'role' && key !== 'status') {
          userForm[key] = ''
        }
      })
      userForm.role = 'user'
      userForm.status = 'active'
      
      await loadUsers()
    }
  } catch (error) {
    ElMessage.error('操作失败：' + error.message)
  }
}

// 加载用户列表
const loadUsers = async () => {
  loading.value = true
  
  try {
    const res = await getUsers({
      page: currentPage.value,
      pageSize: pageSize.value,
      search: searchText.value,
      role: filterRole.value,
      status: filterStatus.value
    })

    if (res.success) {
      users.value = res.data.list
      total.value = res.data.total
    }
  } catch (error) {
    ElMessage.error('加载失败：' + error.message)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.user-management {
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
  flex-wrap: wrap;
}

/* 用户单元格 */
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
  .search-bar {
    flex-direction: column;
  }

  .search-bar > * {
    width: 100% !important;
  }
}
</style>