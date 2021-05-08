package connectpool;

import io.milvus.client.MilvusClient;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * @Author: huangJunJie  2021-04-14 15:45
 */
public class GeneratePool {

    public static MilvusClientPool generate(){
        MilvusClientPoolProperties milvusClientPoolProperties = new MilvusClientPoolProperties();
        MilvusClientFactory milvusClientFactory=new MilvusClientFactory();
        GenericObjectPoolConfig<MilvusClient> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxIdle(milvusClientPoolProperties.getMaxIdle());
        poolConfig.setMaxTotal(milvusClientPoolProperties.getMaxTotal());
        poolConfig.setMinIdle(milvusClientPoolProperties.getMinIdle());

        MilvusClientPool milvusClientPool=new MilvusClientPool(milvusClientFactory,poolConfig);
        /**
         * 初始化
         */
        try {
            for (int i=0;i<10;i++){
                milvusClientPool.addObject();
            }
        } catch (Exception e) {
            System.out.println("初始化失败");
            e.printStackTrace();
        }
        return milvusClientPool;

    }


}
