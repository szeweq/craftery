<template>
    <div>
        <v-text-field
            v-model="modSearch"
            label="Find mods..."
            :disabled="search"
            :loading="search"
            append-icon="mdi-magnify"
            @click:append="startSearch">
        </v-text-field>
        <v-list v-if="value.length">
            <v-list-item v-for="(m, i) in value" :key="i">
                <v-list-item-action>
                    <v-checkbox
                        v-model="m.selected"
                        color="primary"
                        @click.stop="toggleModSelect(i)"
                    ></v-checkbox>
                </v-list-item-action>
                <v-list-item-content>
                    <v-list-item-title v-text="m.name" />
                    <v-list-item-subtitle v-text="m.slug" />
                </v-list-item-content>
            </v-list-item>
        </v-list>
    </div>
</template>
<script>
export default {
    name: 'ModSelector',
    props: {
        value: Array
    },
    data: () => ({
        val: [],
        modSearch: "",
        search: false,
    }),
    computed: {},
    methods: {
        async startSearch() {
            if (this.search) return
            this.search = true
            this.mods = []
            try {
                let d = await this.$ws.call("findAddons", {name: this.modSearch, category: 6})
                this.val = d
                this.$emit('input', this.val)
            } catch (e) {
                console.log(e)
            }
            this.search = false
        },
        toggleModSelect(i) {
            let mod = this.val[i]
            mod.selected = !mod.selected
            this.$set(this.val, i, mod)
            this.$emit('input', this.val)
        }
    }
}
</script>