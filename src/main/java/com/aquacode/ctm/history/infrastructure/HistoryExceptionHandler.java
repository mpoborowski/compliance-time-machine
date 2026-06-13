package com.aquacode.ctm.history.infrastructure;

import com.aquacode.ctm.history.application.HistoricalDecisionUnreplayableException;
import com.aquacode.ctm.history.application.HistoricalReplayNotFoundException;
import com.aquacode.ctm.history.infrastructure.dto.HistoryErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = HistoryController.class)
public class HistoryExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(HistoricalReplayNotFoundException.class)
    public HistoryErrorResponse handleNotFound(HistoricalReplayNotFoundException exception) {
        return HistoryErrorResponse.builder()
            .code("TRANSACTION_NOT_FOUND")
            .message(exception.getMessage())
            .build();
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
    @ExceptionHandler(HistoricalDecisionUnreplayableException.class)
    public HistoryErrorResponse handleUnreplayable(HistoricalDecisionUnreplayableException exception) {
        return HistoryErrorResponse.builder()
            .code("UNREPLAYABLE_DECISION")
            .message(exception.getMessage())
            .build();
    }
}