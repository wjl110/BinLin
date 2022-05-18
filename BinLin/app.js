//app.js
App({
    globalData:
    {
        //跳转的真实路径
        realUrl:null,
        //跳转的参数
        realUrlParam: null,
        //跳转到视频发布者的id
        publisherId:null,
        cookie:""
    },
    //可能不在同一个网段 
    //https://www.lotcloudy.com/scetc-show-videos-mini-api-0.0.1-SNAPSHOT
    // https://www.lotcloudy.com/scetc-show-videos-mini-api-0.0.1-SNAPSHOT
    //http://localhost:8080/scetc-show-videos-mini-api/
      serverUrl: "http://192.168.1.2/",
    //   staticUrl: "https://mo-test.oss-cn-hangzhou.aliyuncs.com/binlin/",
    staticUrl: "../../",
    userInfo: {
        name:"无",
        gender:"无",
        birthday:"无"
    },
    //switchTab 不能携带参数，只好将参数作为全局变量来进行保存
    //内网ip的方式访问
    setGlobalUserInfo: function (user) {
        wx.setStorageSync("userInfo", user);
    },
    getGlobalUserInfo: function () {
        return wx.getStorageSync("userInfo");
    },
    saveUserInfo: function (saveUser) {
        wx.setStorageSync("saveUser", saveUser);
    },
    getSaveUserInfo: function () {
        return wx.getStorageSync("saveUser");
    },
    systemInfo: null
    })
