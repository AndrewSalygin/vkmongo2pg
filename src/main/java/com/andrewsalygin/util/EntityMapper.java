package com.andrewsalygin.util;

import com.andrewsalygin.entity.postgres.Photo;
import com.andrewsalygin.entity.postgres.Post;
import com.andrewsalygin.entity.postgres.PostPhoto;
import org.mapstruct.Mapper;

import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface EntityMapper {

    default Map<String, Object> toMap(Post post) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", post.id());
        map.put("group_id", post.groupId());
        map.put("text", post.text());
        map.put("date", Timestamp.from(post.date()));
        return map;
    }

    default Map<String, Object> toMap(Photo photo) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", photo.id());
        map.put("album_id", photo.albumId());
        map.put("owner_id", photo.ownerId());
        map.put("vk_url", photo.vkUrl());
        map.put("minio_url", photo.minioUrl());
        map.put("height", photo.height());
        map.put("width", photo.width());
        return map;
    }

    default Map<String, Object> toMap(PostPhoto pp) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("group_id", pp.groupId());
        map.put("post_id", pp.postId());
        map.put("photo_id", pp.photoId());
        map.put("album_id", pp.albumId());
        map.put("owner_id", pp.ownerId());
        return map;
    }
}