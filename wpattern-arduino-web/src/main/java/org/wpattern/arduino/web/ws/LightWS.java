package org.wpattern.arduino.web.ws;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.log4j.Logger;
import org.wpattern.arduino.web.Serial;
import org.wpattern.arduino.web.utils.Constants;

@ServerEndpoint(Constants.ROOT_PATH_WS + "/light")
public class LightWS {

	private static final Logger LOGGER = Logger.getLogger(LightWS.class);

	@OnOpen
	public void onOpen(Session session) {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(String.format("Session [%s] opened.", session.getId()));
		}

		SessionsWS.addSession(session);
	}

	@OnClose
	public void onClose(Session session) {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(String.format("Session [%s] closed.", session.getId()));
		}

		SessionsWS.removeSession(session);
	}

	@OnError
	public void onError(Session session, Throwable throwable) {
		LOGGER.error(String.format("Error on session [%s].", session.getId()), throwable);
	}

	@OnMessage
	public void onMessage(String message) {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(String.format("Message received [%s].", message));
		}

		if (message == null) {
			return;
		}

		message = message.trim().toLowerCase();

		if (message.equals("l") || message.equals("d")) {
			Serial.getInstance().sendMessage(message);
		}
	}

}
