import { createStore } from 'vuex'
import user from './modules/user'
import health from './modules/health'
import medication from './modules/medication'
import notification from './modules/notification'

export default createStore({
  modules: {
    user,
    health,
    medication,
    notification
  },
  getters: {
    token: state => state.user.token,
    userInfo: state => state.user.userInfo,
    profile: state => state.user.profile
  }
})