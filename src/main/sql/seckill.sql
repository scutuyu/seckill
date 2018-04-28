-- 秒杀执行存储过程
delimiter $$ -- ;转换为$$重新制定console的换行符
-- 参数定义: in 输入参数；out输出参数
-- row_count();用来返回上一条修改类型sql的影响行数
-- row_count() 0:未修改，>0:表示修改的行数，<0:sql错误或者未执行
create procedure `seckill`.`execute_seckill`(in v_seckill_id bigint, in v_phone bigint,
in v_kill_time timestamp, out r_result int)
begin
  declare insert_count int default 0;
  start transaction;
  insert ignore into `success_killed` (`seckill_id`, `user_phone`, `state`, `create_time`)
  values(v_seckill_id, v_phone, 0, v_kill_time);
  select row_count() into insert_count;
  if(insert_count = 0) then
    rollback;
    set r_result = -1;
  elseif(insert_count < 0) then
    rollback;
    set r_result = -2;
  else
    update `seckill` set `number` = `number` - 1
    where `seckill_id` = v_seckill_id and `end_time` > v_kill_time and `start_time` < v_kill_time and number > 0;
    select row_count() into insert_count;
    if(insert_count = 0) then
      rollback;
      set r_result = 0;
    elseif(insert_count < 0) then
      rollback;
      set r_result = -2;
    else
      commit;
      set r_result = 1;
    end if;
  end if;
end;$$
delimiter ; -- 将换行符修改为原来的分号

-- 在console中定义变量使用@符号，如set @r_result
set @r_result = -3;
-- 执行存储过程
call execute_seckill(1003, 12345678911, now(), @r_result);

-- 获取结果
select @r_result;

-- 存储过程优化：事务行级锁持有的时间，不要过度依赖存储过程