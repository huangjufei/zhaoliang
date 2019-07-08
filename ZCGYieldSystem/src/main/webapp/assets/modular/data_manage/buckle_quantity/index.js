layui.use(['layer', 'form', 'table', 'ztree', 'laydate', 'admin', 'ax'], function () {
    var layer = layui.layer;
    var form = layui.form;
    var table = layui.table;
    var $ZTree = layui.ztree;
    var $ax = layui.ax;
    var laydate = layui.laydate;
    var admin = layui.admin;
    
    var BuckleQuantity = {
        tableId: "table",   
        condition: {
        	name: "",
            company: "",
            channel: "",
            timeLimit: "",
            defaultStr: "" ,
            appName: ""
        }
    };
    BuckleQuantity.initColumn = function () {
        return [[
            {type: 'checkbox'},
            {field: 'id', hide: true, sort: true, title: 'id'},
            {field: 'channelName', sort: true, title: 'App渠道'},
            {field: 'urlCreatorCompany', sort: true, title: '公司'},
            {field: 'urlCreatorName', sort: true, title: '推广专员'},
            {field: 'restrictedName', sort: true, title: '推广id'},
            {field: 'userUrl', sort: true, title: '推广链接'},
            {field: 'showMinData', sort: true, title: '最小显示数量'},
            {field: 'ratioValue', sort: true, title: '扣量值'},
            {field: 'workRestrictedField', sort: true, title: '扣量模式'},
            {field: 'startTime', sort: true, title: '开始时间'},
            {field: 'endTime', sort: true, title: '结束时间'},
            {field: 'info', sort: true, title: '扣量说明'},
            {field: 'createTime', sort: true, title: '创建时间'},
            {field: 'creator', sort: true, title: '创建人'},
            {align: 'center', toolbar: '#tableBar', title: '操作', minWidth: 120}
        ]];
    };

    BuckleQuantity.search = function () {
        var queryData = {};
        queryData['name'] = $("#name").val();
        queryData['company'] = $("#company").val();
        queryData['channel'] = $("#channel").val();
        queryData['defaultStr'] = $("#defaultStr").val();
        queryData['appName'] = $("#appName").val();
        queryData['timeLimit'] = $("#timeLimit").val();
        table.reload(BuckleQuantity.tableId, {where: queryData});
    };

    BuckleQuantity.openAddPage = function () {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            title: '扣量创建',
            area: ['1288px', '800px'],
            content: Feng.ctxPath + '/data/buckle_quantity/toAddPage',
            end: function () {
                admin.getTempData('formOk') && table.reload(BuckleQuantity.tableId);
            }
        });
    };

    BuckleQuantity.exportExcel = function () {
        var checkRows = table.checkStatus(BuckleQuantity.tableId);
        if (checkRows.data.length === 0) {
            Feng.error("请选择要导出的数据");
        } else {
            table.exportFile(tableResult.config.id, checkRows.data, 'xls');
        }
    };

    BuckleQuantity.toEditPage = function (data) {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            title: '编辑页',
            area: ['1024px', '800px'],
            content: Feng.ctxPath + '/data/buckle_quantity/toEditPage?id=' + data.id + '&uuid=' + data.uuid,
            end: function () {
                admin.getTempData('formOk') && table.reload(BuckleQuantity.tableId);
            }
        });
    };

    BuckleQuantity.onDelete = function (data) {
        var operation = function () {
            var ajax = new $ax(Feng.ctxPath + "/data/buckle_quantity/delete?id="+ data.id + "&uuid=" + data.uuid, function () {
                table.reload(BuckleQuantity.tableId);
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
        elem: '#' + BuckleQuantity.tableId,
        url: Feng.ctxPath + '/data/buckle_quantity/list',
        page: true,
        height: "full-158",
        cellMinWidth: 100,
        cols: BuckleQuantity.initColumn()
    });

    //渲染时间选择框
    laydate.render({
        elem: '#timeLimit',
        range: true,
        format: 'yyyy-MM-dd HH:mm:ss'
    });
    
    // 搜索按钮点击事件
    $('#btnSearchDefault').click(function () {
    	$("#defaultStr").val("default");
    	BuckleQuantity.search();
    });

    // 搜索按钮点击事件
    $('#btnSearch').click(function () {
    	$("#defaultStr").val("");
        BuckleQuantity.search();
    });

    // 添加按钮点击事件
    $('#btnAdd').click(function () {
        BuckleQuantity.openAddPage();
    });

    // 导出excel
    $('#btnExp').click(function () {
        BuckleQuantity.exportExcel();
    });

    // 工具条点击事件
    table.on('tool(' + BuckleQuantity.tableId + ')', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'edit') {
            BuckleQuantity.toEditPage(data);
        } else if (layEvent === 'delete') {
            BuckleQuantity.onDelete(data);
        } else if (layEvent === 'detail'){
        	BuckleQuantity.toDetailPage(data);
        }
    });

});
