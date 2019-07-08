layui.use(['layer', 'form', 'table', 'ztree', 'laydate', 'admin', 'ax'], function () {
    var layer = layui.layer;
    var form = layui.form;
    var table = layui.table;
    var $ZTree = layui.ztree;
    var $ax = layui.ax;
    var laydate = layui.laydate;
    var admin = layui.admin;
    
    var Apk = {
        tableId: "table",   
        condition: {
            name: ""
        }
    };
    Apk.initColumn = function () {
        return [[
            {type: 'checkbox'},
            {field: 'id', hide: true, sort: true, title: 'id'},
            {field: 'name', sort: true, title: '名称'},
            {field: 'os', sort: true, title: '平台'},
            {field: 'description', sort: true, title: '介绍'},
            {field: 'createTime', sort: true, title: '创建时间'},
            {field: 'createName', sort: true, title: '创建人'},
            {field: 'status', sort: true, templet: '#statusTpl',title: '状态'},
            {align: 'center', toolbar: '#tableBar', title: '操作', minWidth: 320}
        ]];
    };

    Apk.search = function () {
        var queryData = {};
        queryData['name'] = $("#name").val();
        table.reload(Apk.tableId, {where: queryData});
    };

    Apk.openAddPage = function () {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            title: '添加页',
            area: ['600px', '800px'],
            content: Feng.ctxPath + '/apk/toAddPage',
            end: function () {
                admin.getTempData('formOk') && table.reload(Apk.tableId);
            }
        });
    };

    Apk.exportExcel = function () {
        var checkRows = table.checkStatus(Apk.tableId);
        if (checkRows.data.length === 0) {
            Feng.error("请选择要导出的数据");
        } else {
            table.exportFile(tableResult.config.id, checkRows.data, 'xls');
        }
    };

    Apk.toEditPage = function (data) {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            title: '编辑页',
            area: ['600px', '800px'],
            content: Feng.ctxPath + '/apk/toEditPage?id=' + data.id + '&uuid=' + data.uuid,
            end: function () {
                admin.getTempData('formOk') && table.reload(Apk.tableId);
            }
        });
    };

    Apk.onDelete = function (data) {
        var operation = function () {
            var ajax = new $ax(Feng.ctxPath + "/apk/delete?id="+ data.id + "&uuid=" + data.uuid, function () {
                table.reload(Apk.tableId);
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


    Apk.toDetailPage = function (data) {
    	 layer.open({
             type: 2,
             title: '详情页',
             area: ['600px', '800px'],
             content: Feng.ctxPath + '/apk/toDetailPage?id=' + data.id + "&uuid=" + data.uuid,
             end: function () {
                 table.reload(Apk.tableId);
             }
         });
    };

    var tableResult = table.render({
        elem: '#' + Apk.tableId,
        url: Feng.ctxPath + '/apk/list?tag=apk',
        page: true,
        height: "full-158",
        cellMinWidth: 100,
        cols: Apk.initColumn()
    });

    // 搜索按钮点击事件
    $('#btnSearch').click(function () {
        Apk.search();
    });

    // 添加按钮点击事件
    $('#btnAdd').click(function () {
        Apk.openAddPage();
    });

    // 导出excel
    $('#btnExp').click(function () {
        Apk.exportExcel();
    });

    // 工具条点击事件
    table.on('tool(' + Apk.tableId + ')', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        console.log(obj);
        if (layEvent === 'edit') {
            Apk.toEditPage(data);
        } else if (layEvent === 'delete') {
            Apk.onDelete(data);
        } else if (layEvent === 'detail'){
        	Apk.toDetailPage(data);
        }
    });
    
    /**
     * 修改用户状态
     *
     * @param userId 用户id
     * @param checked 是否选中（true,false），选中就是解锁用户，未选中就是锁定用户
     */
    Apk.changeStatus = function (id, checked) {
    	var status = checked ? "上架" : "下架" ;
        var ajax = new $ax(Feng.ctxPath + "/apk/updateStatus?status=" + status, function (data) {
            Feng.success("成功!");
        }, function (data) {
            Feng.error("失败!");
            table.reload(ChannelUser.tableId);
        });
        ajax.set("id", id);
        ajax.start();
    };
    
    // 修改user状态
    form.on('switch(status)', function (obj) {

        var id = obj.elem.value;
        var checked = obj.elem.checked ? true : false;

        Apk.changeStatus(id, checked);
    });


});
