package org.wpattern.arduino.web;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

public class Serial implements SerialPortEventListener {

	private static final Logger LOGGER = Logger.getLogger(Serial.class);

	private static final String CONFIG_PORT_PROPERTY = "config.port";

	private static String PATTERN = "yyyy:MM:dd:HH:mm:ss:SSS";

	private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(PATTERN);

	private static Serial serialInstance;

	private final List<IListener> listeners;

	public Serial() {
		this.listeners = new ArrayList<IListener>();
	}

	// Instancia
	public static Serial getInstance() {
		if (serialInstance == null) {
			serialInstance = new Serial();

			try {
				serialInstance.initialize();
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
		}

		return serialInstance;
	}

	// Listeners
	public void addListener(IListener listener) {
		this.listeners.add(listener);
	}

	public void removeListener(IListener listener) {
		this.listeners.remove(listener);
	}

	// Buffer de leitura.
	private BufferedReader input;

	// Metodo que recebe os valores enviados pelo arduino.
	@Override
	public void serialEvent(SerialPortEvent oEvent) {
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				String data = SIMPLE_DATE_FORMAT.format(new Date()) + ";" + this.input.readLine();

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(String.format("Data received [%s].", data));
				}

				for (IListener listener : this.listeners) {
					listener.onMessage(data);
				}
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
	}

	// Buffer de escrita.
	private OutputStream output;

	public void sendMessage(String message) {
		try {
			this.output.write(message.getBytes());
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	// Timeout esperado para a abertura da comunicacao.
	private static final int TIME_OUT = 2000;

	// Frequencia de comunicacao da porta serial.
	private static final int DATA_RATE = 9600;

	// Porta serial.
	private SerialPort serialPort;

	public void initialize() throws Exception {
		String port = System.getProperty(CONFIG_PORT_PROPERTY).trim();

		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(String.format("Open connection with serial port [%s].", port));
		}

		// Criada a porta serial.
		CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(port);

		// Abrindo a comunicacao com a porta serial.
		this.serialPort = (SerialPort) portId.open(this.getClass().getName(), TIME_OUT);

		// Configura os valores de espera para receber dados.
		this.serialPort.enableReceiveTimeout(1000);
		this.serialPort.enableReceiveThreshold(0);

		// Parametros de comunicacao com a porta serial.
		this.serialPort.setSerialPortParams(DATA_RATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
				SerialPort.PARITY_NONE);

		// Leitura e escrita.
		this.input = new BufferedReader(new InputStreamReader(this.serialPort.getInputStream()));
		this.output = this.serialPort.getOutputStream();

		// Adiciona o listener para receber dados.
		this.serialPort.addEventListener(this);
		this.serialPort.notifyOnDataAvailable(true);
	}

	// Finaliza a conexao com a porta serial.
	@Override
	public void finalize() {
		this.serialPort.close();
	}

}

