/*
 * ActionServlet.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;
import com.prealpha.dispatch.shared.Action;
import com.prealpha.dispatch.shared.Dispatcher;
import com.prealpha.dispatch.shared.Result;

final class ActionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private final Dispatcher dispatcher;

	@Inject
	private ActionServlet(Dispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException {
		ObjectInputStream ois = new ObjectInputStream(req.getInputStream());
		// use a surrogate stream for now in case we need to writeFatal later
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(out);

		int count = ois.readInt();
		if (count < 0) {
			writeFatal(res, new IllegalArgumentException(
					"negative action count"));
			return;
		}
		oos.writeInt(count);

		for (int i = 0; i < count; i++) {
			try {
				Action<?> action = (Action<?>) ois.readObject();
				Result result = dispatcher.execute(action);
				oos.writeObject(result);
			} catch (ClassNotFoundException cnfx) {
				writeFatal(res, cnfx);
				return;
			} catch (IOException iox) {
				writeFatal(res, iox);
				return;
			} catch (Exception x) {
				// yes, *all* other exceptions should be caught here
				oos.writeObject(x);
			}
		}

		oos.flush();
		res.getOutputStream().write(out.toByteArray());
		oos.close();
		out.close();
		ois.close();
	}

	private static void writeFatal(HttpServletResponse res, Exception x)
			throws IOException {
		res.sendError(HttpServletResponse.SC_BAD_REQUEST, x.toString());
	}
}
