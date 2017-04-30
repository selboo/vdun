package vdun.bolt;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

public class OutputBolt extends BaseRichBolt {
    public static final Logger LOG = LoggerFactory.getLogger(OutputBolt.class);

    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
    }

    public void execute(Tuple tuple) {
        LOG.info("output: {}", tuple);
    }
}