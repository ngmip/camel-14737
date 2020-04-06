package com.issue.jira.camel14737;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.file.FileEndpoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author nicolas.gillet
 */
@Component
public class LogsToDbRoute extends RouteBuilder {

    @Value("${fileComponentUri}")
    private String fileComponentUri;

    @Override
    public void configure() {
        from(createSource(fileComponentUri))
                .log("processing file: ${header.CamelFileName}")
                .unmarshal().gzipDeflater()
                .split(body().tokenize("\n")).streaming()
                .bean(new LineExtractor())
                .to("sql:insert into extracted_log (log) values (:#line)");
    }

    private static class LineExtractor {
        public Map<String, String> extract(Exchange exchange) {
            Map<String, String> values = new HashMap<>();
            String line = exchange.getIn().getBody(String.class);
            if (line.length() > 1000) {
                line = line.substring(0, 1000);
            }
            values.put("line", line);
            return values;
        }
    }

    private Endpoint createSource(String fileUri) {
        FileEndpoint endpoint = getContext().getEndpoint(fileUri, FileEndpoint.class);
        endpoint.setInclude(".*\\.gz");
        endpoint.setNoop(true);
        return endpoint;
    }
}
