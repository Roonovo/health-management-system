import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/dashboard'
  },
  
  // ==================== 认证路由 ====================
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue'),
    meta: { requiresAuth: false, title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/Register.vue'),
    meta: { requiresAuth: false, title: '注册' }
  },
  {
    path: '/forgot-password',
    name: 'ForgotPassword',
    component: () => import('@/views/auth/ForgotPassword.vue'),
    meta: { requiresAuth: false, title: '忘记密码' }
  },
  
  // ==================== 主应用布局 ====================
  {
    path: '/',
    component: () => import('@/views/Layout.vue'),
    meta: { requiresAuth: true },
    children: [
      // 仪表板
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/Index.vue'),
        meta: { title: '仪表板', icon: 'Odometer' }
      },
      
      // ==================== 健康管理 ====================
      {
        path: 'health',
        name: 'Health',
        meta: { title: '健康管理', icon: 'TrendCharts' },
        children: [
          {
            path: 'data',
            name: 'HealthData',
            component: () => import('@/views/health/HealthData.vue'),
            meta: { title: '健康数据', icon: 'DataLine' }
          },
          {
            path: 'record',
            name: 'HealthRecord',
            component: () => import('@/views/health/HealthRecord.vue'),
            meta: { title: '健康档案', icon: 'Document' }
          },
          {
            path: 'report',
            name: 'HealthReport',
            component: () => import('@/views/health/HealthReport.vue'),
            meta: { title: '健康报告', icon: 'DocumentCopy' }
          }
        ]
      },
      
      // ==================== 健康计划 ====================
      {
        path: 'plan',
        name: 'Plan',
        meta: { title: '健康计划', icon: 'Calendar' },
        children: [
          {
            path: 'health',
            name: 'HealthPlan',
            component: () => import('@/views/plan/HealthPlan.vue'),
            meta: { title: '综合健康计划', icon: 'Calendar' }
          },
          {
            path: 'exercise',
            name: 'ExercisePlan',
            component: () => import('@/views/plan/ExercisePlan.vue'),
            meta: { title: '运动计划', icon: 'MostlyCloudy' }
          },
          {
            path: 'diet',
            name: 'DietPlan',
            component: () => import('@/views/plan/DietPlan.vue'),
            meta: { title: '饮食计划', icon: 'Food' }
          }
        ]
      },
      
      // ==================== 用药管理 ====================
      {
        path: 'medication',
        name: 'Medication',
        meta: { title: '用药管理', icon: 'Medicine' },
        children: [
          {
            path: 'list',
            name: 'MedicationList',
            component: () => import('@/views/medication/MedicationList.vue'),
            meta: { title: '药品列表', icon: 'List' }
          },
          {
            path: 'plan',
            name: 'MedicationPlan',
            component: () => import('@/views/medication/MedicationPlan.vue'),
            meta: { title: '用药计划', icon: 'Clock' }
          },
          {
            path: 'reminder',
            name: 'MedicationReminder',
            component: () => import('@/views/medication/MedicationReminder.vue'),
            meta: { title: '用药提醒', icon: 'Bell' }
          }
        ]
      },
      
      // ==================== 健康咨询 ====================
      {
        path: 'consultation',
        name: 'Consultation',
        component: () => import('@/views/consultation/AIConsultation.vue'),
        meta: { title: 'AI 健康咨询', icon: 'ChatDotSquare' }
      },
      
      // ==================== 个人中心 ====================
      {
        path: 'user',
        name: 'User',
        meta: { title: '个人中心', icon: 'User' },
        children: [
          {
            path: 'profile',
            name: 'Profile',
            component: () => import('@/views/user/Profile.vue'),
            meta: { title: '个人资料', icon: 'User' }
          },
          {
            path: 'settings',
            name: 'Settings',
            component: () => import('@/views/user/Settings.vue'),
            meta: { title: '账户设置', icon: 'Setting' }
          },
          {
            path: 'security',
            name: 'Security',
            component: () => import('@/views/user/Security.vue'),
            meta: { title: '安全设置', icon: 'Lock' }
          },
          {
            path: 'devices',
            name: 'Devices',
            component: () => import('@/views/user/Devices.vue'),
            meta: { title: '设备管理', icon: 'Iphone' }
          },
          {
            path: 'notifications',
            name: 'Notifications',
            component: () => import('@/views/user/Notifications.vue'),
            meta: { title: '通知设置', icon: 'Bell' }
          }
        ]
      },
      
      // ==================== 管理员 ====================
      {
        path: 'admin',
        name: 'Admin',
        meta: { title: '系统管理', icon: 'Tools', roles: ['admin'] },
        children: [
          {
            path: 'dashboard',
            name: 'AdminDashboard',
            component: () => import('@/views/admin/Dashboard.vue'),
            meta: { title: '管理仪表板', icon: 'Odometer' }
          },
          {
            path: 'users',
            name: 'UserManagement',
            component: () => import('@/views/admin/UserManagement.vue'),
            meta: { title: '用户管理', icon: 'UserFilled' }
          },
          {
            path: 'statistics',
            name: 'DataStatistics',
            component: () => import('@/views/admin/DataStatistics.vue'),
            meta: { title: '数据统计', icon: 'DataAnalysis' }
          },
          {
            path: 'settings',
            name: 'SystemSettings',
            component: () => import('@/views/admin/SystemSettings.vue'),
            meta: { title: '系统设置', icon: 'Setting' }
          }
        ]
      },
      
      // ==================== 关于 ====================
      {
        path: 'about',
        name: 'About',
        component: () => import('@/views/common/About.vue'),
        meta: { title: '关于', icon: 'InfoFilled' }
      }
    ]
  },
  
  // ==================== 错误页面 ====================
  {
    path: '/403',
    name: 'Forbidden',
    component: () => import('@/views/common/Forbidden.vue'),
    meta: { requiresAuth: false, title: '无权限' }
  },
  {
    path: '/500',
    name: 'ServerError',
    component: () => import('@/views/common/ServerError.vue'),
    meta: { requiresAuth: false, title: '服务器错误' }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/common/NotFound.vue'),
    meta: { requiresAuth: false, title: '页面未找到' }
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 健康管理平台` : '健康管理平台'
  
  // 需要认证的路由
  if (to.meta.requiresAuth !== false) {
    if (!token) {
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
      return
    }
  }
  
  // 已登录用户访问登录页，重定向到首页
  if (token && to.path === '/login') {
    next('/dashboard')
    return
  }
  
  next()
})

export default router