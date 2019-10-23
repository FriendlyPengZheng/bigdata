package com.taomee.bigdata.datamining.seerV2;

import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.Writable;
import java.io.IOException;

public class Output implements OutputCollector<WritableComparable, Writable>
{
    public void collect(WritableComparable k, Writable v) throws IOException {
        System.out.println("key= " + k.toString() + " value= " + v.toString());
    }
}
