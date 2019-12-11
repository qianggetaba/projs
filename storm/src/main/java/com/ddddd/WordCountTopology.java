package com.ddddd;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class WordCountTopology {

    //原始数据转为tuple数据流
    static class MySpout extends BaseRichSpout {
        SpoutOutputCollector collector;

        //初始化
        @Override
        public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
            this.collector = spoutOutputCollector;
        }

        //storm会循环调用，不断的产生数据tuple(一 个 或 者 多 个 键 值 对 的 列 表)，模拟实时计算
        @Override
        public void nextTuple() {
            Utils.sleep(100);
            //The sentences that are randomly emitted
            String[] sentences = new String[]{ "the cow jumped over the moon", "an apple a day keeps the doctor away",
                    "four score and seven years ago", "snow white and the seven dwarfs", "i am at two with nature" };
            //Randomly pick a sentence
            String sentence = sentences[new Random().nextInt(sentences.length)];
            //Emit the sentence
            collector.emit(new Values(sentence));
        }

        //输出
        @Override
        public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
            outputFieldsDeclarer.declare(new Fields("firstStorm"));
        }
    }

    //分词
    static class MySplitBolt extends BaseRichBolt {
        OutputCollector outputCollector;
        @Override
        public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
            this.outputCollector = outputCollector;
        }

        @Override
        public void execute(Tuple tuple) {
            String line = tuple.getString(0);
            String[] words = line.replace(".","")
                    .replace(",","")
                    .replace("\n"," ")
                    .split(" ");
            for (String word : words) {
                outputCollector.emit(new Values(word,1));
            }
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
            outputFieldsDeclarer.declare(new Fields("word","num"));
        }
    }

    //统计
    static class MyCountBolt extends BaseRichBolt {
        OutputCollector collector;
        Map<String, Integer> map = new HashMap<String, Integer>();
        @Override
        public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
            this.collector = outputCollector;
        }

        @Override
        public void execute(Tuple tuple) {
            String word = tuple.getString(0);
            Integer num = tuple.getInteger(1);

            if (map.containsKey(word)) {
                Integer count = map.get(word);
                map.put(word, count + num);
            }else{
                map.put(word,1);
            }

            System.out.println("count:"+map);
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

        }
    }

    //storm实时流式全内存计算，不断计算句子中的词 wordCount
    // 1个spout不断产生句子
    // 10个bolt分隔句子为词
    // 2个bolt统计, 还可以在后面加一个bolt打印结果
    public static void main(String[] args) {
        TopologyBuilder topologyBuilder = new TopologyBuilder();
        topologyBuilder.setSpout("mySpout",new MySpout(),1);
        topologyBuilder.setBolt("mybolt1",new MySplitBolt(),10).shuffleGrouping("mySpout");
        topologyBuilder.setBolt("mybolt2",new MyCountBolt(),2).fieldsGrouping("mybolt1",new Fields("word"));

        Config config=new Config();
        config.setNumWorkers(2);

        //集群模式提交任务
        // StormSubmitter.submitTopologyWithProgressBar("mywordcount",config,topologyBuilder.createTopology());

        LocalCluster localCluster = new LocalCluster();
        localCluster.submitTopology("mywordcount",config,topologyBuilder.createTopology());

//        Utils.sleep(30000);  //从storm开始启动到代码这一行，时间太小会不打印map
//        localCluster.killTopology("mywordcount");
//        localCluster.shutdown();
    }
}
