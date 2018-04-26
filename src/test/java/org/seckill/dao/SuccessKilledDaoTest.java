package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.SuccessKilled;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * @author tuyu
 *         Stay Hungry, Stay Foolish.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest {

    @Resource
    private SuccessKilledDao successKilledDao;


    /**
     * 第一次运行，结果是1
     * 第二次运行，结果是0
     * 联合主键 + insert ignore = 插入成功返回>1的数，插入失败返回0
     * @throws Exception
     */
    @Test
    public void insertSuccessKilled() throws Exception {
        long id = 1001L;
        long userPhone = 12592102220L;
        int i = successKilledDao.insertSuccessKilled(id, userPhone);
        System.out.println(i);
    }

    @Test
    public void queryByIdWithSeckill() throws Exception {
        long seckillId = 1001L;
        long userPhone = 12592102220L;
        SuccessKilled i = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
        System.out.println(i);
        System.out.println(i.getSeckill());
    }

}