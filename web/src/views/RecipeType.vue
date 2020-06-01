<template>
    <h2>Selected type: {{$route.params.type}}</h2>
</template>
<script>
export default {
    watch: {
        $route() {}
    }
}
</script>