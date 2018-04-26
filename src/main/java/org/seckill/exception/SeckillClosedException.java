package org.seckill.exception;

/**
 * 秒杀关闭异常
 * @author tuyu
 *         Stay Hungry, Stay Foolish.
 */
public class SeckillClosedException extends SeckillException {
    public SeckillClosedException(String message) {
        super(message);
    }

    public SeckillClosedException(String message, Throwable cause) {
        super(message, cause);
    }
}
