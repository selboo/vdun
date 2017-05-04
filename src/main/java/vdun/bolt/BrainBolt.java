package vdun.bolt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

public class BrainBolt extends BaseRichBolt {
    private static final Logger LOG = LoggerFactory.getLogger(BrainBolt.class);
    private JexlEngine jexl;
    private List<JexlExpression> exprs;

    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        List<Map<String, String>> policyList = (List<Map<String, String>>)stormConf.get("policy");
        LOG.info("vdun.policy: {}", policyList);

        jexl = new JexlBuilder().create();
        exprs = new ArrayList<JexlExpression>();
        for (Map<String, String> policy : policyList) {
            exprs.add(jexl.createExpression(policy.get("if")));
        }
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
    }

    public void execute(Tuple tuple) {
        Map<String, Object> feature = (Map<String, Object>)tuple.getValue(0);
        JexlContext jc = new MapContext(feature);
        for (JexlExpression e : exprs) {
            Object result = e.evaluate(jc);
            LOG.info("result: {}", result);
        }
    }
}