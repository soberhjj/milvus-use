package connectpool;

import io.milvus.client.MilvusClient;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.AbandonedConfig;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * @Author: huangJunJie  2021-04-14 15:33
 */
public class MilvusClientPool extends GenericObjectPool<MilvusClient> {

    public MilvusClientPool(PooledObjectFactory<MilvusClient> factory) {
        super(factory);
    }

    public MilvusClientPool(PooledObjectFactory<MilvusClient> factory, GenericObjectPoolConfig<MilvusClient> config) {
        super(factory, config);
    }

    public MilvusClientPool(PooledObjectFactory<MilvusClient> factory, GenericObjectPoolConfig<MilvusClient> config, AbandonedConfig abandonedConfig) {
        super(factory, config, abandonedConfig);
    }
}
