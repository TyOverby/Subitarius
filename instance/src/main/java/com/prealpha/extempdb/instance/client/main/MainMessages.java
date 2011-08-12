/*
 * MainMessages.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.client.main;

import com.google.gwt.i18n.client.Messages;

public interface MainMessages extends Messages {
	String aboutHeading();

	String aboutText();

	String usageHeading();

	String usageText();
	
	String addArticleHeading();
	
	String statusLabel();
	
	String statusLabelInvalid();
	
	String statusLabelNoParser();
	
	String statusLabelNoArticle();
	
	String statusLabelFailed();
	
	String urlLabel();
	
	String addButton();
}
