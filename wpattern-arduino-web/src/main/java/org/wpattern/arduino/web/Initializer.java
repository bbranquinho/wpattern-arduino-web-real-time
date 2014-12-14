package org.wpattern.arduino.web;

import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.wpattern.arduino.web.ws.SessionsWS;

public class Initializer implements ServletContextListener {

	private static final Logger LOGGER = Logger.getLogger(Initializer.class);

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Context finalization.");
		}

		SerialScheduler.getInstance().stop();
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Context initialization.");
		}
		ServletContext context = event.getServletContext();
		Enumeration<String> params = context.getInitParameterNames();

		while (params.hasMoreElements()) {
			String param = (String) params.nextElement();
			String value = context.getInitParameter(param);

			if (param.startsWith("config.")) {
				System.setProperty(param, value);
			}
		}


		try {
			SerialScheduler.getInstance().start();
			Serial.getInstance().addListener(SessionsWS.getInstance());
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

}
