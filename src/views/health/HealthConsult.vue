<template>
  <div class="health-consult-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1>AI健康助手</h1>
        <p class="subtitle">基于专业健康知识库，为您提供个性化健康咨询服务</p>
      </div>
      <div class="header-actions">
        <el-button type="primary" @click="openHistoryDrawer">
          <i class="el-icon-time"></i> 历史对话
        </el-button>
        <el-button @click="startNewConversation">
          <i class="el-icon-plus"></i> 新建对话
        </el-button>
      </div>
    </div>

    <!-- 主对话区域 -->
    <div class="consult-main">
      <!-- 侧边栏 - 对话列表 -->
      <div class="conversation-sidebar" v-show="!isMobile || showSidebar">
        <div class="sidebar-header">
          <h3>对话列表</h3>
          <el-button 
            type="text" 
            icon="el-icon-close" 
            @click="showSidebar = false"
            v-if="isMobile"
          />
        </div>
        <div class="conversation-list">
          <div
            v-for="conv in conversations"
            :key="conv.id"
            class="conversation-item"
            :class="{ active: currentConversationId === conv.id }"
            @click="switchConversation(conv.id)"
          >
            <div class="conv-info">
              <div class="conv-title">{{ conv.title }}</div>
              <div class="conv-preview">{{ conv.lastMessage }}</div>
              <div class="conv-time">{{ formatTime(conv.updateTime) }}</div>
            </div>
            <el-button
              type="text"
              icon="el-icon-delete"
              class="delete-btn"
              @click.stop="deleteConversation(conv.id)"
            />
          </div>
          <el-empty v-if="conversations.length === 0" description="暂无对话记录" />
        </div>
      </div>

      <!-- 对话内容区 -->
      <div class="conversation-content">
        <!-- 欢迎界面 -->
        <div class="welcome-screen" v-if="!currentConversationId">
          <div class="welcome-icon">
            <i class="el-icon-chat-dot-round"></i>
          </div>
          <h2>您好！我是您的AI健康助手</h2>
          <p class="welcome-desc">
            我可以为您提供专业的健康咨询服务，包括疾病知识、健康建议、饮食指导等。
            <br/>
            <span class="disclaimer">* 提供的信息仅供参考，不能替代专业医疗建议</span>
          </p>
          
          <!-- 快捷问题卡片 -->
          <div class="quick-questions">
            <h3>常见问题</h3>
            <el-row :gutter="15">
              <el-col :xs="24" :sm="12" :md="8" v-for="question in quickQuestions" :key="question.id">
                <el-card 
                  class="question-card" 
                  shadow="hover"
                  @click.="askQuickQuestion(question.content)"
                >
                  <div class="question-icon">
                    <i :class="question.icon"></i>
                  </div>
                  <div class="question-text">{{ question.content }}</div>
                </el-card>
              </el-col>
            </el-row>
          </div>

          <!-- 个性化建议卡片 -->
          <div class="personalized-tips" v-if="personalizedTips.length > 0">
            <h3>根据您的健康档案，为您推荐</h3>
            <el-carousel height="120px" :interval="5000" arrow="hover">
              <el-carousel-item v-for="tip in personalizedTips" :key="tip.id">
                <div class="tip-card" @click="askQuickQuestion(tip.question)">
                  <div class="tip-icon">
                    <i :class="tip.icon"></i>
                  </div>
                  <div class="tip-content">
                    <h4>{{ tip.title }}</h4>
                    <p>{{ tip.description }}</p>
                  </div>
                </div>
              </el-carousel-item>
            </el-carousel>
          </div>
        </div>

        <!-- 消息列表 -->
        <div class="message-list" ref="messageList" v-else>
          <div
            v-for="message in currentMessages"
            :key="message.id"
            class="message-item"
            :class="message.role"
          >
            <div class="message-avatar">
              <img v-if="message.role === 'user'" :src="userAvatar" alt="用户头像" />
              <i v-else class="el-icon-service"></i>
            </div>
            <div class="message-content">
              <div class="message-header">
                <span class="sender-name">{{ message.role === 'user' ? '我' : 'AI助手' }}</span>
                <span class="message-time">{{ formatMessageTime(message.createTime) }}</span>
              </div>
              <div class="message-text" v-html="formatMessageContent(message.content)"></div>
              
              <!-- 健康建议卡片 -->
              <div v-if="message.healthAdvice" class="health-advice-card">
                <div class="advice-header">
                  <i class="el-icon-warning-outline"></i>
                  <span>个性化健康建议</span>
                </div>
                <div class="advice-content">
                  <div class="advice-item" v-for="(advice, index) in message.healthAdvice" :key="index">
                    <i class="el-icon-check"></i>
                    <span>{{ advice }}</span>
                  </div>
                </div>
                <div class="advice-reason" v-if="message.adviceReason">
                  <strong>推荐理由：</strong>{{ message.adviceReason }}
                </div>
              </div>

              <!-- 食谱推荐卡片 -->
              <div v-if="message.recipe" class="recipe-card">
                <div class="recipe-header">
                  <i class="el-icon-food"></i>
                  <span>{{ message.recipe.name }}</span>
                </div>
                <div class="recipe-info">
                  <div class="recipe-tags">
                    <el-tag size="small" v-for="tag in message.recipe.tags" :key="tag">{{ tag }}</el-tag>
                  </div>
                  <div class="recipe-nutrition">
                    <span>热量: {{ message.recipe.calories }}kcal</span>
                    <span>蛋白质: {{ message.recipe.protein }}g</span>
                    <span>脂肪: {{ message.recipe.fat }}g</span>
                  </div>
                </div>
                <div class="recipe-ingredients">
                  <strong>食材：</strong>{{ message.recipe.ingredients.join('、') }}
                </div>
                <div class="recipe-steps">
                  <strong>做法：</strong>
                  <ol>
                    <li v-for="(step, index) in message.recipe.steps" :key="index">{{ step }}</li>
                  </ol>
                </div>
              </div>

              <!-- 消息操作按钮 -->
              <div class="message-actions" v-if="message.role === 'assistant'">
                <el-button 
                  type="text" 
                  size="small" 
                  @click="copyMessage(message.content)"
                >
                  <i class="el-icon-document-copy"></i> 复制
                </el-button>
                <el-button 
                  type="text" 
                  size="small" 
                  @click="likeMessage(message.id)"
                  :class="{ liked: message.liked }"
                >
                  <i class="el-icon-thumb"></i> {{ message.liked ? '已点赞' : '点赞' }}
                </el-button>
                <el-button 
                  type="text" 
                  size="small" 
                  @click="regenerateResponse(message.id)"
                >
                  <i class="el-icon-refresh"></i> 重新生成
                </el-button>
              </div>
            </div>
          </div>

          <!-- 加载中动画 -->
          <div class="message-item assistant" v-if="isTyping">
            <div class="message-avatar">
              <i class="el-icon-service"></i>
            </div>
            <div class="message-content">
              <div class="typing-indicator">
                <span></span>
                <span></span>
                <span></span>
              </div>
            </div>
          </div>
        </div>

        <!-- 输入区域 -->
        <div class="input-area">
          <!-- 上下文提示 -->
          <div class="context-info" v-if="contextInfo">
            <i class="el-icon-info"></i>
            <span>{{ contextInfo }}</span>
            <el-button type="text" size="small" @click="clearContext">清除</el-button>
          </div>

          <!-- 输入框 -->
          <div class="input-wrapper">
            <el-button 
              class="sidebar-toggle" 
              icon="el-icon-menu" 
              circle 
              @click="showSidebar = true"
              v-if="isMobile"
            />
            
            <el-input
              v-model="inputMessage"
              type="textarea"
              :rows="3"
              :placeholder="currentConversationId ? '输入您的健康问题...' : '请先创建对话或选择快捷问题开始咨询'"
              @keydown.enter.ctrl="sendMessage"
              :disabled="!currentConversationId && quickQuestions.length === 0"
              resize="none"
            >
            </el-input>

            <div class="input-actions">
              <el-tooltip content="上传图片" placement="top">
                <el-upload
                  action="#"
                  :show-file-list="false"
                  :before-upload="handleImageUpload"
                  accept="image/*"
                >
                  <el-button icon="el-icon-picture-outline" circle />
                </el-upload>
              </el-tooltip>
              
              <el-tooltip content="引用健康档案数据" placement="top">
                <el-button 
                  icon="el-icon-document" 
                  circle 
                  @click="showHealthDataSelector = true"
                />
              </el-tooltip>

              <el-button 
                type="primary" 
                @click="sendMessage"
                :loading="isSending"
                :disabled="!inputMessage.trim() || !currentConversationId"
              >
                发送 <span class="shortcut-hint">(Ctrl+Enter)</span>
              </el-button>
            </div>
          </div>

          <!-- 免责声明 -->
          <div class="disclaimer-text">
            <i class="el-icon-warning"></i>
            AI助手提供的建议仅供参考，不能替代专业医疗诊断。如有健康问题，请及时就医。
          </div>
        </div>
      </div>
    </div>

    <!-- 历史对话抽屉 -->
    <el-drawer
      title="历史对话"
      v-model="historyDrawerVisible"
      direction="rtl"
      size="400px"
    >
      <div class="history-list">
        <el-timeline>
          <el-timeline-item
            v-for="conv in conversations"
            :key="conv.id"
            :timestamp="formatTime(conv.createTime)"
            placement="top"
          >
            <el-card shadow="hover" @click.="switchConversation(conv.id)">
              <h4>{{ conv.title }}</h4>
              <p class="conv-summary">{{ conv.summary }}</p>
              <div class="conv-meta">
                <span><i class="el-icon-chat-dot-round"></i> {{ conv.messageCount }} 条消息</span>
                <span><i class="el-icon-time"></i> {{ formatTime(conv.updateTime) }}</span>
              </div>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </div>
    </el-drawer>

    <!-- 健康数据选择器对话框 -->
    <el-dialog
      title="引用健康档案数据"
      v-model="showHealthDataSelector"
      width="600px"
    >
      <el-tabs v-model="activeDataTab">
        <el-tab-pane label="基本信息" name="basic">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="性别">{{ healthProfile.gender }}</el-descriptions-item>
            <el-descriptions-item label="年龄">{{ healthProfile.age }}岁</el-descriptions-item>
            <el-descriptions-item label="身高">{{ healthProfile.height }}cm</el-descriptions-item>
            <el-descriptions-item label="体重">{{ healthProfile.weight }}kg</el-descriptions-item>
            <el-descriptions-item label="BMI">{{ healthProfile.bmi }}</el-descriptions-item>
            <el-descriptions-item label="血型">{{ healthProfile.bloodType }}</el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>
        
        <el-tab-pane label="慢性病史" name="chronic">
          <el-checkbox-group v-model="selectedChronicDiseases">
            <el-checkbox 
              v-for="disease in healthProfile.chronicDiseases" 
              :key="disease"
              :label="disease"
            />
          </el-checkbox-group>
        </el-tab-pane>
        
        <el-tab-pane label="过敏史" name="allergy">
          <el-checkbox-group v-model="selectedAllergies">
            <el-checkbox 
              v-for="allergy in healthProfile.allergies" 
              :key="allergy"
              :label="allergy"
            />
          </el-checkbox-group>
        </el-tab-pane>
        
        <el-tab-pane label="最近指标" name="indicators">
          <el-table :data="recentIndicators" stripe>
            <el-table-column prop="type" label="指标类型" />
            <el-table-column prop="value" label="数值" />
            <el-table-column prop="date" label="记录时间" />
            <el-table-column label="操作">
              <template #default="scope">
                <el-button 
                  type="text" 
                  size="small"
                  @click="selectIndicator(scope.row)"
                >
                  引用
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
      
      <template #footer>
        <el-button @click="showHealthDataSelector = false">取消</el-button>
        <el-button type="primary" @click="insertHealthData">插入到对话</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted, onBeforeUnmount, nextTick, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'
import { useStore } from 'vuex'

export default {
  name: 'HealthConsult',
  setup() {
    const store = useStore()
    
    // 响应式数据
    const isMobile = ref(window.innerWidth < 768)
    const showSidebar = ref(!isMobile.value)
    const currentConversationId = ref(null)
    const conversations = ref([])
    const currentMessages = ref([])
    const inputMessage = ref('')
    const isTyping = ref(false)
    const isSending = ref(false)
    const historyDrawerVisible = ref(false)
    const showHealthDataSelector = ref(false)
    const activeDataTab = ref('basic')
    const contextInfo = ref('')
    const messageList = ref(null)

    // 用户信息
    const userAvatar = computed(() => store.state.user?.avatar || '/default-avatar.png')

    // 快捷问题
    const quickQuestions = ref([
      { id: 1, icon: 'el-icon-question', content: '如何保持健康的饮食习惯?' },
      { id: 2, icon: 'el-icon-bicycle', content: '每天需要运动多长时间?' },
      { id: 3, icon: 'el-icon-moon-night', content: '如何改善睡眠质量?' },
      { id: 4, icon: 'el-icon-ice-cream-square', content: '糖尿病患者饮食注意事项' },
      { id: 5, icon: 'el-icon-sunny', content: '高血压如何控制?' },
      { id: 6, icon: 'el-icon-coffee-cup', content: '如何缓解工作压力?' }
    ])

    // 个性化建议
    const personalizedTips = ref([])

    // 健康档案数据
    const healthProfile = reactive({
      gender: '男',
      age: 30,
      height: 175,
      weight: 70,
      bmi: 22.9,
      bloodType: 'A',
      chronicDiseases: [],
      allergies: []
    })

    const selectedChronicDiseases = ref([])
    const selectedAllergies = ref([])
    const recentIndicators = ref([])

    // 加载对话列表
    const loadConversations = async () => {
      try {
        const response = await axios.get('/api/health-consult/conversations')
        conversations.value = response.data
      } catch (error) {
        console.error('加载对话列表失败:', error)
        ElMessage.error('加载对话列表失败')
      }
    }

    // 加载健康档案
    const loadHealthProfile = async () => {
      try {
        const [profileRes, indicatorsRes] = await Promise.all([
          axios.get('/api/health/profile'),
          axios.get('/api/health/recent-indicators')
        ])
        
        Object.assign(healthProfile, profileRes.data)
        recentIndicators.value = indicatorsRes.data
        
        // 生成个性化建议
        generatePersonalizedTips()
      } catch (error) {
        console.error('加载健康档案失败:', error)
      }
    }

    // 生成个性化建议
    const generatePersonalizedTips = () => {
      const tips = []
      
      if (healthProfile.bmi > 24) {
        tips.push({
          id: 1,
          icon: 'el-icon-food',
          title: '体重管理建议',
          description: `您的BMI为${healthProfile.bmi}，建议合理控制饮食`,
          question: '根据我的BMI指数，给我推荐一份健康减重食谱'
        })
      }
      
      if (healthProfile.chronicDiseases.includes('高血压')) {
        tips.push({
          id: 2,
          icon: 'el-icon-warning',
          title: '高血压管理',
          description: '您有高血压病史，需要注意饮食控制',
          question: '高血压患者的饮食注意事项有哪些?'
        })
      }
      
      if (healthProfile.chronicDiseases.includes('糖尿病')) {
        tips.push({
          id: 3,
          icon: 'el-icon-ice-cream-square',
          title: '血糖控制',
          description: '您有糖尿病史，需要控制糖分摄入',
          question: '糖尿病患者如何科学控制血糖?'
        })
      }
      
      personalizedTips.value = tips
    }

    // 开始新对话
    const startNewConversation = async () => {
      try {
        const response = await axios.post('/api/health-consult/conversations', {
          title: '新对话 ' + new Date().toLocaleString()
        })
        
        currentConversationId.value = response.data.id
        currentMessages.value = []
        conversations.value.unshift(response.data)
        
        ElMessage.success('已创建新对话')
      } catch (error) {
        console.error('创建对话失败:', error)
        ElMessage.error('创建对话失败')
      }
    }

    // 切换对话
    const switchConversation = async (conversationId) => {
      try {
        currentConversationId.value = conversationId
        const response = await axios.get(`/api/health-consult/conversations/${conversationId}/messages`)
        currentMessages.value = response.data
        
        await nextTick()
        scrollToBottom()
        
        if (isMobile.value) {
          showSidebar.value = false
        }
      } catch (error) {
        console.error('加载对话消息失败:', error)
        ElMessage.error('加载对话消息失败')
      }
    }

    // 删除对话
    const deleteConversation = async (conversationId) => {
      try {
        await ElMessageBox.confirm('确定要删除这个对话吗？此操作不可恢复。', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        await axios.delete(`/api/health-consult/conversations/${conversationId}`)
        
        conversations.value = conversations.value.filter(c => c.id !== conversationId)
        
        if (currentConversationId.value === conversationId) {
          currentConversationId.value = null
          currentMessages.value = []
        }
        
        ElMessage.success('对话已删除')
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除对话失败:', error)
          ElMessage.error('删除对话失败')
        }
      }
    }

    // 发送快捷问题
    const askQuickQuestion = async (question) => {
      if (!currentConversationId.value) {
        await startNewConversation()
      }
      
      inputMessage.value = question
      await sendMessage()
    }

    // 发送消息
    const sendMessage = async () => {
      if (!inputMessage.value.trim() || !currentConversationId.value || isSending.value) {
        return
      }
      
      const message = inputMessage.value.trim()
      inputMessage.value = ''
      
      // 添加用户消息
      const userMessage = {
        id: Date.now(),
        role: 'user',
        content: message,
        createTime: new Date()
      }
      currentMessages.value.push(userMessage)
      
      await nextTick()
      scrollToBottom()
      
      // 显示输入中动画
      isTyping.value = true
      isSending.value = true
      
      try {
        const response = await axios.post('/api/health-consult/chat', {
          conversationId: currentConversationId.value,
          message: message,
          context: {
            healthProfile: healthProfile,
            recentIndicators: recentIndicators.value
          }
        })
        
        // 添加AI回复
        const assistantMessage = {
          id: response.data.id,
          role: 'assistant',
          content: response.data.content,
          healthAdvice: response.data.healthAdvice,
          adviceReason: response.data.adviceReason,
          recipe: response.data.recipe,
          createTime: new Date(),
          liked: false
        }
        
        currentMessages.value.push(assistantMessage)
        
        // 更新对话列表
        const convIndex = conversations.value.findIndex(c => c.id === currentConversationId.value)
        if (convIndex !== -1) {
          conversations.value[convIndex].lastMessage = message
          conversations.value[convIndex].updateTime = new Date()
        }
        
        await nextTick()
        scrollToBottom()
        
      } catch (error) {
        console.error('发送消息失败:', error)
        ElMessage.error('发送消息失败，请重试')
      } finally {
        isTyping.value = false
        isSending.value = false
      }
    }

    // 上传图片
    const handleImageUpload = async (file) => {
      const formData = new FormData()
      formData.append('image', file)
      formData.append('conversationId', currentConversationId.value)
      
      try {
        const response = await axios.post('/api/health-consult/upload-image', formData)
        
        const userMessage = {
          id: Date.now(),
          role: 'user',
          content: `[图片] ${file.name}`,
          imageUrl: response.data.url,
          createTime: new Date()
        }
        
        currentMessages.value.push(userMessage)
        
        await nextTick()
        scrollToBottom()
        
        ElMessage.success('图片上传成功')
      } catch (error) {
        console.error('图片上传失败:', error)
        ElMessage.error('图片上传失败')
      }
      
      return false
    }

    // 插入健康数据
    const insertHealthData = () => {
      let dataText = ''
      
      if (activeDataTab.value === 'basic') {
        dataText = `我的基本信息: 性别${healthProfile.gender}，年龄${healthProfile.age}岁，身高${healthProfile.height}cm，体重${healthProfile.weight}kg，BMI${healthProfile.bmi}`
      } else if (activeDataTab.value === 'chronic' && selectedChronicDiseases.value.length > 0) {
        dataText = `我有以下慢性病: ${selectedChronicDiseases.value.join('、')}`
      } else if (activeDataTab.value === 'allergy' && selectedAllergies.value.length > 0) {
        dataText = `我对以下物质过敏: ${selectedAllergies.value.join('、')}`
      }
      
      if (dataText) {
        inputMessage.value = dataText + (inputMessage.value ? '\n' + inputMessage.value : '')
        contextInfo.value = '已引用健康档案数据'
      }
      
      showHealthDataSelector.value = false
    }

    // 选择指标
    const selectIndicator = (indicator) => {
      const dataText = `我最近的${indicator.type}是${indicator.value}，记录时间${indicator.date}`
      inputMessage.value = dataText + (inputMessage.value ? '\n' + inputMessage.value : '')
      contextInfo.value = `已引用${indicator.type}数据`
      showHealthDataSelector.value = false
    }

    // 清除上下文
    const clearContext = () => {
      contextInfo.value = ''
    }

    // 复制消息
    const copyMessage = (content) => {
      navigator.clipboard.writeText(content).then(() => {
        ElMessage.success('已复制到剪贴板')
      }).catch(() => {
        ElMessage.error('复制失败')
      })
    }

    // 点赞消息
    const likeMessage = async (messageId) => {
      try {
        await axios.post(`/api/health-consult/messages/${messageId}/like`)
        
        const message = currentMessages.value.find(m => m.id === messageId)
        if (message) {
          message.liked = !message.liked
        }
        
        ElMessage.success(message.liked ? '已点赞' : '已取消点赞')
      } catch (error) {
        console.error('点赞失败:', error)
        ElMessage.error('操作失败')
      }
    }

    // 重新生成回复
    const regenerateResponse = async (messageId) => {
      try {
        const messageIndex = currentMessages.value.findIndex(m => m.id === messageId)
        if (messageIndex === -1 || messageIndex === 0) return
        
        const userMessage = currentMessages.value[messageIndex - 1]
        
        // 移除当前AI回复
        currentMessages.value.splice(messageIndex, 1)
        
        isTyping.value = true
        
        const response = await axios.post('/api/health-consult/regenerate', {
          conversationId: currentConversationId.value,
          messageId: userMessage.id
        })
        
        const newMessage = {
          id: response.data.id,
          role: 'assistant',
          content: response.data.content,
          healthAdvice: response.data.healthAdvice,
          adviceReason: response.data.adviceReason,
          recipe: response.data.recipe,
          createTime: new Date(),
          liked: false
        }
        
        currentMessages.value.push(newMessage)
        
        await nextTick()
        scrollToBottom()
        
      } catch (error) {
        console.error('重新生成失败:', error)
        ElMessage.error('重新生成失败')
      } finally {
        isTyping.value = false
      }
    }

    // 打开历史抽屉
    const openHistoryDrawer = () => {
      historyDrawerVisible.value = true
    }

    // 滚动到底部
    const scrollToBottom = () => {
      if (messageList.value) {
        messageList.value.scrollTop = messageList.value.scrollHeight
      }
    }

    // 格式化时间
    const formatTime = (time) => {
      const date = new Date(time)
      const now = new Date()
      const diff = now - date
      
      if (diff < 60000) return '刚刚'
      if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
      if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
      if (diff < 604800000) return `${Math.floor(diff / 86400000)}天前`
      
      return date.toLocaleDateString()
    }

    // 格式化消息时间
    const formatMessageTime = (time) => {
      const date = new Date(time)
      return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
    }

    // 格式化消息内容（支持Markdown）
    const formatMessageContent = (content) => {
      if (!content) return ''
      
      // 简单的Markdown解析
      let formatted = content
        .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
        .replace(/\*(.*?)\*/g, '<em>$1</em>')
        .replace(/\n/g, '<br/>')
        .replace(/`(.*?)`/g, '<code>$1</code>')
      
      return formatted
    }

    // 窗口大小改变处理
    const handleResize = () => {
      isMobile.value = window.innerWidth < 768
      showSidebar.value = !isMobile.value
    }

    // 监听对话变化，滚动到底部
    watch(() => currentMessages.value.length, () => {
      nextTick(() => scrollToBottom())
    })

    // 生命周期
    onMounted(() => {
      loadConversations()
      loadHealthProfile()
      window.addEventListener('resize', handleResize)
    })

    onBeforeUnmount(() => {
      window.removeEventListener('resize', handleResize)
    })

    return {
      isMobile,
      showSidebar,
      currentConversationId,
      conversations,
      currentMessages,
      inputMessage,
      isTyping,
      isSending,
      historyDrawerVisible,
      showHealthDataSelector,
      activeDataTab,
      contextInfo,
      messageList,
      userAvatar,
      quickQuestions,
      personalizedTips,
      healthProfile,
      selectedChronicDiseases,
      selectedAllergies,
      recentIndicators,
      startNewConversation,
      switchConversation,
      deleteConversation,
      askQuickQuestion,
      sendMessage,
      handleImageUpload,
      insertHealthData,
      selectIndicator,
      clearContext,
      copyMessage,
      likeMessage,
      regenerateResponse,
      openHistoryDrawer,
      formatTime,
      formatMessageTime,
      formatMessageContent
    }
  }
}
</script>

<style scoped lang="scss">
.health-consult-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f7fa;

  .page-header {
    background: #fff;
    padding: 20px 30px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
    display: flex;
    justify-content: space-between;
    align-items: center;
    z-index: 10;

    .header-content {
      h1 {
        font-size: 24px;
        color: #303133;
        margin: 0 0 5px 0;
      }

      .subtitle {
        font-size: 14px;
        color: #909399;
        margin: 0;
      }
    }

    .header-actions {
      display: flex;
      gap: 10px;
    }
  }

  .consult-main {
    flex: 1;
    display: flex;
    overflow: hidden;
    margin: 20px;
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);

    .conversation-sidebar {
      width: 280px;
      border-right: 1px solid #e4e7ed;
      display: flex;
      flex-direction: column;
      background: #fafafa;

      .sidebar-header {
        padding: 15px 20px;
        border-bottom: 1px solid #e4e7ed;
        display: flex;
        justify-content: space-between;
        align-items: center;

        h3 {
          margin: 0;
          font-size: 16px;
          color: #303133;
        }
      }

      .conversation-list {
        flex: 1;
        overflow-y: auto;
        padding: 10px;

        .conversation-item {
          padding: 15px;
          margin-bottom: 8px;
          background: #fff;
          border-radius: 6px;
          cursor: pointer;
          transition: all 0.3s;
          display: flex;
          justify-content: space-between;
          align-items: flex-start;

          &:hover {
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

            .delete-btn {
              opacity: 1;
            }
          }

          &.active {
            background: #ecf5ff;
            border-left: 3px solid #409eff;
          }

          .conv-info {
            flex: 1;

            .conv-title {
              font-size: 14px;
              font-weight: 500;
              color: #303133;
              margin-bottom: 5px;
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
            }

            .conv-preview {
              font-size: 12px;
              color: #909399;
              margin-bottom: 5px;
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
            }

            .conv-time {
              font-size: 11px;
              color: #c0c4cc;
            }
          }

          .delete-btn {
            opacity: 0;
            transition: opacity 0.3s;
            color: #f56c6c;
          }
        }
      }
    }

    .conversation-content {
      flex: 1;
      display: flex;
      flex-direction: column;
      overflow: hidden;

      .welcome-screen {
        flex: 1;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        padding: 40px;
        overflow-y: auto;

        .welcome-icon {
          font-size: 80px;
          color: #409eff;
          margin-bottom: 20px;
        }

        h2 {
          font-size: 28px;
          color: #303133;
          margin-bottom: 10px;
        }

        .welcome-desc {
          font-size: 14px;
          color: #606266;
          text-align: center;
          line-height: 1.8;
          max-width: 600px;

          .disclaimer {
            color: #e6a23c;
            font-size: 12px;
          }
        }

        .quick-questions {
          margin-top: 40px;
          width: 100%;
          max-width: 900px;

          h3 {
            font-size: 18px;
            color: #303133;
            margin-bottom: 20px;
            text-align: center;
          }

          .question-card {
            cursor: pointer;
            transition: all 0.3s;
            margin-bottom: 15px;

            &:hover {
              transform: translateY(-3px);
              box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
            }

            :deep(.el-card__body) {
              padding: 20px;
              display: flex;
              align-items: center;
            }

            .question-icon {
              font-size: 32px;
              color: #409eff;
              margin-right: 15px;
            }

            .question-text {
              font-size: 14px;
              color: #606266;
              flex: 1;
            }
          }
        }

        .personalized-tips {
          margin-top: 40px;
          width: 100%;
          max-width: 900px;

          h3 {
            font-size: 18px;
            color: #303133;
            margin-bottom: 20px;
            text-align: center;
          }

          .tip-card {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            padding: 20px;
            border-radius: 8px;
            color: #fff;
            cursor: pointer;
            display: flex;
            align-items: center;
            height: 120px;

            .tip-icon {
              font-size: 48px;
              margin-right: 20px;
            }

            .tip-content {
              flex: 1;

              h4 {
                font-size: 16px;
                margin: 0 0 8px 0;
              }

              p {
                font-size: 13px;
                margin: 0;
                opacity: 0.9;
              }
            }
          }
        }
      }

      .message-list {
        flex: 1;
        overflow-y: auto;
        padding: 20px;

        .message-item {
          display: flex;
          margin-bottom: 20px;
          animation: fadeIn 0.3s;

          &.user {
            flex-direction: row-reverse;

            .message-content {
              align-items: flex-end;
              margin-right: 12px;
              margin-left: 0;

              .message-text {
                background: #409eff;
                color: #fff;
                border-radius: 12px 12px 0 12px;
              }
            }
          }

          &.assistant {
            .message-content {
              .message-text {
                background: #f4f4f5;
                color: #303133;
                border-radius: 12px 12px 12px 0;
              }
            }
          }

          .message-avatar {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            overflow: hidden;
            flex-shrink: 0;
            background: #e4e7ed;
            display: flex;
            align-items: center;
            justify-content: center;

            img {
              width: 100%;
              height: 100%;
              object-fit: cover;
            }

            i {
              font-size: 24px;
              color: #409eff;
            }
          }

          .message-content {
            max-width: 70%;
            display: flex;
            flex-direction: column;
            margin-left: 12px;

            .message-header {
              display: flex;
              justify-content: space-between;
              align-items: center;
              margin-bottom: 5px;

              .sender-name {
                font-size: 12px;
                color: #909399;
                font-weight: 500;
              }

              .message-time {
                font-size: 11px;
                color: #c0c4cc;
                margin-left: 10px;
              }
            }

            .message-text {
              padding: 12px 16px;
              font-size: 14px;
              line-height: 1.6;
              word-wrap: break-word;

              :deep(code) {
                background: rgba(0, 0, 0, 0.05);
                padding: 2px 6px;
                border-radius: 3px;
                font-family: monospace;
              }
            }

            .health-advice-card {
              margin-top: 10px;
              padding: 15px;
              background: #f0f9ff;
              border-left: 3px solid #409eff;
              border-radius: 6px;

              .advice-header {
                display: flex;
                align-items: center;
                font-size: 14px;
                font-weight: 500;
                color: #409eff;
                margin-bottom: 10px;

                i {
                  margin-right: 5px;
                }
              }

              .advice-content {
                .advice-item {
                  display: flex;
                  align-items: flex-start;
                  font-size: 13px;
                  color: #606266;
                  margin-bottom: 8px;

                  i {
                    color: #67c23a;
                    margin-right: 8px;
                    margin-top: 3px;
                  }
                }
              }

              .advice-reason {
                margin-top: 10px;
                padding-top: 10px;
                border-top: 1px solid #d9ecff;
                font-size: 12px;
                color: #909399;

                strong {
                  color: #606266;
                }
              }
            }

            .recipe-card {
              margin-top: 10px;
              padding: 15px;
              background: #fef0f0;
              border-left: 3px solid #f56c6c;
              border-radius: 6px;

              .recipe-header {
                display: flex;
                align-items: center;
                font-size: 16px;
                font-weight: 500;
                color: #f56c6c;
                margin-bottom: 12px;

                i {
                  margin-right: 8px;
                }
              }

              .recipe-info {
                margin-bottom: 10px;

                .recipe-tags {
                  margin-bottom: 8px;

                  .el-tag {
                    margin-right: 5px;
                  }
                }

                .recipe-nutrition {
                  display: flex;
                  gap: 15px;
                  font-size: 12px;
                  color: #909399;
                }
              }

              .recipe-ingredients {
                font-size: 13px;
                color: #606266;
                margin-bottom: 10px;
                line-height: 1.6;

                strong {
                  color: #303133;
                }
              }

              .recipe-steps {
                font-size: 13px;
                color: #606266;

                strong {
                  color: #303133;
                }

                ol {
                  margin: 5px 0 0 0;
                  padding-left: 20px;

                  li {
                    margin-bottom: 5px;
                    line-height: 1.6;
                  }
                }
              }
            }

            .message-actions {
              display: flex;
              gap: 5px;
              margin-top: 8px;

              .el-button {
                font-size: 12px;
                padding: 0;

                &.liked {
                  color: #409eff;
                }
              }
            }
          }

          .typing-indicator {
            background: #f4f4f5;
            padding: 12px 16px;
            border-radius: 12px;
            display: flex;
            gap: 5px;

            span {
              width: 8px;
              height: 8px;
              background: #909399;
              border-radius: 50%;
              animation: typing 1.4s infinite;

              &:nth-child(2) {
                animation-delay: 0.2s;
              }

              &:nth-child(3) {
                animation-delay: 0.4s;
              }
            }
          }
        }
      }

      .input-area {
        border-top: 1px solid #e4e7ed;
        padding: 15px 20px;
        background: #fafafa;

        .context-info {
          display: flex;
          align-items: center;
          padding: 8px 12px;
          background: #ecf5ff;
          border-radius: 4px;
          font-size: 12px;
          color: #409eff;
          margin-bottom: 10px;

          i {
            margin-right: 5px;
          }

          span {
            flex: 1;
          }
        }

        .input-wrapper {
          display: flex;
          gap: 10px;
          align-items: flex-end;

          .sidebar-toggle {
            margin-bottom: 5px;
          }

          .el-textarea {
            flex: 1;

            :deep(textarea) {
              border-radius: 8px;
              resize: none;
              font-size: 14px;
            }
          }

          .input-actions {
            display: flex;
            gap: 8px;
            align-items: flex-end;
            margin-bottom: 5px;

            .el-button {
              .shortcut-hint {
                font-size: 11px;
                opacity: 0.7;
                margin-left: 5px;
              }
            }
          }
        }

        .disclaimer-text {
          margin-top: 10px;
          font-size: 11px;
          color: #e6a23c;
          display: flex;
          align-items: center;

          i {
            margin-right: 5px;
          }
        }
      }
    }
  }

  .history-list {
    padding: 10px;

    .el-card {
      cursor: pointer;
      transition: all 0.3s;

      &:hover {
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      }

      h4 {
        font-size: 14px;
        color: #303133;
        margin: 0 0 8px 0;
      }

      .conv-summary {
        font-size: 12px;
        color: #909399;
        margin: 0 0 10px 0;
        line-height: 1.5;
      }

      .conv-meta {
        display: flex;
        justify-content: space-between;
        font-size: 11px;
        color: #c0c4cc;

        span {
          display: flex;
          align-items: center;

          i {
            margin-right: 3px;
          }
        }
      }
    }
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes typing {
  0%, 60%, 100% {
    transform: translateY(0);
  }
  30% {
    transform: translateY(-10px);
  }
}

// 响应式设计
@media (max-width: 768px) {
  .health-consult-container {
    .page-header {
      padding: 15px;
      flex-direction: column;
      align-items: flex-start;

      .header-content {
        margin-bottom: 10px;

        h1 {
          font-size: 20px;
        }
      }

      .header-actions {
        width: 100%;
        justify-content: space-between;

        .el-button {
          flex: 1;
        }
      }
    }

    .consult-main {
      margin: 10px;

      .conversation-sidebar {
        position: fixed;
        left: 0;
        top: 0;
        bottom: 0;
        z-index: 1000;
        background: #fff;
        box-shadow: 2px 0 8px rgba(0, 0, 0, 0.15);
      }

      .conversation-content {
        .welcome-screen {
          padding: 20px;

          .welcome-icon {
            font-size: 60px;
          }

          h2 {
            font-size: 22px;
          }

          .quick-questions {
            .question-card {
              :deep(.el-card__body) {
                padding: 15px;
              }

              .question-icon {
                font-size: 24px;
              }

              .question-text {
                font-size: 13px;
              }
            }
          }
        }

        .message-list {
          padding: 15px;

          .message-item {
            .message-content {
              max-width: 85%;
            }
          }
        }

        .input-area {
          padding: 10px;

          .input-wrapper {
            flex-wrap: wrap;

            .el-textarea {
              width: 100%;
            }

            .input-actions {
              width: 100%;
              justify-content: flex-end;
              margin-top: 10px;
            }
          }
        }
      }
    }
  }
}
</style>