<template>
  <v-container class="home">
    <v-card>
      <v-card-title class="headline">Tasks list</v-card-title>
      <v-card-subtitle>Click one to begin.</v-card-subtitle>
      <v-list nav>
        <v-list-item v-for="(t, i) in tasks" :key="i" :to="t.to">
          <v-list-item-content>
            <v-list-item-title v-text="t.name"></v-list-item-title>
          </v-list-item-content>
        </v-list-item>
      </v-list>
    </v-card>
  </v-container>
</template>

<script>

export default {
  name: 'Home',
  data: () => ({
    tasks: [
      {name: "Find all mod capabilities", to: "/findcaps"}
    ]
  })
}
</script>
