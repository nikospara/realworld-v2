package realworld.user.jaxrs.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import realworld.user.jaxrs.BiographyResource;
import realworld.user.services.BiographyService;

/**
 * Implementation of the {@link BiographyResource}.
 */
@RequestScoped
public class BiographyResourceImpl implements BiographyResource {

	@Inject
	private BiographyService biographyService;

	@Override
	public String get(String username) {
		return biographyService.findByUserName(username);
	}
}
