package com.taomee.bigdata.datamining;

import weka.classifiers.functions.LibSVM;
import weka.core.*;
import weka.classifiers.Classifier;

import java.io.FileReader;  
import java.io.InputStream;
import java.io.IOException; 

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionCodecFactory;

public class GradientSearch {

    private int cvs = 3;
    private LibSVM svm = null;
    private int max_cnt = -10;

    private double trainAndTest(Instances trainSet, Instances testSet, LibSVM svm) {
        try {
            System.out.println("trainset " + trainSet.numInstances() + " testset " + testSet.numInstances());

            //train
            System.out.println("start train");
            svm.buildClassifier(trainSet);
            System.out.println("stop train");

            //test
            System.out.println("start test");
            int m = testSet.numInstances();
            double right = 0;
            for(int i=0; i<m; i++) {
                Instance instance = testSet.instance(i);
                double r[] = svm.distributionForInstance(instance);
                if((r[(int)instance.classValue()]) >= 1.0/r.length) {
                    right++;
                }
            }
            System.out.println("stop test");
            return right*100/m;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Classifier run(String cParams[], String gParams[], Instances dataSet) {
        Instances trainCVS[] = new Instances[cvs];
        Instances testCVS[] = new Instances[cvs];

        for(int i=0; i<cvs; i++) {
            trainCVS[i] = dataSet.trainCV(cvs, i);
            testCVS[i] = dataSet.testCV(cvs, i);
        }

        double cStart = Double.valueOf(cParams[0]);
        double cGap = Double.valueOf(cParams[1]);
        double cEnd = Double.valueOf(cParams[2]);

        double gStart = Double.valueOf(gParams[0]);
        double gGap = Double.valueOf(gParams[1]);
        double gEnd = Double.valueOf(gParams[2]);

        double bestC = 0;
        double bestG = 0;
        double bestP = Double.MIN_VALUE;

        if(cStart <= 0) cStart = cGap;
        if(gStart <0)   gStart = 0;
        
        int try_cnt = 0;
        double last_result = 0;

        for(double d1 = cStart; d1 <= cEnd; d1++) {
            double d2 = (gStart+gEnd)/2;
            svm = new LibSVM();
            svm.setCost(d1);
            svm.setGamma(d2);
            double p = 0;
            for(int i=0; i<trainCVS.length; i++) {
                p += trainAndTest(trainCVS[i], testCVS[i], svm);
            }
            p /= trainCVS.length;
            System.out.println(String.format("c=%f g=%f p=%.2f", d1, d2, p));
            if(p > bestP) {
                bestP = p;
                bestC = d1;
                bestG = d2;
            }
            if(p > last_result) {
                try_cnt = 0;
            } else {
                try_cnt --;
            }

            last_result = p;
            if(try_cnt <= max_cnt)  break;
        }

        cStart = bestC-1+cGap<= 0 ? 0.1 : bestC-1+cGap;
        cEnd = bestC+1-cGap;
        bestP = Double.MIN_VALUE;
        last_result = 0;
        for(double d1 = cStart; d1 <= cEnd; d1+=cGap) {
            double d2 = (gStart+gEnd)/2;
            svm = new LibSVM();
            svm.setCost(d1);
            svm.setGamma(d2);
            double p = 0;
            for(int i=0; i<trainCVS.length; i++) {
                p += trainAndTest(trainCVS[i], testCVS[i], svm);
            }
            p /= trainCVS.length;
            System.out.println(String.format("c=%f g=%f p=%.2f", d1, d2, p));
            if(p > bestP) {
                bestP = p;
                bestC = d1;
                bestG = d2;
            }
            if(p > last_result) {
                try_cnt = 0;
            } else {
                try_cnt --;
            }

            last_result = p;
            if(try_cnt <= max_cnt)  break;
        }

        bestP = Double.MIN_VALUE;
        last_result = 0;
        for(double d2 = gStart; d2 <= gEnd; d2+=gGap) {
            double d1 = bestC;
            svm = new LibSVM();
            svm.setCost(d1);
            svm.setGamma(d2);
            double p = 0;
            for(int i=0; i<trainCVS.length; i++) {
                p += trainAndTest(trainCVS[i], testCVS[i], svm);
            }
            p /= trainCVS.length;
            System.out.println(String.format("c=%f g=%f p=%.2f", d1, d2, p));
            if(p > bestP) {
                bestP = p;
                bestC = d1;
                bestG = d2;
            }
            if(p > last_result) {
                try_cnt = 0;
            } else {
                try_cnt --;
            }

            last_result = p;
            if(try_cnt <= max_cnt)  break;
        }
    
        System.out.println("++++++++++++++++++++++++++++++++++++++++++");
        System.out.println(String.format("c=%f g=%f p=%.2f", bestC, bestG, bestP));

        svm = new LibSVM();
        svm.setCost(bestC);
        svm.setGamma(bestG);
        try {
            svm.buildClassifier(dataSet);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return svm;
    }

    public Classifier run(String[] argv, Instances dataSet) throws java.io.IOException, java.lang.Exception {
        dataSet.setClassIndex(dataSet.numAttributes()-1);

        String cParams[] = argv[0].split(":");
        String gParams[] = argv[1].split(":");

        return run(cParams, gParams, dataSet);
    }

    public static void main(String[] argv) throws java.io.IOException, java.lang.Exception {
        Instances dataSet = new Instances(new FileReader("99.arff"));
        GradientSearch r = new GradientSearch();
        Classifier c = r.run(argv, dataSet);
        Configuration conf = (new Configured()).getConf();

        //写分类器到hdfs
        FileSystem fs = FileSystem.get(conf);
        FSDataOutputStream out = fs.create(new Path(""));
        SerializationHelper.write(out, c);

        //读分类器到hdfs
        FileStatus[] status = fs.globStatus(new Path("/input"));
        CompressionCodecFactory factory = new CompressionCodecFactory(conf);
        for (int statusNo = 0; statusNo < status.length; statusNo++) {
            Path path = status[statusNo].getPath();
            InputStream inStream = null;
            if(path.getName().endsWith(".gz")) {
                CompressionCodec codec = factory.getCodec(path);
                inStream = codec.createInputStream(fs.open(path));
            } else {
                inStream = fs.open(path);
            }
            LibSVM s = (LibSVM) SerializationHelper.read(inStream);
        }

    }
}
