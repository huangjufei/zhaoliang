layui.use(['layer', 'form', 'table', 'ztree', 'laydate', 'admin', 'ax'], function () {
    var layer = layui.layer;
    var form = layui.form;
    var table = layui.table;
    var $ZTree = layui.ztree;
    var $ax = layui.ax;
    var laydate = layui.laydate;
    var admin = layui.admin;
    
    var ChannelMaster = {
        tableId: "table",   
        condition: {
            name: "",
            deptId: "",
            timeLimit: ""
        }
    };
    ChannelMaster.initColumn = function () {
        return [[
            {type: 'checkbox'},
            {field: 'id', hide: true, sort: true, title: 'id'},
            {field: 'uuid', hide: true, sort: true, title: 'uuid'},
            {field: 'name', sort: true, title: '名称'},
            {field: 'description', sort: true, title: '介绍'},
            {field: 'master', sort: true, title: '联系人'},
            {field: 'masterContact', sort: true, title: '联系方式'},
            {field: 'info', sort: true, title: '备注'},
            {field: 'createTime', sort: true, title: '创建时间'},
            {field: 'createName', sort: true, title: '创建人'},
            {field: 'status', sort: true, templet: '#statusTpl', title: '状态'},
            {align: 'center', toolbar: '#tableBar', title: '操作', minWidth: 320}
        ]];
    };

    ChannelMaster.search = function () {
        var queryData = {};
        queryData['name'] = $("#name").val();
        queryData['timeLimit'] = $("#timeLimit").val();
        table.reload(ChannelMaster.tableId, {where: queryData});
    };

    ChannelMaster.openAddPage = function () {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            title: '添加页',
            area: ['600px', '800px'],
            content: Feng.ctxPath + '/channel_master/toAddPage',
            end: function () {
                admin.getTempData('formOk') && table.reload(ChannelMaster.tableId);
            }
        });
    };

    ChannelMaster.exportExcel = function () {
        var checkRows = table.checkStatus(ChannelMaster.tableId);
        if (checkRows.data.length === 0) {
            Feng.error("请选择要导出的数据");
        } else {
            table.exportFile(tableResult.config.id, checkRows.data, 'xls');
        }
    };

    ChannelMaster.toEditPage = function (data) {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            title: '编辑页',
            area: ['600px', '800px'],
            content: Feng.ctxPath + '/channel_master/toEditPage?id=' + data.id + '&uuid=' + data.uuid,
            end: function () {
                admin.getTempData('formOk') && table.reload(ChannelMaster.tableId);
            }
        });
    };

    ChannelMaster.onDelete = function (data) {
        var operation = function () {
            var ajax = new $ax(Feng.ctxPath + "/channel_master/delete?id="+ data.id + "&uuid=" + data.uuid, function () {
                table.reload(ChannelMaster.tableId);
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


    ChannelMaster.toDetailPage = function (data) {
    	 layer.open({
             type: 2,
             title: '详情页',
             area: ['600px', '800px'],
             content: Feng.ctxPath + '/channel_master/toDetailPage?id=' + data.id + "&uuid=" + data.uuid,
             end: function () {
                 table.reload(ChannelMaster.tableId);
             }
         });
    };

    ChannelMaster.changeStatus = function (id, checked) {
        var status = checked ? "正常" : "禁用" ;
        var ajax = new $ax(Feng.ctxPath + "/channel_master/updateStatus?id="+id, function (data) {
            Feng.success("更新成功");
        }, function (data) {
            Feng.error("更新失败");
            table.reload(ChannelMaster.tableId);
        });
        ajax.set("status",status);
        ajax.start();
    };

    var tableResult = table.render({
        elem: '#' + ChannelMaster.tableId,
        url: Feng.ctxPath + '/channel_master/list',
        page: true,
        height: "full-158",
        cellMinWidth: 100,
        cols: ChannelMaster.initColumn()
    });

    //渲染时间选择框
    laydate.render({
        elem: '#timeLimit',
        range: true,
        format: 'yyyy-MM-dd HH:mm:ss'
    });

    // 搜索按钮点击事件
    $('#btnSearch').click(function () {
        ChannelMaster.search();
    });

    // 添加按钮点击事件
    $('#btnAdd').click(function () {
        ChannelMaster.openAddPage();
    });

    // 导出excel
    $('#btnExp').click(function () {
        ChannelMaster.exportExcel();
    });

    // 工具条点击事件
    table.on('tool(' + ChannelMaster.tableId + ')', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        console.log(obj);
        if (layEvent === 'edit') {
            ChannelMaster.toEditPage(data);
        } else if (layEvent === 'delete') {
            ChannelMaster.onDelete(data);
        } else if (layEvent === 'detail'){
        	ChannelMaster.toDetailPage(data);
        }
    });

    // 修改user状态
    form.on('switch(status)', function (obj) {
        var id = obj.elem.value;
        console.log(id);
        var checked = obj.elem.checked ? true : false;
        ChannelMaster.changeStatus(id, checked);
    });

});
