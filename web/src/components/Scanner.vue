<template>
    <div>
        <h2>Scanning...</h2>
        <v-progress-linear :value="100 * count / total" :indeterminate="total === 0" />
        <small v-if="total > 0">{{count}} of {{total}}</small>
        <p v-text="text" />
    </div>
</template>
<script>
export default {
    data: () => ({
        count: 0,
        total: 0,
        text: ""
    }),
    methods: {
        async startScan(func) {
            this.count = 0
            this.total = 0
            this.text = ""
            await func(this)
        }
    }
}
</script>