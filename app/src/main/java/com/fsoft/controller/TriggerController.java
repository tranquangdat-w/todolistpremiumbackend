package com.fsoft.controller;

import com.fsoft.service.TriggerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/triggers")
@RequiredArgsConstructor
@Tag(name = "Trigger Management", description = "API để quản lý database triggers")
public class TriggerController {

    private final TriggerService triggerService;

    @PostMapping("/install")
    @Operation(summary = "Install all database triggers")
    public ResponseEntity<Map<String, String>> installTriggers() {
        try {
            triggerService.installTriggers();
            return ResponseEntity.ok(Map.of(
                "message", "Triggers installed successfully",
                "status", "success"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "message", "Failed to install triggers: " + e.getMessage(),
                "status", "error"
            ));
        }
    }

    @DeleteMapping("/uninstall")
    @Operation(summary = "Uninstall all database triggers")
    public ResponseEntity<Map<String, String>> uninstallTriggers() {
        try {
            triggerService.uninstallTriggers();
            return ResponseEntity.ok(Map.of(
                "message", "Triggers uninstalled successfully",
                "status", "success"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "message", "Failed to uninstall triggers: " + e.getMessage(),
                "status", "error"
            ));
        }
    }

    @PostMapping("/manual/invitation-accept/{invitationId}")
    @Operation(summary = "Manually trigger invitation acceptance notification")
    public ResponseEntity<Map<String, String>> manualTriggerInvitationAccept(@PathVariable UUID invitationId) {
        try {
            triggerService.manualTriggerInvitationAccept(invitationId);
            return ResponseEntity.ok(Map.of(
                "message", "Manual trigger executed successfully",
                "status", "success"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "message", "Failed to execute manual trigger: " + e.getMessage(),
                "status", "error"
            ));
        }
    }

    @PostMapping("/cleanup/expired-invitations")
    @Operation(summary = "Cleanup expired invitations")
    public ResponseEntity<Map<String, Object>> cleanupExpiredInvitations() {
        try {
            int deletedCount = triggerService.cleanupExpiredInvitations();
            return ResponseEntity.ok(Map.of(
                "message", "Cleanup completed successfully",
                "deletedCount", deletedCount,
                "status", "success"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "message", "Failed to cleanup: " + e.getMessage(),
                "status", "error"
            ));
        }
    }

    @GetMapping("/stats/board/{boardId}")
    @Operation(summary = "Get board statistics using stored procedure")
    public ResponseEntity<Map<String, Object>> getBoardStats(@PathVariable UUID boardId) {
        try {
            Map<String, Object> stats = triggerService.getBoardStatistics(boardId);
            return ResponseEntity.ok(Map.of(
                "data", stats,
                "status", "success"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "message", "Failed to get board statistics: " + e.getMessage(),
                "status", "error"
            ));
        }
    }

    @PostMapping("/notification/manual")
    @Operation(summary = "Create manual notification")
    public ResponseEntity<Map<String, String>> createManualNotification(
            @RequestParam UUID userId,
            @RequestParam String type,
            @RequestParam String note,
            @RequestParam(required = false) String jsonData) {

        triggerService.triggerManualNotification(userId, type, note, jsonData);
        return ResponseEntity.ok(Map.of("message", "Notification created successfully"));
    }

    @PutMapping("/cards/{columnId}/positions")
    @Operation(summary = "Update card positions in bulk")
    public ResponseEntity<Map<String, String>> updateCardPositions(
            @PathVariable UUID columnId,
            @RequestBody String positionUpdates) {

        triggerService.updateCardPositions(columnId, positionUpdates);
        return ResponseEntity.ok(Map.of("message", "Card positions updated successfully"));
    }
}
