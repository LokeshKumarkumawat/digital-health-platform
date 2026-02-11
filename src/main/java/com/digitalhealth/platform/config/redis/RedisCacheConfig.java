package com.digitalhealth.platform.config.redis;

import com.fasterxml.jackson.annotation.JsonTypeInfo; //unchanged in Jackson 3
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.DefaultTyping;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis Cache Configuration
 *
 * Configures Redis as the caching provider with different TTL for different cache types
 */
@Configuration
@EnableCaching
@Slf4j
public class RedisCacheConfig implements CachingConfigurer {

    private final RedisConnectionFactory redisConnectionFactory;

    public RedisCacheConfig(RedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
    }

    /**
     * Cache names used throughout the application
     */
    public static class CacheNames {
        // User caches
        public static final String USERS = "users";
        public static final String USER_BY_EMAIL = "userByEmail";
        public static final String USER_ROLES = "userRoles";

        // Patient caches
        public static final String PATIENTS = "patients";
        public static final String PATIENT_BY_USER = "patientByUser";

        // Doctor caches
        public static final String DOCTORS = "doctors";
        public static final String DOCTOR_BY_USER = "doctorByUser";
        public static final String DOCTOR_BY_LICENSE = "doctorByLicense";
        public static final String DOCTORS_BY_SPECIALIZATION = "doctorsBySpecialization";

        // Appointment caches
        public static final String APPOINTMENTS = "appointments";
        public static final String APPOINTMENTS_BY_DOCTOR = "appointmentsByDoctor";
        public static final String APPOINTMENTS_BY_PATIENT = "appointmentsByPatient";
        public static final String UPCOMING_APPOINTMENTS = "upcomingAppointments";

        // Consultation caches
        public static final String CONSULTATIONS = "consultations";
        public static final String CONSULTATION_HISTORY = "consultationHistory";

        // Payment caches
        public static final String PAYMENTS = "payments";
        public static final String PAYMENT_BY_INTENT = "paymentByIntent";

        // Invoice caches
        public static final String INVOICES = "invoices";
        public static final String INVOICE_BY_NUMBER = "invoiceByNumber";

        // Statistics caches (longer TTL)
        public static final String STATISTICS = "statistics";
        public static final String DASHBOARD_STATS = "dashboardStats";
    }

    /**
     * Configure ObjectMapper for Redis serialization
     */

    @Bean
    public ObjectMapper redisObjectMapper() {

        BasicPolymorphicTypeValidator typeValidator =
                BasicPolymorphicTypeValidator.builder()
                        // Allow your domain classes
                        .allowIfSubType("com.digitalhealth.platform")
                        // Allow Java collections safely
                        .allowIfSubType("java.util")
                        .build();
        return JsonMapper.builder()
                .activateDefaultTyping(
                        typeValidator,
                        //For all non-final classes (like Payment, CardPayment, User, Doctor), I will include type information
                        DefaultTyping.NON_FINAL,
                        //In what format should type metadata appear in JSON?
                        JsonTypeInfo.As.PROPERTY
                )
                .build();
    }


    /**
     * Configure Redis Template for custom operations
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Use String serializer for keys
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // The Generic serializer will use the immutable mapper created above
        GenericJacksonJsonRedisSerializer jsonSerializer =
                new GenericJacksonJsonRedisSerializer(redisObjectMapper());
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();

        log.info("Redis template configured successfully");
        return template;
    }

    /**
     * Configure Cache Manager with different TTL for different cache types
     */
    @Bean
    @Override
    public CacheManager cacheManager() {
        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager
                .builder(redisConnectionFactory)
                .cacheDefaults(defaultCacheConfiguration());

        // Set specific TTL for each cache type
        builder.withInitialCacheConfigurations(getCacheConfigurations());

        log.info("Redis cache manager configured with custom TTL settings");
        return builder.build();
    }

    /**
     * Default cache configuration (5 minutes TTL)
     */
    private RedisCacheConfiguration defaultCacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(5))
                .computePrefixWith(cacheName -> "dhp::cache::" + cacheName + "::")
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(new StringRedisSerializer())
                )
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(new GenericJacksonJsonRedisSerializer(redisObjectMapper()))
                )
                .disableCachingNullValues();
    }

    /**
     * Custom cache configurations with different TTL
     */
    private Map<String, RedisCacheConfiguration> getCacheConfigurations() {
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        // User caches - 10 minutes (frequently accessed, changes rarely)
        cacheConfigurations.put(CacheNames.USERS, createCacheConfig(Duration.ofMinutes(10)));
        cacheConfigurations.put(CacheNames.USER_BY_EMAIL, createCacheConfig(Duration.ofMinutes(10)));
        cacheConfigurations.put(CacheNames.USER_ROLES, createCacheConfig(Duration.ofMinutes(15)));

        // Patient caches - 15 minutes (profile data, changes infrequently)
        cacheConfigurations.put(CacheNames.PATIENTS, createCacheConfig(Duration.ofMinutes(15)));
        cacheConfigurations.put(CacheNames.PATIENT_BY_USER, createCacheConfig(Duration.ofMinutes(15)));

        // Doctor caches - 30 minutes (profile data, changes very rarely)
        cacheConfigurations.put(CacheNames.DOCTORS, createCacheConfig(Duration.ofMinutes(30)));
        cacheConfigurations.put(CacheNames.DOCTOR_BY_USER, createCacheConfig(Duration.ofMinutes(30)));
        cacheConfigurations.put(CacheNames.DOCTOR_BY_LICENSE, createCacheConfig(Duration.ofMinutes(30)));
        cacheConfigurations.put(CacheNames.DOCTORS_BY_SPECIALIZATION, createCacheConfig(Duration.ofMinutes(20)));

        // Appointment caches - 3 minutes (frequently changes)
        cacheConfigurations.put(CacheNames.APPOINTMENTS, createCacheConfig(Duration.ofMinutes(3)));
        cacheConfigurations.put(CacheNames.APPOINTMENTS_BY_DOCTOR, createCacheConfig(Duration.ofMinutes(3)));
        cacheConfigurations.put(CacheNames.APPOINTMENTS_BY_PATIENT, createCacheConfig(Duration.ofMinutes(3)));
        cacheConfigurations.put(CacheNames.UPCOMING_APPOINTMENTS, createCacheConfig(Duration.ofMinutes(2)));

        // Consultation caches - 1 hour (historical data, rarely changes)
        cacheConfigurations.put(CacheNames.CONSULTATIONS, createCacheConfig(Duration.ofHours(1)));
        cacheConfigurations.put(CacheNames.CONSULTATION_HISTORY, createCacheConfig(Duration.ofMinutes(30)));

        // Payment caches - 10 minutes
        cacheConfigurations.put(CacheNames.PAYMENTS, createCacheConfig(Duration.ofMinutes(10)));
        cacheConfigurations.put(CacheNames.PAYMENT_BY_INTENT, createCacheConfig(Duration.ofMinutes(10)));

        // Invoice caches - 15 minutes
        cacheConfigurations.put(CacheNames.INVOICES, createCacheConfig(Duration.ofMinutes(15)));
        cacheConfigurations.put(CacheNames.INVOICE_BY_NUMBER, createCacheConfig(Duration.ofMinutes(15)));

        // Statistics caches - 1 hour (expensive queries, can be stale)
        cacheConfigurations.put(CacheNames.STATISTICS, createCacheConfig(Duration.ofHours(1)));
        cacheConfigurations.put(CacheNames.DASHBOARD_STATS, createCacheConfig(Duration.ofMinutes(30)));

        return cacheConfigurations;
    }

    /**
     * Create cache configuration with specific TTL
     */
    private RedisCacheConfiguration createCacheConfig(Duration ttl) {
        return defaultCacheConfiguration()
                .entryTtl(ttl);
    }
}