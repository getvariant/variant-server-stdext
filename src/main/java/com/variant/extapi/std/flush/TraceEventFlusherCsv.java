package com.variant.extapi.std.flush;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;

import static java.nio.file.StandardOpenOption.*;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import com.typesafe.config.Config;
import com.variant.core.schema.Variation.Experience;
import com.variant.server.api.FlushableTraceEvent;
import com.variant.server.api.TraceEventFlusher;

/**
 * An implementation of {@link TraceEventFlusher}, which writes trace events to a local CSV file. 
 * The output file format conforms to the <a href="https://tools.ietf.org/html/rfc4180">IETF RFC4180</a>
 * 
 * Configuration.<br/>You may use the <code>variant.event.flusher.class.init</code> configuration property to pass configuration details to this object.
 * 
 * <ul>
 *  <li><code>header</code> - boolean - Wether or not to include the metadata header as very first line. The default is <code>false</code>
 *  <li><code>file</code> - string - The name of the file to write to. Will be overwritten if exists. The default is "variant-events.csv"
 * </ul>
 * Example:<br/>
 * <code>variant.event.flusher.init = {"file":"/tmp/variant-events.csv","header":true}</code>
 * 
 * @since 0.10
 */
public class TraceEventFlusherCsv implements TraceEventFlusher {
	
	private String fileName = "variant-events.csv";
	private BufferedWriter out;
	
	public TraceEventFlusherCsv(Config config) throws Exception {
		
		boolean header = false;
		
		if (config != null) {
			header = Optional.ofNullable(config.getBoolean("header")).orElse(header);
			fileName = Optional.ofNullable(config.getString("file")).orElse(fileName);		
		}
		
		out = Files.newBufferedWriter(Paths.get(fileName), CREATE, WRITE, TRUNCATE_EXISTING );

		if (header) {
			writeLine(new Object[] {"event_name", "created_on", "session_id", "attributes", "variation", "experience", "is_control"});
			out.flush();
		}

	}

	@Override
	public void flush(Collection<FlushableTraceEvent> events) throws Exception {

		for (FlushableTraceEvent event: events) {

			StringBuilder attrsBuffer = new StringBuilder();
			boolean first = true;
			for (Map.Entry<String, String> param: event.getAttributes().entrySet()) {
				if (first) first = false;
				else attrsBuffer.append(';');
				attrsBuffer.append(param.getKey()).append('=').append(param.getValue());
			}
			String attrs = attrsBuffer.toString();
			
			for (Experience e: event.getLiveExperiences()) {
   				writeLine(
   						event.getName(), 
   						DateTimeFormatter.ISO_INSTANT.format(event.getTimestamp()), 
   						event.getSessionId(),
   						attrs,
   						e.getVariation().getName(),
   						e.getName(),
   						e.isControl());
            }	
		}
		
		out.flush();
	}

	/**
	 * Enclose the string in double quotes. If a double quote already occurs in the string, 
	 * double it, as per the RFC
	 */
	private String quoteString(String raw) {
		return "\"" + raw.replaceAll("\\\"", "\"\"") + "\"";
	}
	
	private void writeLine(Object...tokens) throws IOException {
		
		boolean first = true;
		for (Object token: tokens) {
			if (first) first = false;
			else out.append(',');
			out.append(quoteString(token.toString()));
		}
		out.append(System.lineSeparator());
	}
	
}
