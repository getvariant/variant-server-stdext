package com.variant.server.ext.demo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.variant.server.api.EventFlusher;
import com.variant.server.api.FlushableEvent;

/**
 * An implementation of {@link EventFlusher}, which shuts down a toggle 
 * <p>
 * Configuration:
 * <ul>
 * <li><code>attrName</code> -
 * </ul>
 * Example:<br/>
 * <code>variant.event.flusher.class.init = {init="INFO"}</code>
 * 
 * 
 * @since 0.9
 */
public class EventFlusherToggler implements EventFlusher {
	
	private static final Logger LOG = LoggerFactory.getLogger(EventFlusherToggler.class);

	private String attrName = null;
	private String schemaFileName = null;
	
	public EventFlusherToggler(Config config) {
		attrName = config.getString("attrName");
		schemaFileName = config.getString("schemaFileName");
		
		if (attrName == null) {
			throw new RuntimeException("attrName is missing.");
		}

		if (schemaFileName == null) {
			throw new RuntimeException("schemaFileName is missing.");
		}
	}

	@Override
	public void flush(Collection<FlushableEvent> events) throws Exception {

		for (FlushableEvent event: events) {
			
			if (Boolean.parseBoolean(event.getSession().getAttribute(attrName))) {
			
				StringBuffer newSchema = new StringBuffer();
				
				BufferedReader schemaReader = new BufferedReader(new FileReader(schemaFileName));
				String line = null;
				boolean skipNext = false;
				while ((line = schemaReader.readLine()) != null) {
					
					if (skipNext) {
						skipNext = false;
						continue;
					}
					
					if (line.matches(".*name(.*)" + attrName + ".*")) {
						newSchema.append(line).append('\n').append("'isOn': false,").append('\n');
						skipNext = true;
					}
					else { 
						newSchema.append(line).append("\n");
					}
				}
				schemaReader.close();
				PrintWriter schemaOut = new PrintWriter(schemaFileName);
				schemaOut.write(newSchema.toString());
				schemaOut.close();
			}
			else {
				LOG.info(String.format("Ignored trace event [%s] [%s]", event.getName(), event.getValue()));
			}
			
		}
	
	}

}
