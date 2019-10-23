package com.taomee.tms.mgr.utils;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 目前只对递归创建目录的path做了是否存在的检查，其他方法的暂时都没对这块进行检查
 * 
 * @author looper
 * @date 2017年6月19日 上午11:08:19
 * @project tms-log-schema-detector ZookeeperUtil
 */
public class ZKClientUtil {

	private Logger LOG = LoggerFactory.getLogger(ZKClientUtil.class);

	private static final int SESSION_TIMEOUT = 30000;

	private ZooKeeper zookeeper;

	private static String hosts;

	/**
	 * 监视器
	 */
	private Watcher watcher = new Watcher() {
		public void process(WatchedEvent event) {
			LOG.info("event:" + event.getState());
		}
	};

	/**
	 * 获取Zookeeper
	 * 
	 * @return
	 */
	public ZooKeeper getZookeeper() {
		return zookeeper;
	}

	/**
	 * 
	 * @param host
	 * @param path
	 */
	public ZKClientUtil(String _in_host) {

		hosts = _in_host;
		// path = _in_path;
	}

	/**
	 * 打开连接
	 * 
	 * @throws IOException
	 */

	public void connect() throws IOException {
		zookeeper = new ZooKeeper(hosts, SESSION_TIMEOUT, watcher);
	}

	/**
	 * 关闭连接,zk的关闭操作建议交给zk自己去维护。
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		if (zookeeper != null) {
			try {
				zookeeper.close();
			} catch (InterruptedException e) {
				System.out.println("zookeeper客户端关闭失败！");
			}
		}
	}

	/**
	 * 检查zookeeper指定的path是否存在
	 * 
	 * @return
	 */
	public boolean givedPathIsexists(String path) {

		try {
			Stat stat = zookeeper.exists(path, watcher);
			if (stat == null) {
				LOG.info("zookeepr path " + path + " no exist!");
				return false;
			}
		} catch (KeeperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 根据指定path创建节点,直接使用这个创建节点path，需要每层的父节点存在，否则会报错，zk不支持多级创建节点path
	 * 
	 * @param path
	 * @param data
	 * @throws Exception
	 */
	public void createNode(String path, byte[] data) throws Exception {
		// zookeeper.create(path, data, Ids.OPEN_ACL_UNSAFE,
		// CreateMode.PERSISTENT);
		// zookeeper.create(path, data, Ids.OPEN_ACL_UNSAFE,
		// CreateMode.EPHEMERAL_SEQUENTIAL);
		zookeeper
				.create(path, data, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
	}

	/**
	 * 递归创建zk上的Path节点,这个结合ZKClientsUtil表现的有点不足的地方是最后一层目录多访问判断了一次
	 * 
	 * @param path
	 * @param data
	 * @return
	 * @throws InterruptedException
	 * @throws KeeperException
	 */
	public boolean recursiveCreateNode(String path, byte[] data)
			throws KeeperException, InterruptedException {

		StringBuilder path2 = new StringBuilder("/");
		for (String s : path.split("/")) {

			if (s.length() != 0) {

				path2.append(s);
				if (zookeeper.exists(path2.toString(), watcher) == null) {
					try {
						zookeeper.create(path2.toString(), data,
								Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return false;
					}
				}
				// System.out.println(path2);
				path2.append("/");

			}

		}
		return true;
	}

	/**
	 * 对指定的path设置值
	 * 
	 * @param path
	 * @param data
	 * @throws Exception
	 */
	public void setData(String path, String data) throws Exception {
		byte[] bytes = data.getBytes();
		zookeeper.setData(path, bytes, -1);
	}

	public void setData(String path, byte[] data) throws Exception {
		zookeeper.setData(path, data, -1);
	}

	/**
	 * 根据路径获取数据
	 * 
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public String getDataStr(String path) throws Exception {
		byte[] bytes = zookeeper.getData(path, null, null);
		return new String(bytes);
	}

	/**
	 * 根据路径获取数据
	 * 
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public byte[] getData(String path) throws Exception {
		return zookeeper.getData(path, null, null);
	}

	public static void main(String[] args) throws Exception {

		// ZkClient工具类初始化
		ZKClientUtil zk = new ZKClientUtil(
				"10.1.1.35:2181,10.1.1.153:2181,10.1.1.151:2181");
		zk.connect();

		// 判断zk上Path是否存在
		/*
		 * Boolean flag = zk.givedPathIsexists("/tms/server-detector");
		 * System.out.println("gived Path is exists:" +flag);
		 */

		/**
		 * 注意:zk上的path 是必须"/"字符开头，同时不能以"/"字符结尾
		 */
		/*
		 * String path = "/test/t3/t5/t7"; Boolean flag2 =
		 * zk.givedPathIsexists(path);
		 * System.out.println("gived Path is exists:" + flag2); if (flag2 ==
		 * false) { boolean t2 = zk.recursiveCreateNode(path, "0".getBytes());
		 * System.out.println("create path " + path + " !"); }
		 */

		/**
		 * 获取zk上的某个path里面存储的value
		 */
		/*
		 * String s = zk.getDataStr("/test"); System.out.println("s:" +s);
		 */

		/*
		 * zk.setData("/test/t3/t5/t7", "20"); String s =
		 * zk.getDataStr("/test/t3/t5/t7"); System.out.println("s:"+s);
		 */

		zk.close();

	}

}
