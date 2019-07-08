layui.use(['layer', 'form', 'table', 'ztree', 'laydate', 'admin', 'ax'], function () {
    var $ = layui.jquery;
    var layer = layui.layer;
    var form = layui.form;
    var table = layui.table;
    var $ZTree = layui.ztree;
    var $ax = layui.ax;
    var laydate = layui.laydate;
    var admin = layui.admin;
   
    
    var PROMOTE = {
        tableId: "table",   
        condition: {
            name: "",
            company: "",
            channel: "" ,
            appName: ""
        }
    };
    
    PROMOTE.initColumn = function () {
        return [[
            {type: 'checkbox'},
            {field: 'id', hide: true, sort: true, title: 'id'},
            {field: 'userId', hide: true, sort: true, title: 'userId'},
            {field: 'appId', hide: true, sort: true, title: 'appId'},
            {field: 'company', sort: true, title: '渠道商'},
            {field: 'userName', sort: true, title: '用户名称'},
            {field: 'appName', sort: true, title: 'app名称'},
            {field: 'channel', sort: true, title: 'App渠道'},
            {field: 'channelName', sort: true, title: '渠道名称'},
            {field: 'recommendKey', sort: true, title: '推广key'},
            {field: 'url', sort: true, title: '推广链接'},
            {field: 'createTime', sort: true, title: '创建时间'},
            {align: 'center', toolbar: '#tableBar', title: '操作', minWidth: 320}
        ]];
    };

    PROMOTE.search = function () {
        var queryData = {};
        queryData['name'] = $("#name").val();
        queryData['channel'] = $("#channel").val();
        queryData['company'] = $("#company").val();
        queryData['appName'] = $("#appName").val();
        table.reload(PROMOTE.tableId, {where: queryData});
    };

    var tableResult = table.render({
        elem: '#' + PROMOTE.tableId,
        url: Feng.ctxPath + '/user_url/list?tag=user_url',
        page: true,
        height: "full-158",
        cellMinWidth: 100,
        cols: PROMOTE.initColumn()
    });

    // 搜索按钮点击事件
    $('#btnSearch').click(function () {
        PROMOTE.search();
    });
    
 // 添加按钮点击事件
    $('#btnAdd').click(function () {
    	PROMOTE.openAddPage();
    });
    
    PROMOTE.openAddPage = function (data) {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            title: '设置页',
            area: ['1288px', '800px'],
            content: Feng.ctxPath + '/data/buckle_quantity/toDefaultSettingPage',
            end: function () {
                admin.getTempData('formOk') && table.reload(PROMOTE.tableId);
            }
        });
    };
    
    PROMOTE.toSetting = function (data) {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            title: '设置页',
            area: ['1288px', '800px'],
            content: Feng.ctxPath + '/data/buckle_quantity/toSettingPage?id=' + data.id + '&uuid=' + data.uuid,
            end: function () {
                admin.getTempData('formOk') && table.reload(PROMOTE.tableId);
            }
        });
    };

  
    // 工具条点击事件
    table.on('tool(' + PROMOTE.tableId + ')', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'setting') {
        	PROMOTE.toSetting(data);
        }
    });
});
