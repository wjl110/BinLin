// pages/home/home.js
const app = getApp()
Page({

    /**
     * 页面的初始数据
     */
    data: {
        light1:"hightLingh",
        light2:"",
        light3:"",
        likeSrc:`${app.staticUrl}images/like0.png`,
        isLike:0,
        like:"3.2M",
        platforms:[
            {
                imgsrc:`${app.staticUrl}images/123/tictok.png`
            },{
                imgsrc:`${app.staticUrl}images/123/ks.png`
            },{
                imgsrc:`${app.staticUrl}images/123/huosan.png`
            },{
                imgsrc:`${app.staticUrl}images/123/bilibili.png`
            }
        ],
        video:[
            {
                id:'d0',
                src:`https://v26-web.douyinvod.com/31c5e8c8c469c57bf0aeb519dcfb1f36/6280e82a/video/tos/cn/tos-cn-ve-15c001-alinc2/c0a42597dabb493db80c335c21aecfa7/?a=6383&ch=0&cr=0&dr=0&cd=0%7C0%7C0%7C0&cv=1&br=2517&bt=2517&cs=0&ds=3&ft=t2zLrtjjM990xuyq8ZmCTeK_ScoApZsuB4vrKkBbKqmo0&mime_type=video_mp4&qs=0&rc=OzlpNjQ5NTtnPDdnZDVoaUBpM3kzeGY6ZjU7PDMzNGkzM0BfYDBjM18wNS4xNTIyNV5hYSNjMGxzcjRfaDBgLS1kLS9zcw%3D%3D`,
                like:"132M",
                comment:"15M",
                share:"12M",
                auto:true,
                headSrc:`${app.staticUrl}images/head1.png`,
                name:"张三",
                tip:"吃"
            },{
                id:'d1',
                src:`https://vd3.bdstatic.com/mda-jktk8xtrx02da1zb/sc/mda-jktk8xtrx02da1zb.mp4?v_from_s=hkapp-haokan-nanjing&auth_key=1652366202-0-0-df952edb33eee73ac726dc0f5cd5681d&bcevod_channel=searchbox_feed&pd=1&cd=0&pt=3&logid=0402552852&vid=10517210344677328081&abtest=101830_1-102133_2-17451_2&klogid=0402552852`,
                like:"132M",
                comment:"15M",
                share:"12M",
                auto:false,
                headSrc:`${app.staticUrl}images/head2.png`,
                name:"张三",
                tip:"吃"
            },{
                id:'d2',
                src:`https://vd2.bdstatic.com/mda-nd049649irvpr06s/sc/cae_h264_delogo/1648783726295307438/mda-nd049649irvpr06s.mp4?v_from_s=hkapp-haokan-nanjing&auth_key=1652603093-0-0-aff5cab47b0f35720c62ba6fa4798869&bcevod_channel=searchbox_feed&pd=1&cd=0&pt=3&logid=3293140792&vid=8752305521299965586&abtest=101830_1-102133_2-17451_2&klogid=3293140792`,
                like:"132M",
                comment:"15M",
                share:"12M",
                auto:false,
                headSrc:`${app.staticUrl}images/head3.png`,
                name:"张三",
                tip:"吃"
            },{
                id:'d3',
                src:`https://v26-web.douyinvod.com/1118c3169d3970a41bddf299c96c9dca/627fb376/video/tos/cn/tos-cn-ve-15c001-alinc2/9f53c18b49a942e6934f57b849d5446e/?a=6383&ch=0&cr=0&dr=0&cd=0%7C0%7C0%7C0&cv=1&br=3005&bt=3005&cs=0&ds=6&ft=t2zLrtjjM990xuyq8ZmCTeK_ScoApmSDc4vrK_BbKqmo0&mime_type=video_mp4&qs=0&rc=ZTw8aDZpOjg7aTYzPDM4ZEBpMzV5eWU6ZnJtPDMzNGkzM0A2NF9jLi8tXzExY2EuYWI2YSNgNm8vcjRvLnNgLS1kLS9zcw%3D%3D`,
                like:"132M",
                comment:"15M",
                share:"12M",
                auto:false,
                headSrc:`${app.staticUrl}images/head4.png`,
                name:"张三",
                tip:"吃"
            },{
                id:'d4',
                src:`https://vd4.bdstatic.com/mda-jk4jqk26wmdkp6b7/sc/mda-jk4jqk26wmdkp6b7.mp4?v_from_s=hkapp-haokan-nanjing&auth_key=1652364702-0-0-107c2518dd3b0efe6f9086d611158a4a&bcevod_channel=searchbox_feed&pd=1&cd=0&pt=3&logid=2502321513&vid=2364275861562911792&abtest=101830_1-102133_2-17451_2&klogid=2502321513`,
                like:"132M",
                comment:"15M",
                share:"12M",
                auto:false,
                headSrc:`${app.staticUrl}images/headimg.png`,
                name:"张三",
                tip:"吃"
            },
        ]
    },
    //搜索
    toSeach:function(){
        wx.switchTab({
            url:"/pages/seach/seach"
        })
    },
    
    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
        
    },

    /**
     * 生命周期函数--监听页面初次渲染完成
     */
    onReady() {

    },

    /**
     * 生命周期函数--监听页面显示
     */
    onShow() {

    },

    /**
     * 生命周期函数--监听页面隐藏
     */
    onHide() {

    },
    like(){
        if(this.data.isLike == 0){
            this.setData({
                isLike:1,
                likeSrc:`${app.staticUrl}images/like1.png`
            })
        }else{
            this.setData({
                isLike:0,
                likeSrc:`${app.staticUrl}images/like0.png`
            })
        }
        
    },
    /**
     * 生命周期函数--监听页面卸载
     */
    onUnload() {

    },
    onChange(obj) {
        console.log(obj)
        var i = obj.detail.current
        console.log(this.data.video[i].id)
        var videoCtx2 = wx.createVideoContext(this.data.video[i].id, this)
        videoCtx2.play();
        for (var j = 0; j < this.data.video.length ; j++){
            if(j == i){
                continue
            }
            let videoCtx = wx.createVideoContext(this.data.video[j].id, this);
            videoCtx.pause();
        }
        // let videoCtx = wx.createVideoContext(this.data.video[i-1].id, this)
        // videoCtx.pause();
    },

    /**
     * 页面相关事件处理函数--监听用户下拉动作
     */
    onPullDownRefresh() {

    },

    /**
     * 页面上拉触底事件的处理函数
     */
    onReachBottom() {
        console.log('123')
    },

    /**
     * 用户点击右上角分享
     */
    onShareAppMessage() {

    }
})