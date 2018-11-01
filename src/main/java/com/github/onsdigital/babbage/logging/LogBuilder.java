package com.github.onsdigital.babbage.logging;

import ch.qos.logback.classic.Level;
import com.github.onsdigital.babbage.error.BabbageException;
import com.github.onsdigital.babbage.error.BadRequestException;
import com.github.onsdigital.babbage.error.InternalServerErrorException;
import com.github.onsdigital.babbage.error.ResourceNotFoundException;
import com.github.onsdigital.logging.builder.LogMessageBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpRequestBase;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LogBuilder extends LogMessageBuilder {

    private static final String REQ_ID = "X-Request-Id";

    public static LogBuilder logEvent() {
        return new LogBuilder("");
    }

    public static LogBuilder logEvent(Throwable e) {
        return new LogBuilder(e);
    }

    public static LogBuilder logEvent(Throwable e, String message) {
        return new LogBuilder(e, message);
    }

    protected LogBuilder(String eventDescription) {
        super(eventDescription);
        setNamespace("babbage"); // TODO should this be configurable?
    }

    protected LogBuilder(Throwable e) {
        this(e, "");
    }

    protected LogBuilder(Throwable e, String message) {
        super(e, message);
        setNamespace("babbage"); // TODO should this be configurable?
    }

    @Override
    public String getLoggerName() {
        return "babbage";
    }

    public void trace(String message) {
        logLevel = Level.TRACE;
        description = message;
        log();
    }

    public void debug(String message) {
        logLevel = Level.DEBUG;
        description = message;
        log();
    }

    public void info(String message) {
        logLevel = Level.INFO;
        description = message;
        log();
    }

    public void warn(String message) {
        logLevel = Level.WARN;
        description = message;
        log();
    }

    public void error(String message) {
        logLevel = Level.ERROR;
        description = message;
        log();
    }

    public LogBuilder httpGET() {
        return addParamSafe("httpMethod", "GET");
    }

    public LogBuilder httpPOST() {
        return addParamSafe("httpMethod", "POST");
    }

    public LogBuilder httpDELETE() {
        return addParamSafe("httpMethod", "DELETE");
    }

    public LogBuilder parameter(String key, Object value) {
        return addParamSafe(key, value);
    }

    public <T, R> LogBuilder parameter(String key, Stream<T> s, Function<T, R> valueMapper) {
        String value = s.map(i -> valueMapper.apply(i))
                .collect(Collectors.toList())
                .toString();

        addParamSafe(key, value);
        return this;
    }

    public LogBuilder uri(String uri) {
        return addParamSafe("uri", uri);
    }

    public LogBuilder host(String host) {
        return addParamSafe("host", host);
    }

    public LogBuilder requestParam(List<NameValuePair> params) {
        return addParamSafe("requestParameters", params);
    }

    public LogBuilder responseStatus(int status) {
        return addParamSafe("responseStatus", status);
    }

    public LogBuilder requestID(HttpRequestBase req) {
        if (req != null) {
            HeaderIterator it = req.headerIterator(REQ_ID);
            while (it.hasNext()) {
                Header h = it.nextHeader();
                if (h.getName().equals(REQ_ID)) {
                    addParamSafe("requestID", h.getValue());
                }
            }
        }
        return this;
    }

    public BabbageException logAndCreateException(int statusCode, Throwable cause) {
        parameter("details", cause.getMessage());
        log();
        switch (statusCode) {
            case 400:
                return new BadRequestException(cause.getMessage());
            case 404:
                return new ResourceNotFoundException(cause.getMessage());
            default:
                return new InternalServerErrorException(cause.getMessage(), cause);
        }
    }

    private LogBuilder addParamSafe(String key, Object value) {
        if (StringUtils.isNotEmpty(key) && null != value) {
            super.addParameter(key, value);
        }
        return this;
    }
}
