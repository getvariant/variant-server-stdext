package com.variant.extapi.std.demo;

import com.variant.server.api.Session;
import com.variant.server.api.lifecycle.LifecycleHook;
import com.variant.server.api.lifecycle.VariationQualificationLifecycleEvent;
import com.variant.server.api.lifecycle.VariationTargetingLifecycleEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Optional;


/**
 * Life-cycle hook to disqualify blacklisted users.
 * The black list of users is given in the hook's initializer object, e.g. as following:
 * <code>
 *   'init': {'blackList':['Nikita Krushchev']}
 * </code>
 * 
 */
public class UserTargetingHook implements LifecycleHook<VariationTargetingLifecycleEvent> {

	private static final Logger LOG = LoggerFactory.getLogger(UserTargetingHook.class);

	private final String[] blackList;

	public UserTargetingHook(String init) {
		blackList = new String[0];
		//blackList = config.getList("blackList").stream().map(e -> e.unwrapped()).toArray(String[]::new);
	}

	@Override
	public Class<VariationTargetingLifecycleEvent> getLifecycleEventClass() {
		return VariationTargetingLifecycleEvent.class;
	}

	@Override
	public Optional<VariationTargetingLifecycleEvent.PostResult> post(VariationTargetingLifecycleEvent event) {

		Session ssn = event.getSession();
		String user = ssn.getAttributes().get("user");
		
		boolean blacklisted = Arrays.stream(blackList).filter(d -> user.equals(d)).findFirst().isPresent();

		if (blacklisted)
			LOG.info("Disqualified blacklisted user [" + user + "] from variation " + event.getVariation().getName() + "]");
		
		VariationTargetingLifecycleEvent.PostResult result = event.mkPostResult();
//		result.setQualified(!blacklisted);
		return Optional.of(result);
	}

}
