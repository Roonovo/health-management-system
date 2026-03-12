<template>
  <div class="ai-consultation">
    <el-row :gutter="20">
      <!-- 左侧：对话历史 -->
      <el-col :xs="24" :md="8" :lg="6">
        <el-card class="history-card">
          <template #header>
            <div class="card-header">
              <span>咨询历史</span>
              <el-button type="primary" text @click="startNewConsultation">
                <el-icon><Plus /></el-icon>
                新咨询
              </el-button>
            </div>
          </template>

          <el-scrollbar height="calc(100vh - 220px)">
            <div class="conversation-list">
              <div
                v-for="conv in conversations"
                :key="conv.id"
                class="conversation-item"
                :class="{ active: currentConversation?.id === conv.id }"
                @click="selectConversation(conv)"
              >
                <div class="conversation-title">{{ conv.title }}</div>
                <div class="conversation-time">{{ conv.updatedAt }}</div>
              </div>

              <el-empty
                v-if="conversations.length === 0"
                description="暂无历史记录"
                :image-size="60"
              />
            </div>
          </el-scrollbar>
        </el-card>
      </el-col>

      <!-- 右侧：对话区域 -->
      <el-col :xs="24" :md="16" :lg="18">
        <el-card class="chat-card">
          <template #header>
            <div class="card-header">
              <div class="chat-title">
                <el-icon :size="24" color="#409EFF">
                  <ChatDotSquare />
                </el-icon>
                <span>AI 健康顾问</span>
              </div>
              <div class="chat-actions">
                <el-dropdown @command="handleCommand">
                  <el-button text>
                    <el-icon><MoreFilled /></el-icon>
                  </el-button>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item command="export">导出对话</el-dropdown-item>
                      <el-dropdown-item command="clear" divided>清空对话</el-dropdown-item>
                      <el-dropdown-item command="delete">删除对话</el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </div>
            </div>
          </template>

          <!-- 消息列表 -->
          <el-scrollbar
            ref="scrollbarRef"
            class="message-container"
            height="calc(100vh - 340px)"
          >
            <div class="messages">
              <!-- 欢迎消息 -->
              <div v-if="messages.length === 0" class="welcome-message">
                <div class="welcome-icon">
                  <el-icon :size="60" color="#409EFF">
                    <ChatDotSquare />
                  </el-icon>
                </div>
                <h2>您好！我是 AI 健康顾问</h2>
                <p>我可以帮您：</p>
                <div class="feature-list">
                  <div class="feature-item">
                    <el-icon color="#67C23A"><Check /></el-icon>
                    <span>解答健康问题</span>
                  </div>
                  <div class="feature-item">
                    <el-icon color="#67C23A"><Check /></el-icon>
                    <span>分析健康数据</span>
                  </div>
                  <div class="feature-item">
                    <el-icon color="#67C23A"><Check /></el-icon>
                    <span>提供饮食建议</span>
                  </div>
                  <div class="feature-item">
                    <el-icon color="#67C23A"><Check /></el-icon>
                    <span>推荐运动方案</span>
                  </div>
                </div>

                <!-- 快速问题 -->
                <div class="quick-questions">
                  <p>您可以问我：</p>
                  <el-tag
                    v-for="question in quickQuestions"
                    :key="question"
                    class="quick-question"
                    @click="sendQuickQuestion(question)"
                  >
                    {{ question }}
                  </el-tag>
                </div>
              </div>

              <!-- 对话消息 -->
              <div
                v-for="message in messages"
                :key="message.id"
                class="message-item"
                :class="message.role"
              >
                <div class="message-avatar">
                  <el-avatar v-if="message.role === 'user'" :src="userAvatar">
                    <el-icon><User /></el-icon>
                  </el-avatar>
                  <el-avatar v-else :style="{ backgroundColor: '#409EFF' }">
                    <el-icon><Cpu /></el-icon>
                  </el-avatar>
                </div>

                <div class="message-content">
                  <div class="message-header">
                    <span class="message-name">
                      {{ message.role === 'user' ? '我' : 'AI 顾问' }}
                    </span>
                    <span class="message-time">{{ message.time }}</span>
                  </div>

                  <div class="message-body">
                    <div v-if="message.loading" class="typing-indicator">
                      <span></span>
                      <span></span>
                      <span></span>
                    </div>
                    <div v-else v-html="formatMessage(message.content)"></div>
                  </div>

                  <!-- 消息操作 -->
                  <div v-if="!message.loading && message.role === 'assistant'" class="message-actions">
                    <el-button type="text" size="small" @click="copyMessage(message)">
                      <el-icon><DocumentCopy /></el-icon>
                      复制
                    </el-button>
                    <el-button type="text" size="small" @click="regenerateResponse(message)">
                      <el-icon><Refresh /></el-icon>
                      重新生成
                    </el-button>
                  </div>
                </div>
              </div>

              <!-- 引用上下文 -->
              <div v-if="contextData.length > 0" class="context-data">
                <el-alert
                  title="已引用您的健康数据"
                  type="info"
                  :closable="false"
                  show-icon
                >
                  <template #default>
                    <div class="context-items">
                      <el-tag v-for="item in contextData" :key="item.type" size="small">
                        {{ item.label }}
                      </el-tag>
                    </div>
                  </template>
                </el-alert>
              </div>
            </div>
          </el-scrollbar>

          <!-- 输入框 -->
          <div class="input-area">
            <div class="input-tools">
              <el-tooltip content="引用健康数据" placement="top">
                <el-button
                  text
                  @click="showDataSelector = true"
                  :disabled="sending"
                >
                  <el-icon><DataLine /></el-icon>
                </el-button>
              </el-tooltip>

              <el-tooltip content="上传图片" placement="top">
                <el-upload
                  :show-file-list="false"
                  :before-upload="handleImageUpload"
                  accept="image/*"
                  :disabled="sending"
                >
                  <el-button text>
                    <el-icon><Picture /></el-icon>
                  </el-button>
                </el-upload>
              </el-tooltip>
            </div>

            <el-input
              v-model="inputMessage"
              type="textarea"
              :rows="3"
              placeholder="输入您的问题..."
              :disabled="sending"
              @keydown.enter.exact.prevent="sendMessage"
              @keydown.enter.ctrl="inputMessage += '\n'"
            />

            <div class="input-footer">
              <div class="input-hint">
                按 Enter 发送，Ctrl+Enter 换行
              </div>
              <el-button
                type="primary"
                :loading="sending"
                :disabled="!inputMessage.trim()"
                @click="sendMessage"
              >
                <el-icon v-if="!sending"><Position /></el-icon>
                {{ sending ? '发送中...' : '发送' }}
              </el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 健康数据选择器 -->
    <el-dialog
      v-model="showDataSelector"
      title="选择健康数据"
      width="500px"
    >
      <el-checkbox-group v-model="selectedData">
        <el-checkbox label="bloodPressure">最近血压数据</el-checkbox>
        <el-checkbox label="bloodSugar">最近血糖数据</el-checkbox>
        <el-checkbox label="weight">最近体重数据</el-checkbox>
        <el-checkbox label="heartRate">最近心率数据</el-checkbox>
        <el-checkbox label="exercise">运动记录</el-checkbox>
        <el-checkbox label="diet">饮食记录</el-checkbox>
        <el-checkbox label="medication">用药记录</el-checkbox>
        <el-checkbox label="healthRecord">健康档案</el-checkbox>
      </el-checkbox-group>

      <template #footer>
        <el-button @click="showDataSelector = false">取消</el-button>
        <el-button type="primary" @click="confirmDataSelection">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, nextTick, onMounted } from 'vue'
import { useStore } from 'vuex'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus,
  ChatDotSquare,
  MoreFilled,
  Check,
  User,
  Cpu,
  DocumentCopy,
  Refresh,
  DataLine,
  Picture,
  Position
} from '@element-plus/icons-vue'
import {
  getConversations,
  createConversation,
  getMessages,
  sendMessage as sendMessageApi,
  deleteConversation,
  exportConversation
} from '@/api/consultation'
import { marked } from 'marked'

const store = useStore()

const scrollbarRef = ref()
const showDataSelector = ref(false)
const sending = ref(false)
const inputMessage = ref('')

// 用户头像
const userAvatar = computed(() => store.getters.userInfo?.avatar || '')

// 对话列表
const conversations = ref([])

// 当前对话
const currentConversation = ref(null)

// 消息列表
const messages = ref([])

// 上下文数据
const contextData = ref([])
const selectedData = ref([])

// 快速问题
const quickQuestions = [
  '如何降低血压？',
  '糖尿病患者的饮食建议',
  '适合老年人的运动',
  '如何改善睡眠质量？'
]

// 格式化消息（支持Markdown）
const formatMessage = (content) => {
  return marked(content, { breaks: true })
}

// 新建咨询
const startNewConsultation = async () => {
  try {
    const res = await createConversation({
      title: `咨询 ${new Date().toLocaleString()}`
    })

    if (res.success) {
      currentConversation.value = res.data
      messages.value = []
      contextData.value = []
      await loadConversations()
    }
  } catch (error) {
    ElMessage.error('创建失败：' + error.message)
  }
}

// 选择对话
const selectConversation = async (conv) => {
  currentConversation.value = conv
  await loadMessages(conv.id)
}

// 发送快速问题
const sendQuickQuestion = (question) => {
  inputMessage.value = question
  sendMessage()
}

// 发送消息
const sendMessage = async () => {
    if (!inputMessage.value.trim()) {
    return
  }

  // 如果没有当前对话，创建新对话
  if (!currentConversation.value) {
    await startNewConsultation()
  }

  const userMessage = {
    id: Date.now(),
    role: 'user',
    content: inputMessage.value.trim(),
    time: new Date().toLocaleTimeString(),
    loading: false
  }

  messages.value.push(userMessage)
  
  // 清空输入框
  const messageContent = inputMessage.value
  inputMessage.value = ''

  // 添加AI加载消息
  const aiMessage = {
    id: Date.now() + 1,
    role: 'assistant',
    content: '',
    time: new Date().toLocaleTimeString(),
    loading: true
  }
  messages.value.push(aiMessage)

  // 滚动到底部
  scrollToBottom()

  sending.value = true

  try {
    const res = await sendMessageApi({
      conversationId: currentConversation.value.id,
      message: messageContent,
      context: contextData.value
    })

    if (res.success) {
      // 更新AI消息
      aiMessage.loading = false
      aiMessage.content = res.data.reply

      // 清空上下文数据
      contextData.value = []

      scrollToBottom()
    }
  } catch (error) {
    ElMessage.error('发送失败：' + error.message)
    // 移除加载消息
    messages.value = messages.value.filter(m => m.id !== aiMessage.id)
  } finally {
    sending.value = false
  }
}

// 复制消息
const copyMessage = async (message) => {
  try {
    await navigator.clipboard.writeText(message.content)
    ElMessage.success('已复制到剪贴板')
  } catch (error) {
    ElMessage.error('复制失败')
  }
}

// 重新生成回复
const regenerateResponse = async (message) => {
  const index = messages.value.findIndex(m => m.id === message.id)
  if (index === -1 || index === 0) return

  // 获取上一条用户消息
  const userMessage = messages.value[index - 1]
  if (userMessage.role !== 'user') return

  // 标记为加载中
  message.loading = true
  message.content = ''

  try {
    const res = await sendMessageApi({
      conversationId: currentConversation.value.id,
      message: userMessage.content,
      regenerate: true
    })

    if (res.success) {
      message.loading = false
      message.content = res.data.reply
    }
  } catch (error) {
    ElMessage.error('重新生成失败：' + error.message)
    message.loading = false
  }
}

// 处理图片上传
const handleImageUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片文件！')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB！')
    return false
  }

  // TODO: 实现图片上传和识别
  ElMessage.info('图片上传功能开发中...')
  return false
}

// 确认数据选择
const confirmDataSelection = () => {
  const dataLabels = {
    bloodPressure: '血压数据',
    bloodSugar: '血糖数据',
    weight: '体重数据',
    heartRate: '心率数据',
    exercise: '运动记录',
    diet: '饮食记录',
    medication: '用药记录',
    healthRecord: '健康档案'
  }

  contextData.value = selectedData.value.map(key => ({
    type: key,
    label: dataLabels[key]
  }))

  showDataSelector.value = false
  ElMessage.success(`已选择 ${contextData.value.length} 项数据`)
}

// 处理菜单命令
const handleCommand = async (command) => {
  if (!currentConversation.value) {
    ElMessage.warning('请先选择一个对话')
    return
  }

  switch (command) {
    case 'export':
      await exportConv()
      break
    case 'clear':
      await clearMessages()
      break
    case 'delete':
      await deleteConv()
      break
  }
}

// 导出对话
const exportConv = async () => {
  try {
    const res = await exportConversation(currentConversation.value.id)
    if (res.success) {
      // 创建下载
      const blob = new Blob([res.data], { type: 'text/markdown' })
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = `${currentConversation.value.title}.md`
      link.click()
      window.URL.revokeObjectURL(url)
      
      ElMessage.success('导出成功')
    }
  } catch (error) {
    ElMessage.error('导出失败：' + error.message)
  }
}

// 清空对话
const clearMessages = async () => {
  try {
    await ElMessageBox.confirm('确定要清空当前对话吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    messages.value = []
    ElMessage.success('已清空对话')
  } catch (error) {
    // 用户取消
  }
}

// 删除对话
const deleteConv = async () => {
  try {
    await ElMessageBox.confirm('确定要删除这个对话吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const res = await deleteConversation(currentConversation.value.id)
    if (res.success) {
      ElMessage.success('已删除对话')
      currentConversation.value = null
      messages.value = []
      await loadConversations()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败：' + error.message)
    }
  }
}

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    if (scrollbarRef.value) {
      scrollbarRef.value.setScrollTop(999999)
    }
  })
}

// 加载数据
const loadConversations = async () => {
  try {
    const res = await getConversations()
    if (res.success) {
      conversations.value = res.data
    }
  } catch (error) {
    console.error('加载对话列表失败：', error)
  }
}

const loadMessages = async (conversationId) => {
  try {
    const res = await getMessages(conversationId)
    if (res.success) {
      messages.value = res.data
      scrollToBottom()
    }
  } catch (error) {
    ElMessage.error('加载消息失败：' + error.message)
  }
}

onMounted(() => {
  loadConversations()
})
</script>

<style scoped>
.ai-consultation {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 历史对话列表 */
.history-card {
  height: calc(100vh - 140px);
}

.conversation-list {
  padding: 10px 0;
}

.conversation-item {
  padding: 15px;
  margin-bottom: 10px;
  background-color: #f5f7fa;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.conversation-item:hover {
  background-color: #e4e7ed;
}

.conversation-item.active {
  background-color: #ecf5ff;
  border-left: 3px solid #409EFF;
}

.conversation-title {
  font-size: 14px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.conversation-time {
  font-size: 12px;
  color: #909399;
}

/* 聊天区域 */
.chat-card {
  height: calc(100vh - 140px);
  display: flex;
  flex-direction: column;
}

.chat-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 18px;
  font-weight: bold;
}

.message-container {
  flex: 1;
  padding: 20px;
}

.messages {
  min-height: 100%;
}

/* 欢迎消息 */
.welcome-message {
  text-align: center;
  padding: 60px 20px;
}

.welcome-icon {
  margin-bottom: 20px;
}

.welcome-message h2 {
  margin: 0 0 15px;
  color: #303133;
}

.welcome-message p {
  margin: 10px 0 20px;
  color: #606266;
  font-size: 16px;
}

.feature-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
  max-width: 400px;
  margin: 0 auto 30px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px;
  background-color: #f0f9ff;
  border-radius: 8px;
  color: #606266;
}

.quick-questions {
  margin-top: 30px;
}

.quick-questions p {
  margin-bottom: 15px;
  font-weight: bold;
}

.quick-question {
  margin: 5px;
  cursor: pointer;
  transition: transform 0.2s;
}

.quick-question:hover {
  transform: scale(1.05);
}

/* 消息项 */
.message-item {
  display: flex;
  gap: 15px;
  margin-bottom: 30px;
}

.message-item.user {
  flex-direction: row-reverse;
}

.message-avatar {
  flex-shrink: 0;
}

.message-content {
  flex: 1;
  max-width: 70%;
}

.message-item.user .message-content {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.message-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.message-name {
  font-size: 14px;
  font-weight: bold;
  color: #303133;
}

.message-time {
  font-size: 12px;
  color: #909399;
}

.message-body {
  padding: 15px;
  border-radius: 12px;
  font-size: 15px;
  line-height: 1.6;
  word-wrap: break-word;
}

.message-item.user .message-body {
  background-color: #409EFF;
  color: white;
}

.message-item.assistant .message-body {
  background-color: #f5f7fa;
  color: #303133;
}

/* Markdown样式 */
.message-body :deep(p) {
  margin: 0 0 10px;
}

.message-body :deep(p:last-child) {
  margin-bottom: 0;
}

.message-body :deep(code) {
  padding: 2px 6px;
  background-color: rgba(0, 0, 0, 0.1);
  border-radius: 4px;
  font-family: monospace;
}

.message-body :deep(pre) {
  padding: 15px;
  background-color: #282c34;
  color: #abb2bf;
  border-radius: 8px;
  overflow-x: auto;
}

.message-body :deep(ul),
.message-body :deep(ol) {
  padding-left: 20px;
  margin: 10px 0;
}

/* 打字指示器 */
.typing-indicator {
  display: flex;
  gap: 5px;
  padding: 10px 0;
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  background-color: #909399;
  border-radius: 50%;
  animation: typing 1.4s infinite;
}

.typing-indicator span:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-indicator span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing {
  0%, 60%, 100% {
    transform: translateY(0);
    opacity: 0.5;
  }
  30% {
    transform: translateY(-10px);
    opacity: 1;
  }
}

.message-actions {
  display: flex;
  gap: 10px;
  margin-top: 10px;
}

/* 上下文数据 */
.context-data {
  margin: 20px 0;
}

.context-items {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 10px;
}

/* 输入区域 */
.input-area {
  border-top: 1px solid #e4e7ed;
  padding: 15px;
  background-color: #fafafa;
}

.input-tools {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
}

.input-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}

.input-hint {
  font-size: 12px;
  color: #909399;
}

@media (max-width: 768px) {
  .message-content {
    max-width: 85%;
  }

  .feature-list {
    grid-template-columns: 1fr;
  }
}
</style>