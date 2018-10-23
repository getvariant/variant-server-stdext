package com.variant.server.ext.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.variant.core.lifecycle.LifecycleHook;
import com.variant.core.schema.Variation.Experience;
import com.variant.server.api.lifecycle.PostResultFactory;
import com.variant.server.api.Session;
import com.variant.server.api.lifecycle.VariationTargetingLifecycleEvent;


/**
 * Life-cycle hook to target traffic from Chrome browsers to control.
 * Users with Chrome browsers will participate in the experiment, but always routed to control.
 */
public class ChromeTargetingHook implements LifecycleHook<VariationTargetingLifecycleEvent> {

	private static final Logger LOG = LoggerFactory.getLogger(ChromeTargetingHook.class);
	
	public ChromeTargetingHook(Config config) {
		// No configuration.
	}

	@Override
	public Class<VariationTargetingLifecycleEvent> getLifecycleEventClass() {
		return VariationTargetingLifecycleEvent.class;
	}

	@Override
	public LifecycleHook.PostResult post(VariationTargetingLifecycleEvent event) throws Exception {

		Session ssn = event.getSession();
		if (ssn.getAttributes().get("user-agent").matches(".*Chrome.*")) {
			Experience exp = event.getVariation().getControlExperience();
			LOG.info("Targeted Chrome session [" + ssn.getId() + "] to control experience [" + exp.getName() + "] in test [" + event.getVariation().getName() + "]");
			VariationTargetingLifecycleEvent.PostResult result = PostResultFactory.mkPostResult(event);
			result.setTargetedExperience(exp);
			return result;
		}
		// Not Chrome - delegate down the chain.
		else return null;
	}

}
