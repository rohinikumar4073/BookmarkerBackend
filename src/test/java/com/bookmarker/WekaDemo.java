package com.bookmarker;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.JSONLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Random;

public class WekaDemo {

    public static void main(String[] args) throws Exception {
        JSONLoader jsonLoader =new JSONLoader();
        jsonLoader.setSource(new File("/Users/VISHNU/myProject/history2.json"));
        Instances train = jsonLoader.getDataSet();
        train.setClassIndex(train.numAttributes() - 1);
         J48 nb = new J48();
        Evaluation evaluation = new Evaluation(train);
        evaluation.crossValidateModel(nb, train, 10, new Random(1));
        System.out.println(evaluation.toSummaryString());
        SerializationHelper.write("/some/where/j48.model", nb);



    }
}
