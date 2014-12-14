package org.wpattern.arduino.web.ws;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.websocket.Session;

import org.apache.log4j.Logger;
import org.wpattern.arduino.web.IListener;

public class SessionsWS implements IListener {

	private static final Logger LOGGER = Logger.getLogger(SessionsWS.class);

	private static final Set<Session> sessions = new HashSet<Session>();

	private static SessionsWS sessionsWSIntance;

	private SessionsWS() {
	}

	public static void addSession(Session session) {
		sessions.add(session);
	}

	public static void removeSession(Session session) {
		sessions.remove(session);
	}

	public static SessionsWS getInstance() {
		if (sessionsWSIntance == null) {
			sessionsWSIntance = new SessionsWS();
		}

		return sessionsWSIntance;
	}

	@Override
	public void onMessage(String message) {
		Iterator<Session> sessionIterator = sessions.iterator();

		while (sessionIterator.hasNext()) {
			try {
				sessionIterator.next().getBasicRemote().sendText(message);
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
	}

}
