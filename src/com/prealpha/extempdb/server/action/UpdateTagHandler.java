/*
 * UpdateTagHandler.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.action;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.prealpha.dispatch.server.ActionHandler;
import com.prealpha.dispatch.shared.ActionException;
import com.prealpha.dispatch.shared.Dispatcher;
import com.prealpha.extempdb.server.InjectLogger;
import com.prealpha.extempdb.server.domain.Tag;
import com.prealpha.extempdb.server.domain.User;
import com.prealpha.extempdb.shared.action.MutationResult;
import com.prealpha.extempdb.shared.action.UpdateTag;
import com.prealpha.extempdb.shared.dto.TagDto;
import com.wideplay.warp.persist.Transactional;

class UpdateTagHandler implements ActionHandler<UpdateTag, MutationResult> {
	@InjectLogger
	private Logger log;

	private final EntityManager entityManager;

	private final HttpSession httpSession;

	@Inject
	public UpdateTagHandler(EntityManager entityManager, HttpSession httpSession) {
		this.entityManager = entityManager;
		this.httpSession = httpSession;
	}

	@Transactional
	@Override
	public MutationResult execute(UpdateTag action, Dispatcher dispatcher)
			throws ActionException {
		User user = (User) httpSession.getAttribute("user");
		if (user == null) {
			log.info("denied permission for tag update request (not logged in)");
			return MutationResult.PERMISSION_DENIED;
		} else if (!user.isAdmin()) {
			log.info(
					"denied permission for tag update request by user \"{}\" (non-admin)",
					user.getName());
			return MutationResult.PERMISSION_DENIED;
		}

		TagDto dto = action.getTag();
		Tag tag = entityManager.find(Tag.class, dto.getName());

		if (tag == null) {
			tag = new Tag();
			tag.setName(dto.getName());
		}

		switch (action.getUpdateType()) {
		case SAVE:
			tag.setSearched(dto.isSearched());

			Set<Tag> parents = new HashSet<Tag>();
			for (TagDto parentDto : dto.getParents()) {
				Tag parent = entityManager.find(Tag.class, parentDto.getName());
				parents.add(parent);
			}
			tag.setParents(parents);

			entityManager.persist(tag);
			log.info("user \"{}\" created or updated tag with new values: {}",
					user.getName(), tag);
			break;
		case DELETE:
			entityManager.remove(tag);
			log.info("user \"{}\" deleted tag \"{}\"", user.getName(),
					tag.getName());
			break;
		default:
			throw new IllegalStateException();
		}

		return MutationResult.SUCCESS;
	}
}
