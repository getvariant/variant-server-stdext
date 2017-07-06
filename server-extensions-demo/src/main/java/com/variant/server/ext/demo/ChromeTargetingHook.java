package com.variant.server.ext.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.variant.core.UserHook;
import com.variant.core.schema.Hook;
import com.variant.core.schema.Test.Experience;
import com.variant.server.api.Session;
import com.variant.server.api.hook.PostResultFactory;
import com.variant.server.api.hook.TestTargetingLifecycleEvent;
import com.variant.server.api.hook.TestTargetingLifecycleEventPostResult;


/**
 * User hook to target traffic from chrome browsers to control.
 * Users with Chrome browsers will participate in the experiment, but always routed to control.
 */
public class ChromeTargetingHook implements UserHook<TestTargetingLifecycleEvent> {

	private static final Logger LOG = LoggerFactory.getLogger(ChromeTargetingHook.class);
	
	@Override
	public void init(Config config, Hook hook) throws Exception {
		// No configuration.
	}

	@Override
	public Class<TestTargetingLifecycleEvent> getLifecycleEventClass() {
		return TestTargetingLifecycleEvent.class;
	}

	@Override
	public UserHook.PostResult post(TestTargetingLifecycleEvent event) throws Exception {

		Session ssn = event.getStateRequest().getSession();
		//LOG.debug(ssn.getAttribute("user-agent"));
		if (ssn.getAttribute("user-agent").matches(".*Chrome.*")) {
			Experience exp = event.getTest().getControlExperience();
			LOG.debug("Targeted Chrome session [" + ssn.getId() + "] to control experience [" + exp.getName() + "] in test [" + event.getTest().getName() + "]");
			TestTargetingLifecycleEventPostResult result = PostResultFactory.mkPostResult(event);
			result.setTargetedExperience(exp);
			return result;
		}
		// Other qual hooks may still disqualify.
		else return null;
	}

}
