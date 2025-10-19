package com.andrewsalygin.service;

import com.andrewsalygin.entity.mongo.Item;
import com.andrewsalygin.entity.postgres.VKGroup;
import com.andrewsalygin.entity.postgres.Photo;
import com.andrewsalygin.entity.postgres.Post;
import com.andrewsalygin.entity.postgres.PostPhoto;
import com.andrewsalygin.repository.BatchRepository;
import com.andrewsalygin.repository.mongo.MongoItemRepository;
import com.andrewsalygin.util.EntityMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

import static com.andrewsalygin.util.JsonUtils.getSafeList;
import static com.andrewsalygin.util.JsonUtils.getSafeMap;

@Service
@RequiredArgsConstructor
public class MongoToPostgresConverterService {

    private final static Logger LOGGER = LoggerFactory.getLogger(MongoToPostgresConverterService.class);

    private final MongoItemRepository mongoItemRepository;

    private final BatchRepository batchRepository;

    private final EntityMapper entityMapper;

    public void convertPostsInAllGroups() {
        Set<String> groups = mongoItemRepository.getAllCollectionNames();
        for (var group : groups) {
            LOGGER.info("Конвертация постов группы {}...", group);
            convertPosts(group);
        }
    }

    private void convertPosts(String collectionName) {
        VKGroup group = new VKGroup(collectionName);

        List<Item> items = mongoItemRepository.getAllItemsByCollectionName(collectionName);

        List<Post> posts = new ArrayList<>();
        List<Photo> photos = new ArrayList<>();
        List<PostPhoto> postPhotos = new ArrayList<>();

        for (var item : items) {
            Map<String, Object> data = getSafeMap(item.data(), "data");
            if (data == null) throw invalidJson("data");

            Integer postId = (Integer) data.get("id");
            String postText = (String) data.get("text");
            Integer unixTime = (Integer) data.get("date");
            Instant postDate = Instant.ofEpochSecond(unixTime);

            Post post = new Post(postId, group.id(), postText, postDate);
            posts.add(post);

            Map<String, Object> attachments = getSafeMap(data, "attachments");
            if (attachments == null) throw invalidJson("attachments");

            List<Map<String, Object>> myArrayList = getSafeList(attachments, "myArrayList");

            if (myArrayList.isEmpty()) {
                continue;
            }

            for (Map<String, Object> element : myArrayList) {
                Map<String, Object> map = getSafeMap(element, "map");
                if (map == null) throw invalidJson("map");

                Map<String, Object> photoMap = getSafeMap(map, "photo");
                if (photoMap == null) continue;

                Map<String, Object> photoInnerMap = getSafeMap(photoMap, "map");
                if (photoInnerMap == null) throw invalidJson("map");

                Map<String, Object> origPhoto = getSafeMap(photoInnerMap, "orig_photo");
                Map<String, Object> origOrMaxSizePhotoInnerMap = Map.of();
                if (origPhoto == null) {
                    Map<String, Object> sizes = getSafeMap(photoInnerMap, "sizes");
                    if (sizes == null) throw invalidJson("sizes");

                    List<Map<String, Object>> myArrayListSizes = getSafeList(sizes, "myArrayList");
                    if (myArrayListSizes.isEmpty()) continue;

                    Optional<Map<String, Object>> maxElement = myArrayListSizes.stream()
                        .map(m -> getSafeMap(m, "map"))
                        .filter(Objects::nonNull)
                        .max(Comparator.comparingInt(innerMap -> (Integer) innerMap.get("width")));
                    if (maxElement.isPresent()) {
                        origOrMaxSizePhotoInnerMap = maxElement.get();
                    }
                } else {
                    origOrMaxSizePhotoInnerMap = getSafeMap(origPhoto, "map");
                }
                if (origOrMaxSizePhotoInnerMap == null) throw invalidJson("orig_photo или sizes");

                Photo photo = new Photo(
                    (Integer) photoInnerMap.get("id"),
                    (Integer) photoInnerMap.get("album_id"),
                    (Integer) photoInnerMap.get("owner_id"),
                    (String) origOrMaxSizePhotoInnerMap.get("url"),
                    "",
                    (Integer) origOrMaxSizePhotoInnerMap.get("height"),
                    (Integer) origOrMaxSizePhotoInnerMap.get("width")
                );
                photos.add(photo);

                PostPhoto postPhoto = new PostPhoto(
                    0, group.id(), post.id(), photo.id(), photo.albumId(), photo.ownerId()
                );
                postPhotos.add(postPhoto);
            }
        }

        List<Map<String, Object>> postsForInsert = posts.stream().map(entityMapper::toMap).toList();
        List<Map<String, Object>> photosForInsert = photos.stream().map(entityMapper::toMap).toList();
        List<Map<String, Object>> postPhotosForInsert = postPhotos.stream().map(entityMapper::toMap).toList();

        batchRepository.insertVkGroup(group.id());
        batchRepository.batchInsert("posts", postsForInsert);
        batchRepository.batchInsert("photo", photosForInsert);
        batchRepository.batchInsert("post_photos", postPhotosForInsert);

        LOGGER.info("Конвертация постов группы {} завершена", group.id());
    }

    private static IllegalStateException invalidJson(String key) {
        String message = "Неверный формат json. Не найден ключ " + key + " в json поста.";
        LOGGER.error(message);
        throw new IllegalStateException(message);
    }
}
