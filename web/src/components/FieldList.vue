<template>
    <div>
        <template v-if="values.length > 0">
            <h2>Results</h2>
            <v-list>
                <v-list-item v-for="(r, i) in values" :key="i">
                    <v-list-item-content>
                        <v-list-item-title v-text="`${r.field} (with type ${r.type})`" />
                        <v-list-item-subtitle v-text="`On ${r.path}`" />
                        <v-list-item-subtitle v-text="`From ${r.mod}`" />
                    </v-list-item-content>
                </v-list-item>
            </v-list>
        </template>
        <h2 v-else>No results found</h2>
    </div>
</template>
<script>
export default {
    props: {values: Array}
}
</script>