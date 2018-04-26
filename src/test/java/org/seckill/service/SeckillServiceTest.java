package org.seckill.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillClosedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author tuyu
 *         Stay Hungry, Stay Foolish.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-*.xml"})
public class SeckillServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeckillServiceTest.class);

    @Autowired
    SeckillService seckillService;

    @Test
    public void getSeckillList() throws Exception {
        List<Seckill> seckillList = seckillService.getSeckillList();
        LOGGER.info("list = {}", seckillList);
    }

    @Test
    public void getById() throws Exception {
        long id = 1000L;
        Seckill byId = seckillService.getById(id);
        LOGGER.info("seckill = {}", byId);
    }

    /**
     * 测试代码完整逻辑，注意可以再次执行。
     * @throws Exception
     */
    @Test
    public void testSeckillLogic() throws Exception {
        long id = 1001L;
        // expose = Exposer{exposed=true, md5='2d1121f93fd131fed08dc8e15fe74ca3', seckillId=1000, now=0, start=0, end=0}
        Exposer exposer = seckillService.exportSeckillUrl(id);
        if (exposer.isExposed()){
            LOGGER.info("expose = {}", exposer);
            long phone = 12121212122L;
            String md5 = exposer.getMd5();
            try {
                SeckillExecution seckillExecution = seckillService.executeSeckill(id, phone, md5);
                LOGGER.info("exec = {}", seckillExecution);
            }catch (RepeatKillException e1){
                LOGGER.error(e1.getMessage());
            }catch (SeckillClosedException e2){
                LOGGER.error(e2.getMessage());
            }
        }else {
            // 秒杀未开启
            LOGGER.warn("exposer = {}", exposer);
        }

    }
}