/*
 * BrowseWidget.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.client.jump;

import java.util.Set;

import com.google.gwt.event.logical.shared.ShowRangeEvent;
import com.google.gwt.event.logical.shared.ShowRangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.prealpha.xylophone.shared.DispatcherAsync;
import com.subitarius.action.GetMappingsByTag;
import com.subitarius.action.GetMappingsResult;
import com.subitarius.action.GetTag;
import com.subitarius.action.GetTagResult;
import com.subitarius.action.dto.TagDto;
import com.subitarius.action.dto.TagMappingDto.State;
import com.subitarius.instance.client.error.ManagedCallback;
import com.subitarius.instance.client.taginput.TagInputWidget;

public class JumpWidget extends Composite implements JumpPresenter.Display {
	public static interface JumpUiBinder extends UiBinder<Widget, JumpWidget> {
	}

	@UiField(provided = true)
	final TagInputWidget tagInput;

	@UiField(provided = true)
	final MappingStateSelector stateSelector;

	@UiField(provided = true)
	final Widget articleTable;

	@UiField(provided = true)
	final SimpleEventPager pager;

	private final ArticleTablePresenter tablePresenter;

	private final DispatcherAsync dispatcher;

	private JumpState jumpState;

	@Inject
	public JumpWidget(JumpUiBinder uiBinder, TagInputWidget tagInput,
			MappingStateSelector stateSelector,
			ArticleTablePresenter tablePresenter, SimpleEventPager pager,
			DispatcherAsync dispatcher) {
		this.tagInput = tagInput;
		this.stateSelector = stateSelector;
		this.tablePresenter = tablePresenter;
		this.pager = pager;
		this.dispatcher = dispatcher;
		articleTable = tablePresenter.getDisplay().asWidget();
		pager.setDisplay(tablePresenter.getDisplay().getDataDisplay());
		initWidget(uiBinder.createAndBindUi(this));

		/*
		 * TODO: in the old code, there's an equality check; is it needed?
		 */
		tagInput.addValueChangeHandler(new ValueChangeHandler<TagDto>() {
			@Override
			public void onValueChange(ValueChangeEvent<TagDto> event) {
				TagDto tag = event.getValue();
				String tagName = ((tag == null) ? null : tag.getName());
				JumpState newState = JumpState.getInstance(tagName,
						jumpState.getStates(), jumpState.getSort(), 0);
				setValue(newState, true);
			}
		});

		stateSelector
				.addValueChangeHandler(new ValueChangeHandler<Set<State>>() {
					@Override
					public void onValueChange(ValueChangeEvent<Set<State>> event) {
						JumpState newState = JumpState.getInstance(
								jumpState.getTagName(), event.getValue(),
								jumpState.getSort(), 0);
						setValue(newState, true);
					}
				});

		tablePresenter.getDisplay().addValueChangeHandler(
				new ValueChangeHandler<ArticleSort>() {
					@Override
					public void onValueChange(
							ValueChangeEvent<ArticleSort> event) {
						JumpState newState = JumpState.getInstance(
								jumpState.getTagName(), jumpState.getStates(),
								event.getValue(), 0);
						setValue(newState, true);
					}
				});

		pager.addShowRangeHandler(new ShowRangeHandler<Integer>() {
			@Override
			public void onShowRange(ShowRangeEvent<Integer> event) {
				JumpState newState = JumpState.getInstance(
						jumpState.getTagName(), jumpState.getStates(),
						jumpState.getSort(), event.getStart());
				setValue(newState, true);
			}
		});
	}

	@Override
	public JumpState getValue() {
		return jumpState;
	}

	@Override
	public void setValue(JumpState jumpState) {
		setValue(jumpState, false);
	}

	@Override
	public void setValue(final JumpState jumpState, boolean fireEvents) {
		JumpState oldJumpState = this.jumpState;
		this.jumpState = jumpState;

		String tagName = jumpState.getTagName();
		// if there's no real tag, keep the old results until one is fetched
		if (tagName != null) {
			GetTag tagAction = new GetTag(tagName);
			dispatcher.execute(tagAction, new ManagedCallback<GetTagResult>() {
				@Override
				public void onSuccess(GetTagResult result) {
					TagDto tag = result.getTag();
					tagInput.setValue(tag);

					GetMappingsByTag mappingsAction = new GetMappingsByTag(tag
							.getName(), jumpState.getStates(), jumpState
							.getSort());
					dispatcher.execute(mappingsAction,
							new ManagedCallback<GetMappingsResult>() {
								@Override
								public void onSuccess(GetMappingsResult result) {
									tablePresenter.bind(result.getMappings());
								}
							});
				}
			});
		}

		stateSelector.setValue(jumpState.getStates());
		tablePresenter.getDisplay().setValue(jumpState.getSort());
		pager.setPageStart(jumpState.getPageStart());

		if (fireEvents) {
			ValueChangeEvent.fireIfNotEqual(this, oldJumpState, jumpState);
		}
	}

	@Override
	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<JumpState> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}
}
