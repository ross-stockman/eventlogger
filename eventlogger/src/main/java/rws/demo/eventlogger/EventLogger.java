package rws.demo.eventlogger;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rws.demo.eventlogger.LogLevelPolicy.Level;

public class EventLogger {

	private static final String PARAM = "{}";

	private final String className;

	private static final Logger LOGGER = LoggerFactory.getLogger(EventLogger.class);

	private final LogLevelPolicy logLevelPolicy;

	public EventLogger(Class<?> clazz) {
		className = clazz.getName();
		logLevelPolicy = m -> Level.INFO;
	}

	public EventLogger(Class<?> clazz, LogLevelPolicy logLevelPolicy) {
		className = clazz.getName();
		this.logLevelPolicy = logLevelPolicy;
	}

	public MessageBuilder newEvent() {
		return new MessageBuilder(this);
	}

	public MessageBuilder newEvent(String message) {
		return new MessageBuilder(this).message(message);
	}

	private void debug(Message message) {
		LOGGER.debug(PARAM, message);
	}

	private void info(Message message) {
		LOGGER.info(PARAM, message);
	}

	private void warn(Message message) {
		LOGGER.warn(PARAM, message);
	}

	private void error(Message message) {
		LOGGER.error(PARAM, message);
	}

	private void log(Message message) {
		switch (logLevelPolicy.resolveLogLevel(message)) {
		case DEBUG:
			debug(message);
			break;
		case INFO:
			info(message);
			break;
		case WARN:
			warn(message);
			break;
		case ERROR:
			error(message);
			break;
		default:
			throw new IllegalArgumentException("Unsupported log level");
		}
	}

	public static class Message {

		private static final String DELIMITER_L1 = "|";
		private static final String DELIMITER_L2 = ";";
		private static final String DELIMITER_L3 = "=";
		private static final String DEFAULT = "";

		public final String className;
		public final Long duration;
		public final Throwable throwable;
		public final String guid;
		public final String application;
		public final String component;
		public final String event;
		public final String user;
		public final String message;
		public final Map<String, ? super Object> auxiliary;

		private Message(MessageBuilder builder) {
			this.className = builder.eventLogger.className;
			this.duration = builder.duration;
			this.throwable = builder.throwable;
			this.guid = builder.guid;
			this.component = builder.component;
			this.application = builder.application;
			this.event = builder.event;
			this.user = builder.user;
			this.message = builder.message;
			this.auxiliary = builder.auxiliary != null ? Collections.unmodifiableMap(builder.auxiliary) : Collections.unmodifiableMap(new TreeMap<>());
		}

		public String toString() {
			StringBuilder builder = new StringBuilder().append(className).append(DELIMITER_L1).append(guid != null ? guid : DEFAULT).append(DELIMITER_L1).append(application != null ? application : DEFAULT).append(DELIMITER_L1).append(component != null ? component : DEFAULT).append(DELIMITER_L1).append(event != null ? event : DEFAULT).append(DELIMITER_L1).append(user != null ? user : DEFAULT).append(DELIMITER_L1).append(duration != null ? duration : DEFAULT).append(DELIMITER_L1).append(message != null ? message : DEFAULT).append(DELIMITER_L1);
			boolean first = true;
			for (Map.Entry<?, ?> entry : auxiliary.entrySet()) {
				if (first) {
					first = !first;
				} else {
					builder.append(DELIMITER_L2);
				}
				builder.append(entry.getKey().toString()).append(DELIMITER_L3).append(entry.getValue().toString());
			}
			if (throwable != null) {
				builder.append(DELIMITER_L1).append(throwable.getClass().getName()).append(DELIMITER_L1).append(throwable.getMessage() != null ? throwable.getMessage() : DEFAULT);
				Throwable cause = throwable;
				while (cause.getCause() != null) {
					cause = cause.getCause();
				}
				builder.append(DELIMITER_L1).append(cause.getClass().getName()).append(DELIMITER_L1).append(cause.getMessage() != null ? cause.getMessage() : DEFAULT);
			} else {
				builder.append(DELIMITER_L1 + DELIMITER_L1 + DELIMITER_L1 + DELIMITER_L1);
			}
			return builder.toString();
		}

	}

	public static class MessageBuilder {

		private final EventLogger eventLogger;
		private Long duration;
		private Throwable throwable;
		private String guid;
		private String application;
		private String component;
		private String event;
		private String user;
		private String message;
		private Map<String, ? super Object> auxiliary = new TreeMap<>();

		private MessageBuilder(EventLogger eventLogger) {
			this.eventLogger = eventLogger;
		}

		public MessageBuilder throwable(Throwable throwable) {
			this.throwable = throwable;
			return this;
		}

		public MessageBuilder timeTaken(long timeTaken) {
			this.duration = timeTaken;
			return this;
		}

		public MessageBuilder guid(String guid) {
			this.guid = guid;
			return this;
		}

		public MessageBuilder application(String application) {
			this.application = application;
			return this;
		}

		public MessageBuilder component(String component) {
			this.component = component;
			return this;
		}

		public MessageBuilder event(String event) {
			this.event = event;
			return this;
		}

		public MessageBuilder user(String user) {
			this.user = user;
			return this;
		}

		public MessageBuilder message(String message) {
			this.message = message;
			return this;
		}

		public MessageBuilder auxiliary(String key, Object value) {
			this.auxiliary.put(key, value);
			return this;
		}

		public MessageBuilder auxiliary(Map<String, ?> auxiliary) {
			this.auxiliary.putAll(auxiliary);
			return this;
		}

		public MessageBuilder shoeSize(String shoeSize) {
			this.auxiliary.put("shoeSize", shoeSize);
			return this;
		}

		public void log() {
			eventLogger.log(new Message(this));
		}

		public void debug() {
			eventLogger.debug(new Message(this));
		}

		public void info() {
			eventLogger.info(new Message(this));
		}

		public void warn() {
			eventLogger.warn(new Message(this));
		}

		public void error() {
			eventLogger.error(new Message(this));
		}

	}

}
