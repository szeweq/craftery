import {Client} from 'rpc-websockets'

const ws = new Client('ws://localhost:3000/ws')

export default class WSRPCPlugin {
    static install(Vue) {
        Vue.prototype.$ws = ws
    }
}