package com.taomee.bigdata.datamining.seerv3;

import weka.core.FastVector;
import weka.core.Instances;
import weka.core.Instance;
import weka.core.Attribute;
import weka.classifiers.Classifier;

class TwoStepsBitClassifier extends Classifier {
    private BitClassifier first = new BitClassifier();
    private BitClassifier second = new BitClassifier();

    public void cleanDetail() {
        first.cleanDetail();
        second.cleanDetail();
    }

    public String getDetail() {
        return first.getDetail() + "\n" + second.getDetail();
    }

    public void buildClassifier(Instances instances) {
        FastVector fv = new FastVector(2);
        fv.addElement("k");
        fv.addElement("l");

        Instances firstIns = new Instances(instances);
        Instances secondIns = new Instances(instances);
        firstIns.setClassIndex(0);
        secondIns.setClassIndex(0);

        firstIns.deleteAttributeAt(instances.classIndex());
        Attribute firstAtt = new Attribute("label", fv);
        firstIns.insertAttributeAt(firstAtt, instances.classIndex());

        secondIns.deleteAttributeAt(instances.classIndex());
        Attribute secondAtt = new Attribute("label", fv);
        secondIns.insertAttributeAt(secondAtt, instances.classIndex());

        firstIns.setClassIndex(instances.classIndex());
        secondIns.setClassIndex(instances.classIndex());

        int all = instances.numInstances();
        for(int i=all-1; i>=0; i--) {
            Instance instance = instances.instance(i);
            int c = (int)(instance.classValue());
            switch(c) {
                case 0://kk
                    firstIns.instance(i).setClassValue("k");
                    secondIns.instance(i).setClassValue("k");
                    break;
                case 1://kl
                    firstIns.instance(i).setClassValue("k");
                    secondIns.instance(i).setClassValue("l");
                    break;
                case 2://ll
                    firstIns.instance(i).setClassValue("l");
                    secondIns.delete(i);
                    break;
            }
        }

        first.buildClassifier(firstIns);
        second.buildClassifier(secondIns);
    }

    //public double classifyInstance(Instance instance) {
    //    double fKeep = 0.5;
    //    double sLost = 0.5;
    //    double[] r = first.distributionForInstance(instance);
    //    //System.out.println(r[0] + "  " + r[1]);
    //    if(r[0] >=  fKeep) {
    //        r = second.distributionForInstance(instance);
    //        if(r[1] <= sLost) {
    //            return 0;
    //        } else {
    //            return 1;
    //        }
    //    } else {
    //        return 2;
    //    }
    //}

    public double classifyInstance(Instance instance) {
        int r = (int)first.classifyInstance(instance);
        if(r == 0) {
            r = (int)second.classifyInstance(instance);
            if(r == 0) {
                return 0;
            } else {
                return 1;
            }
        } else {
            return 2;
        }
    }

    public double[] distributionForInstance(Instance instance) {
        return null;
    }

    public static void main(String[] args) throws java.io.IOException {
        System.out.println(args.length);
        for(int i=0; i<args.length; i++) {
            System.out.println(args[i]);
        }
        Instances instances = new Instances(new java.io.FileReader(args[0]));
        instances.setClassIndex(instances.numAttributes()-1);
        Attribute classAtt = instances.classAttribute();
        TwoStepsBitClassifier c = new TwoStepsBitClassifier();
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
