<template>
  <div class="about-page">
    <div class="about-container">
      <!-- 顶部Banner -->
      <div class="about-banner">
        <div class="banner-content">
          <img src="@/assets/logo.png" alt="Logo" class="logo">
          <h1>智能健康管理系统</h1>
          <p class="version">Version {{ systemInfo.version }}</p>
          <p class="slogan">您的健康，我们的关注</p>
        </div>
      </div>

      <!-- 系统介绍 -->
      <el-card class="intro-card">
        <template #header>
          <div class="card-header">
            <el-icon><InfoFilled /></el-icon>
            <span>系统介绍</span>
          </div>
        </template>

        <div class="intro-content">
          <p>
            智能健康管理系统是一款专业的个人健康数据管理平台，致力于帮助用户更好地管理自己的健康信息。
            系统集成了健康数据记录、健康档案管理、用药提醒、AI健康咨询等多项功能，
            为用户提供全方位的健康管理服务。
          </p>

          <h3>核心功能</h3>
          <el-row :gutter="20">
            <el-col :xs="24" :sm="12" :md="6" v-for="feature in features" :key="feature.title">
              <div class="feature-item">
                <div class="feature-icon" :style="{ backgroundColor: feature.color }">
                  <el-icon :size="32">
                    <component :is="feature.icon" />
                  </el-icon>
                </div>
                <h4>{{ feature.title }}</h4>
                <p>{{ feature.desc }}</p>
              </div>
            </el-col>
          </el-row>
        </div>
      </el-card>

      <!-- 技术栈 -->
      <el-card class="tech-card">
        <template #header>
          <div class="card-header">
            <el-icon><SetUp /></el-icon>
            <span>技术栈</span>
          </div>
        </template>

        <el-row :gutter="20">
          <el-col :xs="24" :md="12">
            <h3>前端技术</h3>
            <ul class="tech-list">
              <li v-for="tech in frontendTechs" :key="tech.name">
                <span class="tech-name">{{ tech.name }}</span>
                <span class="tech-version">{{ tech.version }}</span>
              </li>
            </ul>
          </el-col>

          <el-col :xs="24" :md="12">
            <h3>后端技术</h3>
            <ul class="tech-list">
              <li v-for="tech in backendTechs" :key="tech.name">
                <span class="tech-name">{{ tech.name }}</span>
                <span class="tech-version">{{ tech.version }}</span>
              </li>
            </ul>
          </el-col>
        </el-row>
      </el-card>

      <!-- 联系我们 -->
      <el-card class="contact-card">
        <template #header>
          <div class="card-header">
            <el-icon><Phone /></el-icon>
            <span>联系我们</span>
          </div>
        </template>

        <el-row :gutter="30">
          <el-col :xs="24" :md="8">
            <div class="contact-item">
              <el-icon :size="40" color="#409EFF"><Message /></el-icon>
              <h4>电子邮箱</h4>
              <p>{{ contactInfo.email }}</p>
            </div>
          </el-col>

          <el-col :xs="24" :md="8">
            <div class="contact-item">
              <el-icon :size="40" color="#67C23A"><Phone /></el-icon>
              <h4>联系电话</h4>
              <p>{{ contactInfo.phone }}</p>
            </div>
          </el-col>

          <el-col :xs="24" :md="8">
            <div class="contact-item">
              <el-icon :size="40" color="#E6A23C"><Location /></el-icon>
              <h4>联系地址</h4>
              <p>{{ contactInfo.address }}</p>
            </div>
          </el-col>
        </el-row>
      </el-card>

      <!-- 团队介绍 -->
      <el-card class="team-card">
        <template #header>
          <div class="card-header">
            <el-icon><User /></el-icon>
            <span>开发团队</span>
          </div>
        </template>

        <el-row :gutter="20">
          <el-col 
            :xs="24" 
            :sm="12" 
            :md="8" 
            :lg="6" 
            v-for="member in teamMembers" 
            :key="member.name"
          >
            <div class="team-member">
              <el-avatar :size="80" :src="member.avatar">
                <el-icon><User /></el-icon>
              </el-avatar>
              <h4>{{ member.name }}</h4>
              <p class="role">{{ member.role }}</p>
              <p class="intro">{{ member.intro }}</p>
            </div>
          </el-col>
        </el-row>
      </el-card>

      <!-- 更新日志 -->
      <el-card class="changelog-card">
        <template #header>
          <div class="card-header">
            <el-icon><Document /></el-icon>
            <span>更新日志</span>
          </div>
        </template>

        <el-timeline>
          <el-timeline-item
            v-for="log in changelog"
            :key="log.version"
            :timestamp="log.date"
            placement="top"
          >
            <el-card>
              <h4>Version {{ log.version }}</h4>
              <ul class="changelog-list">
                <li v-for="(item, index) in log.changes" :key="index">
                  <el-tag :type="getChangeType(item.type)" size="small">
                    {{ item.type }}
                  </el-tag>
                  {{ item.content }}
                </li>
              </ul>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </el-card>

      <!-- 许可证 -->
      <el-card class="license-card">
        <template #header>
          <div class="card-header">
            <el-icon><Ticket /></el-icon>
            <span>许可证</span>
          </div>
        </template>

        <div class="license-content">
          <p>
            本系统采用 MIT 许可证进行开源。
          </p>
          <p>
            MIT License
          </p>
          <pre class="license-text">
Copyright (c) {{ new Date().getFullYear() }} 智能健康管理系统

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
          </pre>
        </div>
      </el-card>

      <!-- 页脚 -->
      <div class="about-footer">
        <p>© {{ new Date().getFullYear() }} 智能健康管理系统. All rights reserved.</p>
        <p>
          <a href="#">隐私政策</a>
          <span class="divider">|</span>
          <a href="#">服务条款</a>
          <span class="divider">|</span>
          <a href="#">帮助中心</a>
        </p>
        <p class="icp">{{ contactInfo.icp }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import {
  InfoFilled,
  SetUp,
  Phone,
  Location,
  Message,
  User,
  Document,
  Ticket,
  DataLine,
  FolderOpened,
  Bell,
  ChatDotRound
} from '@element-plus/icons-vue'

// 系统信息
const systemInfo = reactive({
  version: '1.0.0',
  buildDate: '2024-01-15',
  environment: 'production'
})

// 核心功能
const features = ref([
  {
    title: '健康数据',
    desc: '记录血压、血糖、体重等健康指标',
    icon: DataLine,
    color: '#409EFF'
  },
  {
    title: '健康档案',
    desc: '建立个人健康档案，追踪健康趋势',
    icon: FolderOpened,
    color: '#67C23A'
  },
  {
    title: '用药提醒',
    desc: '智能用药提醒，不错过每一次服药',
    icon: Bell,
    color: '#E6A23C'
  },
  {
    title: 'AI咨询',
    desc: '智能健康咨询，24小时在线服务',
    icon: ChatDotRound,
    color: '#F56C6C'
  }
])

// 前端技术栈
const frontendTechs = ref([
  { name: 'Vue 3', version: '3.3.x' },
  { name: 'Element Plus', version: '2.4.x' },
  { name: 'Vue Router', version: '4.2.x' },
  { name: 'Pinia', version: '2.1.x' },
  { name: 'Axios', version: '1.6.x' },
  { name: 'ECharts', version: '5.4.x' },
  { name: 'Vite', version: '5.0.x' }
])

// 后端技术栈
const backendTechs = ref([
  { name: 'Node.js', version: '20.x' },
  { name: 'Express', version: '4.18.x' },
  { name: 'MySQL', version: '8.0.x' },
  { name: 'Redis', version: '7.0.x' },
  { name: 'JWT', version: '9.0.x' },
  { name: 'Socket.io', version: '4.6.x' }
])

// 联系信息
const contactInfo = reactive({
  email: 'support@health-system.com',
  phone: '400-123-4567',
  address: '北京市朝阳区某某大厦1001室',
  icp: '京ICP备12345678号'
})

// 团队成员
const teamMembers = ref([
  {
    name: '张三',
    role: '项目负责人',
    intro: '全栈工程师，10年开发经验',
    avatar: ''
  },
  {
    name: '李四',
    role: '前端开发',
    intro: '精通Vue.js生态，注重用户体验',
    avatar: ''
  },
  {
    name: '王五',
    role: '后端开发',
    intro: '专注Node.js开发，性能优化专家',
    avatar: ''
  },
  {
    name: '赵六',
    role: 'UI设计师',
    intro: '5年UI/UX设计经验',
    avatar: ''
  }
])

// 更新日志
const changelog = ref([
  {
    version: '1.0.0',
    date: '2024-01-15',
    changes: [
      { type: '新增', content: '用户注册登录功能' },
      { type: '新增', content: '健康数据记录功能' },
      { type: '新增', content: '健康档案管理功能' },
      { type: '新增', content: '用药提醒功能' },
      { type: '新增', content: 'AI健康咨询功能' }
    ]
  },
  {
    version: '0.9.0',
    date: '2024-01-01',
    changes: [
      { type: '优化', content: '优化系统性能' },
      { type: '优化', content: '改进用户界面' },
      { type: '修复', content: '修复已知bug' }
    ]
  },
  {
    version: '0.8.0',
    date: '2023-12-15',
    changes: [
      { type: '新增', content: '数据可视化功能' },
      { type: '新增', content: '导出报表功能' },
      { type: '优化', content: '优化移动端体验' }
    ]
  }
])

// 获取变更类型
const getChangeType = (type) => {
  const typeMap = {
    '新增': 'success',
    '优化': 'warning',
    '修复': 'danger'
  }
  return typeMap[type] || 'info'
}
</script>

<style scoped>
.about-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.about-container {
  max-width: 1200px;
  margin: 0 auto;
}

/* Banner */
.about-banner {
  background: white;
  border-radius: 16px;
  padding: 60px 40px;
  text-align: center;
  margin-bottom: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.banner-content .logo {
  height: 80px;
  margin-bottom: 20px;
}

.banner-content h1 {
  margin: 0 0 10px;
  font-size: 36px;
  color: #303133;
  font-weight: bold;
}

.version {
  display: inline-block;
  padding: 4px 12px;
  background: #409EFF;
  color: white;
  border-radius: 12px;
  font-size: 12px;
  margin-bottom: 15px;
}

.slogan {
  font-size: 18px;
  color: #606266;
  margin: 0;
}

/* 卡片头部 */
.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

/* 介绍卡片 */
.intro-card {
  margin-bottom: 20px;
}

.intro-content p {
  line-height: 1.8;
  color: #606266;
  margin-bottom: 20px;
}

.intro-content h3 {
  margin: 30px 0 20px;
  color: #303133;
}

.feature-item {
  text-align: center;
  padding: 20px;
  margin-bottom: 20px;
}

.feature-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin: 0 auto 15px;
}

.feature-item h4 {
  margin: 0 0 10px;
  color: #303133;
  font-size: 16px;
}

.feature-item p {
  margin: 0;
  color: #909399;
  font-size: 14px;
  line-height: 1.6;
}

/* 技术栈 */
.tech-card {
  margin-bottom: 20px;
}

.tech-card h3 {
  margin: 0 0 15px;
  color: #303133;
  font-size: 16px;
}

.tech-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.tech-list li {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 15px;
  background: #f5f7fa;
  border-radius: 8px;
  margin-bottom: 10px;
}

.tech-name {
  color: #303133;
  font-weight: 500;
}

.tech-version {
  color: #909399;
  font-size: 14px;
}

/* 联系卡片 */
.contact-card {
  margin-bottom: 20px;
}

.contact-item {
  text-align: center;
  padding: 20px;
}

.contact-item h4 {
  margin: 15px 0 10px;
  color: #303133;
}

.contact-item p {
  margin: 0;
  color: #606266;
}

/* 团队卡片 */
.team-card {
  margin-bottom: 20px;
}

.team-member {
  text-align: center;
  padding: 20px;
  border-radius: 8px;
  transition: all 0.3s;
}

.team-member:hover {
  background: #f5f7fa;
  transform: translateY(-5px);
}

.team-member h4 {
  margin: 15px 0 5px;
  color: #303133;
}

.team-member .role {
  margin: 0 0 10px;
  color: #409EFF;
  font-size: 14px;
}

.team-member .intro {
  margin: 0;
  color: #909399;
  font-size: 13px;
}

/* 更新日志 */
.changelog-card {
  margin-bottom: 20px;
}

.changelog-list {
  list-style: none;
  padding: 0;
  margin: 10px 0 0;
}

.changelog-list li {
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 10px;
}

/* 许可证 */
.license-card {
  margin-bottom: 20px;
}

.license-content p {
  color: #606266;
  line-height: 1.8;
}

.license-text {
  background: #f5f7fa;
  padding: 20px;
  border-radius: 8px;
  font-size: 12px;
  line-height: 1.6;
  color: #606266;
  overflow-x: auto;
  white-space: pre-wrap;
  word-wrap: break-word;
}

/* 页脚 */
.about-footer {
  text-align: center;
  padding: 30px 20px;
  color: white;
}

.about-footer p {
  margin: 10px 0;
}

.about-footer a {
  color: white;
  text-decoration: none;
  transition: opacity 0.3s;
}

.about-footer a:hover {
  opacity: 0.8;
}

.divider {
  margin: 0 10px;
}

.icp {
  font-size: 12px;
  opacity: 0.8;
}

@media (max-width: 768px) {
  .about-banner {
    padding: 40px 20px;
  }

  .banner-content h1 {
    font-size: 28px;
  }

  .slogan {
    font-size: 16px;
  }

  .feature-item,
  .contact-item,
  .team-member {
    margin-bottom: 20px;
  }
}
</style>