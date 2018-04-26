<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--引入jstl--%>
<%@include file="common/tag.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <title>秒杀详情页</title>
    <%--jsp的静态包含--%>
    <%--静态饱和与动态包含的区别，静态包含会将jsp文件合并过来，作为一个Servlet输出，而动态包含，被包含的jsp会作为一个独立的Servlet先执行，把运行结果与另一个Servlet结果做合并--%>
    <%@include file="common/head.jsp"%>
</head>
<body>
<div class="container">
    <div class="panel panel-default text-center">
        <div class="panel-heading">
            <h1>${seckill.name}</h1>
        </div>
    </div>
    <div class="panel-body">
        <h2 class="text-danger">
            <span class="glyphicon glyphicon-time"></span>
            <span class="glyphicon" id="seckill-box"></span>
        </h2>
    </div>
</div>

<div id="killPhoneModal" class="modal fade">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h3 class="modal-title text-center">
                    <span class="glyphicon glyphicon-phone"></span>
                </h3>
            </div>
            <div class="modal-body">
                <div class="row">
                    <div class="col-xs-8 col-xs-offset-2">
                        <input type="text" name="killPhone" id="killPhoneKey" placeholder="填写手机号" class="form-control">
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <span id="killPhoneMessage" class="glyphicon"></span>
                <button class="btn btn-success" id="killPhoneBtn" type="button">
                    <span class="glyphicon glyphicon-phone"></span>
                    submit
                </button>
            </div>
        </div>
    </div>
</div>

</body>
<!-- 新 Bootstrap 核心 CSS 文件 -->
<%--<link href="https://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet">--%>

<!-- 可选的Bootstrap主题文件（一般不使用） -->
<%--<script src="https://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap-theme.min.css"></script>--%>

<!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
<script src="https://cdn.bootcss.com/jquery/2.1.1/jquery.min.js"></script>

<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
<script src="https://cdn.bootcss.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<script src="https://cdn.bootcss.com/jquery.countdown/2.2.0/jquery.countdown.min.js"></script>
<!--使用cdn 获取公共js www.bootcdn.com, jquery cookie操作插件，jQuery count倒计时插件 -->
<script src="https://cdn.bootcss.com/jquery-cookie/1.4.1/jquery.cookie.min.js"></script>
<script type="text/javascript" src="../../resources/script/seckill.js"></script>
<script type="text/javascript">
    $(function(){
       seckill.detail.init({
          // 使用EL表达式传入参数
           seckillId: ${seckill.seckillId},
           startTime: ${seckill.startTime.time}, // 毫秒
           endTime: ${seckill.endTime.time}
       });
    });
</script>
</html>
