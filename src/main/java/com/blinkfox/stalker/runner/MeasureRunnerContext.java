package com.blinkfox.stalker.runner;

import com.blinkfox.IdWorker;
import com.blinkfox.stalker.config.Options;
import com.blinkfox.stalker.kit.StrKit;
import com.blinkfox.stalker.result.MeasurementCollector;
import com.blinkfox.stalker.result.bean.Measurement;
import com.blinkfox.stalker.result.bean.OverallResult;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

/**
 * 待执行的实例方法在各种线程情形中运行实现类的上下文.
 *
 * @author blinkfox on 2019-01-08.
 * @since v1.0.0
 */
@Slf4j
public final class MeasureRunnerContext {

    /**
     * 用于异步提交任务的线程池.
     */
    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(1,
            3, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<>(5));

    /**
     * 用来存储 {@link MeasureRunner} 的容器，Key 是运行时的 sessionId, value 是 {@link MeasureRunner} 的实例.
     */
    private static final Map<String, MeasureRunner> measureMap = new ConcurrentHashMap<>();

    /**
     * 使用雪花算法生成短 ID 的生成器.
     */
    public static final IdWorker idWorker = new IdWorker();

    /**
     * 运行测量的性能参数配置选项.
     */
    private final Options options;

    /**
     * 基于Options的构造方法，其中需要对options参数的合法性做校验.
     *
     * @param options 参数选项
     */
    public MeasureRunnerContext(Options options) {
        options.valid();
        this.options = options;
    }

    /**
     * 正式测量前所需要进行预热的方法.
     *
     * @param options 参数选项
     * @param runnable runnable
     */
    private static void warmup(Options options, Runnable runnable) {
        final boolean printErrorLog = options.isPrintErrorLog();
        log.debug("【stalker 提示】预热开始...");
        long start = System.nanoTime();

        // 循环执行预热次数的方法.
        for (int i = 0, len = options.getWarmups(); i < len; i++) {
            try {
                runnable.run();
            } catch (RuntimeException e) {
                // 预热时的异常日志，根据配置选项的参数值来看是否输出错误日志.
                if (printErrorLog) {
                    log.error("【stalker 错误】测量方法前进行预热时出错!", e);
                }
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("【stalker 提示】预热完毕，预热期间耗时: {}.", StrKit.convertTime(System.nanoTime() - start));
        }
    }

    /**
     * 检查Options参数是否合法，并进行预热准备，然后执行 runnable 方法，并将执行结果的耗时纳秒(ns)值存入到集合中.
     *
     * @param runnable 可运行实例
     * @return 运行的测量结果
     */
    public OverallResult run(Runnable runnable) {
        warmup(options, runnable);
        return options.getThreads() > 1 && options.getConcurrens() > 1
                ? new ConcurrentMeasureRunner().run(options, runnable)
                : new SimpleMeasureRunner().run(options, runnable);
    }

    /**
     * 检查Options参数是否合法，并进行预热准备，然后执行 runnable 方法，并将执行结果的耗时纳秒(ns)值存入到集合中.
     *
     * @param runnable 可运行实例
     * @return 运行的经过测量结果
     * @author blinkfox on 2020-05-23.
     * @since v1.2.0
     */
    public Measurement runAndCollect(Runnable runnable) {
        return new MeasurementCollector().collect(this.run(runnable));
    }

    /**
     * 检查Options参数是否合法，并进行预热准备，然后执行 runnable 方法，并将执行结果的耗时纳秒(ns)值存入到集合中.
     *
     * @param options 运行的选项参数
     * @param runnable 可运行实例
     * @return 此次运行的会话 ID
     * @author blinkfox on 2020-05-23.
     * @since v1.2.0
     */
    public static String submit(final Options options, final Runnable runnable) {
        // 预热运行.
        warmup(options, runnable);

        // 将 measureRunner 存储到 map 中，并异步执行任务.
        MeasureRunner measureRunner = options.getThreads() > 1 && options.getConcurrens() > 1
                ? new ConcurrentMeasureRunner()
                : new SimpleMeasureRunner();
        String sessionId = idWorker.get62RadixId();
        measureMap.put(sessionId, measureRunner);
        executor.execute(() -> measureRunner.run(options, runnable));
        return sessionId;
    }

    /**
     * 根据运行的测量会话 ID，来查询该会话所对应的测量结果.
     *
     * @param sessionId 会话 ID
     * @return 测量结果
     * @author blinkfox on 2020-05-23.
     * @since v1.2.0
     */
    public static Measurement submit(String sessionId) {
        return null;
    }

}
