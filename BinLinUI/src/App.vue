<script setup>
import {reactive} from "vue";

</script>
<template>
	<router-view/>
	<van-tabbar routev>
		<van-tabbar-item replace to="/index" icon="home-o">首页</van-tabbar-item>
		<van-tabbar-item replace to="/search" icon="search">搜索</van-tabbar-item>
		<van-tabbar-item replace to="/publish" icon="add-o">发布</van-tabbar-item>
		<van-tabbar-item replace to="/metaUniverse" icon="guide-o">元宇宙</van-tabbar-item>
		<van-tabbar-item replace to="/mine" icon="user-circle-o">我的</van-tabbar-item>
	</van-tabbar>
</template>

<style>
body>canvas {
	display: none!important;
}
.mo-global-center {
	position: absolute;
	top: 50%;
	left: 50%;
	transform: translate(-50%, -50%);
}

.mo-full-screen {
	position: absolute;
	top: 0px;
	left: 0px;
	right: 0px;
	bottom: 0px;
}

.mo-multi-line-omit {
	display: -webkit-box;
	overflow: hidden;
	text-overflow: ellipsis;
	-webkit-box-orient: vertical;
	/*-webkit-line-clamp: 4;*/
}
.mo-single-line-omit {
	white-space: nowrap;
	overflow: hidden;
	text-overflow: ellipsis;
}
.mo-border-radius {
	border-radius: 4px;
	overflow: hidden;
}
</style>
