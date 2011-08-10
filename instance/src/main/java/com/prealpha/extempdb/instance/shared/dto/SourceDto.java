/*
 * SourceDto.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.shared.dto;

import com.google.gwt.user.client.rpc.IsSerializable;

public class SourceDto implements IsSerializable {
	private Long id;

	private String domainName;

	private String displayName;

	public SourceDto() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((domainName == null) ? 0 : domainName.hashCode());
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
		if (!(obj instanceof SourceDto)) {
			return false;
		}
		SourceDto other = (SourceDto) obj;
		if (domainName == null) {
			if (other.domainName != null) {
				return false;
			}
		} else if (!domainName.equals(other.domainName)) {
			return false;
		}
		return true;
	}
}
