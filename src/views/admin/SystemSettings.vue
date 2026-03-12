<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const activeMenu = ref('basic')
const showLogDetailDialog = ref(false)
const currentLog = ref(null)

// 基础设置
const basicSettings = reactive({
  systemName: '智能健康管理系统',
  logo: '',
  description: '一款专业的健康管理平台',
  announcement: '系统正在测试中',
  contact: '400-123-4567',
  website: 'https://example.com',
  icp: '京ICP备12345678号',
  copyright: '© 2024 智能健康管理系统'
})

// 安全设置
const securitySettings = reactive({
  enable2FA: false,
  minPasswordLength: 8,
  passwordComplexity: ['lowercase', 'uppercase', 'number'],
  passwordExpireDays: 0,
  maxLoginAttempts: 5,
  lockDuration: 30,
  sessionTimeout: 120,
  ipWhitelist: '',
  enableAuditLog: true
})

// 邮件设置
const emailSettings = reactive({
  enabled: false,
  smtpHost: '',
  smtpPort: 465,
  encryption: 'ssl',
  fromEmail: '',
  fromName: '',
  username: '',
  password: ''
})

// 短信设置
const smsSettings = reactive({
  enabled: false,
  provider: 'aliyun',
  accessKeyId: '',
  accessKeySecret: '',
  signName: '',
  verifyCodeTemplateId: '',
  notifyTemplateId: ''
})

// 存储设置
const storageSettings = reactive({
  driver: 'local',
  localPath: '/storage/uploads',
  localUrlPrefix: '',
  bucket: '',
  endpoint: '',
  accessKeyId: '',
  accessKeySecret: '',
  region: '',
  allowedExtensions: 'jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx',
  maxFileSize: 10
})

// 备份设置
const backupSettings = reactive({
  autoBackup: true,
  frequency: 'daily',
  keepBackups: 7
})

// 备份列表
const backupList = ref([])
const backupLoading = ref(false)

// 日志
const logs = ref([])
const logsLoading = ref(false)
const logType = ref('')
const logDateRange = ref([])
const logCurrentPage = ref(1)
const logPageSize = ref(10)
const logTotal = ref(0)

// 处理菜单选择
const handleMenuSelect = (index) => {
  activeMenu.value = index
  if (index === 'backup') {
    loadBackupList()
  } else if (index === 'logs') {
    loadLogs()
  }
}

// 处理Logo上传成功
const handleLogoSuccess = (response) => {
  if (response.success) {
    basicSettings.logo = response.data.url
    ElMessage.success('Logo上传成功')
  }
}

// 模拟 API 调用的辅助函数
const mockApiCall = (message = '操作成功') => {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve({ success: true, message })
    }, 500)
  })
}

// 保存基础设置
const saveBasicSettings = async () => {
  try {
    await mockApiCall()
    ElMessage.success('保存成功')
  } catch (error) {
    ElMessage.error('保存失败：' + error.message)
  }
}

// 保存安全设置
const saveSecuritySettings = async () => {
  try {
    await mockApiCall()
    ElMessage.success('保存成功')
  } catch (error) {
    ElMessage.error('保存失败：' + error.message)
  }
}

// 保存邮件设置
const saveEmailSettings = async () => {
  try {
    await mockApiCall()
    ElMessage.success('保存成功')
  } catch (error) {
    ElMessage.error('保存失败：' + error.message)
  }
}

// 测试邮件
const testEmail = async () => {
  try {
    ElMessage.info('正在发送测试邮件...')
    await mockApiCall()
    ElMessage.success('测试邮件发送成功，请检查收件箱')
  } catch (error) {
    ElMessage.error('测试失败：' + error.message)
  }
}

// 保存短信设置
const saveSmsSettings = async () => {
  try {
    await mockApiCall()
    ElMessage.success('保存成功')
  } catch (error) {
    ElMessage.error('保存失败：' + error.message)
  }
}

// 测试短信
const testSms = async () => {
  try {
    ElMessage.info('正在发送测试短信...')
    await mockApiCall()
    ElMessage.success('测试短信发送成功')
  } catch (error) {
    ElMessage.error('测试失败：' + error.message)
  }
}

// 保存存储设置
const saveStorageSettings = async () => {
  try {
    await mockApiCall()
    ElMessage.success('保存成功')
  } catch (error) {
    ElMessage.error('保存失败：' + error.message)
  }
}

// 保存备份设置
const saveBackupSettings = async () => {
  try {
    await mockApiCall()
    ElMessage.success('保存成功')
  } catch (error) {
    ElMessage.error('保存失败：' + error.message)
  }
}

// 加载备份列表
const loadBackupList = async () => {
  backupLoading.value = true
  try {
    // 模拟数据
    await new Promise(resolve => setTimeout(resolve, 500))
    backupList.value = [
      {
        id: 1,
        name: 'backup_2024_01_15_10_00.zip',
        size: '12.5 MB',
        type: 'auto',
        createdAt: '2024-01-15 10:00:00'
      },
      {
        id: 2,
        name: 'backup_2024_01_14_10_00.zip',
        size: '11.8 MB',
        type: 'auto',
        createdAt: '2024-01-14 10:00:00'
      }
    ]
  } catch (error) {
    ElMessage.error('加载备份列表失败：' + error.message)
  } finally {
    backupLoading.value = false
  }
}

// 创建备份
const createBackup = async () => {
  try {
    await ElMessageBox.confirm('确定要创建备份吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    ElMessage.info('正在创建备份，请稍候...')
    await mockApiCall()
    ElMessage.success('备份创建成功')
    loadBackupList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('创建备份失败')
    }
  }
}

// 下载备份
const downloadBackup = async (backup) => {
  try {
    ElMessage.info('正在准备下载...')
    await mockApiCall()
    ElMessage.success('下载成功')
    console.log('下载备份:', backup)
  } catch (error) {
    ElMessage.error('下载失败：' + error.message)
  }
}

// 恢复备份
const restoreBackup = async (backup) => {
  try {
    await ElMessageBox.confirm(
      '恢复备份将覆盖当前所有数据，此操作不可逆，确定要继续吗？',
      '警告',
      {
        confirmButtonText: '确定恢复',
        cancelButtonText: '取消',
        type: 'error'
      }
    )

    ElMessage.info('正在恢复备份，请稍候...')
    await mockApiCall()
    ElMessage.success('备份恢复成功，系统将在3秒后重新加载')
    setTimeout(() => {
      window.location.reload()
    }, 3000)
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('恢复失败')
    }
  }
}

// 删除备份
const deleteBackup = async (backup) => {
  try {
    await ElMessageBox.confirm('确定要删除这个备份吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await mockApiCall()
    ElMessage.success('删除成功')
    loadBackupList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 加载日志
const loadLogs = async () => {
  logsLoading.value = true
  try {
    // 模拟数据
    await new Promise(resolve => setTimeout(resolve, 500))
    logs.value = [
      {
        id: 1,
        type: 'login',
        username: '张三',
        action: '用户登录',
        ip: '192.168.1.100',
        userAgent: 'Chrome 120.0',
        createdAt: '2024-01-15 10:30:00',
        url: '/api/login',
        method: 'POST',
        params: { username: 'zhangsan' },
        status: 200
      },
      {
        id: 2,
        type: 'operation',
        username: '李四',
        action: '修改用户信息',
        ip: '192.168.1.101',
        userAgent: 'Firefox 121.0',
        createdAt: '2024-01-15 11:00:00',
        url: '/api/user/update',
        method: 'PUT',
        params: { userId: 123 },
        status: 200
      }
    ]
    logTotal.value = 2
  } catch (error) {
    ElMessage.error('加载日志失败：' + error.message)
  } finally {
    logsLoading.value = false
  }
}

// 清空日志
const clearLogs = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要清空所有日志吗？此操作不可逆！',
      '警告',
      {
        confirmButtonText: '确定清空',
        cancelButtonText: '取消',
        type: 'error'
      }
    )

    await mockApiCall()
    ElMessage.success('日志已清空')
    loadLogs()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('清空失败')
    }
  }
}

// 查看日志详情
const viewLogDetail = (log) => {
  currentLog.value = log
  showLogDetailDialog.value = true
}

// 获取日志类型标签
const getLogTypeTag = (type) => {
  const typeMap = {
    operation: 'primary',
    login: 'success',
    error: 'danger',
    system: 'warning'
  }
  return typeMap[type] || 'info'
}

// 获取日志类型标签文本
const getLogTypeLabel = (type) => {
  const labelMap = {
    operation: '操作日志',
    login: '登录日志',
    error: '错误日志',
    system: '系统日志'
  }
  return labelMap[type] || type
}

// 加载系统设置
const loadSystemSettings = async () => {
  try {
    // 使用已经定义的默认值
    console.log('系统设置已加载')
  } catch (error) {
    console.error('加载系统设置失败：', error)
  }
}

onMounted(() => {
  loadSystemSettings()
})
</script>

<style scoped>
/* 样式保持不变 */
.system-settings {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo-uploader .logo {
  width: 200px;
  height: 60px;
  display: block;
  object-fit: contain;
}

.logo-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 200px;
  height: 60px;
  line-height: 60px;
  text-align: center;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
}

.logo-uploader-icon:hover {
  border-color: #409EFF;
  color: #409EFF;
}

.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.backup-settings {
  margin-bottom: 30px;
}

@media (max-width: 768px) {
  .card-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .card-header > div {
    width: 100%;
  }

  .card-header > div > * {
    width: 100%;
    margin-bottom: 10px;
  }
}
</style>