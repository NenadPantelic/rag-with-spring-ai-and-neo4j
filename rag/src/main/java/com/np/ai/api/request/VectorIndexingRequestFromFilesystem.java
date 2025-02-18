package com.np.ai.api.request;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record VectorIndexingRequestFromFilesystem(@NotBlank String path,
                                                  List<String> keywords) {
}
