package com.variant.extapi.std.flush;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;
import com.variant.share.schema.Variation.Experience;
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
	
	private BufferedWriter out = null;

	public TraceEventFlusherCsv(String string) throws Exception {
		Map<String, ?> map = new Yaml().load(string);
		boolean header = (boolean) map.get("header");
		String outFileName = (String) map.get("file");
		out = Files.newBufferedWriter(Paths.get(outFileName), CREATE, WRITE, TRUNCATE_EXISTING );

		if (header) {
			writeLine("event_id", "event_name", "created_on", "session_id", "attributes", "variation", "experience", "is_control");
			out.flush();
		}
	}
	public TraceEventFlusherCsv() throws Exception {
		this("{header: false, file: /tmp/variant-events.csv}");
	}

	/**
	 * Write a bunch of events to file.
	 */
	@Override
	public void flush(FlushableTraceEvent[] events, int size) throws Exception {

		for (int i = 0; i < size; i++) {

			FlushableTraceEvent event = events[i];
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
   						event.getId(), 
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
	 * This flusher is going down. Close the file. 
	 */
	@Override
	public void destroy() throws Exception {
		out.close();
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

	public static void main(String[] args) throws Exception {
		var flusher = new TraceEventFlusherCsv("{header: true, outFileName: /tmp/foo.bar}");
		System.out.println(flusher);
	}
}
