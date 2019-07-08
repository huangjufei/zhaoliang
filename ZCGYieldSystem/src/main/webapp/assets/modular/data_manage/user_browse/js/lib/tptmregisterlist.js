//全部订单列表
$(function () {

    //3、初始化其它
    var oOtherInit = new OtherInit();
    oOtherInit.Init();

    //1.初始化Table
    var oTable = new TableInit();
    oTable.Init();

    //2.初始化Button的点击事件
    var oButtonInit = new ButtonInit();
    oButtonInit.Init();

});

var TableInit = function () {
    var oTableInit = new Object();
    //初始化Table
    oTableInit.Init = function () {
        $('#tb_list').bootstrapTable({
            url: '/TPLoanMarketBBYD/GetTPTMRegisters',
            method: 'get',
            dataType: 'json',
            toolbar: '#toolbar',
            singleSelect: false,
            striped: true,
            cache: false,
            queryParams: oTableInit.queryParams,
            queryParamsType: "limit",
            sidePagination: "server",
            pagination: true,
            pageSize: 15,
            pageList: [15],
            showColumns: false,
            minimumCountColumns: 2,
            showRefresh: false,
            showExport: false, //是否显示导出
            showToggle: false,
            exportDataType: "basic", //basic', 'all', 'selected'.
            clickToSelect: true,
            showFooter: true,
            onLoadSuccess: function () {
                layer.msg("查询完成");
            }, formatNoMatches: function () {
                return "无匹配结果";
            }
        });
    };

    //得到查询的参数
    oTableInit.queryParams = function (params) {
        var temp = {
            pageSize: params.limit,     //页面大小
            pageNumber: params.offset,
            beginTime: $("#querydatestart").val(),
            endTime: $("#querydateend").val(),
            mobile: $.trim($("#txtmobile").val())
        };
        return temp;
    };

    return oTableInit;

};

var ButtonInit = function () {
    var oInit = new Object();

    oInit.Init = function () {

        //条件查询click事件注册
        $("#btn_query").click(function () {
            $("#tb_list").bootstrapTable('refresh');
        });

    }
    return oInit;
};

var OtherInit = function () {
    var otherInit = new Object();

    otherInit.Init = function () {


        laydate.render({
            elem: '#querydatestart', //指定元素
            type: 'date'
        });

        laydate.render({
            elem: '#querydateend', //指定元素
            type: 'date'
        });

        $('#querydatestart').val(getNowFormatDate(addDay(new Date(), -3)));
        $('#querydateend').val(getNowFormatDate(addDay(new Date(), 0)));
    }

    return otherInit;
};

