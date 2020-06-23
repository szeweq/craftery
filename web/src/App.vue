<template>
  <v-app>
    <v-app-bar app dense flat dark>
      <v-toolbar-title><router-link to="/">MCTool</router-link></v-toolbar-title>
      <v-spacer></v-spacer>
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
    //
  }),
};
</script>
