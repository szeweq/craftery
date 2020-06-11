<template>
<v-container>
    <v-row class="justify-space-between pa-3">
        <h1>Find mod registries</h1>
        <v-btn :disabled="step < 3" @click="reset()">Start again</v-btn>
    </v-row>
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
                <Scanner ref="sc" />
            </v-stepper-content>
            <v-stepper-content step="3">
                <FieldList :values="results" />
            </v-stepper-content>
        </v-stepper-items>
    </v-stepper>
</v-container>
</template>
<script>
import Scanner from '../components/Scanner'
import ModSelector from '../components/ModSelector'
import FieldList from '../components/FieldList'

export default {
    components: {ModSelector, Scanner, FieldList},
    data: () => ({
        step: 1,
        mods: [],
        results: []
    }),
    computed: {
        anyModSelected() {
            return this.mods.find(x => x.selected) !== undefined
        }
    },
    methods: {
        reset() {
            if (this.step === 3) this.step = 1
        },
        async scan($sc) {
            let results = []
            const selm = this.mods.filter(x => x.selected)
            $sc.total = selm.length
            for (let cm of selm) {
                const lf = cm.LatestFiles.reduce((p, c) => new Date(p.FileDate) >= new Date(c.FileDate) ? p : c)
                $sc.text = lf.downloadUrl
                try {
                    let d = await this.$ws.call("scanFields", {uri: lf.downloadUrl, access: 0x8, substr: "ForgeRegistry;"})
                    for (let x of d) {
                        const [path, field, type] = x
                        results.push({mod: cm.name, path, field, type})
                    }
                    $sc.count++
                } catch (e) {
                    console.log(e)
                    break
                }
            }
            this.results = results
        },
        async gotoStep2() {
            if (this.anyModSelected) {
                this.step = 2
                await this.$refs.sc.startScan(this.scan)
                this.step = 3
            }
        }
    }
}
</script>