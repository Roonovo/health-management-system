<template>
  <div class="health-summary">
    <el-row :gutter="20">
      <el-col :xs="24" :sm="12" :lg="6" v-for="item in summaryData" :key="item.title">
        <el-card class="summary-card" :class="item.type">
          <div class="card-content">
            <div class="icon-wrapper" :style="{ backgroundColor: item.color }">
              <el-icon :size="32">
                <component :is="item.icon" />
              </el-icon>
            </div>
            <div class="info">
              <div class="title">{{ item.title }}</div>
              <div class="value">{{ item.value }}</div>
              <div class="desc">{{ item.desc }}</div>
            </div>
          </div>
          <div class="card-footer">
            <span class="trend" :class="item.trend">
              <el-icon>
                <component :is="item.trend === 'up' ? ArrowUp : ArrowDown" />
              </el-icon>
              {{ item.change }}
            </span>
            <span class="compare">较上周</span>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import {
  Promotion,
  TrendCharts,
  Timer,
  MagicStick,
  ArrowUp,
  ArrowDown
} from '@element-plus/icons-vue'

const summaryData = ref([
  {
    title: '血压',
    value: '120/80',
    desc: 'mmHg',
    icon: Promotion,
    color: '#409EFF',
    type: 'blood-pressure',
    trend: 'down',
    change: '5%'
  },
  {
    title: '血糖',
    value: '5.6',
    desc: 'mmol/L',
    icon: TrendCharts,
    color: '#67C23A',
    type: 'blood-sugar',
    trend: 'down',
    change: '3%'
  },
  {
    title: '心率',
    value: '72',
    desc: '次/分',
    icon: Timer,
    color: '#E6A23C',
    type: 'heart-rate',
    trend: 'up',
    change: '2%'
  },
  {
    title: '体重',
    value: '65.5',
    desc: 'kg',
    icon: MagicStick,
    color: '#F56C6C',
    type: 'weight',
    trend: 'down',
    change: '1.5%'
  }
])
</script>

<style scoped>
.health-summary {
  margin-bottom: 20px;
}

.summary-card {
  cursor: pointer;
  transition: all 0.3s;
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.summary-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.card-content {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}

.icon-wrapper {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.info {
  flex: 1;
}

.title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 4px;
}

.desc {
  font-size: 12px;
  color: #C0C4CC;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  font-weight: 500;
}

.trend.up {
  color: #F56C6C;
}

.trend.down {
  color: #67C23A;
}

.compare {
  font-size: 12px;
  color: #909399;
}
</style>