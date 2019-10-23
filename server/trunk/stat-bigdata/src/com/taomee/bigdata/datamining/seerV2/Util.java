package com.taomee.bigdata.datamining.seerV2;

import weka.core.Instances;
import weka.core.Instance;
import weka.core.SerializationHelper;
import weka.classifiers.Classifier;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.mapred.JobConf;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

class Util {
    private static int cache[][] = {
        {0,0,0,0,0,0,0,0},
        {1,0,0,0,0,0,0,0},
        {0,1,0,0,0,0,0,0},
        {1,1,0,0,0,0,0,0},
        {0,0,1,0,0,0,0,0},
        {1,0,1,0,0,0,0,0},
        {0,1,1,0,0,0,0,0},
        {1,1,1,0,0,0,0,0},
        {0,0,0,1,0,0,0,0},
        {1,0,0,1,0,0,0,0},
        {0,1,0,1,0,0,0,0},
        {1,1,0,1,0,0,0,0},
        {0,0,1,1,0,0,0,0},
        {1,0,1,1,0,0,0,0},
        {0,1,1,1,0,0,0,0},
        {1,1,1,1,0,0,0,0},
        {0,0,0,0,1,0,0,0},
        {1,0,0,0,1,0,0,0},
        {0,1,0,0,1,0,0,0},
        {1,1,0,0,1,0,0,0},
        {0,0,1,0,1,0,0,0},
        {1,0,1,0,1,0,0,0},
        {0,1,1,0,1,0,0,0},
        {1,1,1,0,1,0,0,0},
        {0,0,0,1,1,0,0,0},
        {1,0,0,1,1,0,0,0},
        {0,1,0,1,1,0,0,0},
        {1,1,0,1,1,0,0,0},
        {0,0,1,1,1,0,0,0},
        {1,0,1,1,1,0,0,0},
        {0,1,1,1,1,0,0,0},
        {1,1,1,1,1,0,0,0},
        {0,0,0,0,0,1,0,0},
        {1,0,0,0,0,1,0,0},
        {0,1,0,0,0,1,0,0},
        {1,1,0,0,0,1,0,0},
        {0,0,1,0,0,1,0,0},
        {1,0,1,0,0,1,0,0},
        {0,1,1,0,0,1,0,0},
        {1,1,1,0,0,1,0,0},
        {0,0,0,1,0,1,0,0},
        {1,0,0,1,0,1,0,0},
        {0,1,0,1,0,1,0,0},
        {1,1,0,1,0,1,0,0},
        {0,0,1,1,0,1,0,0},
        {1,0,1,1,0,1,0,0},
        {0,1,1,1,0,1,0,0},
        {1,1,1,1,0,1,0,0},
        {0,0,0,0,1,1,0,0},
        {1,0,0,0,1,1,0,0},
        {0,1,0,0,1,1,0,0},
        {1,1,0,0,1,1,0,0},
        {0,0,1,0,1,1,0,0},
        {1,0,1,0,1,1,0,0},
        {0,1,1,0,1,1,0,0},
        {1,1,1,0,1,1,0,0},
        {0,0,0,1,1,1,0,0},
        {1,0,0,1,1,1,0,0},
        {0,1,0,1,1,1,0,0},
        {1,1,0,1,1,1,0,0},
        {0,0,1,1,1,1,0,0},
        {1,0,1,1,1,1,0,0},
        {0,1,1,1,1,1,0,0},
        {1,1,1,1,1,1,0,0},
        {0,0,0,0,0,0,1,0},
        {1,0,0,0,0,0,1,0},
        {0,1,0,0,0,0,1,0},
        {1,1,0,0,0,0,1,0},
        {0,0,1,0,0,0,1,0},
        {1,0,1,0,0,0,1,0},
        {0,1,1,0,0,0,1,0},
        {1,1,1,0,0,0,1,0},
        {0,0,0,1,0,0,1,0},
        {1,0,0,1,0,0,1,0},
        {0,1,0,1,0,0,1,0},
        {1,1,0,1,0,0,1,0},
        {0,0,1,1,0,0,1,0},
        {1,0,1,1,0,0,1,0},
        {0,1,1,1,0,0,1,0},
        {1,1,1,1,0,0,1,0},
        {0,0,0,0,1,0,1,0},
        {1,0,0,0,1,0,1,0},
        {0,1,0,0,1,0,1,0},
        {1,1,0,0,1,0,1,0},
        {0,0,1,0,1,0,1,0},
        {1,0,1,0,1,0,1,0},
        {0,1,1,0,1,0,1,0},
        {1,1,1,0,1,0,1,0},
        {0,0,0,1,1,0,1,0},
        {1,0,0,1,1,0,1,0},
        {0,1,0,1,1,0,1,0},
        {1,1,0,1,1,0,1,0},
        {0,0,1,1,1,0,1,0},
        {1,0,1,1,1,0,1,0},
        {0,1,1,1,1,0,1,0},
        {1,1,1,1,1,0,1,0},
        {0,0,0,0,0,1,1,0},
        {1,0,0,0,0,1,1,0},
        {0,1,0,0,0,1,1,0},
        {1,1,0,0,0,1,1,0},
        {0,0,1,0,0,1,1,0},
        {1,0,1,0,0,1,1,0},
        {0,1,1,0,0,1,1,0},
        {1,1,1,0,0,1,1,0},
        {0,0,0,1,0,1,1,0},
        {1,0,0,1,0,1,1,0},
        {0,1,0,1,0,1,1,0},
        {1,1,0,1,0,1,1,0},
        {0,0,1,1,0,1,1,0},
        {1,0,1,1,0,1,1,0},
        {0,1,1,1,0,1,1,0},
        {1,1,1,1,0,1,1,0},
        {0,0,0,0,1,1,1,0},
        {1,0,0,0,1,1,1,0},
        {0,1,0,0,1,1,1,0},
        {1,1,0,0,1,1,1,0},
        {0,0,1,0,1,1,1,0},
        {1,0,1,0,1,1,1,0},
        {0,1,1,0,1,1,1,0},
        {1,1,1,0,1,1,1,0},
        {0,0,0,1,1,1,1,0},
        {1,0,0,1,1,1,1,0},
        {0,1,0,1,1,1,1,0},
        {1,1,0,1,1,1,1,0},
        {0,0,1,1,1,1,1,0},
        {1,0,1,1,1,1,1,0},
        {0,1,1,1,1,1,1,0},
        {1,1,1,1,1,1,1,0},
        {0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,1},
        {0,1,0,0,0,0,0,1},
        {1,1,0,0,0,0,0,1},
        {0,0,1,0,0,0,0,1},
        {1,0,1,0,0,0,0,1},
        {0,1,1,0,0,0,0,1},
        {1,1,1,0,0,0,0,1},
        {0,0,0,1,0,0,0,1},
        {1,0,0,1,0,0,0,1},
        {0,1,0,1,0,0,0,1},
        {1,1,0,1,0,0,0,1},
        {0,0,1,1,0,0,0,1},
        {1,0,1,1,0,0,0,1},
        {0,1,1,1,0,0,0,1},
        {1,1,1,1,0,0,0,1},
        {0,0,0,0,1,0,0,1},
        {1,0,0,0,1,0,0,1},
        {0,1,0,0,1,0,0,1},
        {1,1,0,0,1,0,0,1},
        {0,0,1,0,1,0,0,1},
        {1,0,1,0,1,0,0,1},
        {0,1,1,0,1,0,0,1},
        {1,1,1,0,1,0,0,1},
        {0,0,0,1,1,0,0,1},
        {1,0,0,1,1,0,0,1},
        {0,1,0,1,1,0,0,1},
        {1,1,0,1,1,0,0,1},
        {0,0,1,1,1,0,0,1},
        {1,0,1,1,1,0,0,1},
        {0,1,1,1,1,0,0,1},
        {1,1,1,1,1,0,0,1},
        {0,0,0,0,0,1,0,1},
        {1,0,0,0,0,1,0,1},
        {0,1,0,0,0,1,0,1},
        {1,1,0,0,0,1,0,1},
        {0,0,1,0,0,1,0,1},
        {1,0,1,0,0,1,0,1},
        {0,1,1,0,0,1,0,1},
        {1,1,1,0,0,1,0,1},
        {0,0,0,1,0,1,0,1},
        {1,0,0,1,0,1,0,1},
        {0,1,0,1,0,1,0,1},
        {1,1,0,1,0,1,0,1},
        {0,0,1,1,0,1,0,1},
        {1,0,1,1,0,1,0,1},
        {0,1,1,1,0,1,0,1},
        {1,1,1,1,0,1,0,1},
        {0,0,0,0,1,1,0,1},
        {1,0,0,0,1,1,0,1},
        {0,1,0,0,1,1,0,1},
        {1,1,0,0,1,1,0,1},
        {0,0,1,0,1,1,0,1},
        {1,0,1,0,1,1,0,1},
        {0,1,1,0,1,1,0,1},
        {1,1,1,0,1,1,0,1},
        {0,0,0,1,1,1,0,1},
        {1,0,0,1,1,1,0,1},
        {0,1,0,1,1,1,0,1},
        {1,1,0,1,1,1,0,1},
        {0,0,1,1,1,1,0,1},
        {1,0,1,1,1,1,0,1},
        {0,1,1,1,1,1,0,1},
        {1,1,1,1,1,1,0,1},
        {0,0,0,0,0,0,1,1},
        {1,0,0,0,0,0,1,1},
        {0,1,0,0,0,0,1,1},
        {1,1,0,0,0,0,1,1},
        {0,0,1,0,0,0,1,1},
        {1,0,1,0,0,0,1,1},
        {0,1,1,0,0,0,1,1},
        {1,1,1,0,0,0,1,1},
        {0,0,0,1,0,0,1,1},
        {1,0,0,1,0,0,1,1},
        {0,1,0,1,0,0,1,1},
        {1,1,0,1,0,0,1,1},
        {0,0,1,1,0,0,1,1},
        {1,0,1,1,0,0,1,1},
        {0,1,1,1,0,0,1,1},
        {1,1,1,1,0,0,1,1},
        {0,0,0,0,1,0,1,1},
        {1,0,0,0,1,0,1,1},
        {0,1,0,0,1,0,1,1},
        {1,1,0,0,1,0,1,1},
        {0,0,1,0,1,0,1,1},
        {1,0,1,0,1,0,1,1},
        {0,1,1,0,1,0,1,1},
        {1,1,1,0,1,0,1,1},
        {0,0,0,1,1,0,1,1},
        {1,0,0,1,1,0,1,1},
        {0,1,0,1,1,0,1,1},
        {1,1,0,1,1,0,1,1},
        {0,0,1,1,1,0,1,1},
        {1,0,1,1,1,0,1,1},
        {0,1,1,1,1,0,1,1},
        {1,1,1,1,1,0,1,1},
        {0,0,0,0,0,1,1,1},
        {1,0,0,0,0,1,1,1},
        {0,1,0,0,0,1,1,1},
        {1,1,0,0,0,1,1,1},
        {0,0,1,0,0,1,1,1},
        {1,0,1,0,0,1,1,1},
        {0,1,1,0,0,1,1,1},
        {1,1,1,0,0,1,1,1},
        {0,0,0,1,0,1,1,1},
        {1,0,0,1,0,1,1,1},
        {0,1,0,1,0,1,1,1},
        {1,1,0,1,0,1,1,1},
        {0,0,1,1,0,1,1,1},
        {1,0,1,1,0,1,1,1},
        {0,1,1,1,0,1,1,1},
        {1,1,1,1,0,1,1,1},
        {0,0,0,0,1,1,1,1},
        {1,0,0,0,1,1,1,1},
        {0,1,0,0,1,1,1,1},
        {1,1,0,0,1,1,1,1},
        {0,0,1,0,1,1,1,1},
        {1,0,1,0,1,1,1,1},
        {0,1,1,0,1,1,1,1},
        {1,1,1,0,1,1,1,1},
        {0,0,0,1,1,1,1,1},
        {1,0,0,1,1,1,1,1},
        {0,1,0,1,1,1,1,1},
        {1,1,0,1,1,1,1,1},
        {0,0,1,1,1,1,1,1},
        {1,0,1,1,1,1,1,1},
        {0,1,1,1,1,1,1,1},
        {1,1,1,1,1,1,1,1}
    };

    protected static Instance getInstanceByString(String s, Instances instances) {
        String items[] = s.split(":");
        String classValue = items.length == 1 ? "keep" : items[1];
        String attributes[] = items[0].split(" ");
        Instance i = new Instance(attributes.length * 32 + 1);
        i.setDataset(instances);

        int index = 0;
        for(int j=0; j<attributes.length; j++) {
            long l = Long.parseLong(attributes[j].replaceAll("0x", ""), 16);

            int k[] = cache[(int)((l&0xff000000)>>24)];
            for(int jj=0; jj<k.length; jj++) {
                i.setValue(index++, k[jj]);
            }

            k = cache[(int)((l&0xff0000)>>16)];
            for(int jj=0; jj<k.length; jj++) {
                i.setValue(index++, k[jj]);
            }

            k = cache[(int)((l&0xff00)>>8)];
            for(int jj=0; jj<k.length; jj++) {
                i.setValue(index++, k[jj]);
            }

            k = cache[(int)(l&0xff)];
            for(int jj=0; jj<k.length; jj++) {
                i.setValue(index++, k[jj]);
            }
        }

        i.setValue(i.numAttributes()-1, classValue);

        return i;
    }

    protected static Instance getInstanceWithoutClass(String s, Instances instances) {
        String items[] = s.split(":");
        String attributes[] = items[0].split(" ");

        Instance i = new Instance(instances.numAttributes());
        i.setDataset(instances);

        for(int j=0; j<attributes.length; j++) {
            i.setValue(j, Double.valueOf(attributes[j]));
        }

        return i;
    }

    protected static Instance getInstance(String s, Instances instances) {
        String items[] = s.split(":");
        String classValue = items.length == 1 ? "keep" : items[1].split("\t")[0];
        String attributes[] = items[0].split(" ");

        Instance i = new Instance(instances.numAttributes());
        i.setDataset(instances);

        for(int j=0; j<attributes.length; j++) {
            i.setValue(j, Double.valueOf(attributes[j]));
        }

        i.setValue(i.numAttributes()-1, classValue);
        return i;
    }

    protected static Instances readHeaderFromHDFS(JobConf job, String path) throws Exception {
        FileSystem fs = FileSystem.get(job);

        InputStream inStream = fs.open(new Path(path));
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(inStream));

        Instances header = new Instances(reader);
        header.setClassIndex(header.numAttributes()-1);

        reader.close();
        return header;
    }

    protected static void writeClassifierToHDFS(JobConf conf, Classifier c, String path) throws Exception {
        FileSystem fs = FileSystem.get(conf);
        Path p = new Path(path);
        FSDataOutputStream out;
        if (fs.exists(p)) {
            fs.delete(p, false);
        }
        out = fs.create(p);
        SerializationHelper.write(out, c);

        out.close();
    }

    protected static Classifier[] readClassifierFromHDFS(JobConf conf, String path) throws Exception {
        FileSystem fs = FileSystem.get(conf);
        FileStatus[] status = fs.globStatus(new Path(path));
        Classifier[] c = new Classifier[status.length];
        for (int statusNo = 0; statusNo < status.length; statusNo++) {
            Path p = status[statusNo].getPath();
            InputStream inStream  = fs.open(p);
            c[statusNo] = (Classifier)SerializationHelper.read(inStream);
        }

        return c;
    }
}
