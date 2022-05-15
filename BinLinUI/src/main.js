import {createApp} from 'vue'
import App from './App.vue'
import router from './router/index.js'
import Vant from 'vant';
import 'vant/lib/index.css';

createApp(App)
.use(router)
.use(Vant)
.mount('#app')

