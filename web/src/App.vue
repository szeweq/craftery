<template>
  <v-app>
    <v-app-bar app dense flat dark>
      <v-toolbar-title><router-link to="/">MCTool</router-link></v-toolbar-title>
      <v-spacer></v-spacer>
      <v-tooltip v-if="!status" bottom>
        <template v-slot:activator="{ on, attrs }">
          <v-icon color="red" v-bind="attrs" v-on="on">mdi-alert-circle-outline</v-icon>
        </template>
        <span>Lost connection with backend</span>
      </v-tooltip>
      <v-menu bottom left offset-y :close-on-content-click="false">
        <template v-slot:activator="{on, attrs}">
          <v-btn icon v-bind="attrs" v-on="on" :disabled="!notif.length"><v-icon>mdi-bell-outline</v-icon></v-btn>
        </template>
        <v-alert v-for="(n, i) in notif" :key="i" v-text="n" />
      </v-menu>
    </v-app-bar>
    <v-main>
      <router-view></router-view>
    </v-main>
  </v-app>
</template>
<script>
export default {
  name: 'App',
  data: () => ({
    status: false,
    notif: []
  }),
  mounted() {
    this.$ws.on("error", () => {
      this.notif.push("WebSocket connection error")
    })
    this.$ws.on("close", () => {
      this.status = false
    })
    setTimeout(async () => {
      this.status = !!(await this.$ws.call("healthCheck"))
    }, 200)
  }
};
</script>
