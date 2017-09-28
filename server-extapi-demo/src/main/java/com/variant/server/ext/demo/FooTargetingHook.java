package com.variant.server.ext.demo;

import com.typesafe.config.Config;
import com.variant.core.UserHook;
import com.variant.server.api.PostResultFactory;
import com.variant.server.api.Session;
import com.variant.server.lce.TestTargetingLifecycleEvent;

public class FooTargetingHook implements UserHook<TestTargetingLifecycleEvent> {

	
	public FooTargetingHook(Config config) {
		// No configuration.
	}

	@Override
	public Class<TestTargetingLifecycleEvent> getLifecycleEventClass() {
		return TestTargetingLifecycleEvent.class;
	}

	@Override
	public UserHook.PostResult post(TestTargetingLifecycleEvent event) throws Exception {
		
		TestTargetingLifecycleEvent.PostResult result = PostResultFactory.mkPostResult(event);
		Session ssn = event.getSession();
		if (isInTargetZipCode(ssn.getAttribute("userId"))) {
			result.setTargetedExperience(event.getTest().getExperience("newPaymentService"));
		}
		else {
			result.setTargetedExperience(event.getTest().getExperience("oldPaymentService"));
		}
		
		return result;
	}
	
	private boolean isInTargetZipCode(String userId) {
		return true;
	}

}
