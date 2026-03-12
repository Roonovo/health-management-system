<template>
  <!-- 删除了最外层的 el-container 布局，只保留内容容器 -->
  <div class="dashboard-content">
    
    <!-- 1. 欢迎区域 -->
    <el-card class="welcome-card">
      <div class="welcome-content">
        <div class="welcome-text">
          <h2>欢迎回来，{{ userInfo.nickname }}！</h2>
          <p class="subtitle">今天是 {{ currentDate }}，祝您健康愉快！</p>
          <p class="tip">您有3条待办事项，2条健康提醒</p>
        </div>
        <div class="welcome-image">
          <!-- 这里可以放一个装饰图片，或者留空 -->
          <el-image
            src="https://via.placeholder.com/150x150"
            alt="健康图标"
            fit="cover"
          />
        </div>
      </div>
    </el-card>

    <!-- 2. 数据统计卡片 -->
    <div class="stats-grid">
      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <el-icon class="stat-icon" color="#409EFF"><Calendar /></el-icon>
          <div class="stat-info">
            <div class="stat-value">7</div>
            <div class="stat-label">今日计划</div>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <el-icon class="stat-icon" color="#67C23A"><TrendCharts /></el-icon>
          <div class="stat-info">
            <div class="stat-value">98.6°F</div>
            <div class="stat-label">平均体温</div>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <el-icon class="stat-icon" color="#E6A23C"><Clock /></el-icon>
          <div class="stat-info">
            <div class="stat-value">8h 30m</div>
            <div class="stat-label">平均睡眠</div>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <el-icon class="stat-icon" color="#F56C6C"><Footprint /></el-icon>
          <div class="stat-info">
            <div class="stat-value">12,345</div>
            <div class="stat-label">今日步数</div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 3. 图表与提醒区域 -->
    <div class="chart-grid">
      <el-card class="chart-card">
        <template #header>
          <div class="chart-header">
            <span>健康数据趋势</span>
            <el-select v-model="chartPeriod" size="small" style="width: 100px">
              <el-option label="近7天" value="7d" />
              <el-option label="近30天" value="30d" />
              <el-option label="近90天" value="90d" />
            </el-select>
          </div>
        </template>
        <div class="chart-container">
          <div class="chart-placeholder">
            <el-empty description="图表区域" :image-size="100" />
          </div>
        </div>
      </el-card>

      <el-card class="chart-card">
        <template #header>
          <span>健康提醒</span>
        </template>
        <el-timeline>
          <el-timeline-item
            v-for="(reminder, index) in reminders"
            :key="index"
            :timestamp="reminder.time"
            placement="top"
          >
            <el-card shadow="never">
              <p>{{ reminder.content }}</p>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </el-card>
    </div>

    <!-- 4. 待办事项 -->
    <el-card class="todo-card">
      <template #header>
        <div class="todo-header">
          <span>今日待办事项</span>
          <el-button type="primary" size="small" text>添加</el-button>
        </div>
      </template>
      <el-table :data="todos" style="width: 100%">
        <el-table-column prop="title" label="事项" width="300" />
        <el-table-column prop="time" label="时间" width="180" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.status === '已完成' ? 'success' : 'warning'">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button size="small" @click="handleTodoAction(row)">
              {{ row.status === '未完成' ? '标记完成' : '重新开始' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus' 

// 导入图标 (只保留用到的)
import {
  Calendar,
  TrendCharts,
  Clock,
  Footprint
} from '@element-plus/icons-vue'

const router = useRouter()

// 用户信息
const userInfo = reactive({
  nickname: localStorage.getItem('nickname') || '用户',
  avatar: ''
})

// 当前日期
const currentDate = computed(() => {
  const now = new Date()
  return now.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  })
})

// 图表周期
const chartPeriod = ref('7d')

// 健康提醒
const reminders = ref([
  { time: '08:00', content: '测量血压并记录' },
  { time: '12:00', content: '午餐后30分钟散步' },
  { time: '18:00', content: '晚餐前测量血糖' },
  { time: '21:00', content: '准备明日健康计划' }
])

// 待办事项
const todos = ref([
  { id: 1, title: '完成晨间运动', time: '07:00 - 07:30', status: '已完成' },
  { id: 2, title: '测量并记录体重', time: '08:00 - 08:15', status: '已完成' },
  { id: 3, title: '健康咨询预约', time: '10:00 - 11:00', status: '未完成' },
  { id: 4, title: '午餐后散步20分钟', time: '13:00 - 13:20', status: '未完成' },
  { id: 5, title: '阅读健康文章', time: '20:00 - 20:30', status: '未完成' }
])

// 方法
const handleTodoAction = (todo) => {
  todo.status = todo.status === '未完成' ? '已完成' : '未完成'
  ElMessage.success(`已${todo.status === '已完成' ? '完成' : '重新开始'}：${todo.title}`)
}

// 生命周期检查登录
onMounted(() => {
  if (!localStorage.getItem('token')) {
    ElMessage.warning('请先登录')
    router.push('/login')
  }
})
</script>

<style scoped>
/* 容器样式：不再需要 height: 100vh，因为父级 Layout 已经控制了高度 */
.dashboard-content {
  padding: 20px;
  background-color: #f5f7fa;
  height: auto;
  min-height: 100%;
  width: 100%;
}

/* 欢迎卡片 */
.welcome-card {
  margin-bottom: 20px;
}

.welcome-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.welcome-text h2 {
  margin: 0 0 10px 0;
  color: #303133;
}

.subtitle {
  margin: 5px 0;
  color: #606266;
  font-size: 14px;
}

.tip {
  margin: 10px 0 0 0;
  color: #409EFF;
  font-size: 14px;
}

.welcome-image img {
  border-radius: 8px;
}

/* 数据统计卡片 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  cursor: pointer;
  transition: transform 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-content {
  display: flex;
  align-items: center;
}

.stat-icon {
  font-size: 40px;
  margin-right: 20px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

/* 图表区域 */
.chart-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.chart-card {
  height: 400px;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-container {
  height: 320px;
}

.chart-placeholder {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 待办事项 */
.todo-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 响应式 */
@media (max-width: 768px) {
  .chart-grid {
    grid-template-columns: 1fr;
  }
  
  .welcome-content {
    flex-direction: column;
    text-align: center;
  }
  
  .welcome-image {
    margin-top: 20px;
  }
}
</style>