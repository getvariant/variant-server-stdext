package com.edulastic.variant.extapi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.variant.core.lifecycle.LifecycleHook;
import com.variant.core.schema.Variation;
import com.variant.core.schema.Variation.Experience;
import com.variant.server.api.Session;
import com.variant.server.api.lifecycle.PostResultFactory;
import com.variant.server.api.lifecycle.VariationTargetingLifecycleEvent;
import com.variant.server.impl.VariationTargetingLifecycleEventPostResultImpl;


/**
 * Life-cycle hook to target traffic from Chrome browsers to control.
 * Users with Chrome browsers will participate in the experiment, but always routed to control.
 */
public class DurableTargetingHook implements LifecycleHook<VariationTargetingLifecycleEvent> {

	private static final Logger LOG = LoggerFactory.getLogger(DurableTargetingHook.class);
	
	private static final String READ = 
			"SELECT experience FROM variant_durable_targeting where id = ? AND variation = ?";

	private static final String INSERT = 
			"INSERT INTO variant_durable_targeting " +
		    "(id, variation, experience, created_on) " +
			"VALUES (?, ?, ?, ?)";

	private Connection conn = null;
	private PreparedStatement preparedRead;
	private PreparedStatement preparedInsert;
	
	/**
	 * We expect the JDBC URL to be set in the hook's config object. For example:
	 * 'init':{'url':'"jdbc:mysql://localhost/variant?user=variant&password=variant"}
	 * 
	 * @param config
	 */
	public DurableTargetingHook(Config config) {
		
		if (config == null) throw new RuntimeException("Missing configuration object");
		String url = config.getString("url");
		if (url == null) throw new RuntimeException("Missing configuration parameter 'url' ");
		if (LOG.isDebugEnabled()) LOG.debug("Connecting to URL '" + url + "'");
		
		try {
			conn = DriverManager.getConnection(url);

			LOG.info("Connected to URL '" + url + "'");
			
			preparedRead = conn.prepareStatement(READ);
			preparedInsert = conn.prepareStatement(INSERT);
		}
		catch (SQLException ex) {
			throw new RuntimeException("Unable to connect to URL '" + url + "'", ex);
		}
		
	}

	@Override
	public Class<VariationTargetingLifecycleEvent> getLifecycleEventClass() {
		return VariationTargetingLifecycleEvent.class;
	}

	@Override
	public LifecycleHook.PostResult post(VariationTargetingLifecycleEvent event) throws Exception {

		Session ssn = event.getSession();
		Variation variation = event.getVariation();
		String userId = ssn.getAttributes().get("userId");
		if (userId == null) throw new RuntimeException("No user ID in Variant session");

		Experience experience = null;
		
		// Read. We may already have an experience from a previous visit
		preparedRead.setString(1, userId);
		preparedRead.setString(2, variation.getName());
		ResultSet rs = preparedRead.executeQuery();
		if (rs.next()) {
			// There's an existing experience.
			String expName = rs.getString(1);
			if (LOG.isDebugEnabled()) LOG.debug("Found existing durable eperience [" + expName + "]");
			experience = variation.getExperience(expName)
					.orElseThrow(() -> new RuntimeException("Experience [" + expName + "] does not exist in variation [" + variation + "]"));
			
		}
		else {
			// No existing targeting info for this user/variation combination. Roll the dice.
			VariationTargetingLifecycleEventPostResultImpl postResult = postDefaultTargeter(event);
			
			// Save the experience in the database for next visit.
			preparedInsert.setString(1, userId);
			preparedInsert.setString(2,  variation.getName());
			preparedInsert.setString(3, postResult.getTargetedExperience().getName());
			preparedInsert.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
			try {
				preparedInsert.execute();
				return postResult;
			}
			catch (SQLException ex) {
				if (ex.getErrorCode() == 1062) {
					// Duplicate primary key due to a phantom row: all good, just re-read.
					rs = preparedRead.executeQuery();
					rs.next();
					String expName = rs.getString(1);
					if (LOG.isDebugEnabled()) LOG.debug("Retrieved phantom durable eperience [" + expName + "]");
					experience = variation.getExperience(expName)
							.orElseThrow(() -> new RuntimeException("Experience [" + expName + "] does not exist in variation [" + variation + "]"));
				}
				else throw ex;
			}
		}
		
		VariationTargetingLifecycleEvent.PostResult result = PostResultFactory.mkPostResult(event);
		result.setTargetedExperience(experience);
		return result;
	}

	/**
	 * Obtain random experience in accordance to the weights provided in the schema.
	 * This, it turns out, is not as trivial as once thought. We'll avoid code repetitions
	 * and just snag this from the default targeter. End user will not know to do this,
	 * so we need to sink this into the produce ASAP.
	 * 
	 * @param var Variation
	 * @return Resulting experience
	 * @throws RuntimeException if this Variation's experiences do not have weights.
	 */
	@SuppressWarnings("unchecked")
	private VariationTargetingLifecycleEventPostResultImpl postDefaultTargeter(VariationTargetingLifecycleEvent event) throws Exception {
		return (VariationTargetingLifecycleEventPostResultImpl) 
				((LifecycleHook<VariationTargetingLifecycleEvent>)event.getDefaultHook()).post(event);
		
	}
}
