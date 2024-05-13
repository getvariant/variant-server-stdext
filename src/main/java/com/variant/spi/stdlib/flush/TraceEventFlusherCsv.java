package com.variant.spi.stdlib.flush;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

import org.yaml.snakeyaml.Yaml;
import com.variant.share.schema.Variation.Experience;
import com.variant.server.spi.FlushableTraceEvent;
import com.variant.server.spi.TraceEventFlusher;

/**
 * An implementation of {@link TraceEventFlusher}, which writes trace events to a local CSV file. 
 * The output file format conforms to the <a href="https://tools.ietf.org/html/rfc4180">IETF RFC4180</a>
 * 
 * Configuration.
 * A YAML string containing these properties:
 * <ul>
 *  <li><code>header</code> - boolean - Whether or not to include the header as very first line.
 *                            Optional, defaults to <code>true</code>.
 *  <li><code>file</code> - string - The name of the local file to write to. If starts with the leading '/',
 *                          interpreted as an absolute path. Otherwise, interpreted wrt Variant server's working
 *                          directory. Will be overwritten if exists. Optional, defaults to <code>log/trace-events.csv</code>.
 * </ul>
 * Example:<br/>
 * <code>
 *   flusher:
 *     class: com.variant.spi.stdlib.TraceEventFlusherCsv
 *     init: '{file: /tmp/variant-events.csv, header: false}'
 * </code>
 * 
 * @since 0.10
 */
public class TraceEventFlusherCsv implements TraceEventFlusher {
	
	private BufferedWriter out = null;

	public TraceEventFlusherCsv(String init) throws Exception {
		Map<String, ?> map =
			Optional.ofNullable(init)
				.map(string->(Map<String,?>)new Yaml().load(string))
				.orElse(Map.of());
		out = Files.newBufferedWriter(Paths.get(parseFileName(map)), CREATE, WRITE, TRUNCATE_EXISTING );
		if (parseHeader(map)) {
			writeLine("event_id", "event_name", "created_on", "session_id", "owner", "live_experiences", "attributes");
			out.flush();
		}
	}

	/**
	 * Write a bunch of events to file.
	 */
	@Override
	public void flush(FlushableTraceEvent[] events, int size) throws Exception {

		for (int i = 0; i < size; i++) {

			FlushableTraceEvent event = events[i];
			StringBuilder stringBuilder = new StringBuilder();

			// Attributes
			boolean first = true;
			stringBuilder.append("{");
			for (Map.Entry<String, String> param: event.getAttributes().entrySet()) {
				if (first) first = false;
				else stringBuilder.append(',');
				stringBuilder.append(param.getKey()).append(':').append(param.getValue());
			}
			stringBuilder.append("}");
			String attrs = stringBuilder.toString();

			// Live experiences
			stringBuilder.setLength(0);
			first = true;
			stringBuilder.append("[");
			for (Experience le: event.getLiveExperiences()) {
				if (first) first = false;
				else stringBuilder.append(",");
				stringBuilder.append(le.toString()); // prints varname.expname
			}
			stringBuilder.append("]");

			String liveExperiences = stringBuilder.toString();

			writeLine(
				event.getId(),
				event.getName(),
				DateTimeFormatter.ISO_INSTANT.format(event.getTimestamp()),
				event.getSessionId(),
				event.getSessionOwner().orElse("?"),
				liveExperiences,
				attrs);
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

	private Boolean parseHeader(Map<String, ?> map) {
		final var defaultValue = true;
		return Optional.ofNullable(map.get("header")).map(token->(boolean)token).orElse(defaultValue);
	}

	private String parseFileName(Map<String, ?> map) {
		final var defaultValue = "log/trace-events.csv";
		return Optional.ofNullable(map.get("file")).map(token->(String)token).orElse(defaultValue);
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
