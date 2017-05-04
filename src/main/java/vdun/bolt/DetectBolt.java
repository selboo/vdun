package vdun.bolt;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import backtype.storm.Config;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import backtype.storm.utils.TupleUtils;
import vdun.util.Feature;
import vdun.util.Request;

public class DetectBolt extends BaseRichBolt {
    private OutputCollector outputCollector;
    private Map<String, Feature> ipToFeature;
    private int interval;
    private int windowLength;
    private int elapsed;

    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.outputCollector = collector;
        this.ipToFeature = new HashMap<String,Feature>();

        this.elapsed = 0;
        Map<String, Long> conf = (Map<String, Long>)stormConf.get("detect");
        if (conf != null) {
            this.interval = conf.getOrDefault("interval", 3L).intValue();
            this.windowLength = conf.getOrDefault("window_length", 20L).intValue();
        }
    }

    public Map<String, Object> getComponentConfiguration() {
        Map<String, Object> conf = new HashMap<String,Object>();
        conf.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, 1);
        return conf;
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("feature"));
    }

    public void execute(Tuple tuple) {
        if (TupleUtils.isTick(tuple)) {
            if (++elapsed < interval) {
                return;
            } else {
                elapsed = 0;
            }

            Set<String> ipToRemove = new HashSet<String>();
            for (Entry<String, Feature> e: ipToFeature.entrySet()) {
                Feature f = e.getValue();
                if (f.empty()) {
                   ipToRemove.add(e.getKey()); 
                } else {
                    Map<String, Object> summary = f.getSummaryThenAdvance();
                    String[] attrs = e.getKey().split("/");
                    summary.put("domain", attrs[0]);
                    summary.put("ip", attrs[1]);
                    outputCollector.emit(new Values(summary));
                }
            }
            for (String key : ipToRemove) {
                ipToFeature.remove(key);
            }
        } else {
            Request request = (Request)tuple.getValueByField("request");
            String key = (String)request.get("domain") + "/" + (String)request.get("ip");
            Feature feature = ipToFeature.get(key);
            if (feature == null) {
                feature = new Feature(windowLength);
                ipToFeature.put(key, feature);
            }
            feature.update(request);
        }
    }
}