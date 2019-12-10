package com.ddddd;

import org.apache.commons.io.FileUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Spark {


    public static void main(String[] args) {
        //winutils.exe
        System.setProperty("hadoop.home.dir",new File(".").getAbsolutePath());
        //no need to manual delete output folder for rerun
        try {
            FileUtils.deleteDirectory(new File("output"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        SparkConf sparkConf = new SparkConf().setMaster("local").setAppName("Word Counter");
        JavaSparkContext sparkContext = new JavaSparkContext(sparkConf);

        JavaRDD<String> file = sparkContext.textFile("test.txt");

        JavaRDD<String> wordsFromFile = file.flatMap(content -> Arrays.asList(content.split(" ")).iterator());

        JavaPairRDD countData = wordsFromFile.mapToPair(t -> new Tuple2(t, 1)).reduceByKey((x, y) -> (int) x + (int) y);

        countData.saveAsTextFile("output");
    }
}
