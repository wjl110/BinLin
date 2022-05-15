<script setup>
import {reactive} from "vue";

const data = reactive({
	videos: [
		{
			id: '1',
			videoUrl: '/src/assets/e.mp4',
			bgmUrl: '/src/assets/b.mp3',
			star: 5525,
			comment: 4525,
			like: 4582,
			forward: 4538,
			description: '供大家参考,具体内容如下 实现功能:使用Java获取mp4、mov、avi等视频文件中的图像帧,每秒获...',
			user: {
				nickname: '埃菲尔',
				avatarUrl: '/src/assets/c.jpg'
			},
		},
		{
			id: '1',
			videoUrl: '/src/assets/e.mp4',
			bgmUrl: '/src/assets/b.mp3',
			star: 5525,
			comment: 4525,
			like: 4582,
			forward: 4538,
			description: '供大家参考,具体内容如下 实现功能:使用Java获取mp4、mov、avi等视频文件中的图像帧,每秒获...',
			user: {
				nickname: '埃菲尔',
				avatarUrl: '/src/assets/c.jpg'
			},
		}
		,
		{
			id: '1',
			videoUrl: '/src/assets/a.mp4',
			bgmUrl: '/src/assets/b.mp3',
			star: 5525,
			comment: 4525,
			like: 4582,
			forward: 4538,
			description: '供大家参考,具体内容如下 实现功能:使用Java获取mp4、mov、avi等视频文件中的图像帧,每秒获...',
			user: {
				nickname: '埃菲尔',
				avatarUrl: '/src/assets/c.jpg'
			},
		}
	]
})
const video = reactive({
	id: '1',
	videoUrl: '/src/assets/a.mp4',
	bgmUrl: '/src/assets/b.mp3',
	star: 5525,
	comment: 4525,
	like: 4582,
	forward: 4538,
	description: '供大家参考,具体内容如下 实现功能:使用Java获取mp4、mov、avi等视频文件中的图像帧,每秒获...',
	user: {
		nickname: '埃菲尔',
		avatarUrl: '/src/assets/c.jpg'
	}})
</script>

<template>
	<van-swipe class="mo-main" lazy-render vertical :show-indicators="false" :loop="false">
		<van-swipe-item v-for="video in data.videos">
			<div class="mo-mark mo-full-screen"></div>

			<div class="mo-right">
				<div>
					<van-image
							round
							width="2rem"
							height="2rem"
							:src="video.user.avatarUrl"
					/>
				</div>
				<div class="mo-icon">
					<van-icon name="like"/>
					<span>{{ video.like }}</span>
				</div>
				<div class="mo-icon">
					<van-icon name="chat"/>
					<span>{{ video.comment }}</span>
				</div>
				<div class="mo-icon">
					<van-icon name="star"/>
					<span>{{ video.star }}</span>
				</div>
				<div class="mo-icon">
					<van-icon name="share"/>
					<span>{{ video.forward }}</span>
				</div>
			</div>
			<div>
				<video class="video mo-global-center" preload="metadata" autoplay="autoplay" loop="loop"
							 :src="video.videoUrl"></video>
			</div>
			<div class="mo-bottom">
				<h6 class="mo-single-line-omit">@{{ video.user.nickname }}</h6>
				<p class="mo-multi-line-omit">{{ video.description }}</p>
				<audio autoplay="autoplay" loop="loop" preload="metadata" :src="video.bgmUrl" controls></audio>
			</div>
		</van-swipe-item>
	</van-swipe>
</template>

<style scoped>
.mo-main {
	position: absolute;
	top: 0px;
	width: 100%;
	height: calc(100% - 50px);
}
.mo-mark {
	background-color: #000;
	z-index: -100;
}

.mo-bottom {
	position: absolute;
	bottom: 50px;
	color: #ccc;
	padding-left: 10px;
}

.mo-bottom {
	width: calc(100% - 80px);
}

.mo-bottom p {
	font-size: 14px;
	-webkit-line-clamp: 2;
}

.mo-bottom audio {
	width: 100%;
	height: 20px;
}

.mo-right {
	position: absolute;
	right: 2px;
	bottom: 20%;
	width: 60px;
}

.mo-right > div {
	display: flex;
	margin-bottom: 20px;
	flex-direction: column;
	justify-content: center;
	align-items: center;
	color: #fff;
}

.mo-right > .mo-icon > i {
	font-size: 28px;
	color: #fff;
	text-shadow: #333 0px 0px 1px
}

.video {
	z-index: -10;
	width: 100%;
	max-height: 135%;
	display: block;
}
</style>
