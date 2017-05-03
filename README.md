# 微盾

## 基本规则

对于每一个 IP ，如果其某一分钟内的访问请求满足以下任意条件，标记为异常 IP。

- rate_most_path > $threshold AND pv > $threshold
- rate_most_ua > $threshold AND pv > $threshold
- rate_most_referer > $threshold AND pv > $threshold

## Topylogy 说明

input（输入）-> enrich（提取有效日志特征）-> risk（计算域名风险等级）
   -> detect（检测异常IP） -> output（存储）／brain（处理异常IP）

## 日志格式

配置日志格式为 json 格式，日志需包含以下字段：

```
{
   "request_length" : "$request_length",
   "body_bytes_sent" : "$body_bytes_sent",
   "http_referer" : "$http_referer",
   "http_user_agent" : "$http_user_agent",
   "request_uri" : "$request_uri",
   "hostname" : "$hostname",
   "upstream_response_time" : "$upstream_response_time",
   "upstream_addr" : "$upstream_addr",
   "request_time" : "$request_time",
   "remote_addr" : "$remote_addr",
   "remote_user" : "$remote_user",
   "scheme" : "$scheme",
   "http_host" : "$host",
   "http_x_forwarded_for" : "$http_x_forwarded_for",
   "status" : "$status",
   "method" : "$request_method",
   "time_local" : "$time_iso8601",
   "upstream_status" : "$upstream_status"
}
```

## 规则语法

https://commons.apache.org/proper/commons-jexl/reference/syntax.html