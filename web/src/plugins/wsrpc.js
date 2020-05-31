import {Client} from 'rpc-websockets'

const ws = new Client('ws://localhost:3000/ws')
ws.on('open', () => {
    ws.call('test').then(x => console.log(x))
})

export default class WSRPCPlugin {
    static install(Vue) {
        Vue.prototype.$ws = ws
    }
}