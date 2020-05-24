<template>
<v-container>
    <h1>Find mod capabilities</h1>
    <v-stepper v-model="step">
        <v-stepper-header>
            <v-stepper-step :complete="step > 1" step="1">Select mods</v-stepper-step>
            <v-divider></v-divider>
            <v-stepper-step :complete="step > 2" step="2">Scanning</v-stepper-step>
            <v-divider></v-divider>
            <v-stepper-step step="3">Results</v-stepper-step>
        </v-stepper-header>
        <v-stepper-items>
            <v-stepper-content step="1">
                <ModSelector v-model="mods" />
                <v-btn :disabled="!anyModSelected" @click="gotoStep2()">Continue</v-btn>
            </v-stepper-content>
            <v-stepper-content step="2">
                Scanning... {{scanText}}
            </v-stepper-content>
            <v-stepper-content step="3">
                <v-list>
                    <v-list-item v-for="(r, i) in results" :key="i">
                        <v-list-item-content>
                            <v-list-item-title v-text="`${r.field} (with type ${r.type})`" />
                            <v-list-item-subtitle v-text="`On ${r.path}`" />
                            <v-list-item-subtitle v-text="`From ${r.mod}`" />
                        </v-list-item-content>
                    </v-list-item>
                </v-list>
            </v-stepper-content>
        </v-stepper-items>
    </v-stepper>
</v-container>
</template>
<script>
import ModSelector from '../components/ModSelector'

export default {
    components: {ModSelector},
    data: () => ({
        scanText: "",
        step: 1,
        mods: [],
        results: []
    }),
    computed: {
        anyModSelected() {
            return this.mods.find(x => x.selected) != undefined
        }
    },
    methods: {
        async gotoStep2() {
            if (!this.anyModSelected) return
            this.step = 2
            let results = []
            const selm = this.mods.filter(x => x.selected)
            console.log(selm)
            for (let i = 0; i < selm.length; i++) {
                const cm = selm[i]
                const lf = cm.LatestFiles.reduce((p, c) => new Date(p.FileDate) >= new Date(c.FileDate) ? p : c)
                this.scanText = lf.downloadUrl
                try {
                    let d = await fetch("//localhost:3000/api/scanmod", {method: "POST", body: lf.downloadUrl}).then(r => r.json())
                    for (let x of d) {
                        const [path, field, type] = x
                        results.push({mod: cm.name, path, field, type})
                    }
                } catch (e) {
                    console.log(e)
                    break
                }
            }
            this.results = results
            this.step = 3
        }
    }
}
</script>