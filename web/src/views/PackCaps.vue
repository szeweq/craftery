<template>
<v-container>
    <v-row class="justify-space-between pa-3">
        <h1>Find modpack capabilities</h1>
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
import PackSelector from '../components/PackSelector'
import FieldList from '../components/FieldList'

export default {
    components: {PackSelector, Scanner, FieldList},
    data: () => ({
        step: 1,
        pack: null,
        results: []
    }),
    methods: {
        reset() {
            if (this.step === 3) this.step = 1
        },
        async scan($sc) {
            let results = []
            const lf = this.pack.LatestFiles.reduce((p, c) => new Date(p.FileDate) >= new Date(c.FileDate) ? p : c)
            $sc.text = `Getting manifest from ${lf.downloadUrl}...`
            let man = await this.$ws.call("zipManifest", lf.downloadUrl)
            $sc.total = man.files.length
            for (let cm of man.files) {
                try {
                    let uri = await this.$ws.call("fileURI", {addon: cm.projectID, file: cm.fileID})
                    $sc.text = `Scanning mod ${uri}...`
                    let d = await this.$ws.call("scanFields", {uri, access: 0x8, substr: "/Capability;"})
                    for (let x of d) {
                        const [path, field, type] = x
                        results.push({mod: uri, path, field, type})
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
            if (this.pack) {
                this.step = 2
                await this.$refs.sc.startScan(this.scan)
                this.step = 3
            }
        }
    }
}
</script>