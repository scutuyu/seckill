package org.seckill.service;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillClosedException;
import org.seckill.exception.SeckillException;

import java.util.List;

/**
 * 站在使用者角度设计接口
 * 三个方面：
 * 1. 方法定义粒度，好调用
 * 2. 参数，越简单越好
 * 3. 返回类型，要友好，不要map，不要entity
 * @author tuyu
 *         Stay Hungry, Stay Foolish.
 */
public interface SeckillService {

    /**
     * @return
     */
    List<Seckill> getSeckillList();

    /**
     * @param seckillId
     * @return
     */
    Seckill getById(long seckillId);

    /**
     * 秒杀开始时，输出秒杀接口的地址，否则输出系统时间和秒杀时间
     * @param seckillId
     */
    Exposer exportSeckillUrl(long seckillId);

    /**
     * @param seckillId
     * @param userPhone
     * @param md5
     * 执行秒杀
     */
    SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
            throws SeckillException, RepeatKillException, SeckillClosedException;
}
