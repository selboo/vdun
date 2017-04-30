package vdun.bolt;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import vdun.util.Request;

public class FilterBolt extends BaseBasicBolt {
    private static final Logger LOG = LoggerFactory.getLogger(FilterBolt.class);
    private ObjectMapper json = new ObjectMapper();

    public void execute(Tuple tuple, BasicOutputCollector collector) {
        String log = tuple.getStringByField("log");

        // 解析并清理日志（直接用 Nginx 生成的 json 格式日志, 未对 “\” 进行转义）
        Request request;
        try {
            Map jsonMsg = json.readValue(StringUtils.replace(log, "\\x", "\\\\x"), Map.class);
            request = new Request(jsonMsg);
        } catch (Exception e) {
            LOG.error("json decode failed", e);
            return;
        }
        
        // TODO: 过滤不需要检测的日志
        String domain = (String)request.get("domain");
        String ip = (String)request.get("ip");

        collector.emit(new Values(domain, ip, request));
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("domain", "ip", "request"));
    }
}