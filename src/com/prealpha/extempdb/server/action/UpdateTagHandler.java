/*
 * UpdateTagHandler.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.action;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.prealpha.extempdb.server.InjectLogger;
import com.prealpha.extempdb.server.domain.Tag;
import com.prealpha.extempdb.server.domain.User;
import com.prealpha.extempdb.server.domain.UserSession;
import com.prealpha.extempdb.server.persistence.TagDao;
import com.prealpha.extempdb.server.persistence.Transactional;
import com.prealpha.extempdb.server.persistence.UserSessionDao;
import com.prealpha.extempdb.shared.action.MutationResult;
import com.prealpha.extempdb.shared.action.UpdateTag;
import com.prealpha.extempdb.shared.dto.TagDto;
import com.prealpha.extempdb.shared.id.UserSessionToken;
import com.prealpha.gwt.dispatch.server.ActionHandler;
import com.prealpha.gwt.dispatch.shared.ActionException;
import com.prealpha.gwt.dispatch.shared.Dispatcher;

class UpdateTagHandler implements ActionHandler<UpdateTag, MutationResult> {
	@InjectLogger
	private Logger log;

	private final TagDao tagDao;

	private final UserSessionDao userSessionDao;

	@Inject
	public UpdateTagHandler(TagDao tagDao, UserSessionDao userSessionDao) {
		this.tagDao = tagDao;
		this.userSessionDao = userSessionDao;
	}

	@Override
	public Class<UpdateTag> getActionType() {
		return UpdateTag.class;
	}

	@Transactional
	@Override
	public MutationResult execute(UpdateTag action, Dispatcher dispatcher)
			throws ActionException {
		UserSessionToken sessionToken = action.getSessionToken();
		UserSession session = userSessionDao.validateSession(sessionToken);

		if (session == null) {
			log.info("rejected tag update request because of invalid session, "
					+ "token \"{}\"", sessionToken.getToken());
			return MutationResult.INVALID_SESSION;
		}

		User user = session.getUser();

		if (!user.isAdmin()) {
			log.info("rejected tag update request by non-admin user \"{}\"",
					user.getName());
			return MutationResult.PERMISSION_DENIED;
		}

		TagDto dto = action.getTag();
		Tag tag = tagDao.get(dto.getName());

		if (tag == null) {
			tag = new Tag();
			tag.setName(dto.getName());
		}

		switch (action.getUpdateType()) {
		case SAVE:
			tag.setSearched(dto.isSearched());

			Set<Tag> parents = new HashSet<Tag>();
			for (TagDto parentDto : dto.getParents()) {
				Tag parent = tagDao.get(parentDto.getName());
				parents.add(parent);
			}
			tag.setParents(parents);

			tagDao.save(tag);
			log.info("user \"{}\" created or updated tag with new values: {}",
					user.getName(), tag);
			break;
		case DELETE:
			tagDao.delete(tag);
			log.info("user \"{}\" deleted tag \"{}\"", user.getName(),
					tag.getName());
			break;
		default:
			throw new IllegalStateException();
		}

		return MutationResult.SUCCESS;
	}
}
