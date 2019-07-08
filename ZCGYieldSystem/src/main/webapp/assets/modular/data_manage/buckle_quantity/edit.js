layui.use(['layer', 'form', 'admin', 'laydate', 'ax'], function () {
    var $ = layui.jquery;
    var $ax = layui.ax;
    var form = layui.form;
    var admin = layui.admin;
    var laydate = layui.laydate;
    var layer = layui.layer;

    // 让当前iframe弹层高度适应
    admin.iframeAuto();
    
    var ajax = new $ax(Feng.ctxPath + "/data/buckle_quantity/searchOne?id=" + Feng.getUrlParam("id"));
    var result = ajax.start();
    console.log(result);
    form.val('showForm', result.data);
    
    $("#value").val(result.data.ratioValue);

  //渲染时间选择框
    laydate.render({
        elem: '#timeLimit',
        range: true,
        format: 'yyyy-MM-dd',
        value: result.data.startTime + " - " + result.data.endTime
    });

    // 表单提交事件
    form.on('submit(btnSubmit)', function (data) {
        var ajax = new $ax(Feng.ctxPath + "/data/buckle_quantity/edit?id="+Feng.getUrlParam("id"), function (data) {
        	 if(data.code == 200){
          	   Feng.success("修改成功！");
                 //传给上个页面，刷新table用
                 admin.putTempData('formOk', true);
                 //关掉对话框
                 admin.closeThisDialog();
             }
             else{
          	   Feng.success("修改失败：" + data.message);
             }
        }, function (data) {
            Feng.error("设置失败！" + data.responseJSON.message)
        });
        ajax.set(data.field);
        ajax.start();
    });
});