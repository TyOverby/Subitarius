/*
 * Section.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.util.http.robots;

import static com.google.common.base.Preconditions.*;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Predicate;

class Section implements Predicate<String> {
	private final List<Directive> directives;

	public Section(List<Directive> directives) {
		checkNotNull(directives);
		this.directives = new ArrayList<Directive>(directives);
	}

	@Override
	public boolean apply(String path) {
		for (Directive directive : directives) {
			switch (directive.getType()) {
			case ALLOW:
				PatternDirective allow = (PatternDirective) directive;
				if (allow.apply(path)) {
					return true;
				}
				break;
			case DISALLOW:
				PatternDirective disallow = (PatternDirective) directive;
				if (disallow.apply(path)) {
					return false;
				}
				break;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		String str = "";
		for (Directive directive : directives) {
			str += directive.toString();
		}
		return str + '\n';
	}
}
