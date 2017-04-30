package vdun;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.generated.StormTopology;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import storm.kafka.KafkaSpout;
import vdun.bolt.BrainBolt;
import vdun.bolt.DetectBolt;
import vdun.bolt.FilterBolt;
import vdun.bolt.OutputBolt;
import vdun.bolt.PvBolt;
import vdun.spout.LogFileSpout;

public class VDunTopology {
	public static void main(String[] args) throws Exception {
		TopologyBuilder builder = new TopologyBuilder();

		builder.setSpout("input", new LogFileSpout());
		builder.setBolt("filter", new FilterBolt()).shuffleGrouping("input");
		builder.setBolt("detect", new DetectBolt()).fieldsGrouping("filter", new Fields("domain", "ip"));
		// builder.setBolt("brain", new BrainBolt()).shuffleGrouping("detect");
		builder.setBolt("output", new OutputBolt()).shuffleGrouping("detect");

		Config config = new Config();
		config.setDebug(true);
		config.setMaxTaskParallelism(1);

		StormTopology topology = builder.createTopology();
		LocalCluster cluster = new LocalCluster();
		cluster.submitTopology("vdun", config, topology);
	}
}
