<template>
    <div>
        <v-text-field
            filled
            v-model="modSearch"
            label="Find mods..."
            :disabled="search"
            :loading="search"
            append-icon="mdi-magnify"
            @click:append="startSearch">
        </v-text-field>
        <v-list v-if="val.length">
            <v-list-item v-for="(m, i) in val" :key="i">
                <v-list-item-action>
                    <v-checkbox
                        v-model="m.selected"
                        color="primary"
                        @click.stop="toggleModSelect(i)"
                    ></v-checkbox>
                </v-list-item-action>
                <v-list-item-avatar tile size="48" class="mr-4">
                    <v-img :src="m.avatar" />
                </v-list-item-avatar>
                <v-list-item-content>
                    <v-list-item-title v-text="`${m.name} [${m.slug}]`" />
                    <v-list-item-subtitle v-text="m.summary" />
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
    methods: {
        async startSearch() {
            if (this.search) return
            this.search = true
            try {
                let d = await this.$ws.call("findAddons", {name: this.modSearch, category: 6})
                d.forEach(x => x.avatar = x.attachments.filter(at => at.isDefault)[0].thumbnailUrl)
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