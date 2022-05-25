<script setup>
import {reactive} from "vue";

const data = reactive({
	searchText: '',
	videos: [
		{
			id: '1',
			imgUrl: '/src/assets/d.jpg',
			text: '供大家参考,具体内容如下 实现功能:使用Java获取mp4、mov、avi等视频文件中的图像帧',
			like: 4582,
			user: {
				nickname: '埃菲尔',
				avatarUrl: '/src/assets/c.jpg'
			}
		},
		{
			id: '2',
			imgUrl: '/src/assets/d.jpg',
			text: '供大家参考,具体内容如下 实现功能:使用Java获取mp4、mov、avi等视频文件中的图像帧',
			like: 4582,
			user: {
				nickname: '埃菲尔',
				avatarUrl: '/src/assets/c.jpg'
			}
		},
		{
			id: '3',
			imgUrl: '/src/assets/d.jpg',
			text: '供大家参考,具体内容如下 实现功能:使用Java获取mp4、mov、avi等视频文件中的图像帧',
			like: 4582,
			user: {
				nickname: '埃菲尔',
				avatarUrl: '/src/assets/c.jpg'
			}
		},
		{
			id: '4',
			imgUrl: '/src/assets/d.jpg',
			text: '供大家参考,具体内容如下 实现功能:使用Java获取mp4、mov、avi等视频文件中的图像帧',
			like: 4582,
			user: {
				nickname: '埃菲尔',
				avatarUrl: '/src/assets/c.jpg'
			}
		}
		,
		{
			id: '5',
			imgUrl: '/src/assets/d.jpg',
			text: '供大家参考,具体内容如下 实现功能:使用Java获取mp4、mov、avi等视频文件中的图像帧',
			like: 4582,
			user: {
				nickname: '埃菲尔',
				avatarUrl: '/src/assets/c.jpg'
			}
		}
		,
		{
			id: '6',
			imgUrl: '/src/assets/d.jpg',
			text: '供大家参考,具体内容如下 实现功能:使用Java获取mp4、mov、avi等视频文件中的图像帧',
			like: 4582,
			user: {
				nickname: '埃菲尔',
				avatarUrl: '/src/assets/c.jpg'
			}
		}
	]
})
const onClickButton = () => {

}
const onSearch = () => {

}
</script>

<template>
	<div class="mo-main">
		<van-search
				v-model="data.searchText"
				show-action
				shape="round"
				placeholder="请输入搜索关键词"
				@search="onSearch"
		>

			<template #action>
				<div @click="onClickButton">搜索</div>
			</template>
		</van-search>

		<div class="mo-videos">
			<div class="mo-video mo-border-radius" v-for="video in data.videos">
				<img :src="video.imgUrl">
				<div class="mo-bottom">
					<van-image
							round
							width="2rem"
							height="2rem"
							:src="video.user.avatarUrl"
					/>
					<span>{{ video.user.nickname }}</span>
					<span class="mo-right">
						<van-icon name="like"/>
						<span>{{ video.like }}</span>
					</span>
				</div>
			</div>
		</div>
	</div>
</template>

<style scoped>
.mo-main {
	overflow: hidden;
}

.mo-videos {
	padding: 10px 5px 60px;
}

.mo-videos .mo-video:nth-child(2n-1) {
	margin-right: 10px;
}

.mo-videos .mo-video img {
	max-width: 100%;
}

.mo-videos .mo-video {
	width: calc((100% - 10px) / 2);
	display: inline-block;
	margin-bottom: 10px;
}

.mo-bottom {
	display: flex;
	align-items: center;
}
.mo-bottom .mo-right {
	float: right;
}
</style>
