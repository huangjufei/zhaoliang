layui.use(['layer', 'form', 'table', 'ztree', 'laydate', 'admin', 'ax'], function () {
    var layer = layui.layer;
    var form = layui.form;
    var table = layui.table;
    var $ZTree = layui.ztree;
    var $ax = layui.ax;
    var laydate = layui.laydate;
    var admin = layui.admin;
    var $ = layui.jquery;
    
    var DataFlowUser = {
        tableId: "table",   
        condition: {
        }
    };
    DataFlowUser.initColumn = function () {
        return [[
            {type: 'checkbox'},
            {field: 'id', hide: true, sort: true, title: '注册用户id'},
            {field: 'userId', hide: true, sort: true, title: '推广专员id'},
            {field: 'createTime', sort: true, title: '推广注册时间'},
            {field: 'phone', sort: true, title: '注册手机'},
            {field: 'channelName', sort: true, title: '注册渠道'},
            {field: 'registerIpv4', sort: true, title: '注册地址'},
            {field: 'ios', sort: true, title: '注册平台'},
            {field: 'recommendKey', sort: true, title: '推广key'},
            {field: 'userName', sort: true, title: '推广专员'},
            {field: 'company', sort: true, title: '公司'},
            {field: 'isConversion', sort: true, title: '是否转化'},
            {field: 'conversionTime', sort: true, title: '转化时间'},
        ]];
    };

    DataFlowUser.exportExcel = function () {
        var checkRows = table.checkStatus(DataFlowUser.tableId);
        if (checkRows.data.length === 0) {
            Feng.error("请选择要导出的数据");
        } else {
            table.exportFile(tableResult.config.id, checkRows.data, 'xls');
        }
    };


    var tableResult = table.render({
        elem: '#' + DataFlowUser.tableId,
        url: Feng.ctxPath + '/data/flow_user/failedConversionList?date=' + Feng.getUrlParam("date") + '&name=' + Feng.getUrlParam("name")
    	+ "&company=" + Feng.getUrlParam("company") + "&channel=" + Feng.getUrlParam("channel"),
        page: true,
        height: "full-158",
        cellMinWidth: 100,
        cols: DataFlowUser.initColumn()
    });

    // 导出excel
    $('#btnExp').click(function () {
        DataFlowUser.exportExcel();
    });
});
