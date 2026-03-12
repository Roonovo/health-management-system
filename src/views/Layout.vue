<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '200px'" class="sidebar">
      <div class="logo-container">
        <img src="@/assets/logo.png" alt="Logo" class="logo" />
        <span v-if="!isCollapse" class="logo-text">健康管理</span>
      </div>

      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :unique-opened="true"
        router
        class="sidebar-menu"
      >
        <template v-for="route in menuRoutes" :key="route.path">
          <!-- 有子菜单 -->
          <el-sub-menu
            v-if="route.children && route.children.length > 0"
            :index="route.path"
          >
            <template #title>
              <el-icon><component :is="route.meta.icon" /></el-icon>
              <span>{{ route.meta.title }}</span>
            </template>
            <el-menu-item
              v-for="child in route.children"
              :key="child.path"
              :index="route.path + '/' + child.path"
            >
              <el-icon><component :is="child.meta.icon" /></el-icon>
              <span>{{ child.meta.title }}</span>
            </el-menu-item>
          </el-sub-menu>

          <!-- 无子菜单 -->
          <el-menu-item v-else :index="route.path">
            <el-icon><component :is="route.meta.icon" /></el-icon>
            <span>{{ route.meta.title }}</span>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>

    <!-- 主内容区 -->
    <el-container>
      <!-- 顶部导航栏 -->
      <el-header class="header">
        <div class="header-left">
          <el-icon class="collapse-icon" @click="toggleCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>

          <el-breadcrumb separator="/">
            <el-breadcrumb-item
              v-for="(item, index) in breadcrumbs"
              :key="index"
              :to="index === breadcrumbs.length - 1 ? '' : item.path"
            >
              {{ item.meta.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="header-right">
          <!-- 通知 -->
          <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="notification-badge">
            <el-icon class="header-icon" @click="showNotifications = true">
              <Bell />
            </el-icon>
          </el-badge>

          <!-- 用户信息 -->
          <el-dropdown @command="handleCommand">
            <div class="user-info">
              <el-avatar :src="userInfo.avatar">
                <el-icon><User /></el-icon>
              </el-avatar>
              <span class="username">{{ userInfo.username || '用户' }}</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>
                  个人资料
                </el-dropdown-item>
                <el-dropdown-item command="settings">
                  <el-icon><Setting /></el-icon>
                  账户设置
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 主要内容 -->
      <el-main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <keep-alive :include="cachedViews">
              <component :is="Component" :key="$route.fullPath" />
            </keep-alive>
          </transition>
        </router-view>
      </el-main>
    </el-container>

    <!-- 通知抽屉 -->
    <el-drawer
      v-model="showNotifications"
      title="通知消息"
      direction="rtl"
      size="400px"
    >
      <NotificationList @close="showNotifications = false" />
    </el-drawer>
  </el-container>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { ElMessageBox, ElMessage } from 'element-plus'
import {
  Fold,
  Expand,
  Bell,
  User,
  Setting,
  SwitchButton,
  Odometer,
  TrendCharts,
  Calendar,
  Medicine,
  ChatDotSquare,
  Tools
} from '@element-plus/icons-vue'
import NotificationList from '@/components/common/NotificationList.vue'

const route = useRoute()
const router = useRouter()
const store = useStore()

const isCollapse = ref(false)
const showNotifications = ref(false)
const cachedViews = ref(['Dashboard', 'HealthData'])

// 用户信息
const userInfo = computed(() => store.getters.userInfo || {})
const unreadCount = computed(() => store.getters.unreadCount || 0)

// 当前激活的菜单
const activeMenu = computed(() => route.path)

// 面包屑
const breadcrumbs = computed(() => {
  const matched = route.matched.filter(item => item.meta && item.meta.title)
  return matched
})

// 菜单路由配置
const menuRoutes = computed(() => {
  const routes = [
    {
      path: '/dashboard',
      meta: { title: '仪表板', icon: 'Odometer' }
    },
    {
      path: '/health',
      meta: { title: '健康管理', icon: 'TrendCharts' },
      children: [
        {
          path: 'data',
          meta: { title: '健康数据', icon: 'DataLine' }
        },
        {
          path: 'record',
          meta: { title: '健康档案', icon: 'Document' }
        },
        {
          path: 'report',
          meta: { title: '健康报告', icon: 'DocumentCopy' }
        }
      ]
    },
    {
      path: '/plan',
      meta: { title: '健康计划', icon: 'Calendar' },
      children: [
        {
          path: 'health',
          meta: { title: '综合计划', icon: 'Calendar' }
        },
        {
          path: 'exercise',
          meta: { title: '运动计划', icon: 'Trophy' }
        },
        {
          path: 'diet',
          meta: { title: '饮食计划', icon: 'Food' }
        }
      ]
    },
    {
      path: '/medication',
      meta: { title: '用药管理', icon: 'Medicine' },
      children: [
        {
          path: 'list',
          meta: { title: '药品列表', icon: 'List' }
        },
        {
          path: 'plan',
          meta: { title: '用药计划', icon: 'Clock' }
        },
        {
          path: 'reminder',
          meta: { title: '用药提醒', icon: 'Bell' }
        }
      ]
    },
    {
      path: '/consultation',
      meta: { title: 'AI 咨询', icon: 'ChatDotSquare' }
    }
  ]

  // 管理员菜单
  if (userInfo.value.role === 'admin') {
    routes.push({
      path: '/admin',
      meta: { title: '系统管理', icon: 'Tools' },
      children: [
        {
          path: 'dashboard',
          meta: { title: '管理仪表板', icon: 'Odometer' }
        },
        {
          path: 'users',
          meta: { title: '用户管理', icon: 'UserFilled' }
        },
        {
          path: 'statistics',
          meta: { title: '数据统计', icon: 'DataAnalysis' }
        }
      ]
    })
  }

  return routes
})

// 切换侧边栏
const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

// 下拉菜单命令
const handleCommand = async (command) => {
  switch (command) {
    case 'profile':
      router.push('/user/profile')
      break
    case 'settings':
      router.push('/user/settings')
      break
    case 'logout':
      await handleLogout()
      break
  }
}

// 退出登录
const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await store.dispatch('user/logout')
    ElMessage.success('已退出登录')
    router.push('/login')
  } catch (error) {
    // 取消操作
  }
}

// 监听路由变化，更新页面标题
watch(
  () => route.meta.title,
  (title) => {
    if (title) {
      document.title = `${title} - 健康管理平台`
    }
  },
  { immediate: true }
)
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

/* 侧边栏 */
.sidebar {
  background-color: #304156;
  transition: width 0.3s;
  overflow-x: hidden;
}

.logo-container {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 60px;
  background-color: #2b3a4a;
  color: white;
  font-size: 18px;
  font-weight: bold;
  gap: 10px;
}

.logo {
  width: 32px;
  height: 32px;
}

.logo-text {
  white-space: nowrap;
}

.sidebar-menu {
  border: none;
  background-color: #304156;
}

.sidebar-menu:not(.el-menu--collapse) {
  width: 200px;
}

:deep(.el-menu-item),
:deep(.el-sub-menu__title) {
  color: #bfcbd9;
}

:deep(.el-menu-item:hover),
:deep(.el-sub-menu__title:hover) {
  background-color: #263445 !important;
  color: #ffffff;
}

:deep(.el-menu-item.is-active) {
  background-color: #409eff !important;
  color: #ffffff;
}

/* 顶部导航栏 */
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #ffffff;
  border-bottom: 1px solid #e4e7ed;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.collapse-icon {
  font-size: 20px;
  cursor: pointer;
  color: #606266;
  transition: color 0.3s;
}

.collapse-icon:hover {
  color: #409eff;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.notification-badge {
  cursor: pointer;
}

.header-icon {
  font-size: 20px;
  color: #606266;
  cursor: pointer;
  transition: color 0.3s;
}

.header-icon:hover {
  color: #409eff;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: #f5f7fa;
}

.username {
  font-size: 14px;
  color: #606266;
}

/* 主要内容 */
.main-content {
  background-color: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
}

/* 过渡动画 */
.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.3s;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-30px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(30px);
}
</style>