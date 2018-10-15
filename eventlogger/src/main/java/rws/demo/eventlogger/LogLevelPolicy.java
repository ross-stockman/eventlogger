package rws.demo.eventlogger;

import rws.demo.eventlogger.EventLogger.Message;

@FunctionalInterface
public interface LogLevelPolicy {
	Level resolveLogLevel(Message message);

	public static enum Level {
		DEBUG, INFO, WARN, ERROR;
	}
}