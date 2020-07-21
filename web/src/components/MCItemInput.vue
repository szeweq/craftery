<template>
    <v-combobox v-model="v" :items="items" />
</template>

<script>
    export default {
        name: "MCItemInput",
        props: {value: String},
        data: () => ({v: "", items: []}),
        watch: {
            v(v) {
                this.$emit("input", v)
            }
        }
    }
</script>