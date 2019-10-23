package ad.pay;

import org.apache.hadoop.mapred.JobConf;

/**
 * 
 * @author cheney
 * @date 2013-12-05
 */
public class GetRegisterTagMap extends GetRegisterMap {

	@Override
	public void configure(JobConf job) {
		super.configure(job);
		this.tags = UserTag.DIF_INT_TAG;
	}

}
