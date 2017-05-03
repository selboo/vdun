package vdun;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

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
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		Map vdunConf = mapper.readValue(new FileInputStream("/vagrant/vdun.yml"), Map.class);
		System.out.println(vdunConf);

		TopologyBuilder builder = new TopologyBuilder();

		builder.setSpout("input", new LogFileSpout());
		builder.setBolt("filter", new FilterBolt()).shuffleGrouping("input");
		builder.setBolt("detect", new DetectBolt()).fieldsGrouping("filter", new Fields("domain", "ip"));
		builder.setBolt("brain", new BrainBolt()).shuffleGrouping("detect");
		builder.setBolt("output", new OutputBolt()).shuffleGrouping("detect");

		Config config = new Config();
		config.setDebug(true);
		config.setMaxTaskParallelism(1);
		config.putAll(vdunConf);

		StormTopology topology = builder.createTopology();
		LocalCluster cluster = new LocalCluster();
		cluster.submitTopology("vdun", config, topology);
	}
}
