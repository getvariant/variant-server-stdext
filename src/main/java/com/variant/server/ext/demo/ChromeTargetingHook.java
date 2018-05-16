package com.variant.server.ext.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.variant.core.lifecycle.LifecycleHook;
import com.variant.core.schema.Test.Experience;
import com.variant.server.api.PostResultFactory;
import com.variant.server.api.Session;
import com.variant.server.lifecycle.TestTargetingLifecycleEvent;


/**
 * Life-cycle hook to target traffic from Chrome browsers to control.
 * Users with Chrome browsers will participate in the experiment, but always routed to control.
 */
public class ChromeTargetingHook implements LifecycleHook<TestTargetingLifecycleEvent> {

	private static final Logger LOG = LoggerFactory.getLogger(ChromeTargetingHook.class);
	
	public ChromeTargetingHook(Config config) {
		// No configuration.
	}

	@Override
	public Class<TestTargetingLifecycleEvent> getLifecycleEventClass() {
		return TestTargetingLifecycleEvent.class;
	}

	@Override
	public LifecycleHook.PostResult post(TestTargetingLifecycleEvent event) throws Exception {

		Session ssn = event.getSession();
		if (ssn.getAttribute("user-agent").matches(".*Chrome.*")) {
			Experience exp = event.getTest().getControlExperience();
			LOG.info("Targeted Chrome session [" + ssn.getId() + "] to control experience [" + exp.getName() + "] in test [" + event.getTest().getName() + "]");
			TestTargetingLifecycleEvent.PostResult result = PostResultFactory.mkPostResult(event);
			result.setTargetedExperience(exp);
			return result;
		}
		// Delegate down the chain.
		else return null;
	}

}
