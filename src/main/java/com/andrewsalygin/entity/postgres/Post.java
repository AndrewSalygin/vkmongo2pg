package com.andrewsalygin.entity.postgres;

import java.time.Instant;

public record Post(
    int id,
    String groupId,
    String text,
    Instant date
) {}