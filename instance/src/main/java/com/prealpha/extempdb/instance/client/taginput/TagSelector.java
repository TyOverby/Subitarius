/*
 * TagSelector.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.client.taginput;

import com.google.gwt.user.client.ui.HasValue;
import com.prealpha.extempdb.instance.shared.dto.TagDto;

public interface TagSelector extends HasValue<TagDto> {
	String getSelectedName();
}
