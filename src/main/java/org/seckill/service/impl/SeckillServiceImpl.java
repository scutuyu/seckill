package org.seckill.service.impl;

import org.apache.commons.collections.MapUtils;
import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dao.cache.RedisDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillClosedException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author tuyu
 *         Stay Hungry, Stay Foolish.
 */
@Service
public class SeckillServiceImpl implements SeckillService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeckillServiceImpl.class);

    @Autowired
    private SeckillDao seckillDao;

    @Autowired
    private RedisDao redisDao;

    @Autowired
    private SuccessKilledDao successKilledDao;

    // md5盐值字符串，用于混淆MD5
    private final String SALT = "jslkjflsajflsa;jFJLDSKJFLJ23KJLEWREQEQE~!~!@~ADSJDL";

    @Override
    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0, 4);
    }

    @Override
    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    @Override
    public Exposer exportSeckillUrl(long seckillId) {
        // 优化数据库访问,缓存优化：超时的基础上维护一致性
        // 1: 访问redis
        Seckill seckill = redisDao.getSeckill(seckillId);
        if (seckill == null){
            seckill = seckillDao.queryById(seckillId);
            if (seckill == null){
                return new Exposer(false, seckillId);
            }else {
                redisDao.putSeckill(seckill);
            }
        }
//        Seckill seckill = getById(seckillId);
        if (seckill == null){
            return new Exposer(false, seckillId);
        }
        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        Date now = new Date();
        if (now.getTime() < startTime.getTime() || now.getTime() > endTime.getTime()){
            return new Exposer(false, seckillId, now.getTime(), startTime.getTime(), endTime.getTime());
        }
        // 转化特定字符串的过程，不可逆
        String md5 = getMD5(seckillId);
        return new Exposer(true, md5, seckillId);
    }

    private String getMD5(long seckillId){
        String base = seckillId + "/" + SALT;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    /**
     * 使用注解控制事务方法的优点
     * 1：开发团队达成一致约定，明确标注事务方法的编程风格。
     * 2：保证事务方法的执行时间尽可能短，不要穿插其他的网络操作，如RPC、Redis、Http请求，这些方法运行时间比较长，或者剥离到事务方法外
     * 3：不是所有的方法都需要事务，如只有一条修改操作，只读操作不需要事务控制。
     *
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     * @throws SeckillException
     * @throws RepeatKillException
     * @throws SeckillClosedException
     */
    @Override
    @Transactional
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException, RepeatKillException, SeckillClosedException {
        try {
            if (md5 == null || !md5.equals(getMD5(seckillId))){
                throw new SeckillException("seckill data rewrite");
            }
            /**
             * 先减库存，在写明细，性能不如先写明细，后减库存
             * 性能提升一倍，插入是不容易产生冲突，是否减库存要根据插入是否成功，延迟少了一半
             */
            // 先减库存，热点商品竞争
//            int update = seckillDao.reduceNumber(seckillId, new Date());
//            if (update <= 0){
//                // 没有更新到记录,秒杀结束
//                throw new SeckillClosedException("seckill is closed");
//            }else {
//                // 再记录购买行为
//                int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
//                // 唯一：seckillId，userPhone
//                if (insertCount <= 0){
//                    // 重复秒杀
//                    throw new RepeatKillException("seckill repeat");
//                }else {
//                    // 秒杀成功
//                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
//                    return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
//                }
//            }

            /**
             * 先写明细，再减库存
             */
            int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
            if (insertCount <= 0){
                // 重复秒杀
                throw new RepeatKillException("seckill repeat");
            }else {
                int update = seckillDao.reduceNumber(seckillId, new Date());
                if (update <= 0){
                    // 没有更新到记录,秒杀结束
                    throw new SeckillClosedException("seckill is closed");
                }else {
                    // 秒杀成功
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
                }
            }
        }catch (SeckillClosedException e1){
            throw e1;
        }catch (RepeatKillException e2){
            throw  e2;
        }catch (Exception e){
            LOGGER.error(e.getMessage(), e);
            // 所有编译期异常 转化为运行期异常
            throw new SeckillException("seckill inner error:" + e.getMessage());
        }

    }

    @Override
    public SeckillExecution executeSeckillByProcedure(long seckillId, long userPhone, String md5) {
        if (md5 == null || !md5.equals(getMD5(seckillId))){
            return new SeckillExecution(seckillId, SeckillStateEnum.DATA_REWRITE);
        }
        Date killTime = new Date();
        Map<String, Object> map = new HashMap<>(4);
        map.put("seckillId", seckillId);
        map.put("phone", userPhone);
        map.put("killTime", killTime);
        map.put("result", null);
        try {
            seckillDao.killByProcedure(map);
            int result = MapUtils.getInteger(map, "result", -2);
            if (result == 1){
                SuccessKilled successKilled = successKilledDao
                        .queryByIdWithSeckill(seckillId, userPhone);
                return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
            }else {
                return new SeckillExecution(seckillId, SeckillStateEnum.stateOf(result));
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new SeckillExecution(seckillId, SeckillStateEnum.INNER_ERROR);
        }
    }
}
