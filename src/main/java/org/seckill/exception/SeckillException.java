package org.seckill.exception;

/**
 * @author tuyu
 *         Stay Hungry, Stay Foolish.
 */
public class SeckillException extends RuntimeException {
    public SeckillException(String message) {
        super(message);
    }

    public SeckillException(String message, Throwable cause) {
        super(message, cause);
    }
}
