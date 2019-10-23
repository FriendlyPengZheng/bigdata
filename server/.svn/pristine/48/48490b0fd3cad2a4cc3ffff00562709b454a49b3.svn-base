package com.taomee.bigdata.datamining.seerV2;

public class Math
{
    public static double[] AvgStd(double[] data, int start, int end) {
        if(start < 0 || end >= data.length) {
            return null;
        }

        int cnt = end - start + 1;
        double avg = 0;
        for(int i=start; i<=end; i++) {
            avg += data[i];
        }
        avg /= cnt;

        double sum = 0;
        for(int i=start; i<=end; i++) {
            sum += ((data[i] - avg) * (data[i] - avg));
        }
        sum /= cnt;
        sum = java.lang.Math.sqrt(sum);
        return new double[] {
            avg, sum
        };
    }
}
