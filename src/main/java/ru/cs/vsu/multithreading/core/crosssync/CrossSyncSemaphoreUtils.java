package ru.cs.vsu.multithreading.core.crosssync;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisClientConfig;
import ru.cs.vsu.multithreading.core.crosssync.redis.CrossSemaphoreRedisDao;

public class CrossSyncSemaphoreUtils {

    private static volatile CrossSemaphoreDao DAO_INSTANCE;
    private static final CrossSemaphoreRedisDaoBuilder BUILDER_INSTANCE = new CrossSemaphoreRedisDaoBuilder();

    public static CrossSemaphoreDao getDefaultDaoInstance() {
        if (DAO_INSTANCE == null) {
            DAO_INSTANCE = BUILDER_INSTANCE.build();
        }
        return DAO_INSTANCE;
    }

    public static class CrossSemaphoreRedisDaoBuilder {
        private Jedis jedis;
        private String host;
        private int port;
        private JedisClientConfig clientConfig;

        public CrossSemaphoreRedisDao build() {
            if (jedis != null)
                return new CrossSemaphoreRedisDao(jedis);
            if (host != null && port > 0 && clientConfig != null)
                return new CrossSemaphoreRedisDao(new Jedis(host, port, clientConfig));
            if (host != null && port > 0)
                return new CrossSemaphoreRedisDao(new Jedis(host, port));
            return new CrossSemaphoreRedisDao(new Jedis());
        }

        public Jedis getJedis() {
            return jedis;
        }

        public void setJedis(Jedis jedis) {
            this.jedis = jedis;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public JedisClientConfig getClientConfig() {
            return clientConfig;
        }

        public void setClientConfig(JedisClientConfig clientConfig) {
            this.clientConfig = clientConfig;
        }

        public CrossSemaphoreRedisDaoBuilder withHost(String host) {
            this.host = host;
            return this;
        }

        public CrossSemaphoreRedisDaoBuilder withPort(int port) {
            this.port = port;
            return this;
        }

        public CrossSemaphoreRedisDaoBuilder withJedis(Jedis jedis) {
            this.jedis = jedis;
            return this;
        }

        public CrossSemaphoreRedisDaoBuilder withJedisClientConfig(JedisClientConfig clientConfig) {
            this.clientConfig = clientConfig;
            return this;
        }
    }
}
