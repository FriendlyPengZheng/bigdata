package com.taomee.bigdata.hbase.bean.dao.impl;

import com.taomee.bigdata.hbase.bean.dao.HbaseDao;
import com.taomee.bigdata.hbase.client.utils.HbasePoolConn;
import com.taomee.bigdata.hbase.util.PublicConfigUtil;
import com.taomee.tms.mgr.entity.ResultInfo;

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.filter.RegexStringComparator;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 结果表的增删改查
 * Created by looper on 2017/4/5.
 */

public class ResultDaoImpl implements HbaseDao<ResultInfo> {
    /**
     * 0:日 周 1 月 2 版本周 3 分钟4 小时5
     */
	private static HashMap<Integer, String> tabCodesReflectTableName = new HashMap<Integer, String>();

	static {
		tabCodesReflectTableName.put(0, PublicConfigUtil.readConfig("HBASE_DAY_NAME"));
		tabCodesReflectTableName.put(1, PublicConfigUtil.readConfig("HBASE_WEEK_NAME"));
		tabCodesReflectTableName.put(2, PublicConfigUtil.readConfig("HBASE_MONTH_NAME"));
		tabCodesReflectTableName.put(3, PublicConfigUtil.readConfig("HBASE_VERSION_NAME"));
		tabCodesReflectTableName.put(4, PublicConfigUtil.readConfig("HBASE_MINUTE_NAME"));
		tabCodesReflectTableName.put(5, PublicConfigUtil.readConfig("HBASE_HOUR_NAME"));
	}

	/**
	 * 获取范围数据列表
	 * 
	 * @param start_rb
	 * @param stop_rb
	 * @param tableCode
	 *            时间维度
	 * @return
	 */
	public List<ResultInfo> findListData(ResultInfo start_rb,
			ResultInfo stop_rb, Integer tableCode) {
		if (tableCode < 0 || tableCode > tabCodesReflectTableName.size()) {
			System.out.println("输入的tabCode数值有误");
		}
		List<ResultInfo> values = new ArrayList<ResultInfo>();
		Connection connection = HbasePoolConn.getHbaseConn();
		Table table = null;
		try {
			// Connection connection = HbasePoolConn.getHbaseConn();
			table = connection.getTable(TableName
					.valueOf(tabCodesReflectTableName.get(tableCode)));
			System.out.println("conn:" + connection);
			System.out.println("start_rb：" + start_rb + ",stop_rb" + stop_rb);
			System.out.println("tableCode:"
					+ tabCodesReflectTableName.get(tableCode));
			Scan scan = new Scan();
			scan.setStartRow(Bytes.toBytes(String.format("%d-%d-%d",
					start_rb.getServerId(), start_rb.getDataId(),
					start_rb.getTime())));
			scan.setStopRow(Bytes.toBytes(String.format("%d-%d-%d",
					stop_rb.getServerId(), stop_rb.getDataId(),
					stop_rb.getTime())
					+ (char) 3));
			ResultScanner rs = null;
			rs = table.getScanner(scan);
			for (Result r = rs.next(); r != null; r = rs.next()) {

				String[] keys = Bytes.toString(r.getRow()).split("-");

				if (keys.length != 3) {
					System.out.println("row key 有误 !");
				} else {
					System.out.println("获取到的val的值:"
							+ Bytes.toString(r.getValue(Bytes.toBytes("val"),
									Bytes.toBytes("value"))));
					if (Bytes.toString(r.getValue(Bytes.toBytes("val"),
							Bytes.toBytes("value"))) == null) {
						/*
						 * ResultBean resultBean = new
						 * ResultBean(Integer.parseInt(keys[0]),
						 * Integer.parseInt(keys[1]), Long.parseLong(keys[2]),
						 * Double
						 * .parseDouble(Bytes.toString(r.getValue(Bytes.toBytes
						 * ("val"), Bytes.toBytes("value")))));
						 */
						// return null;
					} else {

						// }
						ResultInfo resultBean = new ResultInfo(
								Integer.parseInt(keys[0]),
								Integer.parseInt(keys[1]),
								Long.parseLong(keys[2]),
								Double.parseDouble(Bytes.toString(r.getValue(
										Bytes.toBytes("val"),
										Bytes.toBytes("value")))));
						values.add(resultBean);
					}
				}
			}
			for (ResultInfo rb : values) {
				System.out.println(rb);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				table.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			HbasePoolConn.pool.returnConnection(connection);
		}

		return values;
	}

	/**
	 * 插入或者更新row的value字段
	 * 
	 * @param dataRow
	 * @param tableCode
	 */
	public void putData(ResultInfo dataRow, Integer tableCode) {
		if (tableCode < 0 || tableCode > tabCodesReflectTableName.size()) {
			System.out.println("输入的tabCode数值有误");
		}
		List<ResultInfo> values = new ArrayList<ResultInfo>();
		Connection connection = HbasePoolConn.getHbaseConn();
		Table table = null;
		try {
			table = connection.getTable(TableName
					.valueOf(tabCodesReflectTableName.get(tableCode)));
			Put put = new Put(Bytes.toBytes(String.format("%d-%d-%d",
					dataRow.getServerId(), dataRow.getDataId(),
					dataRow.getTime())));

			// value的值，先转字符串再转二进制，避免插入的数据到表中会以二进制显示
			put.add(Bytes.toBytes("val"), Bytes.toBytes("value"),
					Bytes.toBytes(dataRow.getValue().toString()));

			table.put(put);
			System.out.println("insert data sucess!" + dataRow + " : "
					+ tableCode);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				table.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			HbasePoolConn.pool.returnConnection(connection);
		}
	}

	/**
	 * 获取单个记录
	 * 
	 * @param dataRow
	 * @param tableCode
	 * @return
	 */
	public ResultInfo getOneData(ResultInfo dataRow, Integer tableCode) {
		if (tableCode < 0 || tableCode > tabCodesReflectTableName.size()) {
			System.out.println("输入的tabCode数值有误");
		}
		ResultInfo resultBean = dataRow;
		List<ResultInfo> values = new ArrayList<ResultInfo>();
		Connection connection = HbasePoolConn.getHbaseConn();
		Table table = null;
		String value = null;
		try {
			table = connection.getTable(TableName
					.valueOf(tabCodesReflectTableName.get(tableCode)));
			Get get = new Get(Bytes.toBytes(String.format("%d-%d-%d",
					dataRow.getServerId(), dataRow.getDataId(),
					dataRow.getTime())));
			get.addFamily(Bytes.toBytes("val"));
			// get.get
			Result r = table.get(get);
			// get.addFamily()
			byte[] b = r.getValue(Bytes.toBytes("val"), Bytes.toBytes("value"));
			value = Bytes.toString(b);
			dataRow.setValue(Double.parseDouble(value));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				table.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			HbasePoolConn.pool.returnConnection(connection);
		}
		return dataRow;
	}

	/**
	 * 精准匹配rowkey，删除单个记录
	 * 
	 * @param dataRow
	 * @param tableCode
	 */
	public void deleteOneData(ResultInfo dataRow, Integer tableCode) {
		if (tableCode < 0 || tableCode > tabCodesReflectTableName.size()) {
			System.out.println("输入的tabCode数值有误");
		}

		Connection connection = HbasePoolConn.getHbaseConn();
		Table table = null;
		try {
			table = connection.getTable(TableName
					.valueOf(tabCodesReflectTableName.get(tableCode)));
			
			Delete delete = new Delete(Bytes.toBytes(String.format("%d-%d-%d",
					dataRow.getServerId(), dataRow.getDataId(),
					dataRow.getTime())));
			table.delete(delete);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				table.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			HbasePoolConn.pool.returnConnection(connection);
		}
	}

	/**
	 * 正则表达式来匹配行键，从而删除匹配上的记录 notice: 该方法在海量数据中存在效率问题
	 * 
	 * @param tableCode
	 * @param regexString
	 *            rowkey对应的正则表达式
	 */
	@Override
	public void deleteRegexRowKeyData(Integer tableCode, String regexString) {
		// TODO Auto-generated method stub
		if (tableCode < 0 || tableCode > tabCodesReflectTableName.size()) {
			System.out.println("输入的tableCode数值有误");
		}

		Connection connection = HbasePoolConn.getHbaseConn();
		Table table = null;
		try {
			table = connection.getTable(TableName
					.valueOf(tabCodesReflectTableName.get(tableCode)));

			Scan scan = new Scan();
			scan.addColumn(Bytes.toBytes("val"), Bytes.toBytes("value"));

			Filter filter = new RowFilter(CompareFilter.CompareOp.EQUAL,
					new RegexStringComparator(regexString));
			scan.setFilter(filter);

			ResultScanner rs = table.getScanner(scan);
			for (Result r = rs.next(); r != null; r = rs.next()) {
				String[] keys = Bytes.toString(r.getRow()).split("-");
				System.out.println("keys" + keys.length);
				for (int i = 0; i < keys.length; i++) {
					System.out.println("keys" + i + " " + keys[i]);
				}

				if (keys.length != 3) {
					System.out.println("rowkey error");
				} else {
					System.out.println("获取到的val的值:"
							+ Bytes.toString(r.getValue(Bytes.toBytes("val"),
									Bytes.toBytes("value"))));
					if (Bytes.toString(r.getValue(Bytes.toBytes("val"),
							Bytes.toBytes("value"))) == null) {

					} else {
						ResultInfo resultInfo = new ResultInfo(
								Integer.parseInt(keys[0]),
								Integer.parseInt(keys[1]),
								Long.parseLong(keys[2]), Double.parseDouble(Bytes.toString(r.getValue(
										Bytes.toBytes("val"),
										Bytes.toBytes("value")))));
						System.out.println("hbase中将删除的记录resultInfo: " + resultInfo);
						deleteOneData(resultInfo, tableCode);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				table.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			HbasePoolConn.pool.returnConnection(connection);
		}
	}

}
