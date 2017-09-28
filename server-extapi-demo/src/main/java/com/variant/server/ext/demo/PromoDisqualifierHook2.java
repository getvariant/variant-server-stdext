package com.variant.server.ext.demo;

import com.typesafe.config.Config;
import com.variant.core.UserHook;
import com.variant.core.schema.Test;
import com.variant.server.api.PostResultFactory;
import com.variant.server.api.Session;
import com.variant.server.lce.TestQualificationLifecycleEvent;

/**
 * User hook to disqualify traffic from Firefox browsers.
 * Users with Firefox browsers will not participate in the experiment.
 */
public class PromoDisqualifierHook2 implements UserHook<TestQualificationLifecycleEvent> {

	public PromoDisqualifierHook2(Config config) {
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
		String phone = ssn.getAttribute("phone");
		if (phone != null) {
			Test.Experience lastExp = figureOutLastExperience(phone, event.getTest());
			result = PostResultFactory.mkPostResult(event);
			result.setQualified(lastExp != null);
			if (lastExp != null) {
				ssn.setAttribute("experience", lastExp.getName());
			}
		}
		
		return result;
	}

	/**
	 * @param phone
	 * @return Test experience last served to user with a given phone number in a given test, or null if noone. 
	 */
	private Test.Experience figureOutLastExperience(String phone, Test test) {
		// Go to the operational database.
		return null;
	}

}
