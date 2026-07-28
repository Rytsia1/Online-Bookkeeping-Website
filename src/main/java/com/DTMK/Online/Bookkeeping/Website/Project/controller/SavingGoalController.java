package com.DTMK.Online.Bookkeeping.Website.Project.controller;

import com.DTMK.Online.Bookkeeping.Website.Project.entity.SavingGoal;
import com.DTMK.Online.Bookkeeping.Website.Project.mapper.SavingGoalMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/saving-goals")
@RequiredArgsConstructor
public class SavingGoalController {

    private final SavingGoalMapper savingGoalMapper;

    // GET /api/saving-goals?userId=1 — list all goals for user
    @GetMapping
    public ResponseEntity<List<SavingGoal>> getGoals(@RequestParam Integer userId) {
        return ResponseEntity.ok(savingGoalMapper.findByUserId(userId));
    }

    // POST /api/saving-goals — create a new goal
    @PostMapping
    public ResponseEntity<Map<String, String>> createGoal(@RequestBody SavingGoal goal) {
        goal.setSaved(BigDecimal.ZERO);
        goal.setCompleted(false);
        savingGoalMapper.insert(goal);
        return ResponseEntity.ok(Map.of("message", "Saving goal created successfully"));
    }

    // PUT /api/saving-goals/{id}/allocate — add amount to saved
    @PutMapping("/{id}/allocate")
    public ResponseEntity<Map<String, String>> allocate(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> payload) {

        // Fetch the current goal to get existing saved value
        // We pass the new total (current saved + allocation) from the frontend
        BigDecimal newSaved = new BigDecimal(payload.get("newSaved").toString());
        savingGoalMapper.updateSaved(id, newSaved);
        return ResponseEntity.ok(Map.of("message", "Allocation saved successfully"));
    }

    // DELETE /api/saving-goals/{id} — delete a goal
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteGoal(@PathVariable Integer id) {
        savingGoalMapper.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Saving goal deleted successfully"));
    }
}
