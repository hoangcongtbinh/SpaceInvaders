package uet.oop.space_invaders.spaceinvaders;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Supplier;

public class ObjectPool<T extends GameObject> {
    private final Queue<T> pool = new LinkedList<>();
    private final Supplier<T> init;

    public ObjectPool(Supplier<T> init) {
        this.init = init;
    }

    public T get() {
        T obj = pool.poll();
        if (obj == null) obj = init.get();
        obj.reset();
        return obj;
    }

    public void release(T obj) {
        pool.offer(obj);
    }
}
