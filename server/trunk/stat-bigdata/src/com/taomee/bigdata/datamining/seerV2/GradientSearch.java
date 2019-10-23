package com.taomee.bigdata.datamining.seerV2;

import weka.classifiers.functions.LibSVM;
import weka.classifiers.functions.RBFNetwork;
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
    private int max_cnt = -3;

    private double trainAndTest(Instances trainSet, Instances testSet, Classifier c) {
        try {
            if(testSet == null) testSet = trainSet;

            System.out.println("trainset " + trainSet.numInstances() + " testset " + testSet.numInstances());

            //train
            System.out.println("start train");
            c.buildClassifier(trainSet);
            System.out.println("stop train");

            //test
            System.out.println("start test");
            int m = testSet.numInstances();
            double right = 0;
            for(int i=0; i<m; i++) {
                Instance instance = testSet.instance(i);
                double r[] = c.distributionForInstance(instance);
                double max = Double.MIN_VALUE;
                int max_index = -1;
                for(int j=0; j<r.length; j++) {
                    if(r[j]>max) {
                        max = r[j];
                        max_index = j;
                    }
                }
                if(((int)instance.classValue()) == max_index ) {
                    right++;
                }
            }
            System.out.println("stop test");
            return right*100/m;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Classifier runO(Instances dataSet) {
        Instances trainCVS[] = new Instances[1];
        Instances testCVS[] = new Instances[1];

        trainCVS[0] = new Instances(dataSet);
        testCVS[0] = new Instances(dataSet);

        LibSVM c = new LibSVM();

        try {
            c.buildClassifier(dataSet);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return c;
    }

    public Classifier run(String cParams[], String gParams[], Instances dataSet) {
        //Instances trainCVS[] = new Instances[cvs];
        //Instances testCVS[] = new Instances[cvs];

        //for(int i=0; i<cvs; i++) {
        //    trainCVS[i] = dataSet.trainCV(cvs, i);
        //    testCVS[i] = dataSet.testCV(cvs, i);
        //}

        Instances trainCVS[] = new Instances[1];
        Instances testCVS[] = new Instances[1];

        trainCVS[0] = new Instances(dataSet);
        testCVS[0] = new Instances(dataSet);

        //svm = new LibSVM();
        //svm.setCost(21.3);
        //svm.setGamma(0.19);
        //try {
        //    svm.buildClassifier(dataSet);
        //} catch (Exception e) {
        //    throw new RuntimeException(e);
        //}

        //return svm;
        
        LibSVM bSvm = null;
        
        double bestC = 0;
        double cStart = 0;
        double cGap = 0;
        double cEnd = 0;

        if(cParams.length == 1) {
            bestC = Double.valueOf(cParams[0]);
        } else {
            cStart = Double.valueOf(cParams[0]);
            cGap = Double.valueOf(cParams[1]);
            cEnd = Double.valueOf(cParams[2]);
        }

        double bestG = 0;
        double gStart = 0;
        double gGap = 0;
        double gEnd = 0;
        if(gParams.length == 1) {
            bestG = Double.valueOf(gParams[0]);
            gStart = Double.valueOf(gParams[0]);
            gEnd = Double.valueOf(gParams[0]);
        } else {
            gStart = Double.valueOf(gParams[0]);
            gGap = Double.valueOf(gParams[1]);
            gEnd = Double.valueOf(gParams[2]);
        }

        double bestP = Double.MIN_VALUE;

        if(cStart <= 0) cStart = cGap;
        if(gStart <0)   gStart = 0;
        
        int try_cnt = 0;
        double last_result = 0;

        if(cParams.length > 1) {
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
                System.out.println(String.format("c=%f g=%f p=%.8f", d1, d2, p));
                if(p > bestP) {
                    bestP = p;
                    bestC = d1;
                    bestG = d2;
                    bSvm = svm;
                }
                if(p > last_result) {
                    try_cnt = 0;
                } else {
                    try_cnt --;
                }

                last_result = p;
                if(try_cnt <= max_cnt || p >= 99.0)  break;
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
                System.out.println(String.format("c=%f g=%f p=%.8f", d1, d2, p));
                if(p > bestP) {
                    bestP = p;
                    bestC = d1;
                    bestG = d2;
                    bSvm = svm;
                }
                if(p > last_result) {
                    try_cnt = 0;
                } else {
                    try_cnt --;
                }

                last_result = p;
                if(try_cnt <= max_cnt || p >= 99.0)  break;
            }
        }

        if(gParams.length > 1) {
            bestP = Double.MIN_VALUE;
            last_result = 0;
            try_cnt = 0;
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
                System.out.println(String.format("c=%f g=%f p=%.8f", d1, d2, p));
                if(p > bestP) {
                    bestP = p;
                    bestC = d1;
                    bestG = d2;
                    bSvm = svm;
                }
                if(p > last_result) {
                    try_cnt = 0;
                } else {
                    try_cnt --;
                }

                last_result = p;
                if(try_cnt <= max_cnt)  break;
            }
        }
    
        System.out.println("++++++++++++++++++++++++++++++++++++++++++");
        System.out.println(String.format("c=%f g=%f p=%.2f", bestC, bestG, bestP));

        if(bSvm == null) {
            svm = new LibSVM();
            svm.setCost(bestC);
            svm.setGamma(bestG);
            try {
                svm.buildClassifier(dataSet);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            svm = bSvm;
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
