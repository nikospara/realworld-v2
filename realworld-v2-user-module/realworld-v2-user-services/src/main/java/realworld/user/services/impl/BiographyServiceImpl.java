package realworld.user.services.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import realworld.EntityDoesNotExistException;
import realworld.user.dao.BiographyDao;
import realworld.user.services.BiographyService;

/**
 * Implementation of the {@link BiographyService}.
 */
@ApplicationScoped
public class BiographyServiceImpl implements BiographyService {

	private BiographyServiceAuthorizer authorizer;

	private BiographyDao biographyDao;

	/**
	 * Default constructor for the frameworks.
	 */
	BiographyServiceImpl() {
		// NOOP
	}

	/**
	 * Constructor for injection.
	 *
	 * @param authorizer   The authorizer
	 * @param biographyDao The Biography DAO
	 */
	@Inject
	public BiographyServiceImpl(BiographyServiceAuthorizer authorizer, BiographyDao biographyDao) {
		this.authorizer = authorizer;
		this.biographyDao = biographyDao;
	}

	@Override
	public void create(String outerUserId, String outerContent) {
		authorizer.create(outerUserId, outerContent, (userId, content) -> {
			biographyDao.create(userId, content);
		});
	}

	@Override
	public String findByUserName(String outerUsername) {
		return authorizer.findByUserName(outerUsername, username-> biographyDao.findByUserName(username).orElseThrow(EntityDoesNotExistException::new));
	}

	@Override
	public void updateByUserName(String outerUsername, String outerContent) {
		authorizer.updateByUserName(outerUsername, outerContent, (username, content) -> {
			biographyDao.updateByUserName(username, content);
		});
	}

	@Override
	public void updateById(String outerUserId, String outerContent) {
		authorizer.updateById(outerUserId, outerContent, (userId, content) -> {
			biographyDao.updateById(userId, content);
		});
	}
}
