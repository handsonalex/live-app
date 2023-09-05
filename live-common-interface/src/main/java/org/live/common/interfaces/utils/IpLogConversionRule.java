package org.live.common.interfaces.utils;

import ch.qos.logback.core.PropertyDefinerBase;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author :Joseph Ho
 * Description: 保证每个docker容器的日志挂载目录唯一性
 * Date: 15:41 2023/9/5
 */
public class IpLogConversionRule extends PropertyDefinerBase {
    @Override
    public String getPropertyValue() {
        return this.getLogIndex();
    }

    private String getLogIndex() {
        try{
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e){
            e.printStackTrace();
        }
        return String.valueOf(ThreadLocalRandom.current().nextInt(100000));
    }
}
