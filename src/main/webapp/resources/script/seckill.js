// 存放主要交互逻辑代码
// javascript 模块化

var seckill = {
    // 封装秒杀相关ajax相关的地址
    URL: {
        now: function () {
            return '/seckill/time/now';
        },
        exposer: function (seckillId) {
            return '/seckill/' + seckillId + '/exposer';
        },
        execution: function (seckillId, md5) {
            return '/seckill/' + seckillId + '/' + md5 + '/execution';
        }
    },
    // 验证手机号
    validDataPhone: function (phone) {
        if(phone && phone.length == 11 && !isNaN(phone)){
            return true;
        }else {
            return false;
        }
    },
    handleSeckill: function (seckillId, node) {
        // 获取秒杀地址，控制实现逻辑，执行秒杀
        node.hide()
            .html('<button class="btn btn-primary btn-lg" id="killBtn">开始秒杀</button>');
        $.post(seckill.URL.exposer(seckillId), {}, function (result) {
            // 在回调函数中执行交互流程
            if (result && result['success']){
                var exposer = result['data'];
                if (exposer['exposed']){
                    var md5 = exposer['md5']
                    var killUrl = seckill.URL.execution(seckillId, md5);
                    console.log('killUrl = ' + killUrl);
                    // 绑定一次点击事件
                    $('#killBtn').one('click', function () {
                        /// 执行秒杀请求的操作
                        // 禁用按钮
                        $(this).addClass('disabled');
                        // 发送请求
                        $.post(killUrl, {}, function (result) {
                            if (result && result['success']){
                                var killResult = result['data'];
                                var state = killResult['state'];
                                var stateInfo = killResult['stateInfo'];
                                // 显示秒杀结果
                                node.html('<span class="label label-success">' + stateInfo + '</span>')
                            }else {

                            }
                        });
                    });
                    node.show();
                }else {
                    // 客户端时间差异
                    var now = exposer['now'];
                    var start = exposer['start'];
                    var end = exposer['end'];
                    // 重新进入计时逻辑
                    seckill.countdown(seckillId, now, start, end);
                }
            }else {
                console.log('result = ' +result);
            }
        })


    },
    countdown: function (seckillId, nowTime, startTime, endTime) {
        var seckillBox = $('#seckill-box');
        if (nowTime > endTime){
            seckillBox.html('秒杀结束');
        }else if (nowTime < startTime){
            // seckillBox.html('秒杀未开始结束');
            /// 计时事件绑定,防止事件偏移
            var killTime = new Date(startTime + 1000);
            seckillBox.countdown(killTime, function (event) {
                // 控制时间的格式
                var format = event.strftime('秒杀计时： %D天 %H时 %M分 %S秒')
                seckillBox.html(format);
            }).on('finish.countdown', function () {
                seckill.handleSeckill(seckillId, seckillBox)
            });
        }else {
            seckill.handleSeckill(seckillId, seckillBox)
        }
    },
    // 详情页秒杀逻辑
    detail: {
        // 详情页初始化
        init: function(params){
            // 用户手机验证和登录，计时交互
            // 规划交互流程
            // 在cookie中查找手机号
            var killPhone = $.cookie('killPhone');
            // 验证手机号
            if (!seckill.validDataPhone(killPhone)){
                // 绑定phone
                // 控制输出
                var killPhoneModel = $('#killPhoneModal');
                // 显示了弹出层
                killPhoneModel.modal({
                    show: true,// 显示弹出层
                    backdrop: 'static', // 禁止位置关闭
                    keyboard: false, // 关闭键盘事件
                });
                $('#killPhoneBtn').click(function () {
                   var inputPhone = $('#killPhoneKey').val();
                   console.log("inputPhone = ", inputPhone); // TODO
                   if (seckill.validDataPhone(inputPhone)){
                       // 电话写入cookie
                       $.cookie('killPhone', inputPhone, {expiress: 7, path: '/seckill'});
                       // 刷新页面
                       window.location.reload();
                   }else {
                       $('#killPhoneMessage')
                           .hide()
                           .html('<label class="label label-danger">手机号错误！</label>')
                           .show(300);
                   }
                });
            }
            // 已登录
            // 计时交互
            var startTime =params['startTime'];
            var endTime = params['endTime'];
            var seckillId = params['seckillId'];
            $.get(seckill.URL.now(), {}, function (result) {
                if (result && result['success']){
                    var nowTime = result['data'];
                    // 时间判断，计时服务
                    seckill.countdown(seckillId, nowTime, startTime, endTime)
                }else {
                    console.log('result: ' + result);
                }
            });
        }

    }
}