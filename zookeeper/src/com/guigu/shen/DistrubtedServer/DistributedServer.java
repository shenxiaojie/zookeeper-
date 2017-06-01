package com.guigu.shen.DistrubtedServer;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;

public class DistributedServer {

	
	private ZooKeeper zooKeeper=null;
	private static final String connectionsting = "192.168.153.129:2181,192.168.153.128:2181,192.168.153.130:2181";
	private static final int sessionTimeOut = 2000;//两秒钟
	private static final String parentNode="/server";
	
	// 1:获取zk连接
	public  void getConnect() throws Exception
	{
		
		zooKeeper=new ZooKeeper(connectionsting, sessionTimeOut, new Watcher()
				{
					@Override
					public void process(WatchedEvent event) {
						
					}
				});
		
		Thread.sleep(10000);
	}
	
	//2:利用zk连接 注册服务器消息
	public void registerServer(String hostname) throws Exception
	{
		//创建临时节点，并且是带序号的节点。
	    String create=zooKeeper.create(parentNode+"/server", hostname.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
		System.out.println(hostname+"is online.."+create);
	}
	
	
	public void handlebusiness(String hostname)
	{
		System.out.println(hostname+"ִ执行业务功能");
		try {
			
			// 一直睡
			Thread.sleep(Long.MAX_VALUE);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public static void main(String[] args) throws Exception {
		// 要实现的逻辑
		// 1:获取zk连接
		// 2:利用zk连接 注册服务器消息
		// 3:启动业务
		DistributedServer distributedServer=new DistributedServer();
		//1:
		distributedServer.getConnect();
		//2:
		distributedServer.registerServer(args[0]);
		//3:
		distributedServer.handlebusiness(args[0]);
	}
	
	
	
	
	
}
