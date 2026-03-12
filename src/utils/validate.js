/**
 * 表单验证规则
 */

/**
 * 验证用户名
 */
export function validateUsername(rule, value, callback) {
  if (!value) {
    callback(new Error('请输入用户名'))
  } else if (value.length < 3 || value.length > 20) {
    callback(new Error('用户名长度为 3-20 个字符'))
  } else if (!/^[a-zA-Z0-9_]+$/.test(value)) {
    callback(new Error('用户名只能包含字母、数字和下划线'))
  } else {
    callback()
  }
}

/**
 * 验证密码
 */
export function validatePassword(rule, value, callback) {
  if (!value) {
    callback(new Error('请输入密码'))
  } else if (value.length < 6) {
    callback(new Error('密码长度不能少于 6 位'))
  } else {
    callback()
  }
}

/**
 * 验证邮箱
 */
export function validateEmail(rule, value, callback) {
  const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/
  if (!value) {
    callback(new Error('请输入邮箱'))
  } else if (!emailRegex.test(value)) {
    callback(new Error('请输入正确的邮箱格式'))
  } else {
    callback()
  }
}

/**
 * 验证手机号
 */
export function validatePhone(rule, value, callback) {
  const phoneRegex = /^1[3-9]\d{9}$/
  if (!value) {
    callback(new Error('请输入手机号'))
  } else if (!phoneRegex.test(value)) {
    callback(new Error('请输入正确的手机号'))
  } else {
    callback()
  }
}

/**
 * 验证验证码
 */
export function validateVerifyCode(rule, value, callback) {
  if (!value) {
    callback(new Error('请输入验证码'))
  } else if (!/^\d{6}$/.test(value)) {
    callback(new Error('验证码为 6 位数字'))
  } else {
    callback()
  }
}

/**
 * 验证血压值
 */
export function validateBloodPressure(rule, value, callback) {
  if (!value) {
    callback(new Error('请输入血压值'))
  } else if (isNaN(value) || value < 0 || value > 300) {
    callback(new Error('请输入有效的血压值 (0-300)'))
  } else {
    callback()
  }
}

/**
 * 验证血糖值
 */
export function validateBloodSugar(rule, value, callback) {
  if (!value) {
    callback(new Error('请输入血糖值'))
  } else if (isNaN(value) || value < 0 || value > 50) {
    callback(new Error('请输入有效的血糖值 (0-50)'))
  } else {
    callback()
  }
}

/**
 * 验证体重
 */
export function validateWeight(rule, value, callback) {
  if (!value) {
    callback(new Error('请输入体重'))
  } else if (isNaN(value) || value < 0 || value > 500) {
    callback(new Error('请输入有效的体重 (0-500kg)'))
  } else {
    callback()
  }
}