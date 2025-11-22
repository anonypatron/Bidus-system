package com.web.bff.controller;

import com.common.dto.user.UserPrincipal;
import com.web.bff.dto.stats.DashboardResponseDto;
import com.web.bff.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/dashboard")
public class DashboardBffController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public Mono<ResponseEntity<DashboardResponseDto>> getDashboardStats(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        Long userId = userPrincipal == null ? null : userPrincipal.getId();
        return dashboardService.getDashboardStats(userId)
                .map(ResponseEntity::ok);
    }

}
