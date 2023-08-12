package com.variant.extapi.std.demo;

import com.variant.server.api.Session;
import com.variant.server.api.lifecycle.VariationTargetingLifecycleEvent;
import com.variant.server.api.lifecycle.VariationTargetingLifecycleHook;
import com.variant.share.schema.Variation;

import java.util.Optional;


/**
 * New demo targeting hook. TBD
 */
public class UserTargetingHook implements VariationTargetingLifecycleHook {


	@Override
	public Optional<Variation.Experience> post(VariationTargetingLifecycleEvent event) {
		Session ssn = event.getSession();
		String user = ssn.getAttributes().get("user");
		return Optional.empty();
	}

}
