package com.variant.server.ext.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.variant.core.UserHook;
import com.variant.server.api.PostResultFactory;
import com.variant.server.api.Session;
import com.variant.server.lce.TestQualificationLifecycleEvent;

/**
 * User hook to disqualify traffic from Firefox browsers.
 * Users with Firefox browsers will not participate in the experiment.
 */
public class FirefoxDisqualHook implements UserHook<TestQualificationLifecycleEvent> {

	private static final Logger LOG = LoggerFactory.getLogger(FirefoxDisqualHook.class);
	
	public FirefoxDisqualHook(Config config) {
		// No configuration.
	}

	@Override
	public Class<TestQualificationLifecycleEvent> getLifecycleEventClass() {
		return TestQualificationLifecycleEvent.class;
	}

	@Override
	public UserHook.PostResult post(TestQualificationLifecycleEvent event) throws Exception {

		Session ssn = event.getSession();
		if (ssn.getAttribute("user-agent").matches(".*Firefox.*")) {
			LOG.info("Disqualified Firefox session [" + ssn.getId() + "]");
			TestQualificationLifecycleEvent.PostResult result = PostResultFactory.mkPostResult(event);
			result.setQualified(false);
			return result;
		}
		// Other qual hooks may still disqualify.
		else return null;
	}

}
