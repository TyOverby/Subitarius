/*
 * DistributedEntityReference.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.domain;

import static com.google.common.base.Preconditions.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

@Entity
public class DistributedEntityReference {
	/*
	 * TODO: should this check for id/hash validity? If so, how?
	 */
	@Embeddable
	public static class Key implements Serializable {
		private static final long serialVersionUID = 5048593113643800558L;

		private String teamId;

		private String entityHash;

		protected Key() {
		}

		public Key(String teamId, String entityHash) {
			setTeamId(teamId);
			setEntityHash(entityHash);
		}

		public String getTeamId() {
			return teamId;
		}

		protected void setTeamId(String teamId) {
			checkNotNull(teamId);
			this.teamId = teamId;
		}

		public String getEntityHash() {
			return entityHash;
		}

		protected void setEntityHash(String entityHash) {
			checkNotNull(entityHash);
			this.entityHash = entityHash;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((entityHash == null) ? 0 : entityHash.hashCode());
			result = prime * result
					+ ((teamId == null) ? 0 : teamId.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof Key)) {
				return false;
			}
			Key other = (Key) obj;
			if (entityHash == null) {
				if (other.entityHash != null) {
					return false;
				}
			} else if (!entityHash.equals(other.entityHash)) {
				return false;
			}
			if (teamId == null) {
				if (other.teamId != null) {
					return false;
				}
			} else if (!teamId.equals(other.teamId)) {
				return false;
			}
			return true;
		}

		private void readObject(ObjectInputStream ois) throws IOException,
				ClassNotFoundException {
			ois.defaultReadObject();
			setTeamId(teamId);
			setEntityHash(entityHash);
		}
	}

	private Key key;
	
	private Team team;

	private byte[] blob;

	protected DistributedEntityReference() {
	}

	public DistributedEntityReference(Key key, byte[] blob) {
		setKey(key);
		setBlob(blob);
	}

	@EmbeddedId
	public Key getKey() {
		return key;
	}

	protected void setKey(Key key) {
		checkNotNull(key);
		this.key = key;
	}
	
	@MapsId("teamId")
	@ManyToOne
	@JoinColumn(nullable = false, updatable = false)
	protected Team getTeam() {
		return team;
	}
	
	protected void setTeam(Team team) {
		checkNotNull(team);
		this.team = team;
	}

	@Lob
	@Column(nullable = false)
	public byte[] getBlob() {
		return blob;
	}

	protected void setBlob(byte[] blob) {
		checkNotNull(blob);
		checkArgument(blob.length > 0);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof DistributedEntityReference)) {
			return false;
		}
		DistributedEntityReference other = (DistributedEntityReference) obj;
		if (key == null) {
			if (other.key != null) {
				return false;
			}
		} else if (!key.equals(other.key)) {
			return false;
		}
		return true;
	}
}
