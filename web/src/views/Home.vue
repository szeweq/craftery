<template>
  <v-container class="home">
    <v-row>
      <v-col>
        <v-card>
          <v-card-title class="headline">Tasks list</v-card-title>
          <v-card-subtitle>Click one to begin.</v-card-subtitle>
          <v-list nav>
            <v-list-item v-for="(t, i) in tasks" :key="i" :to="t.to">
              <v-list-item-content>
                <v-list-item-title v-text="t.name" />
              </v-list-item-content>
            </v-list-item>
          </v-list>
        </v-card>
      </v-col>
      <v-col>
        <v-card>
          <v-card-title class="headline">Recipes</v-card-title>
          <v-card-subtitle>Create a new recipe</v-card-subtitle>
          <v-list nav>
            <v-list-item v-for="(r, i) in recipes" :key="i" :to="`recipe/${r.type}`">
              <v-list-item-title v-text="r.name" />
            </v-list-item>
          </v-list>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>
<script>

export default {
  name: 'Home',
  data: () => ({
    tasks: [
      {name: "Find all mod capabilities", to: "/findmod/caps"},
      {name: "Find all mod registries", to: "/findmod/registries"},
      {name: "Find modpack capabilities", to: "/findpack/caps"},
      {name: "Find modpack registries", to: "/findpack/registries"}
    ],
    recipes: [
      {name: "Crafting", type: "minecraft:crafting"},
      {name: "Smelting", type: "minecraft:smelting"}
    ]
  })
}
</script>
