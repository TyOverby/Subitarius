/*
 * MainMessages.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.client.main;

import com.google.gwt.i18n.client.Messages;

public interface MainMessages extends Messages {
	String aboutHeading();

	String aboutText();

	String usageHeading();

	String usageText();

	String controlsHeading();

	String controlsText();

	String fetchButton();

	String parseButton();

	String addArticleHeading();

	String statusLabel();

	String statusLabelInvalid();

	String statusLabelNoParser();

	String statusLabelNoArticle();

	String statusLabelFailed();

	String urlLabel();

	String addButton();
}
