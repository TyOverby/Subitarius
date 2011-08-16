/*
 * DownloadAction.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.launcher;

import static com.google.common.base.Preconditions.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.EntityManager;

import org.slf4j.Logger;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.prealpha.extempdb.domain.DistributedEntity;
import com.prealpha.extempdb.util.http.RobotsExclusionException;
import com.prealpha.extempdb.util.http.SimpleHttpClient;
import com.prealpha.extempdb.util.logging.InjectLogger;

public final class DownloadAction implements UserAction {
	private static final String BASE_URL = "http://extempdb.pre-alpha.com/DistributedEntity/";

	@InjectLogger
	private Logger log;

	private final EntityManager entityManager;

	private final SimpleHttpClient httpClient;

	private final Date timestamp;

	private final Set<String> hashes;

	@Inject
	private DownloadAction(EntityManager entityManager,
			SimpleHttpClient httpClient) throws IOException,
			RobotsExclusionException {
		this.entityManager = entityManager;
		this.httpClient = httpClient;
		timestamp = new Date();

		InputStream stream = this.httpClient.doGet(BASE_URL);
		byte[] bytes = ByteStreams.toByteArray(stream);
		String[] hashArray = new String(bytes).split("\n");
		Set<String> allHashes = ImmutableSet.copyOf(Arrays.asList(hashArray));

		// make a copy of the filtered set
		// if it's just a view, the iterator has to go to DB every element
		this.entityManager.getTransaction().begin();
		hashes = ImmutableSet.copyOf(Sets.filter(allHashes,
				new Predicate<String>() {
					@Override
					public boolean apply(String input) {
						DistributedEntity entity = DownloadAction.this.entityManager
								.find(DistributedEntity.class, input);
						return (entity != null);
					}
				}));
		this.entityManager.getTransaction().commit();
	}
	
	@Override
	public Type getType() {
		return Type.INFO;
	}

	@Override
	public Date getTimestamp() {
		return new Date(timestamp.getTime());
	}

	@Override
	public URL getUrl() {
		return null;
	}

	@Override
	public String toString() {
		return "Downloading entities...";
	}

	@Override
	public Iterator<Runnable> iterator() {
		return Iterators.transform(hashes.iterator(),
				new Function<String, Runnable>() {
					@Override
					public Runnable apply(String input) {
						return new DownloadTask(input);
					}
				});
	}

	@Override
	public boolean apply(DistributedEntity entity) {
		return hashes.contains(entity.getHash());
	}

	@Override
	public int size() {
		return hashes.size();
	}

	private final class DownloadTask implements Runnable {
		private final String hash;

		private DownloadTask(String hash) {
			checkNotNull(hash);
			this.hash = hash;
		}

		@Transactional
		@Override
		public void run() {
			try {
				InputStream stream = httpClient.doGet(BASE_URL + hash);
				ObjectInputStream ois = new ObjectInputStream(stream);
				DistributedEntity entity = (DistributedEntity) ois.readObject();
				entityManager.persist(entity);
			} catch (ObjectStreamException osx) {
				log.error("exception while deserializing", osx);
			} catch (ClassCastException ccx) {
				log.error("server returned an invalid entity", ccx);
			} catch (ClassNotFoundException cnfx) {
				log.error("entity class not found", cnfx);
			} catch (IOException iox) {
				log.error("I/O exception while downloading", iox);
			} catch (RobotsExclusionException rex) {
				log.error("robots.txt excluded entity download", rex);
			}
		}
	}
}
