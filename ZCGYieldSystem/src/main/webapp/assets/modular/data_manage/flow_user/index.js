layui.use(['layer', 'form', 'table', 'ztree', 'laydate', 'admin', 'ax'], function () {
    var layer = layui.layer;
    var form = layui.form;
    var table = layui.table;
    var $ZTree = layui.ztree;
    var $ax = layui.ax;
    var laydate = layui.laydate;
    var admin = layui.admin;
    
    var DataFlowUser = {
        tableId: "table",   
        condition: {
            name: "",
            company: "",
            channel: "",
            timeLimit: "",
            showType: "",
            appName: "",
            os: ""
        }
    };
    DataFlowUser.initColumn = function () {
    	if($("#show_type").val() == "统计数据"){
    		return [[
                {type: 'checkbox'},
                {field: 'company', sort: true, title: '公司'},
                {field: 'userName', sort: true, title: '推广专员'},
                {field: 'appName', sort: true, title: 'app名称'},
                {field: 'channelName', sort: true, title: '渠道'},
                {field: 'date', sort: true, title: '日期' ,minwidth:100},
                {field: 'flowUserVisitCount', sort: true, title: 'H5独立访客数'},
                {field: 'flowUserCount', sort: true, title: 'H5注册数'},
                {field: 'appUserCount', sort: true, title: 'app登录数'},
                {field: 'dateConversionRate', sort: true, title: '登录转化率'},
                {field: 'appBrowseCount', sort: true, title: '产品点击数'},
                {field: 'appBrowseRate', sort: true, title: '点击率'},
                {align: 'center', toolbar: '#tableBar', title: '操作', minWidth: 400}
            ]];
    	}
        return [[
            {type: 'checkbox' },
            {field: 'id', hide: true, sort: true, title: '注册用户id'},
            {field: 'appId', hide: true, sort: true, title: 'appid'},
            {field: 'userId', hide: true, sort: true, title: '推广专员id'},
            {field: 'createTime', sort: true, title: '注册日期'},
            {field: 'phone', sort: true, title: '注册手机'},
            {field: 'appName', sort: true, title: '产品'},
            {field: 'channelName', sort: true, title: '注册渠道'},
            {field: 'registerIpv4', sort: true, title: '注册地址'},
            {field: 'os', sort: true, title: '注册平台'},
            {field: 'recommendKey', sort: true, title: '推广key'},
            {field: 'userName', sort: true, title: '推广专员'},
            {field: 'company', sort: true, title: '公司'},
        ]];
    };

    DataFlowUser.search = function () {
        var queryData = {};
        queryData['name'] = $("#name").val();
        queryData['timeLimit'] = $("#timeLimit").val();
        queryData['company'] = $("#company").val();
        queryData['channel'] = $("#channel").val();
        queryData['showType'] = $("#show_type").val();
        queryData['appName'] = $("#appName").val();
        queryData['os'] = $("#os").val();
        table.reload(DataFlowUser.tableId, {where: queryData});
    };

    DataFlowUser.exportExcel = function () {
        var checkRows = table.checkStatus(DataFlowUser.tableId);
        if (checkRows.data.length === 0) {
            Feng.error("请选择要导出的数据");
        } else {
            table.exportFile(tableResult.config.id, checkRows.data, 'xls');
        }
    };
    
    function refresh(){
    	layui.use('element', function() {
            var element = layui.element;
            element.init();
            form.render();
        });
    }

    var tableResult = table.render({
        elem: '#' + DataFlowUser.tableId,
        url: Feng.ctxPath + '/data/flow_user/list',
        page: true,
        height: "full-158",
        cellMinWidth: 100,
        totalRow: true,
        cols: DataFlowUser.initColumn()
    });

    //渲染时间选择框
    laydate.render({
        elem: '#timeLimit',
        range: true,
        format: 'yyyy-MM-dd HH:mm:ss'
    });

    // 搜索按钮点击事件
    $('#btnSearch').click(function () {
        DataFlowUser.search();
    });

    // 统计数据与列表数据切换
    $('#btnShow').click(function () {
    	var show_type_val = $('#show_type').val();
    	$('#btnShow').text(show_type_val);
    	show_type_val = show_type_val == "列表数据" ? "统计数据" : "列表数据";
    	$('#show_type').val(show_type_val);
    	$('#btnShow').val(show_type_val);
    	
    	//重新加载表格样式
    	tableResult = table.render({
            elem: '#' + DataFlowUser.tableId,
            url: Feng.ctxPath + '/data/flow_user/list',
            page: true,
            height: "full-158",
            cellMinWidth: 100,
            cols: DataFlowUser.initColumn()
        });
    	DataFlowUser.search();
		refresh();
    });

    // 导出excel
    $('#btnExp').click(function () {
        DataFlowUser.exportExcel();
    });
    
    DataFlowUser.dateConversionList = function (data) {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            title: '日期转化列表',
            area: ['1288px', '800px'],
            content: Feng.ctxPath + '/data/flow_user/toDateConversionList?date=' + data.date + '&name=' + $("#name").val()
            	+ "&company=" + $("#company").val() + "&channel=" + $("#channel").val(),
            end: function () {
                admin.getTempData('formOk') && table.reload(DataFlowUser.tableId);
            }
        });
    };
    
    DataFlowUser.registerConversionList = function (data) {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            title: '注册转化列表',
            area: ['1288px', '800px'],
            content: Feng.ctxPath + '/data/flow_user/toRegisterConversionList?date=' + data.date + '&name=' + $("#name").val()
            	+ "&company=" + $("#company").val() + "&channel=" + $("#channel").val(),
            end: function () {
                admin.getTempData('formOk') && table.reload(DataFlowUser.tableId);
            }
        });
    };
    
    DataFlowUser.differenceConversionList = function (data) {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            title: '差异转化列表',
            area: ['1288px', '800px'],
            content: Feng.ctxPath + '/data/flow_user/toDifferenceConversionList?date=' + data.date + '&name=' + $("#name").val()
            	+ "&company=" + $("#company").val() + "&channel=" + $("#channel").val(),
            end: function () {
                admin.getTempData('formOk') && table.reload(DataFlowUser.tableId);
            }
        });
    };
    
    DataFlowUser.failedConversionList = function (data) {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            title: '未转化列表',
            area: ['1288px', '800px'],
            content: Feng.ctxPath + '/data/flow_user/toFailedConversionList?date=' + data.date + '&name=' + $("#name").val()
            	+ "&company=" + $("#company").val() + "&channel=" + $("#channel").val(),
            end: function () {
                admin.getTempData('formOk') && table.reload(DataFlowUser.tableId);
            }
        });
    };


    // 工具条点击事件
    table.on('tool(' + DataFlowUser.tableId + ')', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'dateConversionList') {
            DataFlowUser.dateConversionList(data);
        } else if (layEvent === 'registerConversionList') {
            DataFlowUser.registerConversionList(data);
        } else if (layEvent === 'differenceConversionList') {
            DataFlowUser.differenceConversionList(data);
        } else if (layEvent === 'failedConversionList') {
            DataFlowUser.failedConversionList(data);
        } 
    });
});
