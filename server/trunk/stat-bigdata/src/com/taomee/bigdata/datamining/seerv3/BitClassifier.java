package com.taomee.bigdata.datamining.seerv3;

import java.util.HashMap;
import java.util.BitSet;
import java.util.Iterator;

import weka.core.Instances;
import weka.core.Instance;
import weka.core.Attribute;
import weka.classifiers.Classifier;

class BitClassifier extends Classifier {
    private HashMap<BitSet, ClassValue> _instances;
    private HashMap<BitSet, double[]> _notFound;
    private double weights[];
    private String[] className;
    private int classNum;
    private int classIndex;
    private int numAttributes;
    private int numInstances;

    private int fromInstances;
    private int fromFound;
    private int notFound;

    public void buildClassifier(Instances instances) {
        //设置类别
        Attribute classAtt = instances.classAttribute();
        classNum = classAtt.numValues();
        className = new String[classNum];
        for(int i=0; i<classNum; i++) {
            className[i] = classAtt.value(i);
        }
        weights = new double[classNum];

        //对每个instance，记录分类结果
        numInstances = instances.numInstances();
        numAttributes = instances.instance(0).numAttributes();
        classIndex = instances.instance(0).classIndex();
        _instances = new HashMap<BitSet, ClassValue>(numInstances/10);
        _notFound = new HashMap<BitSet, double[]>(numInstances/10);
        for(int i=0; i<numInstances; i++) {
            Instance instance = instances.instance(i);
            int classValue = (int)(instance.classValue());
            weights[classValue] ++;

            BitSet bitSet = InstanceToBitSet(instance);
            //设置对应的class值
            if(_instances.containsKey(bitSet)) {
                _instances.put(bitSet, _instances.get(bitSet).addValue(classValue));
            } else {
                _instances.put(bitSet, (new ClassValue(classNum, classValue)));
            }
        }

        for(int i=0; i<classNum; i++) {
            weights[i] = 1 - (weights[i] / numInstances);
        }
    }

    public double classifyInstance(Instance instance) {
        double[] d = distributionForInstance(instance);
        double max = Double.MIN_VALUE;
        int index = 0;
        //System.out.println("class num = " + d.length + "; ");
        for(int i=0; i<d.length; i++) {
            //System.out.print(i + " => " + d[i] + " ");
            if(d[i] > max) {
                max = d[i];
                index = i;
            }
        }
        System.out.println();
        return index;
    }

    public double[] distributionForInstance(Instance instance) {
        BitSet bitSet = InstanceToBitSet(instance);
        ClassValue classValue = _instances.get(bitSet);
        if(classValue != null) {
            //找到已经有的
            fromInstances ++;
            return classValue.getValue(weights);
        } else {
            double[] ret = _notFound.get(bitSet);
            if(ret == null) {
                notFound ++;
                ret = distributionForBitSet(bitSet);
                _notFound.put(bitSet, ret);
                return ret;
            } else {
                fromFound ++;
                return ret;
            }
        }
    }

    public void cleanDetail() {
        fromInstances = 0;
        notFound = 0;
        fromFound = 0;
    }

    public String getDetail() {
        return String.format("from instances %d not found %d from found %d _instances.size()=%d _notFound.size()=%d",
                fromInstances, notFound, fromFound, _instances.size(), _notFound.size());
    }

    private BitSet InstanceToBitSet(Instance instance) {
        BitSet bitSet = new BitSet(numAttributes);
        //将instance转换成bitset
        for(int j=0; j<numAttributes; j++) {
            if(j != classIndex) {
                double v = instance.value(j);
                if(v != 0.0) {
                    bitSet.set(j);
                }
            }
        }

        return bitSet;
    }

    private double[] distributionForBitSet(BitSet instance) {
        boolean find = false;
        BitSet bitSet = (BitSet)instance.clone();
        int[] values = new int[classNum];
        double totalCnt = 0;
        int minDistance = bitSet.size()+1;

        Iterator<BitSet> it = _instances.keySet().iterator();
        while(it.hasNext()) {
            BitSet key = it.next();
            ClassValue cv = _instances.get(key);
            int[] v = cv.get();
            BitSet tmpKey = new BitSet(key.size());
            tmpKey.andNot(key);
            tmpKey.xor(bitSet);
            int dis = key.cardinality();
            if(dis < minDistance) {
                for(int k=0; k<v.length; k++) {
                    values[k] = v[k];
                    totalCnt = v[k];
                }
                minDistance = dis;
            } else if(dis == minDistance) {
                for(int k=0; k<v.length; k++) {
                    values[k] += v[k];
                    totalCnt += v[k];
                }
            }
        }

        double[] ret = new double[classNum];
        for(int k=0; k<classNum; k++) {
            ret[k] = values[k]/totalCnt*weights[k];
        }
        System.out.println("minDistance = " + minDistance);
        return ret;
    }

    public static void main(String[] args) throws java.io.IOException {
        System.out.println(args.length);
        for(int i=0; i<args.length; i++) {
            System.out.println(args[i]);
        }
        Instances instances = new Instances(new java.io.FileReader(args[0]));
        instances.setClassIndex(instances.numAttributes()-1);
        Attribute classAtt = instances.classAttribute();
        BitClassifier c = new BitClassifier();
        c.buildClassifier(instances);

        int numInstances = instances.numInstances();
        int r = 0;
        c.cleanDetail();
        for(int i=0; i<numInstances; i++) {
            Instance instance = instances.instance(i);
            double v = c.classifyInstance(instance);
            if(v == instance.classValue()) {
                r++;
            }
            System.err.println(String.format("test %s => %s",
                        classAtt.value((int)instance.classValue()), classAtt.value((int)v)));
        }

        System.out.println(String.format("%d %d %.2f%%", r, numInstances, r*100.0/numInstances));
        System.out.println(c.getDetail());

        instances = new Instances(new java.io.FileReader(args[1]));
        instances.setClassIndex(instances.numAttributes()-1);
        numInstances = instances.numInstances();
        r = 0;
        c.cleanDetail();
        for(int i=0; i<numInstances; i++) {
            Instance instance = instances.instance(i);
            double v = c.classifyInstance(instance);
            if(v == instance.classValue()) {
                r++;
            }
            System.err.println(String.format("run %s => %s",
                        classAtt.value((int)instance.classValue()), classAtt.value((int)v)));
        }

        System.out.println(String.format("%d %d %.2f%%", r, numInstances, r*100.0/numInstances));
        System.out.println(c.getDetail());
    }
}
