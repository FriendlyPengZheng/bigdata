package topics.boss;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Partitioner;

public class GameTypeCostPartitioner 
    implements Partitioner<GameTypeCostKey, LongWritable>
{
    public int getPartition(GameTypeCostKey key, LongWritable value, int numPartitions) {
        return Math.abs(key.getSubHashCode()) % numPartitions;
    }

    public void configure(JobConf job) { }
}
