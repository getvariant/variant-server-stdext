package com.variant.extapi.std.flush;

import com.variant.server.api.FlushableTraceEvent;
import com.variant.server.api.TraceEventFlusher;
import com.variant.share.schema.Variation.Experience;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * An implementation of {@link TraceEventFlusher}, which appends trace events
 * to the application logger. This is the default, out of the box event flusher,
 * which is completely independent of the operational environment. Probably not for production use. 
 * <p>
 * Configuration.
 * <ul>
 * <li><code>level</code> - specifies the logging level to be used. Defaults to 'INFO'.<br/>
 * </ul>
 * Example:<br/>
 * <code>variant.event.flusher.class.init = {level="INFO"}</code>
 * 
 * 
 * @since 0.5
 */
public class TraceEventFlusherServerLog implements TraceEventFlusher {
	
	private static final Logger LOG = LoggerFactory.getLogger(TraceEventFlusherServerLog.class);
	private static enum Level {Trace, Debug, Info, Error}
	private Level level = Level.Info;  // The default.

	public TraceEventFlusherServerLog(String init) {
		if (init != null) {
			Map<String, ?> initMap = new Yaml().load(init);
			level = Level.valueOf((String) initMap.get("level"));
		}
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
