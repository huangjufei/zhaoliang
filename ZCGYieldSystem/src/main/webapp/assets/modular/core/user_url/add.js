
layui.use(['layer', 'form', 'admin', 'laydate', 'ax' ,'upload'], function () {
    var $ = layui.jquery;
    var $ax = layui.ax;
    var form = layui.form;
    var admin = layui.admin;
    var laydate = layui.laydate;
    var layer = layui.layer;

    // 让当前iframe弹层高度适应
    admin.iframeAuto();

    // 表单提交事件
    form.on('submit(btnSubmit)', function (data) {
    	data.field.channelName = $("#channel :checked").text();
        var ajax = new $ax(Feng.ctxPath + "/user_url/add", function (data) {
        	if(data.code == 200){
	            Feng.success("添加成功！");
	            //传给上个页面，刷新table用
	            admin.putTempData('formOk', true);
	            //关掉对话框
	            admin.closeThisDialog();
	            return;
        	}
        	Feng.error("添加失败！" + data.message)
        }, function (data) {
            Feng.error("添加失败！" + data.message)
        });
        ajax.set(data.field);
        ajax.start();
    });
    
    form.on('select(channel)' ,function (data){
    	var channel = $("#channel option:checked");
    	$("#channelName").val(channel.text());
    	form.render();
        refresh();
    })
    
    form.on('select(company)' ,function (data){
    	//清除之前添加的用户
    	var $oldUser = $("#user").children();
    	for(var i = 0 ;i < $oldUser.length ;++i){
    		$oldUser.eq(i).remove();
    	}
    	//拦截请选择按钮
    	if(!$("#company").val() || $("#company").val() == ""){
    		refresh();
    		return ;
    	}
        var ajax = new $ax(Feng.ctxPath + "/channel_user/search/all/user?company=" + $("#company").val()
	        , function (data) {
	        	console.log(data);
	        	if(data.code == 200){
	        		var userArray = data.data;
	        		if(userArray.length == 0){
	        			Feng.error($("#company").val() + "暂无用户，请先创建");
	        			return;
	        		}
	        		for(var i = 0 ;i < userArray.length ;++i){
	        			var ele = '<option value='+userArray[i].userId+'>' + userArray[i].name + '</option>';
	        			$('#user').append(ele);
	        		}
	        		refresh();
	        	}
	        	else{
	        		Feng.error("查询公司用户失败：" + data.message);
	        	}
	        }, function (data) {
	            Feng.error("查询公司用户失败：" + data.message);
	        });
        ajax.start();
    })
    function refresh(){
    	layui.use(['element','form'], function() {
            var element = layui.element;
            element.init();
    		var form = layui.form; //只有执行了这一步，部分表单元素才会自动修饰成功
			 form.render();
        });
    }
});