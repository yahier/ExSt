package yahier.exst.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by lr on 2016/1/22.
 */
public class ThreadPool {

    private static volatile ThreadPool instance;

    public static ThreadPool getInstance() {
        if (instance == null) {
            synchronized (ThreadPool.class) {
                if (instance == null) {
                    instance = new ThreadPool();
                }
            }
        }
        return instance;
    }

    private ExecutorService mExecutorService;

    private ThreadPool() {
        mExecutorService = Executors.newCachedThreadPool();
    }

    public void execute(Runnable runnable) {
        mExecutorService.execute(runnable);
    }
}
