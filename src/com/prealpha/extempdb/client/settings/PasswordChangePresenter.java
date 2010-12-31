/*
 * PasswordChangePresenter.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.settings;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.prealpha.extempdb.client.Presenter;
import com.prealpha.extempdb.client.SessionManager;
import com.prealpha.extempdb.client.error.ManagedCallback;
import com.prealpha.extempdb.shared.action.ChangePassword;
import com.prealpha.extempdb.shared.action.MutationResult;
import com.prealpha.extempdb.shared.id.UserSessionToken;
import com.prealpha.gwt.dispatch.shared.DispatcherAsync;

/*
 * TODO: doesn't present anything
 */
public class PasswordChangePresenter implements Presenter<Void> {
	public static interface Display extends IsWidget {
		HasText getStatusLabel();

		HasText getCurrentPasswordBox();

		HasText getNewPasswordBox();

		HasText getConfirmPasswordBox();

		HasClickHandlers getSubmitButton();
	}

	private final Display display;

	private final SettingsMessages messages;

	@Inject
	public PasswordChangePresenter(final Display display,
			final SettingsMessages messages, final DispatcherAsync dispatcher,
			final SessionManager sessionManager) {
		this.display = display;
		this.messages = messages;

		display.getSubmitButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String currentPassword = display.getCurrentPasswordBox()
						.getText();
				String newPassword = display.getNewPasswordBox().getText();
				String confirmPassword = display.getConfirmPasswordBox()
						.getText();

				if (!newPassword.equals(confirmPassword)) {
					display.getStatusLabel()
							.setText(messages.confirmMismatch());
				} else {
					UserSessionToken sessionToken = sessionManager
							.getSessionToken();

					if (sessionToken == null) {
						display.getStatusLabel()
								.setText(messages.notLoggedIn());
					} else {
						ChangePassword action = new ChangePassword(
								sessionToken, currentPassword, newPassword);
						dispatcher
								.execute(action, new PasswordChangeCallback());
					}
				}
			}
		});
	}

	@Override
	public Display getDisplay() {
		return display;
	}

	@Override
	public void bind(Void v) {
	}

	private class PasswordChangeCallback extends
			ManagedCallback<MutationResult> {
		@Override
		public void onSuccess(MutationResult result) {
			String statusMessage;

			switch (result) {
			case SUCCESS:
				statusMessage = messages.changeSuccess();
				break;
			case INVALID_REQUEST:
				statusMessage = messages.changeInvalid();
				break;
			case INVALID_SESSION:
				statusMessage = messages.notLoggedIn();
				break;
			case PERMISSION_DENIED:
				statusMessage = messages.changeDenied();
				break;
			default:
				throw new IllegalStateException();
			}

			display.getStatusLabel().setText(statusMessage);
		}
	}
}
