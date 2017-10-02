package com.bill.storm;

import java.util.Map;
import java.util.Random;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.shade.com.google.common.collect.Maps;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 单词统计
 * @author bill
 * @date 2017年9月16日 下午8:15:10
 */
public class WordCountTopology {
	
	/**
	 * 
	 * 编写spout ,继承一个基类，负责从数据源获取数据
	 * @author bill
	 * @date 2017年9月16日 下午8:21:46
	 */
	public static class RandomSentenceSpout extends BaseRichSpout{
		
		private static final long serialVersionUID = 6102239192526611945L;

		private static final Logger LOGGER = LoggerFactory.getLogger(RandomSentenceSpout.class);
		
		private SpoutOutputCollector collector;
		private Random random;

		/**
		 * 当一个Task被初始化的时候会调用此open方法,
		 * 一般都会在此方法中对发送Tuple的对象SpoutOutputCollector和配置对象TopologyContext初始化
		 */
		public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
			this.collector = collector;
			this.random = new Random();
		}

		/**
		 * 这个spout类，之前说过，最终会运行在task中，某个worker进程的某个executor线程内部的某个task中
		 * 那个task会负责去不断的无限循环调用nextTuple()方法
		 * 只要的话呢，无限循环调用，可以不断发射最新的数据出去，形成一个数据流
		 */
		public void nextTuple() {
			String[] sentences = new String[]{
					 "I used to watch her from my kitchen widow"
					, "she seemed so small as she muscled her way through the crowd of boys on the playground"
					, "The school was across the street from our home and I would often watch the kids as they played during recess"
					, "A sea of children, and yet tome"
					, "she stood out from them all"};
			String sentence = sentences[random.nextInt(sentences.length)];
			LOGGER.info(" ★★★  发射 sentence 数据 > {}", sentence);  
			// 这个values，你可以认为就是构建一个tuple,tuple是最小的数据单位，无限个tuple组成的流就是一个stream,通过 emit 发送数据到下游bolt tuple
			this.collector.emit(new Values(sentence));
		}

		/**
		 * 用于声明当前Spout的Tuple发送流的域名字。Stream流的定义是通过OutputFieldsDeclare.declareStream方法完成的
		 * 通俗点说法：就是这个方法是定义一个你发射出去的每个tuple中的每个field的名称是什么，作为下游 bolt 中 execute 接收数据 key 
		 */
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			declarer.declare(new Fields("sentence"));
		}
	}
	
	
	/**
	 * 
	 * 编写一个bolt，用于切分每个单词，同时把单词发送出去
	 * @author bill
	 * @date 2017年9月16日 下午8:27:45
	 */
	public static class SplitSentenceBolt extends BaseRichBolt{
		
		private static final long serialVersionUID = -4758047349803579486L;
		
		private OutputCollector collector;

		/**
		 * 当一个Task被初始化的时候会调用此prepare方法,对于bolt来说，第一个方法，就是prepare方法
		 * OutputCollector，这个也是Bolt的这个tuple的发射器,一般都会在此方法中对发送Tuple的对象OutputCollector初始化
		 */
		public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
			this.collector = collector;
		}
		
		/**
		 * 这是Bolt中最关键的一个方法，对于Tuple的处理都可以放到此方法中进行。具体的发送也是通过emit方法来完成的
		 * 就是说，每次接收到一条数据后，就会交给这个executor方法来执行
		 * 切分单词
		 */
		public void execute(Tuple input) {
			// 接收上游数据
			String sentence = input.getStringByField("sentence");
			String[] words = sentence.split(" ");
			for(String word : words){
				//发射数据
				this.collector.emit(new Values(word));
			}
			
		}

		/**
		 * 用于声明当前bolt的Tuple发送流的域名字。Stream流的定义是通过OutputFieldsDeclare.declareStream方法完成的
		 * 通俗点说法：就是这个方法是定义一个你发射出去的每个tuple中的每个field的名称是什么，作为下游 bolt 中 execute 接收数据 key 
		 * 定义发射出去的tuple，每个field的名称
		 */
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			declarer.declare(new Fields("word"));
		}
		
	}
	
	
	/**
	 * 
	 * 单词次数统计bolt
	 * @author bill
	 * @date 2017年9月16日 下午8:35:00
	 */
	public static class WordCountBolt extends BaseRichBolt{
		
		private static final Logger LOGGER = LoggerFactory.getLogger(WordCountBolt.class);

		private static final long serialVersionUID = -7114915627898482737L;
		
		private OutputCollector collector;
		
		Map<String,Long> countMap = Maps.newConcurrentMap();
		
		/**
		 * 当一个Task被初始化的时候会调用此prepare方法,对于bolt来说，第一个方法，就是prepare方法
		 * OutputCollector，这个也是Bolt的这个tuple的发射器,一般都会在此方法中对发送Tuple的对象OutputCollector初始化
		 */
		public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
			this.collector = collector;
		}

		/**
		 * 这是Bolt中最关键的一个方法，对于Tuple的处理都可以放到此方法中进行。具体的发送也是通过emit方法来完成的
		 * 就是说，每次接收到一条数据后，就会交给这个executor方法来执行
		 * 统计单词
		 */
		public void execute(Tuple input) {
			// 接收上游数据
			String word = input.getStringByField("word");
			Long count = countMap.get(word);
			if(null == count){
				count = 0L;
			}
			count ++;
			countMap.put(word, count);
			LOGGER.info(" ★★★  单词计数[{}] 出现的次数：{}", word, count); 
			//发射数据
			this.collector.emit(new Values(word,count));
		}
		
		/**
		 * 用于声明当前bolt的Tuple发送流的域名字。Stream流的定义是通过OutputFieldsDeclare.declareStream方法完成的
		 * 通俗点说法：就是这个方法是定义一个你发射出去的每个tuple中的每个field的名称是什么，作为下游 bolt 中 execute 接收数据 key 
		 * 定义发射出去的tuple，每个field的名称
		 */
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			declarer.declare(new Fields("word","count"));
		}
		
	}
	
	public static void main(String[] args) {
		//去将spout和bolts组合起来，构建成一个拓扑
		TopologyBuilder builder = new TopologyBuilder();
		
		// 第一个参数的意思，就是给这个spout设置一个名字
		// 第二个参数的意思，就是创建一个spout的对象
		// 第三个参数的意思，就是设置spout的executor有几个
		builder.setSpout("RandomSentence", new RandomSentenceSpout(), 2);
		builder.setBolt("SplitSentence", new SplitSentenceBolt(), 5)
		//为bolt 设置 几个task
		.setNumTasks(10)
		//设置流分组策略
		.shuffleGrouping("RandomSentence");
		
		// fieldsGrouping 这个很重要，就是说，相同的单词，从SplitSentenceSpout发射出来时，一定会进入到下游的指定的同一个task中
		// 只有这样子，才能准确的统计出每个单词的数量
		// 比如你有个单词，hello，下游task1接收到3个hello，task2接收到2个hello
		// 通过fieldsGrouping 可以将 5个hello，全都进入一个task
		builder.setBolt("wordCount", new WordCountBolt(), 10)
		//为bolt 设置 几个task
		.setNumTasks(20)
		//设置流分组策略
		.fieldsGrouping("SplitSentence", new Fields("word"));
		
		// 运行配置项
		Config config = new Config();
		
		//说明是在命令行执行，打算提交到storm集群上去
		if(args != null && args.length > 0){
			/** 
			 *  要想提高storm的并行度可以从三个方面来改造
			 *	worker(进程)>executor(线程)>task(实例)
			 *	增加work进程，增加executor线程，增加task实例
			 *	对应 supervisor.slots.port 中配置个数
			 *	这里可以动态设置使用个数
			 *  最好一台机器上的一个topology只使用一个worker,主要原因时减少了worker之间的数据传输
			 *  
			 *  注意：如果worker使用完的话再提交topology就不会执行，因为没有可用的worker，只能处于等待状态，把之前运行的topology停止一个之后这个就会继续执行了
			 */
			config.setNumWorkers(3);
			try {
				// 将Topolog提交集群
				StormSubmitter.submitTopology(args[0], config, builder.createTopology());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			// 说明是在eclipse里面本地运行
			
			// 用本地模式运行1个拓扑时，用来限制生成的线程的数量
			config.setMaxTaskParallelism(20);
			
			// 将Topolog提交本地集群
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology("wordCountTopology", config, builder.createTopology());
			
			// 为了测试模拟等待
			Utils.sleep(60000);
			// 执行完毕，关闭cluster
			cluster.shutdown();
		}
	}
}
