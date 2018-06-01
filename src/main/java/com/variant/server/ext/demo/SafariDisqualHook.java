package com.variant.server.ext.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.variant.core.lifecycle.LifecycleHook;
import com.variant.server.api.lifecycle.PostResultFactory;
import com.variant.server.api.Session;
import com.variant.server.api.lifecycle.TestQualificationLifecycleEvent;

/**
 * Life-cycle hook to disqualify traffic from Firefox browsers.
 * Users with Firefox browsers will not participate in the experiment.
 */
public class SafariDisqualHook implements LifecycleHook<TestQualificationLifecycleEvent> {

	private static final Logger LOG = LoggerFactory.getLogger(SafariDisqualHook.class);
	
	public SafariDisqualHook(Config config) {
		// No configuration.
	}

	@Override
	public Class<TestQualificationLifecycleEvent> getLifecycleEventClass() {
		return TestQualificationLifecycleEvent.class;
	}

	@Override
	public LifecycleHook.PostResult post(TestQualificationLifecycleEvent event) throws Exception {

		Session ssn = event.getSession();
		String ua = ssn.getAttribute("user-agent");
		if (ua.matches(".*Safari.*") && !ua.matches(".*Chrome.*")) {
			LOG.info("Disqualified Safari session [" + ssn.getId() + "]");
			TestQualificationLifecycleEvent.PostResult result = PostResultFactory.mkPostResult(event);
			result.setQualified(false);
			return result;
		}
		// Other qual hooks may still disqualify.
		else return null;
	}

}
