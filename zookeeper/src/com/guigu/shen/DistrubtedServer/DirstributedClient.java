package com.guigu.shen.DistrubtedServer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public class DirstributedClient {


	
	private ZooKeeper zooKeeper = null;
	private static final String connectionsting = "192.168.153.129:2181,192.168.153.128:2181,192.168.153.130:2181";
	private static final int sessionTimeOut = 2000;// 两秒钟
	private static final String parentNode = "/server";

	// 这边用volatile修饰
	// 为什么要用这个修饰呢，因为我的serverlist相赋予的值其实就是方法
	// getServicelist()里面的servers的值，但是我的serverlist的使用与servers
	// 不在一个线程里面，那就会有内存可见性的问题。我在这边加一个volatile，直接内存可见了。
	private volatile List<String> serverlist;

	// 1:获取zk连接
	public void getConnect() throws Exception {

		zooKeeper = new ZooKeeper(connectionsting, sessionTimeOut, new Watcher() {
			@Override
			public void process(WatchedEvent event) {
				// 为了实现一直监听的功能，更新了服务器列表，重新监听
				try {
					getServicelist();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		Thread.sleep(10000);
	}

	// 获取服务器的方法
	public void getServicelist() throws Exception {
		// 获取服务器子节点信息，并且对父节点进行监听，看子节点有没有变化
		List<String> children = zooKeeper.getChildren(parentNode, true);

		// 先创建一个局部的list来存服务器信息
		List<String> servers = new ArrayList<String>();

		for (Iterator<String> iterator = children.iterator(); iterator.hasNext();) {

			// 得到节点里面的值，也就是客户端应该去访问的IP地址。
			byte[] data = zooKeeper.getData(parentNode +"/"+ iterator.next(), false, null);

			// 把服务器的子节点里面的值都遍历出来，放入我们的服务器变量。
			// 这个值其实是给客户端来访问的
			System.out.println("服务器列表"+new String(data));
			servers.add(new String(data));
			// 把servers赋值给成员变量，以提供给各业务线程使用
			serverlist = servers;

		}

	}


	public void handlebusiness() {
		System.out.println("客户端执行业务功能");
		try {

			// 一直睡
			Thread.sleep(Long.MAX_VALUE);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}





	public static void main(String[] args) throws Exception {
		// 客户端要做的事情：
		// 1：获取zk连接
		// 2：获取servers的子节点信息（并监听），从中获取服务器信息列表
		// 3:业务线程，去访问服务器
		DirstributedClient dirstributedClient = new DirstributedClient();
		dirstributedClient.getConnect();
		// 获取服务器列表
		dirstributedClient.getServicelist();
	     dirstributedClient.handlebusiness();
	}
	
	
	
	
	

}
