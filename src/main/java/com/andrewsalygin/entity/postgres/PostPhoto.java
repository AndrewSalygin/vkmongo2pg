package com.andrewsalygin.entity.postgres;

public record PostPhoto(
    int id,
    String groupId,
    int postId,
    int photoId,
    int albumId,
    int ownerId
) {}