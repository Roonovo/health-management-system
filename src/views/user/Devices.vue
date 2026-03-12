<template>
  <div class="user-devices">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>登录设备管理</span>
          <el-button type="danger" @click="logoutAllDevices">
            <el-icon><SwitchButton /></el-icon>
            退出所有设备
          </el-button>
        </div>
      </template>

      <el-alert
        title="安全提示"
        type="info"
        :closable="false"
        show-icon
        style="margin-bottom: 20px"
      >
        <p>如果发现可疑设备，请立即退出该设备登录并修改密码</p>
      </el-alert>

      <div class="devices-list">
        <div
          v-for="device in devices"
          :key="device.id"
          class="device-card"
        >
          <div class="device-icon">
            <el-icon :size="48" :color="device.current ? '#409EFF' : '#909399'">
              <Monitor v-if="device.type === 'desktop'" />
              <Cellphone v-else-if="device.type === 'mobile'" />
              <Tablet v-else />
            </el-icon>
          </div>

          <div class="device-info">
            <div class="device-header">
              <h3>{{ device.name }}</h3>
              <el-tag v-if="device.current" type="success">当前设备</el-tag>
            </div>

            <div class="device-details">
              <div class="detail-item">
                <el-icon><Location /></el-icon>
                <span>{{ device.location }}</span>
              </div>
              <div class="detail-item">
                <el-icon><Calendar /></el-icon>
                <span>最后登录：{{ device.lastLogin }}</span>
              </div>
              <div class="detail-item">
                <el-icon><Connection /></el-icon>
                <span>IP：{{ device.ip }}</span>
              </div>
              <div class="detail-item">
                <el-icon><Platform /></el-icon>
                <span>系统：{{ device.os }}</span>
              </div>
              <div class="detail-item">
                <el-icon><Postcard /></el-icon>
                <span>浏览器：{{ device.browser }}</span>
              </div>
            </div>
          </div>

          <div class="device-actions">
            <el-button
              v-if="!device.current"
              type="danger"
              @click="logoutDevice(device)"
            >
              退出登录
            </el-button>
          </div>
        </div>

        <el-empty v-if="devices.length === 0" description="暂无登录设备" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  SwitchButton,
  Monitor,
  Cellphone,
  Tablet,
  Location,
  Calendar,
  Connection,
  Platform,
  Postcard
} from '@element-plus/icons-vue'
import {
  getLoginDevices,
  logoutDevice as logoutDeviceApi,
  logoutAllDevices as logoutAllDevicesApi
} from '@/api/user'

const devices = ref([])

// 退出单个设备
const logoutDevice = async (device) => {
  try {
    await ElMessageBox.confirm(
      `确定要退出设备"${device.name}"的登录吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const res = await logoutDeviceApi(device.id)
    if (res.success) {
      ElMessage.success('已退出该设备登录')
      await loadDevices()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败：' + error.message)
    }
  }
}

// 退出所有设备
const logoutAllDevices = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要退出所有设备的登录吗？您将需要重新登录。',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const res = await logoutAllDevicesApi()
    if (res.success) {
      ElMessage.success('已退出所有设备，请重新登录')
      // 跳转到登录页
      setTimeout(() => {
        window.location.href = '/login'
      }, 1500)
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败：' + error.message)
    }
  }
}

// 加载设备列表
const loadDevices = async () => {
  try {
    const res = await getLoginDevices()
    if (res.success) {
      devices.value = res.data
    }
  } catch (error) {
    console.error('加载设备列表失败：', error)
  }
}

onMounted(() => {
  loadDevices()
})
</script>

<style scoped>
.user-devices {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.devices-list {
  padding: 20px 0;
}

.device-card {
  display: flex;
  gap: 20px;
  padding: 25px;
  background-color: #f5f7fa;
  border-radius: 12px;
  margin-bottom: 20px;
  transition: box-shadow 0.3s;
}

.device-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.device-icon {
  flex-shrink: 0;
}

.device-info {
  flex: 1;
}

.device-header {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 15px;
}

.device-header h3 {
  margin: 0;
  font-size: 18px;
  color: #303133;
}

.device-details {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #606266;
}

.detail-item .el-icon {
  color: #909399;
}

.device-actions {
  display: flex;
  align-items: center;
}

@media (max-width: 768px) {
  .device-card {
    flex-direction: column;
  }

  .device-details {
    grid-template-columns: 1fr;
  }

  .device-actions {
    justify-content: center;
  }
}
</style>