package ad.active;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.apache.hadoop.io.*;

public class MergeKey implements Writable {
    public Text tad = new Text();
    public IntWritable gid = new IntWritable();
    public IntWritable timeday = new IntWritable();

    public MergeKey() { this("", -1, 19700101); }
    public MergeKey(String ad, int g, int t) {
        tad.set(ad);
        gid.set(g);
        timeday.set(t);
    }

    public void write(DataOutput out) throws IOException {
        tad.write(out);
        gid.write(out);
        timeday.write(out);
    }

    public void readFields(DataInput in) throws IOException {
        tad.readFields(in);
        gid.readFields(in);
        timeday.readFields(in);
    }

    public String toString() {
            return String.format("%s\t%d\t%d", tad.toString(), gid.get(), timeday.get());
    }
}
