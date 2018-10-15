package rws.demo.eventlogger.samples;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rws.demo.eventlogger.EventLogger;
import rws.demo.eventlogger.LogLevelPolicy.Level;

public class Main {

	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

	private static final EventLogger EVENT_LOGGER = new EventLogger(Main.class, m -> {
		if (m.throwable != null && !(m.throwable instanceof RuntimeException)) {
			return Level.WARN;
		}
		if (m.duration != null && m.duration > 5000) {
			return Level.WARN;
		}
		return Level.INFO;
	});

	public static void main(String[] args) {
		LOGGER.info("regular log");
		EVENT_LOGGER.newEvent().log();
		EVENT_LOGGER.newEvent("hello world!").log();

		LOGGER.info("sample log level settings");
		EVENT_LOGGER.newEvent("using a custom log level policy").timeTaken(3000).error();
		EVENT_LOGGER.newEvent("using a custom log level policy").timeTaken(3001).log();
		EVENT_LOGGER.newEvent("using a custom log level policy").timeTaken(6000).log();

		LOGGER.error("sample logs with exceptions", new IllegalArgumentException("some error", new SQLException("some SQL error", new ParseException("parse error", 0))));
		EVENT_LOGGER.newEvent().throwable(new IllegalArgumentException("some error", new SQLException("some SQL error", new ParseException("parse error", 0)))).log();
		EVENT_LOGGER.newEvent().throwable(new IllegalArgumentException()).auxiliary("first", 1).auxiliary("second", "second").timeTaken(124).log();
		EVENT_LOGGER.newEvent().throwable(new Exception()).auxiliary("first", 1).auxiliary("second", "second").timeTaken(412).shoeSize("9 1/2").log();
		EVENT_LOGGER.newEvent().throwable(new InternalException("some message")).event("my action").auxiliary("first", 1).guid(UUID.randomUUID().toString()).message("testing").auxiliary("second", "second").timeTaken(124).log();

	}

	static class InternalException extends IllegalArgumentException {

		private static final long serialVersionUID = 2516976202839890234L;

		public InternalException(String msg) {
			super(msg);
		}

	}
}
