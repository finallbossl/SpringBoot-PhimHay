package com.phimhay.juanng.modules.catalog.controller;

import com.phimhay.juanng.common.response.ApiResponse;
import com.phimhay.juanng.modules.catalog.dto.ExternalMovieResponse;
import com.phimhay.juanng.modules.catalog.entity.Movie;
import com.phimhay.juanng.modules.catalog.service.MovieSyncService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/catalog/sync")
@RequiredArgsConstructor
public class MovieSyncController {

    private final MovieSyncService movieSyncService;

    @PostMapping("/movie")
    public ApiResponse<Movie> syncMovie(@RequestBody @Valid ExternalMovieResponse request) {
        Movie movie = movieSyncService.syncMovie(request);
        return ApiResponse.<Movie>builder()
                .code(1000)
                .message("Đồng bộ phim thành công!")
                .result(movie)
                .build();
    }
}
