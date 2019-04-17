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
	 * @param biographyDao The Biography DAO
	 */
	@Inject
	public BiographyServiceImpl(BiographyDao biographyDao) {
		this.biographyDao = biographyDao;
	}

	@Override
	public void create(String userId, String content) {
		biographyDao.create(userId, content);
	}

	@Override
	public String findByUserName(String username) {
		return biographyDao.findByUserName(username).orElseThrow(EntityDoesNotExistException::new);
	}

	@Override
	public void updateByUserName(String username, String content) {
		biographyDao.updateByUserName(username, content);
	}

	@Override
	public void updateById(String userId, String content) {
		biographyDao.updateById(userId, content);
	}
}
