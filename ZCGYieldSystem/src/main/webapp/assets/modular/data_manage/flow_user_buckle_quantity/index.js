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
              buckleStr: "",
              appName: "" ,
              os: ""
        }
    };
    DataFlowUser.initColumn = function () {
    	if($("#admin").val() == 'true'){
    		return [[
	            {type: 'checkbox',totalRowText: '合计'},
	            {field: 'appName', sort: true, title: '产品名'},
	            {field: 'date', sort: true, title: '日期'},
	            {field: 'number', sort: true, totalRow: true,title: 'app登录数'},
	            {field: 'channelName', sort: true, title: 'App渠道'},
	            {field: 'company', sort: true, title: '公司'},
	            {field: 'userName', sort: true, title: '推广专员'},
	        ]];
    	}
    	else{
			return [[
	            {type: 'checkbox',totalRowText: '合计'},
	            {field: 'appName', sort: true, title: '产品名'},
	            {field: 'channelName', sort: true, title: 'App渠道'},
	            {field: 'date', sort: true, title: '日期'},
	            {field: 'number', sort: true, totalRow: true,title: '注册数'},
	        ]];
    	}
    };

    DataFlowUser.search = function () {
        var queryData = {};
        queryData['name'] = $("#name").val();
        queryData['timeLimit'] = $("#timeLimit").val();
        queryData['company'] = $("#company").val();
        queryData['channel'] = $("#channel").val();
        queryData['buckleStr'] = $("#buckle").val();
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
        url: Feng.ctxPath + '/data/flow_user/buckle_quantity_list',
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
        format: 'yyyy-MM-dd'
    });

    // 搜索按钮点击事件
    $('#btnSearch').click(function () {
        DataFlowUser.search();
    });
    // 扣量显示
    $('#btnShowType').click(function () {
    	var $buckle = $("#buckle");
    	var val = $buckle.val() == "扣量显示" ? "" : "扣量显示";
    	$buckle.val(val);
    	$(this).text(val == "扣量显示" ? "不扣量显示" : "扣量显示");
    	DataFlowUser.search();
    });
    // 导出excel
    $('#btnExp').click(function () {
        DataFlowUser.exportExcel();
    });
});
