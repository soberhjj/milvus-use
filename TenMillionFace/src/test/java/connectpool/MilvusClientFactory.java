package connectpool;

import io.milvus.client.ConnectParam;
import io.milvus.client.MilvusClient;
import io.milvus.client.MilvusGrpcClient;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * @Author: huangJunJie  2021-04-14 15:14
 */
public class MilvusClientFactory implements PooledObjectFactory<MilvusClient> {
    /**
     * 创建连接
     * @return
     * @throws Exception
     */
    @Override
    public PooledObject<MilvusClient> makeObject() throws Exception {
        return new DefaultPooledObject<>(new MilvusGrpcClient(new ConnectParam.Builder().withHost("192.168.136.222").withPort(19540).keepAliveWithoutCalls(true).build())) ;
    }

    /**
     * 销毁连接
     * @param pooledObject
     * @throws Exception
     */
    @Override
    public void destroyObject(PooledObject<MilvusClient> pooledObject) throws Exception {
        pooledObject.getObject().close();
    }

    /**
     * 验证连接是否可用。
     * @param pooledObject
     * @return
     */
    @Override
    public boolean validateObject(PooledObject<MilvusClient> pooledObject) {
        return true;
    }

    /**
     * 激活不可用连接
     * @param pooledObject
     * @throws Exception
     */
    @Override
    public void activateObject(PooledObject pooledObject) throws Exception {

    }

    @Override
    public void passivateObject(PooledObject pooledObject) throws Exception {

    }
}
