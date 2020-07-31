package ru.itmo.idu.admin.services

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.filter.LevelFilter
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.AppenderBase
import org.apache.commons.collections4.queue.CircularFifoQueue
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.itmo.idu.admin.api_classes.dto.LogEntryDTO
import javax.annotation.PostConstruct


@Service
class LogService(
        @Value("\${log.entries}")
        private val logEntries: Int
) {

    private val messages = CircularFifoQueue<ILoggingEvent>(logEntries)

    inner class LogAppender: AppenderBase<ILoggingEvent>() {
        override fun append(p0: ILoggingEvent?) {
            messages.add(p0);
        }

    }

    @PostConstruct
    fun setUp() {
        val logAppender = LogAppender()
        val levelFilter = LevelFilter()
        levelFilter.setLevel(Level.INFO)
        logAppender.addFilter(levelFilter)
        val loggerContext: LoggerContext = LoggerFactory.getILoggerFactory() as LoggerContext

        logAppender.setContext(loggerContext)

        val root: Logger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger

        root.addAppender(logAppender)

        logAppender.start()
    }

    fun getLogEntries(): List<LogEntryDTO> {
        return messages.reversed().map { LogEntryDTO(it.level.levelStr, it.timeStamp, it.formattedMessage) }
    }
}