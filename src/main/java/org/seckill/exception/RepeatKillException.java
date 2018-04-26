package org.seckill.exception;

/**
 * 重复秒杀异常
 * @author tuyu
 *         Stay Hungry, Stay Foolish.
 */
public class RepeatKillException extends SeckillException {
    public RepeatKillException(String message) {
        super(message);
    }

    public RepeatKillException(String message, Throwable cause) {
        super(message, cause);
    }
}
