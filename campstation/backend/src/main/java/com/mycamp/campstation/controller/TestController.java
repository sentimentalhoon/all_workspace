package com.mycamp.campstation.controller;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import com.mycamp.campstation.entity.TestData;
import com.mycamp.campstation.repository.TestDataRepository;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private TestDataRepository testDataRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Value("${minio.endpoint}")
    private String minioEndpoint;

    @Value("${minio.access-key}")
    private String minioAccessKey;

    @Value("${minio.secret-key}")
    private String minioSecretKey;

    @Value("${minio.bucket}")
    private String minioBucket;

    @GetMapping("/postgres")
    public Map<String, Object> testPostgres() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Create test data
            TestData testData = new TestData("test", "Hello PostgreSQL!");
            TestData saved = testDataRepository.save(testData);
            
            // Retrieve all data
            List<TestData> allData = testDataRepository.findAll();
            
            response.put("status", "success");
            response.put("saved", saved);
            response.put("count", allData.size());
            response.put("message", "PostgreSQL connection successful");
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
        }
        
        return response;
    }

    @GetMapping("/redis")
    public Map<String, Object> testRedis() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String key = "test:key";
            String value = "Hello Redis!";
            
            // Set value
            redisTemplate.opsForValue().set(key, value);
            
            // Get value
            String retrieved = redisTemplate.opsForValue().get(key);
            
            response.put("status", "success");
            response.put("key", key);
            response.put("value", retrieved);
            response.put("message", "Redis connection successful");
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
        }
        
        return response;
    }

    @GetMapping("/minio")
    public Map<String, Object> testMinio() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            MinioClient minioClient = MinioClient.builder()
                    .endpoint(minioEndpoint)
                    .credentials(minioAccessKey, minioSecretKey)
                    .build();
            
            // Check if bucket exists
            boolean bucketExists = minioClient.bucketExists(
                io.minio.BucketExistsArgs.builder().bucket(minioBucket).build()
            );
            
            if (!bucketExists) {
                minioClient.makeBucket(
                    io.minio.MakeBucketArgs.builder().bucket(minioBucket).build()
                );
            }
            
            // Upload test file
            String content = "Hello MinIO!";
            byte[] bytes = content.getBytes();
            ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
            
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(minioBucket)
                    .object("test.txt")
                    .stream(stream, bytes.length, -1)
                    .contentType("text/plain")
                    .build()
            );
            
            response.put("status", "success");
            response.put("bucket", minioBucket);
            response.put("file", "test.txt");
            response.put("message", "MinIO connection successful");
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
        }
        
        return response;
    }

    @GetMapping("/all")
    public Map<String, Object> testAll() {
        Map<String, Object> response = new HashMap<>();
        
        response.put("postgres", testPostgres());
        response.put("redis", testRedis());
        response.put("minio", testMinio());
        
        return response;
    }
}
