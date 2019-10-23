package topics.boss;

import java.io.IOException;
import java.io.DataInput;
import java.io.DataOutput;
import org.apache.hadoop.io.*;

// util class for boss's mean/median etc. statistical computing
// gameid : bosstype : cost
public class GameTypeCostKey 
    implements WritableComparable<GameTypeCostKey>
{
    public IntWritable gameid = new IntWritable();
    // for boss type:
    //       1 all
    //       2 mb 
    //       3 vip
    public IntWritable bosstype = new IntWritable();

    public IntWritable cost = new IntWritable();
    /// cost unit in yuans (raw data in fen)
    public void setCost(int xx) {
        cost.set(xx / 100 * 100);
    }

    public void write(DataOutput out) throws IOException {
        gameid.write(out);
        bosstype.write(out);
        cost.write(out);
    }

    public void readFields(DataInput in) throws IOException {
        gameid.readFields(in);
        bosstype.readFields(in);
        cost.readFields(in);
    }

    public int compareTo(GameTypeCostKey o) {
        if (!this.gameid.equals(o.gameid)) {
            return this.gameid.get() - o.gameid.get();
        }
        if (!this.bosstype.equals(o.bosstype)) {
            return this.bosstype.get() - o.bosstype.get();
        }
        if (!this.cost.equals(o.cost)) {
            return this.cost.get() - o.cost.get();
        }
        return 0;
    }

    public String getSubKey () {
        return String.format("%d,%d", gameid.get(), bosstype.get());
    }
    public String toString() {
        return String.format("%d,%d,%d", gameid.get(), bosstype.get(), cost.get());
    }

    public int getSubHashCode() {
        return gameid.hashCode() * 163 + bosstype.hashCode();
    }
    public int hashCode() {
        return gameid.hashCode() * 1009 + bosstype.hashCode() * 163 + cost.hashCode();
    }

    public boolean equals(Object x) {
        if (x instanceof GameTypeCostKey) {
            GameTypeCostKey o = (GameTypeCostKey )x;
            return gameid.equals(o.gameid) && 
                bosstype.equals(o.bosstype) && cost.equals(cost);
        }
        return false;
    }
}
