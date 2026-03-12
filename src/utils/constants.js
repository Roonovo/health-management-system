/**
 * 常量定义
 */

// 健康数据类型
export const HEALTH_DATA_TYPES = {
  BLOOD_PRESSURE: 'blood_pressure',
  BLOOD_SUGAR: 'blood_sugar',
  WEIGHT: 'weight',
  HEART_RATE: 'heart_rate'
}

// 用餐类型
export const MEAL_TYPES = {
  BEFORE_BREAKFAST: '早餐前',
  AFTER_BREAKFAST: '早餐后',
  BEFORE_LUNCH: '午餐前',
  AFTER_LUNCH: '午餐后',
  BEFORE_DINNER: '晚餐前',
  AFTER_DINNER: '晚餐后',
  BEFORE_SLEEP: '睡前'
}

// 用药频率
export const MEDICATION_FREQUENCY = {
  ONCE_DAILY: '每日一次',
  TWICE_DAILY: '每日两次',
  THREE_TIMES_DAILY: '每日三次',
  AS_NEEDED: '按需服用'
}

// 文件类型
export const FILE_TYPES = {
  IMAGE: ['jpg', 'jpeg', 'png', 'gif'],
  PDF: ['pdf'],
  DOCUMENT: ['doc', 'docx', 'xls', 'xlsx']
}

// 最大文件大小（MB）
export const MAX_FILE_SIZE = 10

// 日期格式
export const DATE_FORMATS = {
  DATE: 'YYYY-MM-DD',
  TIME: 'HH:mm:ss',
  DATETIME: 'YYYY-MM-DD HH:mm:ss',
  MONTH: 'YYYY-MM'
}

// 性别
export const GENDER = {
  MALE: '男',
  FEMALE: '女',
  OTHER: '其他'
}

// 血压等级
export const BP_LEVELS = {
  NORMAL: { label: '正常', color: 'success', max: 120 },
  ELEVATED: { label: '偏高', color: 'warning', max: 139 },
  HIGH: { label: '高血压', color: 'danger', max: 180 }
}

// 血糖等级
export const BS_LEVELS = {
  LOW: { label: '偏低', color: 'warning', max: 3.9 },
  NORMAL: { label: '正常', color: 'success', max: 6.1 },
  HIGH: { label: '偏高', color: 'danger', max: 20 }
}