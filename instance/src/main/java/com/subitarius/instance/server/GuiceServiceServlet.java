/*
 * GuiceServiceServlet.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.server;

import org.slf4j.Logger;

import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;
import com.google.gwt.user.client.rpc.RpcTokenException;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RPC;
import com.google.gwt.user.server.rpc.RPCRequest;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.subitarius.util.logging.InjectLogger;

final class GuiceServiceServlet extends RemoteServiceServlet {
	private static final long serialVersionUID = 1L;

	@InjectLogger
	private Logger log;

	private final Injector injector;

	@Inject
	private GuiceServiceServlet(Injector injector) {
		this.injector = injector;
	}

	@Override
	public String processCall(String payload) throws SerializationException {
		// see the superclass implementation
		// we basically just substitute our own delegate for the default one
		try {
			RPCRequest request = RPC.decodeRequest(payload, null, this);
			Object delegate = injector.getInstance(request.getMethod()
					.getDeclaringClass());
			return RPC.invokeAndEncodeResponse(delegate, request.getMethod(),
					request.getParameters(), request.getSerializationPolicy(),
					request.getFlags());
		} catch (IncompatibleRemoteServiceException irsx) {
			log.warn("incompatible remote service", irsx);
			return RPC.encodeResponseForFailure(null, irsx);
		} catch (RpcTokenException rtx) {
			log.warn("invalid RPC token", rtx);
			return RPC.encodeResponseForFailure(null, rtx);
		}
	}
}
