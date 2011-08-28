/*
 * HasBytes.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.domain;

interface HasBytes {
	/**
	 * Generates a representation of this object as a {@code byte} array,
	 * suitable for calculation of a hash. This representation is not intended
	 * for use in restoring the original state of the object. Therefore, the
	 * meanings of the bytes within the output are implementation details and
	 * are specific to the implementing class; they should not be documented.
	 * 
	 * @return a {@code byte} array representation of this object
	 */
	byte[] getBytes();
}
