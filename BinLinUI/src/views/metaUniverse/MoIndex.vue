<script setup>
import {reactive} from "vue";

const data = reactive({
	isLoading: true
})
const loaded = () => {
	data.isLoading = false
}
</script>

<template>
	<div class="mo-globe">
		<van-loading v-if="data.isLoading" type="spinner" size="60" color="#1989fa"/>
		<iframe v-show="!data.isLoading" src="/src/assets/github-globe-main/index.html" class="mo-full" @load="loaded"></iframe>
	</div>
</template>

<style scoped>
.mo-globe {
	display: flex;
	justify-content: center;
	align-items: center;
	justify-items: center;
}
.mo-full {
	width: 100%;
	height: 100%;
	border: none;
	display:block;
	overflow: hidden;
}
</style>
