package vdun.bolt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;

public class BrainBolt extends BaseBasicBolt {
    private static final Logger LOG = LoggerFactory.getLogger(BrainBolt.class);

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
    }

    public void execute(Tuple tuple, BasicOutputCollector collector) {

    }
}