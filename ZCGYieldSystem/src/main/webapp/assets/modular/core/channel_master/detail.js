/**
 * 用户详情对话框
 */
var UserInfoDlg = {
    data: {
        deptId: "",
        deptName: ""
    }
};

layui.use(['layer', 'form', 'admin', 'laydate', 'ax'], function () {
    var $ = layui.jquery;
    var $ax = layui.ax;
    var form = layui.form;
    var admin = layui.admin;
    var laydate = layui.laydate;
    var layer = layui.layer;

    // 让当前iframe弹层高度适应
    admin.iframeAuto();
 // 获取用户信息
	var ajax = new $ax(Feng.ctxPath
			+ "/channel_master/searchOne?id="
			+ Feng.getUrlParam("id"));
	var result = ajax.start();
	console.log(result);
	form.val('showForm', result.data);
});