//logs.js
const util = require('../../utils/util.js')
const app = getApp();
Page({
    data: {
        logs: [],
        historyimg:`${app.staticUrl}images/history.png`,
        platform:[
            {
                imgsrc: `${app.staticUrl}images/tictok.png`,
                platname:`抖音`
            },
            {
                imgsrc: `${app.staticUrl}images/ks.png`,
                platname:`快手`
            },
            {
                imgsrc: `${app.staticUrl}images/huosan.png`,
                platname:`火山视频`
            },
            {
                imgsrc: `${app.staticUrl}images/weishi.png`,
                platname:`微视`
            },
            {
                imgsrc: `${app.staticUrl}images/redbook.png`,
                platname:`小红书`
            },
            {
                imgsrc: `${app.staticUrl}images/bilibili.png`,
                platname:`bilibili`
            }
        ],
        hots:["首架交付的C919首飞实验成功","台湾回归","疫情结束","央视新闻","新型KTV",],
        seachHistory:""
    },
    searchTo(e){
        var seach = e.detail.value.input1;
        wx.navigateTo({
            url: '/pages/videoBack/videoBack?seach='+seach
        })
    },
    del:function(i){
        console.log(i.currentTarget.dataset.operation)
        var del = i.currentTarget.dataset.operation;
        var that = this;
        console.log(del)
        wx.request({
            url: `${app.serverUrl}/search/history?text=${del}`,
            header:{
                "Cookie": app.cookie
            },
            method:"DELETE",
            success(res){
                that.getHistory();
            setData({
                seachHistory:res//未写完
            })
        }
    })
    // console.log(i.currentTarget.dataset.operation,);
    // console.log(i.target.dataset.operation)
    // var list = this.data.seachHistory;
    // // var that = this;
    // // 用splice方法，请问这里怎么给splice传参数 删除当前行的元素
    // let j = 0
    // list.forEach(o => {
    //     if (i.currentTarget.dataset.operation.num === o.num) {
    //         list.splice(j,1)
    //         console.log("test")
    //     }
    //     j++;
    // })
    // this.setData({
    //     seachHistory:list
    // })
    },
    onLoad: function () {
        this.setData({
            logs: (wx.getStorageSync('logs') || []).map(log => {
                return util.formatTime(new Date(log))
            })
        });
        this.getHistory();
        this.getHot();
    // wx.request({
    //   url:  `${app.staticUrl}search/history`,
    //   data:5,
    //   success(res){
    //       console.log(res)
    //       setData({
    //         hots:res.data
    //       })
    //   }
    // })
    },
    getHistory(){
        var that = this
        wx.request({
            url: `${app.serverUrl}search/history`,
            method: "GET",
            header: {
                "Cookie": app.cookie
            },
            success(res){
                console.log(res.data.data)
                that.setData({
                    seachHistory:res.data.data
                })
            }
        })
    },
    getHot(){
        var that = this;
        wx.request({
            url: `${app.serverUrl}search/hot/20`,
            method:"GET",
            success(res){
                console.log(res)
            that.setData({
                hots:res.data.data
            })
          }
        })
    }
})
