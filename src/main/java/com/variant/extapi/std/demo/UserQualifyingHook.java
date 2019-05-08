package com.variant.extapi.std.demo;

import java.util.Arrays;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.variant.core.lifecycle.LifecycleHook;
import com.variant.server.api.Session;
import com.variant.server.api.lifecycle.VariationQualificationLifecycleEvent;


/**
 * Life-cycle hook to disqualify blacklisted users.
 * The black list of users is given in the hook's initializer object, e.g. as following:
 * <code>
 *   'init': {'blackList':['Nikita Krushchev']}
 * </code>
 * 
 */
public class UserQualifyingHook implements LifecycleHook<VariationQualificationLifecycleEvent> {

	private static final Logger LOG = LoggerFactory.getLogger(UserQualifyingHook.class);
	
	private final String[] blackList;
	
	public UserQualifyingHook(Config config) {
		blackList = config.getList("blackList").stream().map(e -> e.unwrapped()).toArray(String[]::new);
	}

	@Override
	public Class<VariationQualificationLifecycleEvent> getLifecycleEventClass() {
		return VariationQualificationLifecycleEvent.class;
	}

	@Override
	public Optional<VariationQualificationLifecycleEvent.PostResult> post(VariationQualificationLifecycleEvent event) throws Exception {

		Session ssn = event.getSession();
		String user = ssn.getAttributes().get("user");
		
		boolean blacklisted = Arrays.stream(blackList).filter(d -> user.equals(d)).findFirst().isPresent();

		if (blacklisted)
			LOG.info("Disqualified blacklisted user [" + user + "] from variation " + event.getVariation().getName() + "]");
		
		VariationQualificationLifecycleEvent.PostResult result = event.newPostResult();
		result.setQualified(!blacklisted);
		return Optional.of(result);
	}

}
