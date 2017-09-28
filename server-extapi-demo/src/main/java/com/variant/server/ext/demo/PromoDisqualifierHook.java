package com.variant.server.ext.demo;

import com.typesafe.config.Config;
import com.variant.core.UserHook;
import com.variant.server.api.PostResultFactory;
import com.variant.server.api.Session;
import com.variant.server.lce.TestQualificationLifecycleEvent;

/**
 * User hook to disqualify traffic from Firefox browsers.
 * Users with Firefox browsers will not participate in the experiment.
 */
public class PromoDisqualifierHook implements UserHook<TestQualificationLifecycleEvent> {
	
	public PromoDisqualifierHook(Config config) {
		// No configuration.
	}

	@Override
	public Class<TestQualificationLifecycleEvent> getLifecycleEventClass() {
		return TestQualificationLifecycleEvent.class;
	}

	@Override
	public UserHook.PostResult post(TestQualificationLifecycleEvent event) throws Exception {
		
		TestQualificationLifecycleEvent.PostResult result = null;
		Session ssn = event.getSession();
		if (Boolean.parseBoolean(ssn.getAttribute("isPromo"))) {
			result = PostResultFactory.mkPostResult(event);
			result.setQualified(false);
		}
		
		return result;
	}

}
