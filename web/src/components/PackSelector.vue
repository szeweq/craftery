<template>
    <div>
        <v-text-field
            v-model="packSearch"
            label="Find mods..."
            :disabled="search"
            :loading="search"
            append-icon="mdi-magnify"
            @click:append="startSearch">
        </v-text-field>
        <v-list v-if="val.length">
            <v-list-item-group v-model="sel">
                <v-list-item v-for="(m, i) in val" :key="i">
                    <v-list-item-content>
                        <v-list-item-title v-text="m.name" />
                        <v-list-item-subtitle v-text="m.slug" />
                    </v-list-item-content>
                </v-list-item>
            </v-list-item-group>
        </v-list>
    </div>
</template>
<script>
export default {
    name: 'PackSelector',
    props: {
        value: Object
    },
    data: () => ({
        val: [],
        sel: -1,
        packSearch: "",
        search: false,
    }),
    watch: {
        sel() {
            const item = this.sel === undefined || this.sel < 0 ? null : this.val[this.sel]
            this.$emit('input', item)
        }
    },
    methods: {
        async startSearch() {
            if (this.search) return
            this.search = true
            this.sel = -1
            this.val = []
            try {
                let d = await fetch("//localhost:3000/api/findpacks/" + this.packSearch).then(r => r.json())
                this.val = d
                this.$emit('input', this.sel !== undefined ? null : this.val[this.sel])
            } catch (e) {
                console.log(e)
            }
            this.search = false
        }
    }
}
</script>