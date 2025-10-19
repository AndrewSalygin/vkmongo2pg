package com.andrewsalygin.entity.postgres;

public record Photo(
    int id,
    int albumId,
    int ownerId,
    String vkUrl,
    String minioUrl,
    int height,
    int width
) {}