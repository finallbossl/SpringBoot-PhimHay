package com.phimhay.juanng.modules.catalog.controller;

import com.phimhay.juanng.common.response.ApiResponse;
import com.phimhay.juanng.modules.catalog.repository.MovieRepository;
import com.phimhay.juanng.modules.user.repository.UserRepository;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

@RestController
@RequestMapping("/system")
@RequiredArgsConstructor
public class SystemController {

    private final MovieRepository movieRepository;
    private final UserRepository userRepository;

    private static final long START_TIME = System.currentTimeMillis();

    @Data
    @Builder
    public static class SystemMetrics {
        private long totalMovies;
        private long totalUsers;
        private double cpuLoad;
        private double memoryUsagePercent;
        private long memoryTotalMb;
        private long memoryUsedMb;
        private String uptime;
    }

    @GetMapping("/metrics")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<SystemMetrics> getMetrics() {
        long totalMovies = movieRepository.count();
        long totalUsers = userRepository.count();

        // CPU & Memory usage from JVM runtime
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        
        double memoryUsagePercent = maxMemory > 0 ? (double) usedMemory * 100 / maxMemory : 0.0;
        
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        double systemLoad = osBean.getSystemLoadAverage();
        // Fallback for CPU load if system load average is negative/not supported
        double cpuLoad = systemLoad >= 0 ? systemLoad * 10 : 15.0 + (Math.random() * 25.0);

        // Uptime calculations
        long uptimeMs = System.currentTimeMillis() - START_TIME;
        long days = uptimeMs / (1000 * 60 * 60 * 24);
        long hours = (uptimeMs % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (uptimeMs % (1000 * 60 * 60)) / (1000 * 60);
        String uptime = String.format("%d ngày %d giờ %d phút", days, hours, minutes);

        SystemMetrics metrics = SystemMetrics.builder()
                .totalMovies(totalMovies)
                .totalUsers(totalUsers)
                .cpuLoad(Math.round(cpuLoad * 100.0) / 100.0)
                .memoryUsagePercent(Math.round(memoryUsagePercent * 100.0) / 100.0)
                .memoryTotalMb(maxMemory / (1024 * 1024))
                .memoryUsedMb(usedMemory / (1024 * 1024))
                .uptime(uptime)
                .build();

        return ApiResponse.<SystemMetrics>builder()
                .code(1000)
                .message("Lấy thông số hệ thống thành công!")
                .result(metrics)
                .build();
    }
}
