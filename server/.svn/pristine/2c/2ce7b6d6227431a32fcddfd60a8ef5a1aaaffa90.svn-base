package com.taomee.bigdata.datamining.seerV2;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.IOException;
import java.util.Iterator;
import java.lang.StringBuffer;

public class TrainSetReducer extends MapReduceBase implements Reducer<Text, Text, Text, NullWritable>
{
    private Text outputKey = new Text();
    private NullWritable outputValue = NullWritable.get();

    private int nday = 0;
    private int isTrain = 0;
    private int loginCnt[] = null;  //每天是否登陆
    private double loginOltm[] = null; //每天登陆时长
    private int payCnt[] = null;    //每天付费次数
    private double paySum[] = null; //每天付费额
    private double loginAvg[] = null;//登陆时长周平均数
    private double loginStd[] = null;//登陆时长周标准差
    private int payCntDistr[] = null;//周付费次数分布
    private int paySumDistr[] = null;//周付费额分布
    private MultipleOutputs mos = null;

    public void configure(JobConf job) {
        mos = new MultipleOutputs(job);
        nday = Integer.valueOf(job.get("n"));
        isTrain = job.get("type").compareTo("train");

        loginCnt = new int[nday+1];
        loginOltm = new double[nday+1];
        payCnt = new int[nday+1];
        paySum = new double[nday+1];
        loginAvg = new double[nday/7];
        loginStd = new double[nday/7];
        payCntDistr = new int[nday/7];
        paySumDistr = new int[nday/7];
    }

    public void close() throws IOException {
        mos.close();
    }

    private void clear() {
        clear(loginCnt);
        clear(loginOltm);
        clear(payCnt);
        clear(paySum);
        clear(loginAvg);
        clear(loginStd);
        clear(payCntDistr);
        clear(paySumDistr);
    }

    private void clear(int a[]) {
        for(int i=0; i<a.length; i++) a[i] = 0;
    }

    private void clear(double a[]) {
        for(int i=0; i<a.length; i++) a[i] = 0;
    }

    private int getPayCntDistr(int pcnt[], int start, int end) {
        int cnt = 0;
        for(int i=start; i<=end; i++) cnt += pcnt[i];
        if(cnt < 1) {
            return 0;
        } else if(cnt < 2) {
            return 1;
        } else if(cnt < 3) {
            return 2;
        } else if(cnt < 4) {
            return 3;
        } else if(cnt < 5) {
            return 4;
        } else if(cnt < 6) {
            return 5;
        } else if(cnt < 11) {
            return 6;
        } else if(cnt < 21) {
            return 7;
        } else if(cnt < 31) {
            return 8;
        } else if(cnt < 41) {
            return 9;
        } else if(cnt < 51) {
            return 10;
        } else {
            return 11;
        }
    }

    private int getPaySumDistr(double psum[], int start, int end) {
        double sum = 0.0;
        for(int i=start; i<=end; i++) sum += psum[i];
        if(sum == 0.0) {
            return 0;
        } else if(sum < 5) {
            return 1;
        } else if(sum < 10) {
            return 2;
        } else if(sum < 11) {
            return 3;
        } else if(sum < 15) {
            return 4;
        } else if(sum < 20) {
            return 5;
        } else if(sum < 30) {
            return 6;
        } else if(sum < 40) {
            return 7;
        } else if(sum < 50) {
            return 8;
        } else if(sum < 60) {
            return 9;
        } else if(sum < 70) {
            return 10;
        } else if(sum < 80) {
            return 11;
        } else if(sum < 90) {
            return 12;
        } else if(sum < 100) {
            return 13;
        } else if(sum < 101) {
            return 14;
        } else if(sum < 120) {
            return 15;
        } else if(sum < 121) {
            return 16;
        } else if(sum < 150) {
            return 17;
        } else if(sum < 200) {
            return 18;
        } else if(sum < 300) {
            return 19;
        } else if(sum < 500) {
            return 20;
        } else if(sum < 1000) {
            return 21;
        } else {
            return 22;
        }
    }

    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, NullWritable> output, Reporter reporter) throws IOException {
        boolean allpay = false; //累积付费额是否达到标准
        boolean actived = false;
        boolean keep = false;
        boolean label = false;
        int activeDay = 0;
        clear();
        int index;
        int ivalue;
        double dvalue;
        while(values.hasNext()) {
            String v = values.next().toString();
            String items[] = v.split("\t");
            int type = Integer.valueOf(items[0]);
            switch(type) {
                case Type.LOGOUT:
                    //index oltm
                    index = Integer.valueOf(items[1]);
                    ivalue = Integer.valueOf(items[2]);
                    loginCnt[index] = 1;
                    loginOltm[index] += ivalue;
                    actived = true;
                    break;
                case Type.ACPAY:
                    //index amt
                    index = Integer.valueOf(items[1]);
                    dvalue = Double.valueOf(items[2])/100.0;
                    payCnt[index] ++;
                    paySum[index] += dvalue;
                    break;
                case Type.ACTIVEDAY:
                    activeDay = Integer.valueOf(items[1]);
                    break;
                case Type.KEEP:
                    keep = true;
                    break;
                case Type.ALLPAY:
                    allpay = true;
                    break;
                case Type.LABEL:
                    label = true;
                    break;
                default:
                    break;
            }
        }

        if(!allpay || !actived) return ;

        for(int i=0; i<loginAvg.length; i++) {
            int start = i*7+1;
            int end   = i*7+7;
            double as[] = Math.AvgStd(loginOltm, start, end);
            loginAvg[i] = as[0];
            loginStd[i] = as[1];

            payCntDistr[i] = getPayCntDistr(payCnt, start, end);
            paySumDistr[i] = getPaySumDistr(paySum, start, end);
        }

        StringBuffer buf = new StringBuffer(key.toString() + ",");

        //登陆情况
        for(int i=1; i<loginCnt.length; i++) {
            buf.append(" " + loginCnt[i]);
            buf.append(" " + (loginOltm[i] >= 600 ? 1 : 0));
        }

        ////登陆时长
        //for(int i=0; i<loginAvg.length; i++) {
        //    buf.append(" " + String.format("%.2f", loginAvg[i]));
        //    buf.append(" " + String.format("%.2f", loginStd[i]));
        //}

        ////付费
        //for(int i=0; i<payCntDistr.length; i++) {
        //    buf.append(" " + payCntDistr[i]);
        //    buf.append(" " + paySumDistr[i]);
        //}

        //if(isTrain == 0) {
        //    if(!keep) {
        //        outputKey.set(buf.toString() + ":lost");
        //        mos.getCollector("first", reporter).collect(outputKey, outputValue);
        //    } else {
        //        outputKey.set(buf.toString() + ":keep");
        //        mos.getCollector("first", reporter).collect(outputKey, outputValue);

        //        if(label) {
        //            outputKey.set(buf.toString() + ":keep");
        //            mos.getCollector("second", reporter).collect(outputKey, outputValue);
        //        } else {
        //            outputKey.set(buf.toString() + ":lost");
        //            mos.getCollector("second", reporter).collect(outputKey, outputValue);
        //        }
        //    }
        //} else {
        //    outputKey.set(buf.toString());
        //    output.collect(outputKey, outputValue);
        //}
        
        if(isTrain == 0) {
            if(keep && label) {
                outputKey.set(buf.toString() + ":kk");
            } else if(keep && !label) {
                outputKey.set(buf.toString() + ":kl");
            } else {
                outputKey.set(buf.toString() + ":ll");
            }
            //} else if(!keep && label) {
            //    outputKey.set(buf.toString() + ":lk");
            //} else if(!keep && !label) {
            //    outputKey.set(buf.toString() + ":ll");
            //}
            output.collect(outputKey, outputValue);

            //if(keep) {
            //    outputKey.set(buf.toString() + ":k");
            //    mos.getCollector("first", reporter).collect(outputKey, outputValue);
            //} else {
            //    outputKey.set(buf.toString() + ":l");
            //    mos.getCollector("first", reporter).collect(outputKey, outputValue);
            //}

            //if(label) {
            //    outputKey.set(buf.toString() + ":k");
            //    mos.getCollector("second", reporter).collect(outputKey, outputValue);
            //} else {
            //    outputKey.set(buf.toString() + ":l");
            //    mos.getCollector("second", reporter).collect(outputKey, outputValue);
            //}
        }

    }

}
