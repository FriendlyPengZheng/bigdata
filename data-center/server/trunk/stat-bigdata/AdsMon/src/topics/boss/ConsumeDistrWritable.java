package topics.boss;

import java.io.*;
import org.apache.hadoop.io.*;

public class ConsumeDistrWritable implements Writable
{
    // uniq --- uniq players
    // cost --- sum cost
    public int allUniq = 0;
    public long allCost = 0;
    public int allmbvip = 0;
    // all cost distr low point
    //private int allCostPoint = 0;
    //public int mbUniq = 0;
    //public long mbCost = 0;
    // all cost distr low point
    //private int mbCostPoint = 0;
    //private int vipUniq = 0;
    //private long vipCost = 0;

    public void reset() {
        allUniq = 0;
        allCost = 0;
        allmbvip = 0;
    }

    public void write(DataOutput out) throws IOException {
        out.writeInt(allUniq);
        out.writeLong(allCost);
        out.writeInt(allmbvip);
        //out.writeLong(mbUniq);
        //out.writeLong(mbCost);
    }

    public void readFields(DataInput in) throws IOException {
        allUniq = in.readInt();
        allCost = in.readLong();
        allmbvip = in.readInt();
        //mbUniq = in.readInt();
        //mbCost = in.readLong();
    }

    public String toString () {
        return String.format("%d\t%d\t%d", allUniq, allCost, allmbvip);
        //return String.format("%d\t%d\t%d\t%d\t%d", 
                //allUniq, allCost, allmbvip, mbUniq, mbCost);
    }
    
    public String toStringUniqCost() {
        return String.format("%d\t%d", allUniq, allCost);
    }
}
