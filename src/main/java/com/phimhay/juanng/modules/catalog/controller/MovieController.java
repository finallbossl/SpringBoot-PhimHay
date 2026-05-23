package com.phimhay.juanng.modules.catalog.controller;

import com.phimhay.juanng.common.response.ApiResponse;
import com.phimhay.juanng.modules.catalog.entity.Movie;
import com.phimhay.juanng.modules.catalog.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/catalog/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieRepository movieRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<Movie>> getAllMovies() {
        List<Movie> movies = movieRepository.findAll();
        return ApiResponse.<List<Movie>>builder()
                .code(1000)
                .message("Lấy danh sách phim thành công!")
                .result(movies)
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteMovie(@PathVariable String id) {
        if (!movieRepository.existsById(id)) {
            return ApiResponse.<Void>builder()
                    .code(1001)
                    .message("Không tìm thấy phim cần xóa!")
                    .build();
        }
        movieRepository.deleteById(id);
        return ApiResponse.<Void>builder()
                .code(1000)
                .message("Xóa phim thành công!")
                .build();
    }

    @PutMapping("/{id}/premium")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Movie> togglePremium(@PathVariable String id, @RequestParam boolean isPremium) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phim"));
        movie.setPremiumOnly(isPremium);
        Movie updated = movieRepository.save(movie);
        return ApiResponse.<Movie>builder()
                .code(1000)
                .message("Cập nhật gói VIP phim thành công!")
                .result(updated)
                .build();
    }
}
