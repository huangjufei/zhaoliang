//全局js 
(function () {

    //登出
    $(".j-loginout").on("click", function () {
        bootbox.confirm("确认要退出么?", function (result) {
            if (!result) return;
            $.ajax({
                url: "/authen/logout.html",
                type: "POST"
            }).done(function (ret) {
                window.top.location = "/authen/login.html";
            });
        });
    });

    //弹出修改密码框
    $(".j-modify-pwd").click(function () {
        parent.layer.open({
            type: 2,
            title: '密码重置',
            area: ['30%', '40%'],
            fixed: false, //不固定
            maxmin: false,
            content: '/AdminUser/ResetPwd',
            end: function () {

            }
        });
    });

    //初始化滑动框
    var elems = document.querySelectorAll('.js-switch');
    if (elems != undefined) {
        $(elems).each(function (index, element) {
            var init = new Switchery(element);
        });
    }


})();

//时间戳格式化/Date(1482510025253)/
function dateFormatter(timestamp) {

    var d = eval('new ' + timestamp.substr(1, timestamp.length - 2));
    return d.getFullYear() + '-' +
             dFormat(d.getMonth() + 1) + '-' +
             dFormat(d.getDate()) + ' ' +
             dFormat(d.getHours()) + ':' +
             dFormat(d.getMinutes()) + ':' +
             dFormat(d.getSeconds());

    function dFormat(i) {
        return i < 10 ? "0" + i.toString() : i;
    }
}

//弹出加载浮层
function showLoading(msg, time) {
    if (!msg)
        msg = "努力加载中...";
    if (!time)
        time = 0000;

    layer.msg(msg,
    {
        icon: 16,
        shade: [0.5, '#23262E'],
        scrollbar: true,
        offset: '50%',
        time: time
    });
}

//隐藏加载浮层
function hideLoading() {
    layer.closeAll();
}

//隐藏全部layer层
function hideLayer() {
    parent.layer.closeAll();
}

function layerConfirm(msg, callback) {

    layer.alert(msg, {
        title: "提示"
                    , skin: 'layui-layer-molv' //样式类名  自定义样式
                    , closeBtn: 1    // 是否显示关闭按钮
                    , anim: 1 //动画类型
                    , btn: ['确认', '取消'] //按钮
        // , icon: 6    // icon
                    , yes: function () {
                        callback(true);
                    }
                    , btn2: function () {
                        layer.closeAll();
                        callback(false);
                    }
    });
}

function layerParentConfirm(msg, callback) {

    parent.layer.alert(msg, {
        title: "提示"
                    , skin: 'layui-layer-molv' //样式类名  自定义样式
                    , closeBtn: 1    // 是否显示关闭按钮
                    , anim: 1 //动画类型
                    , btn: ['确认', '取消'] //按钮
        // , icon: 6    // icon
                    , yes: function () {
                        callback(true);
                    }
                    , btn2: function () {
                        layer.closeAll();
                        callback(false);
                    }
    });
}

//订单状态翻译
function orderStatusFormatter(orderStatusCode) {
    switch (orderStatusCode) {
        case 10:
            return "待支付";
            break;
        case 20:
            return "支付成功等待处理";
            break;
        case 30:
            return "等待发货";
            break;
        case 40:
            return "已发货";
            break;
        case 80:
            return "交易成功";
            break;
        case 90:
            return "交易未成功";
            break;
        case 100:
            return "申请退款";
            break;
        default:
            return "未知状态：" + orderStatusCode;
            break;
    }
}

// 获取当前时间，年月日时分秒
function getNowFormatDate() {
    var date = new Date();
    var seperator1 = "-";
    var seperator2 = ":";
    var month = date.getMonth() + 1;
    var strDate = date.getDate();
    if (month >= 1 && month <= 9) {
        month = "0" + month;
    }
    if (strDate >= 0 && strDate <= 9) {
        strDate = "0" + strDate;
    }
    var currentdate = date.getFullYear() + seperator1 + month + seperator1 + strDate
            + " " + date.getHours() + seperator2 + date.getMinutes()
            + seperator2 + date.getSeconds();
    return currentdate;
}

// 指定时间格式化，年月日
function getNowFormatDate(date) {

    var seperator1 = "-";
    var year = date.getFullYear();
    var month = date.getMonth() + 1;
    var strDate = date.getDate();
    if (month >= 1 && month <= 9) {
        month = "0" + month;
    }
    if (strDate >= 0 && strDate <= 9) {
        strDate = "0" + strDate;
    }
    var currentdate = year + seperator1 + month + seperator1 + strDate;
    return currentdate;
}

// 指定时间加上指定天
function addDay(date, dayNumber) {
    date = date ? date : new Date();
    var ms = dayNumber * (1000 * 60 * 60 * 24);
    var newDate = new Date(date.getTime() + ms);
    return newDate;
}

//线程暂停
//function sleep(ms) {
//    return new Promise(resolve => setTimeout(resolve, ms));
//}



//显示用户点击产品列表
function showTrackerActionRecordPage(cid, source,nickname) {
    source = $.trim(source);
    parent.layer.open({
       
    });
}

/*
* @@to.js
* */
$(function () {
    $(".roll-left").on('click', function () {
        $("body").addClass('mini-navbar');
    });
    $(".nav>li:eq(1) a").click(function (e) {
        $("body").removeClass('mini-navbar');
        $(this).parent().addClass('active');
        $(this).parent().siblings().removeClass('active');
        $(".nav-second-level").removeClass('in');
        e.preventDefault();
    });
    $("body").attr('data-pato', '1');
    $(".nav>li").click(function () {



        var attrpato = $("body").attr('data-pato');
        $("body").removeClass('mini-navbar');
        $(this).parent().addClass('active');
        $(this).parent().siblings().removeClass('active');
        $(".nav-second-level").removeClass('in');
        if (attrpato == '1') {
            console.log('abc11');
            $(this).children('ul').addClass('in');
            $("body").attr('data-pato', '2');
            return;
        }
        if (attrpato == '2') {
            console.log('abc1122');
            $(this).children('ul').removeClass('in');
            $("body").attr('data-pato', '1');
            return;
        }

    });
    $(".roll-right").on('click', function () {
        $("body").removeClass('mini-navbar');
    })
});