<template>
  <div class="consultation-history">
    <el-card>
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-icon><ChatDotRound /></el-icon>
            <span>AI咨询历史</span>
          </div>
          <el-button type="primary" text @click="viewAll">
            查看全部
            <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
      </template>

      <el-empty v-if="consultations.length === 0" description="暂无咨询记录" />

      <div v-else class="consultation-list">
        <div
          v-for="item in consultations"
          :key="item.id"
          class="consultation-item"
          @click="viewDetail(item)"
        >
          <div class="item-header">
            <div class="title">
              <el-icon class="icon"><ChatLineSquare /></el-icon>
              <span class="text">{{ item.title }}</span>
            </div>
            <el-tag :type="getStatusType(item.status)" size="small">
              {{ getStatusText(item.status) }}
            </el-tag>
          </div>

          <div class="item-content">
            <p class="question">
              <span class="label">问：</span>
              {{ item.question }}
            </p>
            <p class="answer" v-if="item.answer">
              <span class="label">答：</span>
              {{ item.answer }}
            </p>
          </div>

          <div class="item-footer">
            <div class="meta">
              <span class="time">
                <el-icon><Clock /></el-icon>
                {{ formatTime(item.createdAt) }}
              </span>
              <span class="doctor" v-if="item.doctor">
                <el-icon><User /></el-icon>
                {{ item.doctor }}
              </span>
            </div>
            <div class="actions">
              <el-button type="primary" text size="small" @click.stop="continueChat(item)">
                <el-icon><ChatDotRound /></el-icon>
                继续咨询
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <div v-if="consultations.length > 0" class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[5, 10, 20]"
          layout="total, prev, pager, next"
          small
          @current-change="loadData"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  ChatDotRound,
  ChatLineSquare,
  ArrowRight,
  Clock,
  User
} from '@element-plus/icons-vue'
import { formatDistanceToNow } from 'date-fns'
import { zhCN } from 'date-fns/locale'

const router = useRouter()

const consultations = ref([])
const currentPage = ref(1)
const pageSize = ref(5)
const total = ref(0)
const loading = ref(false)

// 模拟数据
const mockData = [
  {
    id: 1,
    title: '关于血压偏高的咨询',
    question: '最近测量血压一直在140/90左右，需要吃药吗？',
    answer: '根据您的描述，血压确实偏高。建议先通过调整生活方式来改善，包括低盐饮食、适量运动、保持良好作息。如果持续偏高，建议到医院就诊。',
    status: 'completed',
    doctor: 'AI健康助手',
    createdAt: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000)
  },
  {
    id: 2,
    title: '血糖控制咨询',
    question: '空腹血糖6.5，需要注意什么？',
    answer: '空腹血糖6.5mmol/L处于临界值，建议控制饮食，减少糖分摄入，增加运动量，定期监测血糖变化。',
    status: 'completed',
    doctor: 'AI健康助手',
    createdAt: new Date(Date.now() - 5 * 24 * 60 * 60 * 1000)
  },
  {
    id: 3,
    title: '减重方案咨询',
    question: '如何科学减重？目标减重10公斤',
    answer: '',
    status: 'pending',
    doctor: '',
    createdAt: new Date(Date.now() - 1 * 60 * 60 * 1000)
  }
]

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    consultations.value = mockData
    total.value = mockData.length
  } catch (error) {
    ElMessage.error('加载失败：' + error.message)
  } finally {
    loading.value = false
  }
}

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    pending: 'warning',
    processing: 'primary',
    completed: 'success',
    cancelled: 'info'
  }
  return typeMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const textMap = {
    pending: '待回复',
    processing: '处理中',
    completed: '已完成',
    cancelled: '已取消'
  }
  return textMap[status] || status
}

// 格式化时间
const formatTime = (date) => {
  return formatDistanceToNow(new Date(date), {
    locale: zhCN,
    addSuffix: true
  })
}

// 查看详情
const viewDetail = (item) => {
  router.push(`/consultation/${item.id}`)
}

// 继续咨询
const continueChat = (item) => {
  router.push(`/consultation/chat/${item.id}`)
}

// 查看全部
const viewAll = () => {
  router.push('/consultation/history')
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.consultation-history {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.consultation-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.consultation-item {
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.consultation-item:hover {
  background: #e8edf5;
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 500;
  color: #303133;
}

.title .icon {
  color: #409EFF;
}

.item-content {
  margin-bottom: 12px;
}

.item-content p {
  margin: 8px 0;
  font-size: 14px;
  line-height: 1.6;
  color: #606266;
}

.item-content .label {
  font-weight: 500;
  color: #303133;
  margin-right: 4px;
}

.question {
  color: #606266;
}

.answer {
  color: #67C23A;
}

.item-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #e4e7ed;
}

.meta {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: #909399;
}

.meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.pagination-wrapper {
  margin-top: 16px;
  display: flex;
  justify-content: center;
}
</style>