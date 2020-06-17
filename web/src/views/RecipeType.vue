<template>
    <v-container>
        <v-card v-if="$route.params.type === 'minecraft:crafting'">
            <v-card-title>Crafting</v-card-title>
            <v-card-text>
                <MCItemInput />
            </v-card-text>
        </v-card>
        <v-card v-else-if="$route.params.type === 'minecraft:smelting'">
            <v-card-title>Smelting</v-card-title>
            <MCItemInput />
        </v-card>
        <h2 v-else>Selected type: {{$route.params.type}}</h2>
    </v-container>
</template>
<script>
import MCItemInput from "../components/MCItemInput";
export default {
    components: {MCItemInput},
    watch: {
        $route() {}
    }
}
</script>