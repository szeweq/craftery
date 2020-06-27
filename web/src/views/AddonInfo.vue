<template>
    <v-container>
        <v-card>
            <v-card-title>Addon info</v-card-title>
            <v-card-text>
                <v-select filled v-model="type" :items="selectTypes" label="Select type to find"></v-select>
                <v-text-field
                    filled
                    v-model="searchText"
                    label="Find mods..."
                    :disabled="search"
                    :loading="search"
                    append-icon="mdi-magnify"
                    @click:append="startSearch">
                </v-text-field>
                <v-list v-if="items.length">
                    <v-list-item v-for="m in items" :key="m.id" @click="() => selectID(m.id)">
                        <v-list-item-avatar tile size="64" class="mr-4">
                            <v-img :src="m.avatar" />
                        </v-list-item-avatar>
                        <v-list-item-content>
                            <v-list-item-title v-text="`${m.name} [${m.slug}]`" />
                            <v-list-item-subtitle v-text="m.summary" />
                            <v-list-item-subtitle v-text="`Downloads: ${m.downloadCount}`" />
                            <v-list-item-subtitle v-text="`Popularity: ${m.popularityScore} (ranked ${m.gamePopularityRank})`" />
                        </v-list-item-content>
                    </v-list-item>
                </v-list>
            </v-card-text>
        </v-card>
    </v-container>
</template>
<script>

const typevals = ["Mods", "Modpacks"].map((v, i) => ({text: v, value: i}))
const typecats = [6, 4471]

export default {
    data: () => ({
        search: false,
        searchText: "",
        selectTypes: typevals,
        type: 0,
        items: []
    }),
    methods: {
        async startSearch() {
            if (this.search) return
            this.search = true
            try {
                let d = await this.$ws.call("findAddons", {name: this.searchText, category: typecats[this.type]})
                d.forEach(x => {
                    const d = x.attachments.filter(at => at.isDefault)[0]
                    x.avatar = d && "thumbnailUrl" in d ? d.thumbnailUrl : ""
                })
                this.items = d
            } catch (e) {
                console.log(e)
            }
            this.search = false
        },
        select() {}
    }
}
</script>