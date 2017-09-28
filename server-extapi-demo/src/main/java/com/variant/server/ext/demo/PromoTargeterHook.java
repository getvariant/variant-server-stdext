package com.variant.server.ext.demo;

import com.typesafe.config.Config;
import com.variant.core.UserHook;
import com.variant.server.api.PostResultFactory;
import com.variant.server.api.Session;
import com.variant.server.lce.TestTargetingLifecycleEvent;

/**
 * User hook to custom target traffic from Firefox browsers.
 * Users with Firefox browsers will not participate in the experiment.
 */
public class PromoTargeterHook implements UserHook<TestTargetingLifecycleEvent> {

	public PromoTargeterHook(Config config) {
		// No configuration.
	}

	@Override
	public Class<TestTargetingLifecycleEvent> getLifecycleEventClass() {
		return TestTargetingLifecycleEvent.class;
	}

	@Override
	public UserHook.PostResult post(TestTargetingLifecycleEvent event) throws Exception {
		
		TestTargetingLifecycleEvent.PostResult result = null;
		Session ssn = event.getSession();
		String expName = ssn.getAttribute("experience");
		if (expName != null) {
			result = PostResultFactory.mkPostResult(event);
			result.setTargetedExperience(event.getTest().getExperience(expName));
		}
		
		return result;
	}

}
