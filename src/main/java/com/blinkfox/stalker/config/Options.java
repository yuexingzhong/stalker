package com.blinkfox.stalker.config;

import com.blinkfox.stalker.output.MeasureOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.Getter;

/**
 * 性能测试参数选项实体类.
 *
 * @author blinkfox on 2019-01-02.
 * @since v1.0.0
 */
@Getter
public class Options {

    /**
     * 进行测量的名称，便于和其他执行的测量作区分.
     */
    private String name;

    /**
     * 执行的线程数.
     */
    private int threads;

    /**
     * 执行的并发数.
     */
    private int concurrens;

    /**
     * 执行前的预热次数.
     */
    private int warmups;

    /**
     * 执行的运行次数.
     */
    private int runs;

    /**
     * 运行的持续时间.
     *
     * @since v1.2.0
     */
    private RunDuration duration;

    /**
     * 是否打印出执行错误(异常运行)的日志，默认是false.
     */
    private boolean printErrorLog;

    /**
     * 将测量结果通过多种方式输出出来的集合.
     */
    private List<MeasureOutput> outputs;

    /**
     * 校验失败时的提示消息.
     */
    private String message;

    /**
     * 用于定时更新统计数据的定时更新器，通常在调用 {@code Stalker.submit} 的异步执行任务时才设置并开启此配置项，默认是空值.
     */
    private ScheduledUpdater scheduledUpdater;

    /**
     * 根据'执行次数'来构建Options实例.
     *
     * @return Options实例
     */
    public static Options of() {
        // 从全局唯一的配置实例中获取到默认的选项参数实例，并将默认的属性值赋给新的 Options 实例.
        Options defaultOptions = StalkerConfigManager.getInstance().getDefaultOptions();
        return new Options()
                .threads(defaultOptions.getThreads())
                .concurrens(defaultOptions.getConcurrens())
                .warmups(defaultOptions.getWarmups())
                .runs(defaultOptions.getRuns())
                .printErrorLog(defaultOptions.isPrintErrorLog())
                .outputs(defaultOptions.getOutputs());
    }

    /**
     * 根据'执行次数'来构建Options实例.
     *
     * @param name 名称
     * @return Options实例
     */
    public static Options of(String name) {
        Options options = of();
        options.name = name;
        return options;
    }

    /**
     * 根据'执行次数'来构建Options实例.
     *
     * @param runs 执行次数
     * @return Options实例
     */
    public static Options of(int runs) {
        Options options = of();
        options.runs = runs;
        return options;
    }

    /**
     * 根据'执行次数'来构建Options实例.
     *
     * @param name 名称
     * @param runs 执行次数
     * @return Options实例
     */
    public static Options of(String name, int runs) {
        Options options = of(name);
        options.runs = runs;
        return options;
    }

    /**
     * 根据'线程数'、'并发数'来构建Options实例.
     *
     * @param threads 线程数
     * @param concurrens 并发数
     * @return Options实例
     */
    public static Options of(int threads, int concurrens) {
        Options options = of();
        options.threads = threads;
        options.concurrens = concurrens;
        options.runs = 1;
        return options;
    }

    /**
     * 根据'线程数'、'并发数'来构建Options实例.
     *
     * @param name 名称
     * @param threads 线程数
     * @param concurrens 并发数
     * @return Options实例
     */
    public static Options of(String name, int threads, int concurrens) {
        Options options = of(threads, concurrens);
        options.name = name;
        return options;
    }

    /**
     * 根据'线程数'、'并发数'、每个线程的'执行次数'来构建Options实例.
     *
     * @param name 名称
     * @param threads 线程数
     * @param concurrens 并发数
     * @param runs 每个线程的执行次数
     * @return Options实例
     */
    public static Options of(String name, int threads, int concurrens, int runs) {
        Options options = of(name, threads, concurrens);
        options.runs = runs;
        return options;
    }

    /**
     * 根据'持续时间的量'、'持续时间的单位'来构建 Options 实例.
     *
     * @param amount 运行持续时间的量
     * @param timeUnit 运行持续时间的单位
     * @return Options实例
     * @author blinkfox on 2020-06-01.
     * @since v1.2.0
     */
    public static Options ofDuration(long amount, TimeUnit timeUnit) {
        Options options = of(1, 1);
        options.runs = 1;
        options.duration = RunDuration.of(amount, timeUnit);
        return options;
    }

    /**
     * 根据'持续时间的量'、'持续时间的单位'和'并发数'来构建 Options 实例.
     *
     * @param amount 运行持续时间的量
     * @param timeUnit 运行持续时间的单位
     * @param concurrens 并发数
     * @return Options实例
     * @author blinkfox on 2020-06-01.
     * @since v1.2.0
     */
    public static Options ofDuration(long amount, TimeUnit timeUnit, int concurrens) {
        Options options = of(1, 1);
        options.concurrens = concurrens;
        options.runs = 1;
        options.duration = RunDuration.of(amount, timeUnit);
        return options;
    }

    /**
     * 根据'持续秒数的量'和'并发数'来构建 Options 实例.
     *
     * @param amount 运行持续秒数的量
     * @param concurrens 并发数
     * @return Options实例
     * @author blinkfox on 2020-06-01.
     * @since v1.2.0
     */
    public static Options ofDurationSeconds(long amount, int concurrens) {
        return ofDuration(amount, TimeUnit.SECONDS, concurrens);
    }

    /**
     * 根据'持续分钟数的量'和'并发数'来构建 Options 实例.
     *
     * @param amount 运行持续分钟数的量
     * @param concurrens 并发数
     * @return Options实例
     * @author blinkfox on 2020-06-01.
     * @since v1.2.0
     */
    public static Options ofDurationMinutes(long amount, int concurrens) {
        return ofDuration(amount, TimeUnit.MINUTES, concurrens);
    }

    /**
     * 根据'持续小时数的量'和'并发数'来构建 Options 实例.
     *
     * @param amount 运行持续小时数的量
     * @param concurrens 并发数
     * @return Options实例
     * @author blinkfox on 2020-06-01.
     * @since v1.2.0
     */
    public static Options ofDurationHours(long amount, int concurrens) {
        return ofDuration(amount, TimeUnit.HOURS, concurrens);
    }

    /**
     * 根据'持续天数的量'和'并发数'来构建 Options 实例.
     *
     * @param amount 运行持续小时数的量
     * @param concurrens 并发数
     * @return Options实例
     * @author blinkfox on 2020-06-01.
     * @since v1.2.0
     */
    public static Options ofDurationDays(long amount, int concurrens) {
        return ofDuration(amount, TimeUnit.DAYS, concurrens);
    }

    /**
     * 校验需要进行测量的 Options 选项参数是否合法，如果不合法，则抛出异常.
     */
    public void valid() {
        // 对各个选项参数进行检查、校验，失败时抛出异常.
        if (this.verify(this.getThreads() <= 0, "Options 中的线程数 threads 的值必须大于0.")
                || this.verify(this.getConcurrens() <= 0, "Options 中的线程数 concurrens 的值必须大于0.")
                || this.verify(this.getWarmups() < 0, "Options 中的线程数 warmups 的值必须大于0.")
                || this.verify(this.getRuns() <= 0, "Options 中的线程数 runs 的值必须大于0.")) {
            throw new IllegalArgumentException(this.message);
        }
    }

    /**
     * 检查结果是否为false，如果为false，则记录message提示信息.
     *
     * @param condition 检查结果
     * @param message 提示信息
     * @return 布尔值
     */
    private boolean verify(boolean condition, String message) {
        if (condition) {
            this.message = message;
        }
        return condition;
    }

    /**
     * 设置测量名称 name 的属性值.
     *
     * @param name 线程数
     * @return Options实例
     */
    public Options named(String name) {
        this.name = name;
        return this;
    }

    /**
     * 设置线程数 threads 的属性值.
     *
     * @param threads 线程数
     * @return Options实例
     */
    public Options threads(int threads) {
        this.threads = threads;
        return this;
    }

    /**
     * 设置线程数 concurrens 的属性值.
     *
     * @param concurrens 并发数
     * @return Options实例
     */
    public Options concurrens(int concurrens) {
        this.concurrens = concurrens;
        return this;
    }

    /**
     * 设置预热次数的 warmups 的属性值.
     *
     * @param warmups 热加载次数
     * @return Options实例
     */
    public Options warmups(int warmups) {
        this.warmups = warmups;
        return this;
    }

    /**
     * 设置执行次数的 runs 的属性值.
     *
     * @param runs 运行次数
     * @return Options 实例
     */
    public Options runs(int runs) {
        this.runs = runs;
        return this;
    }

    /**
     * 设置运行的持续时间.
     *
     * @param amount 持续时间的量
     * @param timeUnit 持续时间的单位
     * @return Options 实例
     * @author blinkfox on 2020-06-01.
     * @since v1.2.0
     */
    public Options duration(long amount, TimeUnit timeUnit) {
        this.duration = RunDuration.of(amount, timeUnit);
        return this;
    }

    /**
     * 设置是否打印运行错误的日志的 printErrorLog 的属性值.
     *
     * @param printErrorLog 是否打印异常运行的日志
     * @return Options实例
     */
    public Options printErrorLog(boolean printErrorLog) {
        this.printErrorLog = printErrorLog;
        return this;
    }

    /**
     * 设置需要将结果输出的通道.
     *
     * @param measureOutputs 输出通道的集合.
     * @return 本 {@link Options} 实例
     */
    public Options outputs(MeasureOutput... measureOutputs) {
        if (this.outputs == null) {
            this.outputs = new ArrayList<>();
        }

        this.outputs.addAll(Arrays.asList(measureOutputs));
        return this;
    }

    /**
     * 设置需要将结果输出的通道.
     *
     * @param outputs 输出通道的集合.
     * @return 本 {@link Options} 实例
     */
    public Options outputs(List<MeasureOutput> outputs) {
        this.outputs = outputs;
        return this;
    }

    /**
     * 设置默认的定时统计数据更新任务的配置选项，默认是 10 秒.
     *
     * @return 本 {@link Options} 实例
     */
    public Options enableScheduledUpdater() {
        ScheduledUpdater updater = StalkerConfigManager.getInstance().getDefaultScheduledUpdater();
        this.scheduledUpdater = ScheduledUpdater.of(true, updater.getInitialDelay(),
                updater.getDelay(), updater.getTimeUnit());
        return this;
    }

    /**
     * 设置默认的定时统计数据更新任务的配置选项.
     *
     * @param delay 时间间隔
     * @param timeUnit 时间单位
     * @return 本 {@link Options} 实例
     */
    public Options enableScheduledUpdater(long delay, TimeUnit timeUnit) {
        this.scheduledUpdater = ScheduledUpdater.of(delay, timeUnit);
        return this;
    }

    /**
     * 设置默认的定时统计数据更新任务的配置选项.
     *
     * @param initialDelay 第一次的延迟执行时间
     * @param delay 时间间隔
     * @param timeUnit 时间单位
     * @return 本 {@link Options} 实例
     */
    public Options enableScheduledUpdater(long initialDelay, long delay, TimeUnit timeUnit) {
        this.scheduledUpdater = ScheduledUpdater.of(true, initialDelay, delay, timeUnit);
        return this;
    }

}
