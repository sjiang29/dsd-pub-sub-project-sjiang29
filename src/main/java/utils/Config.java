package utils;

import java.util.HashMap;

/**
 * utils.Config class: class to store App or connection's config data
 */

public class Config {

    // host1, run on "localhost" , port: 8080
    public static HostInfo host1 = new HostInfo("broker", "localhost", 1851);
    // host2, run on "localhost" , port: 8090
    public static HostInfo host2 = new HostInfo("producer1", "localhost",1852);
    // host3, run on "mcvm011.cs.usfca.edu" , port: 1851
    public static HostInfo host3 = new HostInfo("producer2", "localhost",1853);
    // host4, run on "mcvm022.cs.usfca.edu" , port: 1852
    public static HostInfo host4 = new HostInfo("producer3", "localhost",1854);
    // host5, run on "mcvm033.cs.usfca.edu" , port: 1853
    public static HostInfo host5 = new HostInfo("consumer1", "localhost",1855);
    // host5, run on "mcvm044.cs.usfca.edu" , port: 1854
    public static HostInfo host6 = new HostInfo("consumer2", "localhost",1856);

    // HashMap for looking for a host information using its name
    public static final HashMap<String, HostInfo> hostList = new HashMap<String, HostInfo>()
    {{ put(host1.getHostName(), host1); put(host2.getHostName(), host2); put(host3.getHostName(), host3);
        put(host4.getHostName(), host4); put(host5.getHostName(), host5); put(host6.getHostName(), host6);}};

    //
    public static final String file1 = "/Users/sj/Desktop/Distributed Software Dev/Projects/p2/proxifier1.log";
    public static final String file2 = "/Users/sj/Desktop/Distributed Software Dev/Projects/p2/proxifier2.log";
    public static final String file3 = "/Users/sj/Desktop/Distributed Software Dev/Projects/p2/zookeeper1.log";
    public static final HashMap<String, String> producerAndFile = new HashMap<>(){{
        put(host2.getHostName(), file1); put(host3.getHostName(), file2); put(host4.getHostName(), file3);
    }};

    //
    public static final String topic1 = "proxifier";
    public static final String topic2 = "zookeeper";
    public static final HashMap<String, String> topics = new HashMap<>(){{
        put(file1, topic1); put(file2, topic1); put(file3, topic2);
    }};



}
