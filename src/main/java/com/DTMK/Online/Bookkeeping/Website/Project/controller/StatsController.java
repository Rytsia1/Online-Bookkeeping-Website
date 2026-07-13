package com.DTMK.Online.Bookkeeping.Website.Project.controller;

import com.DTMK.Online.Bookkeeping.Website.Project.dto.MonthlyStatsDTO;
import com.DTMK.Online.Bookkeeping.Website.Project.service.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final BillService billService;

    // GET /api/stats/summary?userId=1&month=7&year=2026
    @GetMapping("/summary")
    public ResponseEntity<MonthlyStatsDTO> getSummary(
            @RequestParam Integer userId,
            @RequestParam int month,
            @RequestParam int year) {
        return ResponseEntity.ok(billService.getMonthlyStats(userId, month, year));
    }

    // GET /api/stats/categories?userId=1&month=7&year=2026
    @GetMapping("/categories")
    public ResponseEntity<java.util.List<com.DTMK.Online.Bookkeeping.Website.Project.dto.CategoryStatDTO>> getCategories(
            @RequestParam Integer userId,
            @RequestParam int month,
            @RequestParam int year) {
        return ResponseEntity.ok(billService.getExpenseByCategory(userId, month, year));
    }
}