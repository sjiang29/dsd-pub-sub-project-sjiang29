package utils;

import java.util.HashMap;

/**
 * utils.Config class: class to store App or connection's config data
 */

public class Config {

    // host1, run on "localhost" , port: 8080
    public static HostInfo host1 = new HostInfo("host1", "localhost", 1851);
    // host2, run on "localhost" , port: 8090
    public static HostInfo host2 = new HostInfo("host2", "localhost",1852);
    // host3, run on "mcvm011.cs.usfca.edu" , port: 1851
    public static HostInfo host3 = new HostInfo("host3", "localhost",1853);
    // host4, run on "mcvm022.cs.usfca.edu" , port: 1852
    public static HostInfo host4 = new HostInfo("host4", "localhost",1854);
    // host5, run on "mcvm033.cs.usfca.edu" , port: 1853
    public static HostInfo host5 = new HostInfo("host5", "localhost",1855);
    // host5, run on "mcvm044.cs.usfca.edu" , port: 1854
    public static HostInfo host6 = new HostInfo("host6", "localhost",1856);

    // HashMap for looking for a host information using its name
    public static final HashMap<String, HostInfo> hostList = new HashMap<String, HostInfo>()
    {{ put(host1.getHostName(), host1); put(host2.getHostName(), host2); put(host3.getHostName(), host3);
        put(host4.getHostName(), host4); put(host5.getHostName(), host5); put(host6.getHostName(), host6);}};

    public static final int delay = 10;
    public static final double lossRate = 0.3;



}
