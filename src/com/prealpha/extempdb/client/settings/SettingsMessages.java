/*
 * SettingsMessages.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.settings;

import com.google.gwt.i18n.client.Messages;

/*
 * TODO: this interface is getting long, maybe it should be split?
 */
public interface SettingsMessages extends Messages {
	String noPermission();

	String tagManagement();

	String tagLabel();

	String searchedLabel();

	String parentLabel();

	String updateButton();

	String deleteButton();

	String resetButton();

	String saveNoParent();

	String noTagInput();

	String noTagLoaded();

	String changePassword();

	String currentPassword();

	String newPassword();

	String confirmPassword();

	String submitButton();

	String confirmMismatch();

	String changeSuccess();

	String changeInvalid();

	String notLoggedIn();

	String changeDenied();

	String addArticle();

	String statusLabel();

	String statusLabelInvalid();

	String statusLabelNoParser();

	String statusLabelNoArticle();

	String statusLabelFailed();

	String urlLabel();

	String addButton();
}
