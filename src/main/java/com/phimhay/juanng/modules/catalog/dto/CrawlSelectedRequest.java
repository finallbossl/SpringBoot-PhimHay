package com.phimhay.juanng.modules.catalog.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrawlSelectedRequest {

    @NotEmpty(message = "Danh sách slugs chọn lọc không được để trống.")
    private List<String> slugs;
}
