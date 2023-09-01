package org.live.id.generate.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.live.id.generate.dao.mapper.IdGenerateMapper;
import org.live.id.generate.dao.po.IdGeneratePO;
import org.live.id.generate.service.IdGenerateService;
import org.live.id.generate.service.bo.LocalSeqIdBo;
import org.live.id.generate.service.bo.LocalUnSeqIdBo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Slf4j
public class IdGenerateServiceImpl implements IdGenerateService, InitializingBean {

    private static Map<Integer, LocalSeqIdBo> localSeqIdBoMap = new ConcurrentHashMap<>();
    private static Map<Integer, LocalUnSeqIdBo> localUnSeqIdBoMap = new ConcurrentHashMap<>();
    private static final float UPDATE_RATE = 0.75f;
    private static final int SEQ_ID = 1;
    //线程限流
    private static Map<Integer,Semaphore> semaphoreMap = new ConcurrentHashMap<>();

    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(8,16,3, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000),
        new ThreadFactory(){
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("id-generate-thread-" + ThreadLocalRandom.current().nextInt(1000));
                return thread;
            }
        }
    );

    @Resource
    private IdGenerateMapper idGenerateMapper;

    @Override
    public Long getUnSeqId(Integer id) {
        if (id == null) {
            log.error("[getUnSeqId] id is error,id is null");
            return null;
        }
        //从本地缓存获取当前id
        LocalUnSeqIdBo localUnSeqIdBo = localUnSeqIdBoMap.get(id);
        if (localUnSeqIdBo == null) {
            log.error("[getUnSeqId] localUnSeqIdBo is error,id is null");
            return null;
        }
        Long returnId = localUnSeqIdBo.getIdQueue().poll();
        if (returnId == null){
            log.error("[getUnSeqId] localUnSeqIdBo is error,id is null");
            return null;
        }
        this.refreshLocalUnSeqId(localUnSeqIdBo);
        return returnId;
    }


    @Override
    public Long getSeqId(Integer id) {
        if (id == null) {
            log.error("[getSeqId] id is error,id is null");
            return null;
        }
        //从本地缓存获取当前id
        LocalSeqIdBo localSeqIdBo = localSeqIdBoMap.get(id);
        if (localSeqIdBo == null) {
            log.error("[getSeqId] localSeqIdBo is error,id is null");
            return null;
        }
        //判断该id段已用id数是否大于75%了，大于则更新id段
        this.refreshLocalSeqId(localSeqIdBo);
        //当前id+1则为可用id，并且返回结果
        long returnId = localSeqIdBo.getCurrentNum().getAndIncrement();
        if (returnId > localSeqIdBo.getNextThreshold()){
            log.error("[getSeqId] id is over limit,id is null");
            return null;
        }
        return returnId;
    }

    /**bean初始化的时候会回调到这里
     * 加载本地id段缓存
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<IdGeneratePO> idGeneratePOList = idGenerateMapper.selectAll();
        for (IdGeneratePO idGeneratePO : idGeneratePOList) {
            log.info("服务刚启动，抢占新的id段");
            tryUpdateMySqlRecord(idGeneratePO);
            // 初始化指定1，代表一次只允许一个线程通过
            semaphoreMap.put(idGeneratePO.getId(),new Semaphore(1));
        }
    }

    /**
     * 刷新本地有序id段
     * @param localSeqIdBo
     */
    private void refreshLocalSeqId(LocalSeqIdBo localSeqIdBo) {
        long step = localSeqIdBo.getNextThreshold() - localSeqIdBo.getCurrentStart();
        //判断该id段已用id数是否大于75%了，大于则更新id段
        if (localSeqIdBo.getCurrentNum().get() - localSeqIdBo.getCurrentStart() > step * UPDATE_RATE){
            Semaphore semaphore = semaphoreMap.get(localSeqIdBo.getId());
            if (semaphore == null){
                log.error("semaphore is null, id is {}",localSeqIdBo.getId());
                return;
            }
            if (semaphore.tryAcquire()){
                log.info("开始尝试进行本地id段同步");
                //异步进行同步id操作
                threadPoolExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            IdGeneratePO idGeneratePO = idGenerateMapper.selectById(localSeqIdBo.getId());
                            tryUpdateMySqlRecord(idGeneratePO);
                            log.info("本地id段的同步完成,id is {}",localSeqIdBo.getId());
                        }catch (Exception e){
                            log.error("[refreshLocalUnSeqId] error is ",e);
                        }finally {
                            semaphoreMap.get(localSeqIdBo.getId()).release();
                        }
                    }
                });
            }
        }
    }

    /**
     * 刷新本地无序id段
     * @param localUnSeqIdBo
     */
    private void refreshLocalUnSeqId(LocalUnSeqIdBo localUnSeqIdBo) {
        long begin = localUnSeqIdBo.getCurrentStart();
        long end = localUnSeqIdBo.getNextThreshold();
        long remainSize = localUnSeqIdBo.getIdQueue().size();
        //如果使用剩余空间不足25%，则进行刷新
        if ((end - begin) * 0.25 > remainSize){
            Semaphore semaphore = semaphoreMap.get(localUnSeqIdBo.getId());
            if (semaphore == null){
                log.error("semaphore is null, id is {}",localUnSeqIdBo.getId());
                return;
            }
            if (semaphore.tryAcquire()){
                threadPoolExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            IdGeneratePO idGeneratePO = idGenerateMapper.selectById(localUnSeqIdBo.getId());
                            tryUpdateMySqlRecord(idGeneratePO);
                            log.info("无序id段同步完成，id is {}",localUnSeqIdBo.getId());
                        }catch (Exception e){
                            log.error("[refreshLocalUnSeqId] error is ",e);
                        }finally {
                            semaphoreMap.get(localUnSeqIdBo.getId()).release();
                        }

                    }
                });
            }
        }
    }


    /**
     * 更新mysql里面的分布式id配置信息，占用相应的id段
     * 同步执行，很多网络io，性能较慢
     * @param idGeneratePO
     */
    private void tryUpdateMySqlRecord(IdGeneratePO idGeneratePO){

        int updateResult = idGenerateMapper.updateNewIdCountAndVersion(idGeneratePO.getId(),idGeneratePO.getVersion());
        if (updateResult > 0){
            localBOHandler(idGeneratePO);
            return;
        }
        //重试机制
        for (int i = 0; i < 3; i++) {
            idGeneratePO = idGenerateMapper.selectById(idGeneratePO.getId());
            updateResult = idGenerateMapper.updateNewIdCountAndVersion(idGeneratePO.getId(),idGeneratePO.getVersion());
            if (updateResult > 0){
                localBOHandler(idGeneratePO);
                break;
            }
        }
        throw new RuntimeException("表id段占用失败，竞争过于激烈，id is " + idGeneratePO.getId());
    }

    /**
     * 专门处理如何将本地ID对象放入Map中，并且进行初始化
     * @param idGeneratePO
     */
    private void localBOHandler(IdGeneratePO idGeneratePO){
        long currentStart = idGeneratePO.getCurrentStart();
        long nextThreshold = idGeneratePO.getNextThreshold();
        if (idGeneratePO.getIsSeq() == SEQ_ID){
            LocalSeqIdBo localSeqIdBo = new LocalSeqIdBo();
            AtomicLong atomicLong = new AtomicLong(currentStart);
            localSeqIdBo.setId(idGeneratePO.getId());
            localSeqIdBo.setCurrentNum(atomicLong);
            localSeqIdBo.setCurrentStart(currentStart);
            localSeqIdBo.setNextThreshold(nextThreshold);
            localSeqIdBoMap.put(localSeqIdBo.getId(), localSeqIdBo);
        }else {
            LocalUnSeqIdBo localUnSeqIdBo = new LocalUnSeqIdBo();
            localUnSeqIdBo.setId(idGeneratePO.getId());
            localUnSeqIdBo.setCurrentStart(currentStart);
            localUnSeqIdBo.setNextThreshold(nextThreshold);
            List<Long> idList = new ArrayList<>();
            for (long i = localUnSeqIdBo.getCurrentStart();i < localUnSeqIdBo.getNextThreshold();i++){
                idList.add(i);
            }
            //将本地id打乱，放入队列
            Collections.shuffle(idList);
            ConcurrentLinkedQueue<Long> idQueue = new ConcurrentLinkedQueue<>(idList);
            localUnSeqIdBo.setIdQueue(idQueue);
            localUnSeqIdBoMap.put(localUnSeqIdBo.getId(), localUnSeqIdBo);
        }
    }
}
