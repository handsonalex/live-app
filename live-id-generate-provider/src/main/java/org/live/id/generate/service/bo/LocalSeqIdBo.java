package org.live.id.generate.service.bo;

import lombok.Data;

import java.util.concurrent.atomic.AtomicLong;

@Data
public class LocalSeqIdBo {

    private int id;

    /**
     * 在内存中记录的当前有序id的值
     */
    private AtomicLong currentNum;

    /**
     * 当前id段的开始
     */
    private Long currentStart;


    /**
     * 当前id段的结束
     */
    private Long nextThreshold;


}
