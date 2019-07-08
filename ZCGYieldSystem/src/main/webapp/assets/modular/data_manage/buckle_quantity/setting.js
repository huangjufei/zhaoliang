layui.use(['layer', 'form', 'admin', 'laydate', 'ax'], function () {
    var $ = layui.jquery;
    var $ax = layui.ax;
    var form = layui.form;
    var admin = layui.admin;
    var laydate = layui.laydate;
    var layer = layui.layer;

    // 让当前iframe弹层高度适应
    admin.iframeAuto();

    //设置扣量链接id
    $("#restrictedName").val(Feng.getUrlParam("id"));
    
  //渲染时间选择框
    laydate.render({
        elem: '#timeLimit',
        range: true,
        format: 'yyyy-MM-dd'
    });


    // 表单提交事件
    form.on('submit(btnSubmit)', function (data) {
        var ajax = new $ax(Feng.ctxPath + "/data/buckle_quantity/add", function (data) {
           if(data.code == 200){
        	   Feng.success("设置成功！");
               //传给上个页面，刷新table用
               admin.putTempData('formOk', true);
               //关掉对话框
               admin.closeThisDialog();
           }
           else{
        	   Feng.success("设置失败：" + data.message);
           }
        }, function (data) {
            Feng.error("设置失败！" + data.message)
        });
        ajax.set(data.field);
        ajax.start();
    });
});