package com.taomee.bigdata.datamining.seerv3;

class ClassValue {
    private int[] value;
    private int classValue;
    private double totalCnt;

    public ClassValue(int valueNum, int valueIndex) {
        value = new int[valueNum];
        value[valueIndex] ++;
        totalCnt++;
        classValue = valueIndex;
    }

    public ClassValue addValue(int valueIndex) {
        value[valueIndex]++;
        totalCnt++;
        if(value[valueIndex] > value[classValue]) {
            classValue = valueIndex;
        }

        return this;
    }

    public double[] getValue() {
        double[] ret = new double[value.length];
        for(int i=0; i<ret.length; i++) {
            ret[i] = value[i]/totalCnt;
        }
        return ret;
    }

    public double[] getValue(double[] weights) {
        double wsum = 0;
        for(int i=0; i<weights.length; i++) {
            wsum += (weights[i]*value[i]);
        }
        double[] ret = new double[value.length];
        for(int i=0; i<ret.length; i++) {
            ret[i] = value[i]*weights[i]/wsum;
        }
        return ret;
    }

    public int[] get() {
        return value;
    }
}
