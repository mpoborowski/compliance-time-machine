package com.aquacode.ctm.history.infrastructure;

import com.aquacode.ctm.history.application.HistoricalReplayService;
import com.aquacode.ctm.history.infrastructure.dto.HistoricalReplayResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/history/transactions")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoricalReplayService historicalReplayService;
    private final HistoricalReplayResponseMapper mapper;

    @GetMapping("/{transactionId}")
    HistoricalReplayResponse replay(@PathVariable String transactionId) {
        var replay = historicalReplayService.replay(transactionId);
        return mapper.toResponse(replay);
    }
}