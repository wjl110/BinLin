//logs.js
const util = require('../../utils/util.js')
const app = getApp()
Page({
  data: {
    headimg:`${app.staticUrl}images/headimg.png`,
    logs: [],
    name:app.userInfo.name,
    gender:app.userInfo.gender,
    birthday:app.userInfo.birthday,
    state:"登录",
    btnType:"primary",
    beautiful:[
        {
          imgsrc:`${app.staticUrl}images/head1.jpg`,
          name:"Youlanda Renee",
          time:"2.31 M Subjdslk"
        },{
          imgsrc:`${app.staticUrl}images/head2.jpg`,
          name:"Youlanda Renee",
          time:"2.31 M Subjdslk"
        },{
          imgsrc:`${app.staticUrl}images/head3.jpg`,
          name:"Youlanda Renee",
          time:"2.31 M Subjdslk"
        },{
          imgsrc:`${app.staticUrl}images/head4.jpg`,
          name:"Youlanda Renee",
          time:"2.31 M Subjdslk"
        }
      ],
    islamic:[
      {
        imgsrc:`${app.staticUrl}images/head1.jpg`,
        name:"Youlanda Renee",
        time:"2.31 M Subjdslk"
      },{
        imgsrc:`${app.staticUrl}images/head2.jpg`,
        name:"Youlanda Renee",
        time:"2.31 M Subjdslk"
      },{
        imgsrc:`${app.staticUrl}images/head3.jpg`,
        name:"Youlanda Renee",
        time:"2.31 M Subjdslk"
      },{
        imgsrc:`${app.staticUrl}images/head4.jpg`,
        name:"Youlanda Renee",
        time:"2.31 M Subjdslk"
      }
    ],
    hots:[
        {
            word:"Google"
        },{
            word:"Taylor Swift"
        },{
            word:"Taylor Swift"
        },{
            word:"Taylor Swift"
        },{
            word:"Taylor Swift"
        },{
            word:"Taylor Swift"
        },{
            word:"Taylor Swift"
        }
    ]
  },
  login(){
      if(this.data.state == "登录"){
        wx.navigateTo({
            url: '/pages/userLogin/login'
        })
        this.setData({
            state:"退出",
            btnType:"default"
        })
      }else{
        this.setData({
            state:"登录",
            btnType:"primary"
        })
      }
  },
  chooseHead:function(){
    console.log("ad")
    wx.chooseImage({
      count: 1,
      success(res){
        const tempFilePaths = res.tempFilePaths
        var userId = app.getGlobalUserInfo().id;
        console.log(userId)
        wx.uploadFile({
          url: app.serverUrl+'user/uploadFace?userId=' + userId, //仅为示例，非真实的接口地址
          filePath: tempFilePaths[0],
          name: 'file',
          formData: {
            'user': 'test'
          },
          success (res){
            const data = res.data

            //do something
          }
        })
      }
    })
  },
  onLoad: function () {
    this.setData({
      logs: (wx.getStorageSync('logs') || []).map(log => {
        return util.formatTime(new Date(log))
      })
    })
  }
})
