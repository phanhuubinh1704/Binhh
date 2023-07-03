package ConnectDB;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Connection;

public class ConncetRedis {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("redis://default:tAoljg4N4LvV7gxFTiEWsR053wKw1epJ@redis-10751.c299.asia-northeast1-1.gce.cloud.redislabs.com:10751");
        Connection connection = jedis.getConnection();
        
        String Binh =  jedis.get("Binh");
        
        
        System.out.println("Binh: " + Binh);
        
        
        jedis.close();
        
    }
}
