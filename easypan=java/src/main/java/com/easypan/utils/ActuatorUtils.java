package com.easypan.utils;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ActuatorUtils {
    @Resource
    private MeterRegistry meterRegistry;

    /**
     * 记录接口调用次数
     *
     * @param method
     */
    public void incrementMonitorConut(String method, String description) {
        //定义指标名称
        String metricName = "method_count";
        Counter counter = meterRegistry.counter(metricName, "methodName", method, "description", description);
        counter.increment();
    }

    /**
     * 记录接口的RT
     *
     * @param method
     */
    public void recordMethodRt(String method, Long rt) {
        //定义指标名称
        String metricName = "method_rt";
        meterRegistry.timer(metricName, Tags.of("methodName", method)).record(rt, java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    /**
     * 记录接口资源消耗
     *
     * @param method
     */

}