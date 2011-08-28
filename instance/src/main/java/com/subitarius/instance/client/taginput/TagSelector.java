/*
 * TagSelector.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.client.taginput;

import com.google.gwt.user.client.ui.HasValue;
import com.subitarius.instance.shared.dto.TagDto;

public interface TagSelector extends HasValue<TagDto> {
	String getSelectedName();
}
