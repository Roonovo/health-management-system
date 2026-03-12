<template>
  <div class="personal-center">
    <!-- 个人资料头部 -->
    <div class="profile-header">
      <div class="row align-items-center">
        <div class="col-md-2 text-center">
          <img :src="userAvatar" alt="用户头像" class="profile-avatar" />
          <button class="btn btn-sm btn-outline-primary mt-2" @click="changeAvatar">
            更换头像
          </button>
        </div>
        <div class="col-md-6">
          <h2 class="mb-1">{{ userName }}</h2>
          <p class="text-muted mb-2">
            <i class="fas fa-envelope me-2"></i>{{ userEmail }}
          </p>
          <p class="text-muted mb-3">
            <i class="fas fa-phone me-2"></i>{{ userPhone }}
          </p>
          <div class="d-flex gap-2">
            <span class="status-badge badge-success">
              <i class="fas fa-check-circle me-1"></i>已认证
            </span>
            <span class="status-badge badge-warning">
              <i class="fas fa-clock me-1"></i>会员有效期至2026-12-31
            </span>
          </div>
        </div>
        <div class="col-md-4 text-end">
          <button class="btn btn-primary" @click="showEditModal">
            <i class="fas fa-edit me-2"></i>编辑资料
          </button>
        </div>
      </div>
    </div>

    <!-- 基本信息卡片 -->
    <div class="row">
      <div class="col-md-6">
        <div class="info-card">
          <h5><i class="fas fa-info-circle me-2"></i>基本信息</h5>
          <div class="row mb-2">
            <div class="col-sm-4 fw-bold">出生日期</div>
            <div class="col-sm-8">{{ userProfile.birthday || '未设置' }}</div>
          </div>
          <div class="row mb-2">
            <div class="col-sm-4 fw-bold">性别</div>
            <div class="col-sm-8">{{ userProfile.gender === 'male' ? '男' : userProfile.gender === 'female' ? '女' : '未设置' }}</div>
          </div>
          <div class="row mb-2">
            <div class="col-sm-4 fw-bold">身高</div>
            <div class="col-sm-8">{{ userProfile.height ? userProfile.height + ' cm' : '未设置' }}</div>
          </div>
          <div class="row mb-2">
            <div class="col-sm-4 fw-bold">体重</div>
            <div class="col-sm-8">{{ userProfile.weight ? userProfile.weight + ' kg' : '未设置' }}</div>
          </div>
          <div class="row mb-2">
            <div class="col-sm-4 fw-bold">BMI指数</div>
            <div class="col-sm-8">{{ bmiDisplay }}</div>
          </div>
        </div>
      </div>
      <div class="col-md-6">
        <div class="info-card">
          <h5><i class="fas fa-history me-2"></i>健康档案</h5>
          <div class="mb-3">
            <h6>过敏史</h6>
            <div>{{ healthRecord.allergies || '无' }}</div>
          </div>
          <div class="mb-3">
            <h6>慢性病史</h6>
            <div>{{ healthRecord.chronicDiseases || '无' }}</div>
          </div>
          <div class="mb-3">
            <h6>用药清单</h6>
            <div>{{ healthRecord.medications || '无' }}</div>
          </div>
          <button class="btn btn-outline-primary btn-sm" @click="manageHealthRecords">
            <i class="fas fa-folder-open me-1"></i>管理健康档案
          </button>
        </div>
      </div>
    </div>

    <!-- 健康指标卡片 -->
    <div class="row">
      <div class="col-12">
        <div class="info-card">
          <h5><i class="fas fa-heartbeat me-2"></i>近期健康指标</h5>
          <div class="row">
            <div class="col-md-3" v-for="(metric, index) in healthMetrics" :key="index">
              <div class="health-metric">
                <div class="d-flex justify-content-between">
                  <div>
                    <div class="small">{{ metric.name }}</div>
                    <div class="h4 mb-0">{{ metric.value }}</div>
                  </div>
                  <i :class="metric.icon + ' fa-2x opacity-50'"></i>
                </div>
                <div class="small mt-2">{{ metric.unit }}</div>
              </div>
            </div>
          </div>
          <button class="btn btn-outline-primary mt-3" @click="recordHealthData">
            <i class="fas fa-plus me-1"></i>记录新数据
          </button>
        </div>
      </div>
    </div>

    <!-- 医疗文件管理 -->
    <div class="row">
      <div class="col-12">
        <div class="info-card">
          <div class="d-flex justify-content-between align-items-center mb-3">
            <h5 class="mb-0"><i class="fas fa-file-medical me-2"></i>医疗文件</h5>
            <label class="btn btn-primary upload-btn">
              <i class="fas fa-upload me-1"></i>上传病历
              <input type="file" ref="fileInput" accept=".pdf,.jpg,.jpeg,.png" @change="uploadMedicalFile" style="display: none;" />
            </label>
          </div>

          <div v-if="uploadStatus.show" :class="'alert alert-' + uploadStatus.type" role="alert">
            <i class="fas fa-info-circle me-2"></i>
            {{ uploadStatus.message }}
          </div>

          <div class="table-responsive">
            <table class="table table-hover">
              <thead>
                <tr>
                  <th>文件名</th>
                  <th>类型</th>
                  <th>上传时间</th>
                  <th>状态</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="file in medicalFiles" :key="file.id">
                  <td>{{ file.fileName }}</td>
                  <td><span class="badge bg-secondary">{{ file.fileType }}</span></td>
                  <td>{{ formatDate(file.uploadTime) }}</td>
                  <td>
                    <span :class="'badge ' + (file.status === 'processed' ? 'bg-success' : 'bg-warning')">
                      {{ file.status === 'processed' ? '已处理' : '待处理' }}
                    </span>
                  </td>
                  <td>
                    <button class="btn btn-sm btn-outline-primary me-2" @click="viewFile(file.id)">
                      <i class="fas fa-eye"></i>
                    </button>
                    <button class="btn btn-sm btn-outline-danger" @click="deleteFile(file.id)">
                      <i class="fas fa-trash"></i>
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>

    <!-- 编辑资料模态框 -->
    <div class="modal fade" ref="editProfileModal" tabindex="-1">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">编辑个人资料</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="saveProfile">
              <div class="row">
                <div class="col-md-6 mb-3">
                  <label class="form-label">姓名</label>
                  <input type="text" class="form-control" v-model="editForm.name" required />
                </div>
                <div class="col-md-6 mb-3">
                  <label class="form-label">邮箱</label>
                  <input type="email" class="form-control" v-model="editForm.email" required />
                </div>
              </div>
              <div class="row">
                <div class="col-md-6 mb-3">
                  <label class="form-label">手机号</label>
                  <input type="tel" class="form-control" v-model="editForm.phone" required />
                </div>
                <div class="col-md-6 mb-3">
                  <label class="form-label">出生日期</label>
                  <input type="date" class="form-control" v-model="editForm.birthday" required />
                </div>
              </div>
              <div class="row">
                <div class="col-md-6 mb-3">
                  <label class="form-label">性别</label>
                  <select class="form-select" v-model="editForm.gender">
                    <option value="male">男</option>
                    <option value="female">女</option>
                  </select>
                </div>
                <div class="col-md-6 mb-3">
                  <label class="form-label">身高 (cm)</label>
                  <input type="number" class="form-control" v-model.number="editForm.height" min="100" max="250" />
                </div>
              </div>
              <div class="row">
                <div class="col-md-6 mb-3">
                  <label class="form-label">体重 (kg)</label>
                  <input type="number" class="form-control" v-model.number="editForm.weight" step="0.1" min="20" max="200" />
                </div>
              </div>
              <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                <button type="submit" class="btn btn-primary">保存更改</button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>

    <!-- 识别结果模态框 -->
    <div class="modal fade" ref="recognitionModal" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">病历识别结果</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <div v-if="recognitionResult">
              <h6>识别到的信息：</h6>
              <ul>
                <li v-if="recognitionResult.medications"><strong>药品：</strong>{{ recognitionResult.medications }}</li>
                <li v-if="recognitionResult.diagnosis"><strong>诊断：</strong>{{ recognitionResult.diagnosis }}</li>
                <li v-if="recognitionResult.nextVisit"><strong>复诊时间：</strong>{{ recognitionResult.nextVisit }}</li>
                <li v-if="recognitionResult.doctor"><strong>医生：</strong>{{ recognitionResult.doctor }}</li>
              </ul>
              <p class="text-muted">请确认或修改以上信息，然后保存到健康档案中。</p>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">关闭</button>
            <button type="button" class="btn btn-primary" @click="saveRecognitionResult">
              保存到档案
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import * as bootstrap from 'bootstrap'

const router = useRouter()

// --- 模拟API函数 (仅用于防止报错，开发时请替换为真实API) ---
const getUserProfile = () => Promise.resolve({ 
  data: { 
    name: '张佳怡', 
    email: 'zhangjiayi@example.com', 
    phone: '138****5678', 
    birthday: '2000-05-15', 
    gender: 'female', 
    height: 165, 
    weight: 55, 
    bmi: 20.2, 
    healthRecord: { 
      allergies: '青霉素过敏', 
      chronicDiseases: '无', 
      medications: '维生素C（每日1次）' 
    } 
  } 
})

const updateUserProfile = () => Promise.resolve({ data: { success: true } })
const getMedicalFiles = () => Promise.resolve({ data: [] })
const uploadMedicalFileApi = () => Promise.resolve({ code: 200, data: { fileId: '123' } })
const deleteFileApi = () => Promise.resolve({})
// --- 模拟API函数结束 ---

// 响应式数据
const userAvatar = ref('https://randomuser.me/api/portraits/women/32.jpg')
const userName = ref('张佳怡')
const userEmail = ref('zhangjiayi@example.com')
const userPhone = ref('138****5678')
const userProfile = reactive({
  birthday: '2000-05-15',
  gender: 'female',
  height: 165,
  weight: 55,
  bmi: 20.2
})
const healthRecord = reactive({
  allergies: '青霉素过敏',
  chronicDiseases: '无',
  medications: '维生素C（每日1次）'
})
const healthMetrics = ref([
  { name: '血压', value: '120/80', unit: 'mmHg', icon: 'fas fa-tint' },
  { name: '血糖', value: '5.2', unit: 'mmol/L', icon: 'fas fa-syringe' },
  { name: '心率', value: '72', unit: '次/分钟', icon: 'fas fa-heart' },
  { name: '步数', value: '8,542', unit: '今日步数', icon: 'fas fa-walking' }
])
const medicalFiles = ref([])
const uploadStatus = reactive({
  show: false,
  type: 'info',
  message: ''
})
const editForm = reactive({
  name: '',
  email: '',
  phone: '',
  birthday: '',
  gender: 'female',
  height: null,
  weight: null
})
const recognitionResult = ref(null)

// DOM 引用
const fileInput = ref(null)
const editProfileModal = ref(null)
const recognitionModal = ref(null)

// Bootstrap Modal 实例
let editModalInstance = null
let recognitionModalInstance = null

// 计算属性
const bmiDisplay = computed(() => {
  if (!userProfile.bmi && userProfile.bmi !== 0) return '未计算'
  const status = getBmiStatus(userProfile.bmi)
  return `${userProfile.bmi.toFixed(1)} (${status})`
})

// 生命周期钩子
onMounted(() => {
  // 检查 bootstrap 是否可用，并初始化 Modal 实例
  if (bootstrap && typeof bootstrap.Modal === 'function') {
    if (editProfileModal.value) {
      editModalInstance = new bootstrap.Modal(editProfileModal.value)
    }
    if (recognitionModal.value) {
      recognitionModalInstance = new bootstrap.Modal(recognitionModal.value)
    }
  } else {
    console.warn('Bootstrap not loaded properly')
  }

  // 加载数据
  loadUserProfile()
  loadMedicalFiles()
})

onUnmounted(() => {
  // 清理 Modal 实例
  if (editModalInstance) {
    editModalInstance.dispose()
  }
  if (recognitionModalInstance) {
    recognitionModalInstance.dispose()
  }
})

// 方法
async function loadUserProfile() {
  try {
    const response = await getUserProfile()
    const user = response.data

    // 更新用户信息
    userName.value = user.name || '张佳怡'
    userEmail.value = user.email || 'zhangjiayi@example.com'
    userPhone.value = user.phone || '138****5678'

    // 更新个人资料
    Object.assign(userProfile, {
      birthday: user.birthday || '2000-05-15',
      gender: user.gender || 'female',
      height: user.height || 165,
      weight: user.weight || 55,
      bmi: user.bmi || 20.2
    })

    // 更新健康档案
    if (user.healthRecord) {
      Object.assign(healthRecord, {
        allergies: user.healthRecord.allergies || '无',
        chronicDiseases: user.healthRecord.chronicDiseases || '无',
        medications: user.healthRecord.medications || '无'
      })
    }
  } catch (error) {
    console.error('加载用户资料失败:', error)
    showToast('加载资料失败，请稍后重试', 'error')
  }
}

async function loadMedicalFiles() {
  try {
    const response = await getMedicalFiles()
    // 确保 response.data 是数组
    medicalFiles.value = Array.isArray(response.data) ? response.data : []
  } catch (error) {
    console.error('加载文件列表失败:', error)
    medicalFiles.value = [] // 出错时设置为空数组
  }
}

async function uploadMedicalFile(event) {
  const file = event.target.files[0]
  if (!file) return

  // 检查文件格式
  const allowedTypes = ['application/pdf', 'image/jpeg', 'image/jpg', 'image/png']
  if (!allowedTypes.includes(file.type)) {
    showUploadStatus('文件格式错误，请上传PDF、JPG或PNG格式的文件', 'danger')
    return
  }

  showUploadStatus('正在上传文件...', 'info')

  try {
    const formData = new FormData()
    formData.append('file', file)

    const response = await uploadMedicalFileApi(formData)

    if (response.code === 200) {
      showUploadStatus('正在识别文件内容...', 'info')

      // 模拟 OCR 识别 - 使用从响应中获取的fileId
      const fileId = response.data.fileId
      await processOCR(fileId)

      showUploadStatus('文件上传成功，识别完成！', 'success')

      // 重新加载文件列表
      setTimeout(loadMedicalFiles, 1000)
    }
  } catch (error) {
    console.error('文件上传失败:', error)
    const errorMsg = error.response?.data?.message || error.message || '未知错误'
    showUploadStatus(`文件上传失败: ${errorMsg}`, 'danger')
  } finally {
    // 清空 input 值，以便再次上传同一个文件
    if (fileInput.value) {
      fileInput.value.value = ''
    }
  }
}

// 修复：使用传入的 fileId 参数，移除未使用的变量声明
async function processOCR(fileId) {
  try {
    // 使用 fileId 获取文件信息或发送到后端
    console.log('开始处理文件ID:', fileId, '的OCR识别')

    // 模拟发送到后端进行OCR识别
    // 在实际开发中，这里应该调用真实的OCR API
    // const response = await api.ocrRecognize({ fileId: fileId })
    
    // 模拟 OCR 识别过程
    await new Promise(resolve => setTimeout(resolve, 1500))

    // 模拟识别结果（实际应该使用后端返回的数据）
    recognitionResult.value = {
      medications: '阿司匹林 100mg',
      diagnosis: '高血压',
      nextVisit: '2024-06-15',
      doctor: '王医生',
      fileId: fileId // 保存fileId以便后续使用
    }

    // 显示识别结果
    if (recognitionModalInstance) {
      recognitionModalInstance.show()
    }
    
    // 可选：将识别结果保存到本地或发送到后端
    console.log(`文件 ${fileId} OCR识别完成，识别结果已保存`)
    
    // 这里可以调用API将识别结果保存到服务器
    // await saveRecognitionResultToServer(fileId, recognitionResult.value)
    
  } catch (error) {
    console.error('OCR识别失败:', error)
    showToast('识别失败，请手动输入', 'error')
  }
}

function showEditModal() {
  // 加载当前数据到表单
  Object.assign(editForm, {
    name: userName.value,
    email: userEmail.value,
    phone: userPhone.value,
    birthday: userProfile.birthday,
    gender: userProfile.gender,
    height: userProfile.height,
    weight: userProfile.weight
  })

  if (editModalInstance) {
    editModalInstance.show()
  }
}

async function saveProfile() {
  try {
    await updateUserProfile(editForm)

    // 更新本地数据
    userName.value = editForm.name
    userEmail.value = editForm.email
    userPhone.value = editForm.phone

    Object.assign(userProfile, {
      birthday: editForm.birthday,
      gender: editForm.gender,
      height: editForm.height,
      weight: editForm.weight
    })

    // 计算并更新 BMI
    if (editForm.height && editForm.weight && editForm.height > 0) {
      const heightInMeters = editForm.height / 100
      userProfile.bmi = Number((editForm.weight / (heightInMeters * heightInMeters)).toFixed(1))
    } else {
      userProfile.bmi = null
    }

    // 关闭模态框
    if (editModalInstance) {
      editModalInstance.hide()
    }

    showToast('资料更新成功', 'success')
  } catch (error) {
    console.error('保存资料失败:', error)
    const errorMsg = error.response?.data?.message || error.message || '未知错误'
    showToast(`保存失败: ${errorMsg}`, 'error')
  }
}

function showUploadStatus(message, type = 'info') {
  uploadStatus.show = true
  uploadStatus.type = type
  uploadStatus.message = message

  // 3秒后自动隐藏
  setTimeout(() => {
    uploadStatus.show = false
  }, 3000)
}

function getBmiStatus(bmi) {
  if (bmi < 18.5) return '偏瘦'
  if (bmi < 24) return '正常'
  if (bmi < 28) return '超重'
  return '肥胖'
}

function showToast(message, type = 'info') {
  // 确保 bootstrap 可用
  if (typeof window === 'undefined' || !window.bootstrap) {
    alert(message) // 降级处理
    return
  }

  // 创建 toast 元素
  const toastId = 'toast-' + Date.now()
  const toastHtml = `
    <div id="${toastId}" class="toast align-items-center border-0 
        ${type === 'success' ? 'bg-success text-white' : 
          type === 'error' ? 'bg-danger text-white' : 'bg-info text-white'}" 
        role="alert" style="position: fixed; top: 20px; right: 20px; z-index: 9999;">
      <div class="d-flex">
        <div class="toast-body">
          <i class="fas ${type === 'success' ? 'fa-check-circle' : 
                        type === 'error' ? 'fa-exclamation-circle' : 'fa-info-circle'} 
             me-2"></i>
          ${message}
        </div>
        <button type="button" class="btn-close btn-close-white me-2 m-auto" 
                data-bs-dismiss="toast"></button>
      </div>
    </div>
  `

  document.body.insertAdjacentHTML('beforeend', toastHtml)
  const toastEl = document.getElementById(toastId)
  if (toastEl) {
    const toast = new window.bootstrap.Toast(toastEl, { delay: 3000 })
    toast.show()

    toastEl.addEventListener('hidden.bs.toast', () => {
      toastEl.remove()
    })
  }
}

function formatDate(dateString) {
  if (!dateString) return '未知时间'
  try {
    const date = new Date(dateString)
    return date.toLocaleString('zh-CN')
  } catch (e) {
    return dateString
  }
}

function changeAvatar() {
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = 'image/*'
  input.onchange = (e) => {
    const file = e.target.files[0]
    if (file) {
      const reader = new FileReader()
      reader.onload = (event) => {
        userAvatar.value = event.target.result
        showToast('头像更新成功', 'success')
      }
      reader.onerror = () => {
        showToast('头像读取失败', 'error')
      }
      reader.readAsDataURL(file)
    }
  }
  input.click()
}

function manageHealthRecords() {
  router.push('/health-records')
}

function recordHealthData() {
  router.push('/health-data?record=new')
}

function viewFile(fileId) {
  const baseUrl = import.meta.env?.VITE_API_BASE_URL || ''
  if (baseUrl) {
    window.open(`${baseUrl}/medical-files/${fileId}/view`, '_blank')
  } else {
    window.open(`/api/medical-files/${fileId}/view`, '_blank')
  }
}

async function deleteFile(fileId) {
  if (confirm('确定要删除这个文件吗？')) {
    try {
      await deleteFileApi(fileId)
      showToast('文件删除成功', 'success')
      loadMedicalFiles()
    } catch (error) {
      showToast('删除失败', 'error')
    }
  }
}

function saveRecognitionResult() {
  // 保存识别结果到健康档案
  if (recognitionResult.value?.medications) {
    healthRecord.medications = recognitionResult.value.medications
  }
  
  // 可以保存更多字段
  if (recognitionResult.value?.diagnosis) {
    // 如果想把诊断添加到慢性病史中
    // healthRecord.chronicDiseases = recognitionResult.value.diagnosis
  }

  if (recognitionModalInstance) {
    recognitionModalInstance.hide()
  }

  showToast('已保存到健康档案', 'success')
}
</script>

<style scoped>
:root {
  --primary-color: #4a90e2;
  --secondary-color: #6c757d;
  --success-color: #28a745;
  --light-bg: #f8f9fa;
}

body {
  background-color: #f5f7fa;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}
.main-content {
  margin-left: 250px;
  padding: 20px;
}
.nav-link {
  color: rgba(255, 255, 255, 0.8);
  padding: 12px 20px;
  margin: 5px 10px;
  border-radius: 8px;
  transition: all 0.3s;
  text-decoration: none;
  display: block;
}
.nav-link:hover,
.nav-link.active {
  color: white;
  background-color: rgba(255, 255, 255, 0.1);
}
.nav-link i {
  width: 24px;
  margin-right: 10px;
}
.nav-link.active {
  color: white;
  background-color: rgba(255, 255, 255, 0.1);
}
.profile-header {
  background: white;
  border-radius: 15px;
  padding: 30px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  margin-bottom: 30px;
}
.profile-avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  border: 5px solid white;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}
.info-card {
  background: white;
  border-radius: 12px;
  padding: 25px;
  margin-bottom: 20px;
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.08);
  transition: transform 0.3s;
}
.info-card:hover {
  transform: translateY(-5px);
}
.info-card h5 {
  color: var(--primary-color);
  border-bottom: 2px solid #e9ecef;
  padding-bottom: 10px;
  margin-bottom: 20px;
}
.health-metric {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 10px;
  padding: 15px;
  margin-bottom: 15px;
}
.upload-btn {
  position: relative;
  overflow: hidden;
  display: inline-block;
  cursor: pointer;
}
.btn-primary {
  background-color: var(--primary-color);
  border: none;
  padding: 10px 25px;
  border-radius: 8px;
}
.btn-primary:hover {
  background-color: #357ae8;
}
.status-badge {
  padding: 5px 12px;
  border-radius: 20px;
  font-size: 0.85rem;
}
.badge-success {
  background-color: #d4edda;
  color: #155724;
}
.badge-warning {
  background-color: #fff3cd;
  color: #856404;
}
</style>