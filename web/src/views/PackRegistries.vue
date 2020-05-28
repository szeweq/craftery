<template>
<v-container>
    <v-row class="justify-space-between pa-3">
        <h1>Find modpack registries</h1>
        <v-btn :disabled="step < 3" @click="reset()">Start again</v-btn>
    </v-row>
    <v-stepper v-model="step">
        <v-stepper-header>
            <v-stepper-step :complete="step > 1" step="1">Select modpack</v-stepper-step>
            <v-divider></v-divider>
            <v-stepper-step :complete="step > 2" step="2">Scanning</v-stepper-step>
            <v-divider></v-divider>
            <v-stepper-step step="3">Results</v-stepper-step>
        </v-stepper-header>
        <v-stepper-items>
            <v-stepper-content step="1">
                <PackSelector v-model="pack" />
                <v-btn :disabled="!pack" @click="gotoStep2()">Continue</v-btn>
            </v-stepper-content>
            <v-stepper-content step="2">
                <h2>Scanning</h2>
                <p v-text="scanText" />
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
import api from '../api'
import PackSelector from '../components/PackSelector'

export default {
    components: {PackSelector},
    data: () => ({
        scanText: "",
        step: 1,
        pack: null,
        results: []
    }),
    computed: {
        packSelected() {return this.pack != null || this.pack != undefined}
    },
    methods: {
        reset() {
            if (this.step == 3) this.step = 1
        },
        async gotoStep2() {
            if (!this.packSelected) return
            this.step = 2
            let results = []
            const pack = this.pack
            const lf = pack.LatestFiles.reduce((p, c) => new Date(p.FileDate) >= new Date(c.FileDate) ? p : c)
            this.scanText = `Getting manifest from ${lf.downloadUrl}...`
            let man = await api("scanpack/manifest", {method: "POST", body: lf.downloadUrl})
            for (let cm of man.files) {
                try {
                    let uri = await api("addonuri/" + cm.projectID + "/" + cm.fileID)
                    this.scanText = `Scanning mod ${uri}...`
                    let d = await api("scanmod/regs", {method: "POST", body: uri})
                    for (let x of d) {
                        const [path, field, type] = x
                        results.push({mod: uri, path, field, type})
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