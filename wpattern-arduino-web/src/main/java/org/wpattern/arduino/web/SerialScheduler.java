package org.wpattern.arduino.web;

import java.util.Timer;
import java.util.TimerTask;

public class SerialScheduler {

	private static final String READ_INTERVAL_PROPERTY = "config.serial.read.interval";

	private static SerialScheduler serialSchedulerInstance;

	private final Serial serial;

	private final TimerTask timerTask;

	private final Timer timer;

	public SerialScheduler(final Serial serial) {
		this.serial = serial;
		this.timerTask = new TimerTask() {
			@Override
			public void run() {
				serial.sendMessage("r");
			}
		};
		this.timer = new Timer(this.getClass().getName() + System.currentTimeMillis());
	}

	public static SerialScheduler getInstance() {
		if (serialSchedulerInstance == null) {
			serialSchedulerInstance = new SerialScheduler(Serial.getInstance());
		}

		return serialSchedulerInstance;
	}

	public void start() {
		long readInterval = Long.parseLong(System.getProperty(READ_INTERVAL_PROPERTY).trim());
		this.timer.scheduleAtFixedRate(this.timerTask, 0, readInterval);
	}

	public void addListener(IListener listener) {
		this.serial.addListener(listener);
	}

	public void stop() {
		this.timer.cancel();
		this.serial.finalize();
	}

}
