package io;

import java.io.*;
import org.apache.hadoop.io.*;

// util class for computing 2 number sum
public class PairSumWritable implements Writable
{
    public long key1 = 0;
    public long key2 = 0;

    public void reset() {
        key1 = 0;
        key2 = 0;
    }

    public void write(DataOutput out) throws IOException {
        out.writeLong(key1);
        out.writeLong(key2);
    }

    public void readFields(DataInput in) throws IOException {
        key1 = in.readLong();
        key2 = in.readLong();
    }

    public String toString() {
        return String.format("%d\t%d", key1, key2);
    }
}
