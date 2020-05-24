<template>
  <v-app>
    <v-app-bar app dense flat dark>
      <v-toolbar-title><router-link to="/">MCTool</router-link></v-toolbar-title>
      <v-spacer></v-spacer>
    </v-app-bar>
    <v-content>
      <router-view></router-view>
    </v-content>
  </v-app>
</template>
<script>
export default {
  name: 'App',

  data: () => ({
    //
  }),
};
</script>
