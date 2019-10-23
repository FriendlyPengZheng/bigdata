package ad.pay;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import util.MBParser;
import util.MBProduct;
import util.VipParser;

/**
 * 
 * @author cheney
 * @date 2013-12-05
 */
public class GetMBMap extends MapReduceBase implements
		Mapper<LongWritable, Text, Text, LongWritable> {

	private JobConf jobConf = null;
	private Text outputKey = new Text();
	private LongWritable outputValue = new LongWritable(0);

	private MBProduct mbp = new MBProduct();
	private MBParser mbps = new MBParser();
	private VipParser vipps = new VipParser();

	public void configure(JobConf job) {
		this.jobConf = job;
		String fsname = jobConf.get("fs.default.name");
		String prdmapfile = fsname.equals("file:///") ? "/home/lc/hadoop/trunk/ads/mapred/conf/prdmap.conf"
				: "prdmap.conf";
		mbp.configure(prdmapfile);
	}

	public void map(LongWritable key, Text value,
			OutputCollector<Text, LongWritable> output, Reporter reporter)
			throws IOException {
		
		// 1. mb product record ::= (8 collumns)
		// 2. vip cost record ::= (10 collumns)
		
		String line = value.toString();
		String[] items = line.split("\t", -1);
		String mimi = null;
		switch (items.length) {
			case MBParser.collumCount: // mb product
				try {
					if (!mbps.init(items)) {
						return;
					}
	
					int productid = mbps.getProductId();
					int gameid = mbp.getGameid(productid);
					mimi = mbps.getMimiSrc();
	
					// gameid,mimi
					outputKey.set(String.format("%d\t%s", gameid, mimi));
					outputValue.set(mbps.getMBCost());
					
				} catch (Exception ex) {
					System.err.printf("error mb record: %s\n", line);
					ex.printStackTrace();
					return;
				}
				break;
				
			case VipParser.collumCount: //vip record
				try {
	
					if (!vipps.init(items)) {
						return;
					}
	
					String gameid = vipps.getGameid();
					mimi = vipps.getMimi();
	
					int mbpcost = vipps.getVipCost();
	
					outputValue.set(mbpcost);
					outputKey.set(String.format("%s\t%s", gameid, mimi));
					
				} catch (Exception ex) {
					System.err.printf("error vip record: %s", line);
					ex.printStackTrace();
				}
				break;
				
			default:
				System.err.printf("error ad boss format: %s\n", value.toString());
				return;
			}

		output.collect(outputKey, outputValue);
	}

}
