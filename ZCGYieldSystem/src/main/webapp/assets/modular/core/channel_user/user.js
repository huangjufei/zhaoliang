layui.use(['layer', 'form', 'table', 'ztree', 'laydate', 'admin', 'ax'], function () {
    var layer = layui.layer;
    var form = layui.form;
    var table = layui.table;
    var $ZTree = layui.ztree;
    var $ax = layui.ax;
    var laydate = layui.laydate;
    var admin = layui.admin;

    /**
     * 系统管理--用户管理
     */
    var ChannelUser = {
        tableId: "userTable",    //表格id
        condition: {
            name: "",
            deptId: "",
            timeLimit: ""
        }
    };

    /**
     * 初始化表格的列
     */
    ChannelUser.initColumn = function () {
        return [[
            {type: 'checkbox'},
            {field: 'userId', hide: true, sort: true, title: '用户id'},
            {field: 'account', sort: true, title: '账号'},
            {field: 'name', sort: true, title: '姓名'},
            {field: 'roleName', sort: true, title: '角色'},
            {field: 'company', sort: true, title: '公司'},
            {field: 'phone', sort: true, title: '电话'},
            {field: 'createTime', sort: true, title: '创建时间'},
            {field: 'status', sort: true, templet: '#statusTpl', title: '状态'},
            {align: 'center', toolbar: '#tableBar', title: '操作', minWidth: 320}
        ]];
    };
    
   

    /**
     * 点击查询按钮
     */
    ChannelUser.search = function () {
        var queryData = {};
        queryData['company'] = $("#company_sel option:selected").val();
        queryData['name'] = $("#name").val();
        queryData['timeLimit'] = $("#timeLimit").val();
        table.reload(ChannelUser.tableId, {where: queryData});
    };

    /**
     * 弹出添加用户对话框
     */
    ChannelUser.openAddUser = function () {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            title: '添加用户',
            area: ['600px', '800px'],
            content: Feng.ctxPath + '/channel_user/user_add',
            end: function () {
                admin.getTempData('formOk') && table.reload(ChannelUser.tableId);
            }
        });
    };

    /**
     * 导出excel按钮
     */
    ChannelUser.exportExcel = function () {
        var checkRows = table.checkStatus(ChannelUser.tableId);
        if (checkRows.data.length === 0) {
            Feng.error("请选择要导出的数据");
        } else {
            table.exportFile(tableResult.config.id, checkRows.data, 'xls');
        }
    };

    /**
     * 点击编辑用户按钮时
     *
     * @param data 点击按钮时候的行数据
     */
    ChannelUser.onEditUser = function (data) {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            title: '编辑用户',
            area: ['600px', '800px'],
            content: Feng.ctxPath + '/channel_user/user_edit?userId=' + data.userId + '&company=' + data.company,
            end: function () {
                admin.getTempData('formOk') && table.reload(ChannelUser.tableId);
            }
        });
    };

    /**
     * 点击删除用户按钮
     *
     * @param data 点击按钮时候的行数据
     */
    ChannelUser.onDeleteUser = function (data) {
        var operation = function () {
            var ajax = new $ax(Feng.ctxPath + "/channel_user/delete", function () {
                table.reload(ChannelUser.tableId);
                Feng.success("删除成功!");
            }, function (data) {
                Feng.error("删除失败!" + data.responseJSON.message + "!");
            });
            ajax.set("userId", data.userId);
            ajax.start();
        };
        Feng.confirm("是否删除用户" + data.account + "?", operation);
    };

    /**
     * 分配角色
     *
     * @param data 点击按钮时候的行数据
     */
    ChannelUser.roleAssign = function (data) {
        layer.open({
            type: 2,
            title: '角色分配',
            area: ['300px', '400px'],
            content: Feng.ctxPath + '/channel_user/role_assign?userId=' + data.userId,
            end: function () {
                table.reload(ChannelUser.tableId);
            }
        });
    };

    /**
     * 用户详情
     */
    ChannelUser.detail = function (data) {
    	 layer.open({
             type: 2,
             title: '用户详情',
             area: ['600px', '800px'],
             content: Feng.ctxPath + '/channel_user/toDetailPage?userId=' + data.userId,
             end: function () {
                 table.reload(ChannelUser.tableId);
             }
         });
    };
    
    /**
     *      * 重置密码
     *
     * @param data 点击按钮时候的行数据
     */
    ChannelUser.resetPassword = function (data) {
        Feng.confirm("是否重置密码为111111 ?", function () {
            var ajax = new $ax(Feng.ctxPath + "/channel_user/reset", function (data) {
                Feng.success("重置密码成功!");
            }, function (data) {
                Feng.error("重置密码失败!");
            });
            ajax.set("userId", data.userId);
            ajax.start();
        });
    };

    /**
     * 修改用户状态
     *
     * @param userId 用户id
     * @param checked 是否选中（true,false），选中就是解锁用户，未选中就是锁定用户
     */
    ChannelUser.changeUserStatus = function (userId, checked) {
        if (checked) {
            var ajax = new $ax(Feng.ctxPath + "/channel_user/unfreeze", function (data) {
                Feng.success("解除冻结成功!");
            }, function (data) {
                Feng.error("解除冻结失败!");
                table.reload(ChannelUser.tableId);
            });
            ajax.set("userId", userId);
            ajax.start();
        } else {
            var ajax = new $ax(Feng.ctxPath + "/channel_user/freeze", function (data) {
                Feng.success("冻结成功!");
            }, function (data) {
                Feng.error("冻结失败!" + data.responseJSON.message + "!");
                table.reload(ChannelUser.tableId);
            });
            ajax.set("userId", userId);
            ajax.start();
        }
    };

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + ChannelUser.tableId,
        url: Feng.ctxPath + '/channel_user/list',
        page: true,
        height: "full-158",
        cellMinWidth: 100,
        cols: ChannelUser.initColumn()
    });

    //渲染时间选择框
    laydate.render({
        elem: '#timeLimit',
        range: true,
    });

    // 搜索按钮点击事件
    $('#btnSearch').click(function () {
        ChannelUser.search();
    });

    // 添加按钮点击事件
    $('#btnAdd').click(function () {
        ChannelUser.openAddUser();
    });

    // 导出excel
    $('#btnExp').click(function () {
        ChannelUser.exportExcel();
    });

    // 工具条点击事件
    table.on('tool(' + ChannelUser.tableId + ')', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;

        if (layEvent === 'edit') {
            ChannelUser.onEditUser(data);
        } else if (layEvent === 'delete') {
            ChannelUser.onDeleteUser(data);
        } else if (layEvent === 'roleAssign') {
            ChannelUser.roleAssign(data);
        } else if (layEvent === 'reset') {
            ChannelUser.resetPassword(data);
        }
        else if (layEvent === 'detail'){
        	ChannelUser.detail(data);
        }
    });

    // 修改user状态
    form.on('switch(status)', function (obj) {

        var userId = obj.elem.value;
        var checked = obj.elem.checked ? true : false;

        ChannelUser.changeUserStatus(userId, checked);
    });

});
