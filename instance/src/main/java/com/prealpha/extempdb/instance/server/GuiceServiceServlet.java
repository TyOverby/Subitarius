/*
 * GuiceServiceServlet.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.server;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RPC;
import com.google.gwt.user.server.rpc.RPCRequest;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Injector;

class GuiceServiceServlet extends RemoteServiceServlet {
	private static final long serialVersionUID = 1L;

	@Inject
	private Injector injector;

	public GuiceServiceServlet() {
	}

	@Override
	public final String processCall(String payload)
			throws SerializationException {
		RPCRequest rpcRequest = null;
		Method method = null;

		try {
			rpcRequest = RPC.decodeRequest(payload, null, this);

			checkNotNull(rpcRequest, "No RPC request");

			method = rpcRequest.getMethod();

			preInvoke(rpcRequest);
			Object returnValue = invoke(rpcRequest);
			returnValue = sanitizeValue(returnValue);

			return RPC.encodeResponseForSuccess(method, returnValue,
					rpcRequest.getSerializationPolicy());
		} catch (IllegalArgumentException iax) {
			String message = "Blocked attempt to invoke method " + method;
			SecurityException securityException = new SecurityException(message);
			securityException.initCause(iax);
			throw securityException;
		} catch (IllegalAccessException iax) {
			String message = "Blocked attempt to access inaccessible method "
					+ method;
			SecurityException securityException = new SecurityException(message);
			securityException.initCause(iax);
			throw securityException;
		} catch (InvocationTargetException itx) {
			Throwable thrown = sanitizeValue(itx.getCause());
			return RPC.encodeResponseForFailure(method, thrown,
					rpcRequest.getSerializationPolicy());
		} catch (IncompatibleRemoteServiceException irsx) {
			Exception exception = sanitizeValue(irsx);
			return RPC.encodeResponseForFailure(null, exception,
					rpcRequest.getSerializationPolicy());
		}
	}

	protected void preInvoke(RPCRequest request) {
	}

	protected Object invoke(RPCRequest rpcRequest)
			throws IllegalAccessException, InvocationTargetException {
		Method method = rpcRequest.getMethod();

		Class<?> serviceClass = method.getDeclaringClass();
		Object service = injector.getInstance(serviceClass);

		Object[] parameters = rpcRequest.getParameters();

		return method.invoke(service, parameters);
	}

	protected <T> T sanitizeValue(T value) {
		return value;
	}
}
