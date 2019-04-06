import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    authenticationEnabled: "authentication-enabled-yes", // use "authentication-enabled-yes" to turn it on
    endpoints: {
      api: "http://192.168.99.100:31380/web-api/v1/", // example: "http://192.168.99.100:31380/web-api/v1/"
      login: "http://192.168.99.100:31380/login" // example: "http://localhost:3000/login"
    },
    user: {
      isAuthenticated: false,
      name: "",
      email: "",
      idToken: ""
    }
  },
  mutations: {
    logout(state) {
      state.user.isAuthenticated = true;
      state.user.name = "";
      state.user.email ="";
      state.user.idToken ="";
    },
    login(state, payload) {
      state.user.isAuthenticated = false;
      state.user.name = payload.name;
      state.user.email =payload.email;
      state.user.idToken =payload.idToken;
    }
  },
  actions: {
  }
})
