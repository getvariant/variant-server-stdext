package com.variant.spi.stdlib.flush;

import com.variant.server.spi.FlushableTraceEvent;
import com.variant.server.spi.TraceEventFlusher;
import com.variant.share.schema.Variation.Experience;
import com.variant.share.yaml.YamlMap;
import com.variant.share.yaml.YamlNode;
import com.variant.share.yaml.YamlScalar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

/**
 * An implementation of {@link TraceEventFlusher}, which appends trace events
 * to the application logger. This is the default, out of the box event flusher,
 * which is completely independent of the operational environment. Probably not for production use. 
 * <p>
 * Configuration.
 * An optional YAML map containing a single key:
 * <ul>
 * <li><code>level</code> - string -- The logging level to be used.
 * </ul>
 * Example:<br/>
 * <code>
 *   flusher:
 *     class: com.variant.spi.stdlib.flush.TraceEventFlusherServerLog
 *     init:
 *       level: Info
 * </code>
 * If no init is specified, Info is assumed.
 * 
 * @since 0.5
 */
public class TraceEventFlusherServerLog implements TraceEventFlusher {
	
	private static final Logger LOG = LoggerFactory.getLogger(TraceEventFlusherServerLog.class);
	private static enum Level {Trace, Debug, Info, Error}
	private final Level level;

	/**
	 * Init is either null, or has a single string
	 */
	public TraceEventFlusherServerLog(YamlNode<?> init) {
		level = Optional.ofNullable(init)
			.map(node -> {
				var map = ((YamlMap)node).value();
				var levelStr = ((YamlScalar<String>)map.get("level")).value();
				return Level.valueOf(levelStr);
			})
			.orElse(Level.Info);
	}

	@Override
	public void flush(FlushableTraceEvent[] events, int size) throws Exception {

		for (int i = 0; i < size; i++) {
			FlushableTraceEvent event = events[i];
			StringBuilder msg = new StringBuilder();
			msg.append("{")
    			.append("event_id:'").append(event.getId()).append("', ")
				.append("event_name:'").append(event.getName()).append("', ")
				.append("created_on:'").append(DateTimeFormatter.ISO_INSTANT.format(event.getTimestamp())).append("', ")
				.append("session_id:'").append(event.getSessionId()).append("'");

			if (!event.getLiveExperiences().isEmpty()) {
			   msg.append(", event_experiences:[");
			   boolean first = true;
   			for (Experience e: event.getLiveExperiences()) {
   			   if (first) first = false;
   			   else msg.append(", ");
   			   msg.append("{")
               .append("test_name:'").append(e.getVariation().getName()).append("', ")
               .append("experience_name:'").append(e.getName()).append("', ")
               .append("is_control:").append(e.isControl())
               .append("}");
            }
   			msg.append("]");
			}			
			
	      if (!event.getAttributes().isEmpty()) {
            msg.append(", event_attributes:[");
            boolean first = true;
	         for (Map.Entry<String, String> param: event.getAttributes().entrySet()) {
	            if (first) first = false;
	            else msg.append(", ");
	            msg.append("{")
	            .append("key:'").append(param.getKey()).append("', ")
	            .append("value:'").append(param.getValue()).append("'")
	            .append("}");
	         }
	         msg.append("]");
	      }     

	      msg.append("}");

	      switch (level) {
	      case Trace: LOG.trace(msg.toString()); break;
	      case Debug: LOG.debug(msg.toString()); break;
	      case Info: LOG.info(msg.toString()); break;
	      case Error: LOG.error(msg.toString()); break;
	      }
		}
								
	}

}
