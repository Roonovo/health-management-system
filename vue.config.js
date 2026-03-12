const { defineConfig } = require("@vue/cli-service");

module.exports = defineConfig({
  transpileDependencies: true, // 添加这一行
  
  devServer: {
    port: 8081,
    open: true, // 自动打开浏览器
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        pathRewrite: {
          '^/api': ''  // ⚠️ 这里必须是空字符串，不是 '/api'
        }
      }
    }
  },
  
  productionSourceMap: false,
  
  chainWebpack: config => {
    config.plugin('html').tap(args => {
      args[0].title = '智能健康管理平台'
      return args
    })
  },
  
  css: {
    loaderOptions: {
      scss: {
        additionalData: `@import "~@/assets/styles/variables.scss";`  // 添加 ~ 符号
      }
    }
  }
})