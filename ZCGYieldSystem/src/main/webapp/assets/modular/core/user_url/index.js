layui.use(['layer', 'form', 'table', 'ztree', 'laydate', 'admin', 'ax'], function () {
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
            channel: "",
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
            {
                field: 'ooo', title: '链接', align: 'center', templet: function (d) {
                return   '<a href="javascript:;" data-clipboard-text="" class="font-primary layui-btn layui-btn-xs" lay-event="share">复制链接</a>';
                }
            }
        ]];
    };

    PROMOTE.search = function () {
        var queryData = {};
        queryData['name'] = $("#name").val();
        queryData['appName'] = $("#appName").val();
        queryData['channel'] = $("#channel").val();
        queryData['company'] = $("#company").val();
        table.reload(PROMOTE.tableId, {where: queryData});
    };

    PROMOTE.openAddPage = function () {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            title: '链接创建',
            area: ['600px', '800px'],
            content: Feng.ctxPath + '/user_url/toAddPage',
            end: function () {
                admin.getTempData('formOk') && table.reload(PROMOTE.tableId);
            }
        });
    };

    PROMOTE.exportExcel = function () {
        var checkRows = table.checkStatus(PROMOTE.tableId);
        if (checkRows.data.length === 0) {
            Feng.error("请选择要导出的数据");
        } else {
            table.exportFile(tableResult.config.id, checkRows.data, 'xls');
        }
    };

    PROMOTE.onDelete = function (data) {
        var operation = function () {
            var ajax = new $ax(Feng.ctxPath + "/user_url/delete?id="+ data.id + "&uuid=" + data.uuid, function () {
                table.reload(PROMOTE.tableId);
                Feng.success("删除成功!");
            }, function (data) {
                Feng.error("删除失败!" + data.responseJSON.message + "!");
            });
            ajax.set("id", data.id);
            ajax.set("uuid", data.uuid);
            ajax.start();
        };
        Feng.confirm("确认删除吗?", operation);
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

    // 导出excel
    $('#btnExp').click(function () {
        PROMOTE.exportExcel();
    });
    // 工具条点击事件
    table.on('tool(' + PROMOTE.tableId + ')', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'share') {
            $(".font-primary").attr("data-clipboard-text",data.url);
            var clipboard = new ClipboardJS('.font-primary');
            clipboard.on('success', function(e) {
            	Feng.success("复制成功!");
                clipboard.destroy();  //解决多次弹窗
                e.clearSelection();
            });
	 }
    });
});
