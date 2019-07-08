layui.use(['layer', 'form', 'admin', 'laydate', 'ax' ,'upload'], function () {
    var $ = layui.jquery;
    var $ax = layui.ax;
    var form = layui.form;
    var admin = layui.admin;
    var laydate = layui.laydate;
    var layer = layui.layer;

    // 让当前iframe弹层高度适应
    admin.iframeAuto();

    var ajax = new $ax(Feng.ctxPath + "/apk/searchOne?id=" + Feng.getUrlParam("id"));
    var result = ajax.start();
    form.val('showFrom', result.data);

    var upload = layui.upload;
	//图片上传
	  var uploadInst = upload.render({
	    elem: '#apkFile' //绑定元素
	    ,auto: false //选择文件后不自动上传
	    ,bindAction: '#btnSubmit'
	    ,url: Feng.ctxPath+'/apk/update/'//上传接口
	    ,accept: 'file'
	    ,before:function(){
	  		this.data = {
	  			id: Feng.getUrlParam("id"),
	  			name: $("#name").val(),
	  			os: $("#os").val(),
	  			description:  $("#description").val()
	  		}
	  	}
	    ,done: function(res){
	    	if(res.code != 200 ){
	    		Feng.error("失败：" + res.message);
	    		return;
	    	}
	    	Feng.success("成功！");
	    	// 传给上个页面，刷新table用
			admin.putTempData('formOk', true);
			// 关掉对话框
			admin.closeThisDialog();
	    }
	  	,error: function(res){
	  		Feng.error("失败：" + res.message);
	  	}
	  });

    // 表单提交事件
    form.on('submit(btnSubmit)', function (data) {
        return false;
    });
    
    form.on('select(channel)' ,function (data){
    	$("#channelName").val($("#channel option:checked").text());
    })
});