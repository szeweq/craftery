<template>
    <div>
        <v-text-field
            v-model="text"
            label="Find modpacks..."
            :disabled="lock"
            :loading="lock"
            append-icon="mdi-magnify"
            @click:append="search">
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
    props: {value: Object},
    data: () => ({
        val: [],
        sel: -1,
        text: "",
        lock: false,
    }),
    watch: {
        sel() {
            this.$emit('input', this.sel === undefined || this.sel < 0 ? null : this.val[this.sel])
        }
    },
    methods: {
        async search() {
            if (this.lock) return
            this.lock = true
            this.sel = -1
            this.val = []
            try {
                let d = await this.$ws.call("findAddons", {name: this.text, category: 4471})
                this.val = d
                this.$emit('input', this.sel !== undefined ? null : this.val[this.sel])
            } catch (e) {
                console.log(e)
            }
            this.lock = false
        }
    }
}
</script>