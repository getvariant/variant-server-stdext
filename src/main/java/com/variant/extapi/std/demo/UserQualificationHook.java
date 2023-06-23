package com.variant.extapi.std.demo;

import java.util.Arrays;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.variant.server.api.Session;
import com.variant.server.api.lifecycle.LifecycleHook;
import com.variant.server.api.lifecycle.VariationQualificationLifecycleEvent;


/**
 * Life-cycle hook to disqualify blacklisted users.
 * The black list of users is given in the hook's initializer object, e.g. as following:
 * <code>
 *   'init': {'blackList':['Nikita Krushchev']}
 * </code>
 * 
 */
public class UserQualificationHook implements LifecycleHook<VariationQualificationLifecycleEvent> {

	private static final Logger LOG = LoggerFactory.getLogger(UserQualificationHook.class);
	
	private final String[] blackList;
	
	public UserQualificationHook(String init) {
		blackList = init.split(",");
	}

	@Override
	public Class<VariationQualificationLifecycleEvent> getLifecycleEventClass() {
		return VariationQualificationLifecycleEvent.class;
	}

	@Override
	public Optional<VariationQualificationLifecycleEvent.PostResult> post(VariationQualificationLifecycleEvent event) throws Exception {

		Session ssn = event.getSession();
		String user = ssn.getAttributes().get("user");
		if (user == null) throw new RuntimeException("Session attribute 'user' must be set");
		boolean blacklisted = Arrays.asList(blackList).contains(user);

		if (blacklisted)
			LOG.info("Disqualified blacklisted user [" + user + "] from variation " + event.getVariation().getName() + "]");
		
		VariationQualificationLifecycleEvent.PostResult result = event.mkPostResult();
		result.setQualified(!blacklisted);
		return Optional.of(result);
	}

}
