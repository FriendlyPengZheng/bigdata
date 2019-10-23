package prepare;

import java.io.*;
import org.apache.hadoop.io.*;

public class MergeKey implements WritableComparable<MergeKey>{
    private IntWritable gameid = new IntWritable(0);
    private LongWritable mimi = new LongWritable(0);

    public MergeKey () {}

    public void setGameid(int gid) { gameid.set(gid); }
    public long getMimi() { return mimi.get(); }

    public void setMimi(long mm) { mimi.set(mm); }
    public int getGameid() { return gameid.get(); }

    public void write(DataOutput out) throws IOException {
        //out.writeInt(gameid);
        //out.writeLong(mimi);
        gameid.write(out);
        mimi.write(out);
    }

    public void readFields(DataInput in) throws IOException {
        //gameid = in.readInt();
        //mimi = in.readLong();
        gameid.readFields(in);
        mimi.readFields(in);
    }

    public int compareTo(MergeKey w) {
        //if(o instanceOf MergeKey) return -1; 
        //MergeKey w = (MergeKey)o;
        int gid = this.gameid.get();
        int wgid = w.gameid.get();
        if (gid != wgid) {
            return gid - wgid;
        }
        long diff = this.mimi.get() - w.mimi.get();
        return diff < 0 ? -1 : (diff == 0 ? 0 : 1);
    }

    public int hashCode() {
        return (int)(gameid.get() * 73 + mimi.get());
    }

    public String toString() {
        return String.format("gid:%d,mimi:%d",gameid.get(), mimi.get());
    }
}
