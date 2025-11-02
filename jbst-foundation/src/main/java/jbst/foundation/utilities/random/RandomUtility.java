package jbst.foundation.utilities.random;

import feign.FeignException;
import feign.Request;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.exceptions.random.JbstIllegalEnumException;
import jbst.foundation.domain.properties.base.JbstPropertyTimeAmount;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.Math.abs;
import static java.math.BigDecimal.ONE;
import static java.time.ZoneId.systemDefault;
import static java.time.ZoneOffset.UTC;
import static jbst.foundation.utilities.spring.SpringAuthoritiesUtility.getSimpleGrantedAuthorities;
import static jbst.foundation.utilities.time.DateUtility.convertLocalDateTime;
import static jbst.foundation.utilities.time.TimestampUtility.getCurrentTimestamp;

@UtilityClass
public class RandomUtility {

    private static final String LETTERS_OR_NUMBERS = "AaBbCcDdEeFfGgHgIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz0123456789";
    private static final SecureRandom RND = new SecureRandom();

    private static final Map<Class<?>, Supplier<?>> PRIMITIVE_WRAPPERS = Map.of(
            // NOTE: classes should be considered/double-checked in future releases
            // NOTE: classes - Byte, Character, CharSequence, Float, check Number inheritance tree)
            Short.class, RandomUtility::randomShort,
            Boolean.class, RandomUtility::randomBoolean,
            Integer.class, RandomUtility::randomInteger,
            Long.class, RandomUtility::randomLong,
            Double.class, RandomUtility::randomDouble,
            BigDecimal.class, RandomUtility::randomBigDecimal
    );

    public static Number one(Class<? extends Number> clazz) {
        if (clazz == Short.class) {
            return 1;
        }
        if (clazz == Integer.class) {
            return 1;
        }
        if (clazz == Long.class) {
            return 1L;
        }
        if (clazz == Double.class) {
            return 1.0d;
        }
        if (clazz == BigDecimal.class) {
            return ONE;
        }
        throw new IllegalArgumentException("Unexpected clazz: " + clazz.getName());
    }

    public static Short randomShort() {
        return (short) RND.nextInt(Short.MAX_VALUE);
    }

    public static boolean randomBoolean() {
        return RND.nextBoolean();
    }

    public static Double randomDouble() {
        return RND.nextDouble();
    }

    public static Integer randomInteger() {
        return RND.nextInt();
    }

    public static Integer randomIntegerGreaterThanZero() {
        return abs(randomInteger());
    }

    public static Integer randomIntegerLessThanZero() {
        return -randomIntegerGreaterThanZero();
    }

    public static Integer randomIntegerGreaterThanZeroByBounds(int lowerBound, int upperBound) {
        return lowerBound + RND.nextInt(upperBound - lowerBound + 1);
    }

    public static Long randomLong() {
        return RND.nextLong();
    }

    public static Long randomLongGreaterThanZero() {
        return abs(randomLong());
    }

    public static Long randomLongLessThanZero() {
        return -randomLongGreaterThanZero();
    }

    public static Long randomLongGreaterThanZeroByBounds(long lowerBound, long upperBound) {
        return lowerBound + RND.nextInt((int) (upperBound - lowerBound + 1));
    }

    public static BigDecimal randomBigDecimal() {
        return BigDecimal.valueOf(randomDouble() * randomIntegerGreaterThanZeroByBounds(-90, 90));
    }

    public static BigDecimal randomBigDecimalGreaterThanZero() {
        return BigDecimal.valueOf(randomDouble() * randomIntegerGreaterThanZeroByBounds(10, 90));
    }

    public static BigDecimal randomBigDecimalLessThanZero() {
        return randomBigDecimalGreaterThanZero().multiply(BigDecimal.ONE.negate());
    }

    public static BigDecimal randomBigDecimalGreaterThanZeroByBounds(long lowerBound, long upperBound) {
        var longValue = randomLongGreaterThanZeroByBounds(lowerBound, upperBound - 2);
        var delta = BigDecimal.valueOf(randomDouble()).add(ONE);
        return new BigDecimal(longValue).add(delta);
    }

    public static BigDecimal randomBigDecimalLessThanZeroByBounds(long lowerBound, long upperBound) {
        return randomBigDecimalGreaterThanZeroByBounds(lowerBound, upperBound).multiply(BigDecimal.ONE.negate());
    }

    public static BigDecimal randomBigDecimalByBounds(long lowerBound, long upperBound) {
        return randomBoolean() ?
                randomBigDecimalGreaterThanZeroByBounds(lowerBound, upperBound) :
                randomBigDecimalLessThanZeroByBounds(lowerBound, upperBound);
    }

    public static BigInteger randomBigInteger() {
        return BigInteger.valueOf(randomIntegerGreaterThanZeroByBounds(-90, 90));
    }

    public static BigInteger randomBigIntegerGreaterThanZero() {
        return BigInteger.valueOf(randomLongGreaterThanZero());
    }

    public static BigInteger randomBigIntegerLessThanZero() {
        return BigInteger.valueOf(randomLongLessThanZero());
    }

    public static BigInteger randomBigIntegerGreaterThanZeroByBounds(long lowerBound, long upperBound) {
        return BigInteger.valueOf(randomLongGreaterThanZeroByBounds(lowerBound, upperBound));
    }

    public static BigInteger randomBigIntegerLessThanZeroByBounds(long lowerBound, long upperBound) {
        return BigInteger.valueOf(-randomLongGreaterThanZeroByBounds(lowerBound, upperBound));
    }

    public static BigInteger randomBigIntegerByBounds(long lowerBound, long upperBound) {
        return randomBoolean() ?
                randomBigIntegerGreaterThanZeroByBounds(lowerBound, upperBound) :
                randomBigIntegerLessThanZeroByBounds(lowerBound, upperBound);
    }

    public static String randomString() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String randomStringLetterOrNumbersOnly(int size) {
        var sb = new StringBuilder();
        while (sb.length() < size) {
            var index = randomIntegerGreaterThanZeroByBounds(0, LETTERS_OR_NUMBERS.length() - 1);
            sb.append(LETTERS_OR_NUMBERS.charAt(index));
        }
        return sb.toString();
    }

    public static String randomIPv4() {
        return RND.nextInt(256) + "." + RND.nextInt(256) + "." + RND.nextInt(256) + "." + RND.nextInt(256);
    }

    public static String randomServerURL() {
        var ip = randomIPv4();
        var protocol = RND.nextBoolean() ? "http" : "https";
        int port = 4000 + RND.nextInt(5000);
        return protocol + "://" + ip + ":" + port;
    }

    public static List<String> randomStringsAsList(int size) {
        return IntStream.range(0, size)
                .mapToObj(position -> randomString())
                .toList();
    }

    public static Set<String> randomStringsAsSet(int size) {
        return IntStream.range(0, size)
                .mapToObj(position -> randomString())
                .collect(Collectors.toSet());
    }

    public static String[] randomStringsAsArray(int size) {
        return IntStream.range(0, size)
                .mapToObj(position -> randomString())
                .toArray(String[]::new);
    }

    public static <T> T randomElement(List<T> list) {
        var randomIndex = RND.nextInt(list.size());
        return list.get(randomIndex);
    }

    public static <T> T randomElement(Set<T> set) {
        return randomElement(new ArrayList<>(set));
    }

    public static <T> T randomElementExcept(List<T> options, List<T> except) {
        return randomElementExcept(
                new HashSet<>(options),
                new HashSet<>(except)
        );
    }

    public static <T> T randomElementExcept(Set<T> options, Set<T> except) {
        Set<T> availableOptions = new HashSet<>(options);
        availableOptions.removeAll(except);
        if (availableOptions.isEmpty()) {
            return null;
        }
        return RandomUtility.randomElement(availableOptions);
    }

    public static LocalDate randomLocalDate() {
        return randomLocalDateByBounds(2000, LocalDate.now().getYear());
    }

    public static LocalDate randomLocalDateByBounds(int lowerYear, int upperYear) {
        var minDay = LocalDate.of(lowerYear, 1, 1).toEpochDay();
        var maxDay = LocalDate.of(upperYear, 1, 1).toEpochDay();
        var randomDay = randomLongGreaterThanZeroByBounds(minDay, maxDay);
        return LocalDate.ofEpochDay(randomDay);
    }

    public static LocalDateTime randomLocalDateTime() {
        return randomLocalDateTimeByBounds(2000, LocalDate.now().getYear());
    }

    public static LocalDateTime randomLocalDateTimeByBounds(int lowerYear, int upperYear) {
        var minSeconds = 0;
        var maxSeconds = 24 * ChronoUnit.HOURS.getDuration().getSeconds();
        var randomSeconds = randomLongGreaterThanZeroByBounds(minSeconds, maxSeconds);
        return randomLocalDateByBounds(lowerYear, upperYear).atStartOfDay().plusSeconds(randomSeconds);
    }

    public static Date randomDate() {
        return Date.from(randomLocalDateTime().atZone(systemDefault()).toInstant());
    }

    public static Date randomDateByBounds(int lowerYear, int upperYear) {
        return Date.from(randomLocalDateTimeByBounds(lowerYear, upperYear).atZone(systemDefault()).toInstant());
    }

    public static <T extends Enum<T>> T randomEnum(Class<T> enumClazz) {
        var values = enumClazz.getEnumConstants();
        return values[RND.nextInt(values.length)];
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T extends Enum> T randomEnumWildcard(Class<?> enumClazz) {
        var values = enumClazz.getEnumConstants();
        return (T) values[RND.nextInt(values.length)];
    }

    public static <T extends Enum<T>> T randomEnumExcept(Class<T> enumClazz, T enumValue) {
        var values = enumClazz.getEnumConstants();
        return Stream.of(values)
                .filter(item -> !item.equals(enumValue))
                .findAny()
                .orElseThrow(() -> new JbstIllegalEnumException(enumClazz));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T extends Enum> T randomEnumExceptWildcard(Class<?> enumClazz, T enumValue) {
        var values = enumClazz.getEnumConstants();
        return (T) Stream.of(values)
                .filter(item -> !item.equals(enumValue))
                .findAny()
                .orElseThrow(() -> new JbstIllegalEnumException(enumClazz));
    }

    public static <T extends Enum<T>> T randomEnumExcept(Class<T> enumClazz, List<T> enumValues) {
        var values = enumClazz.getEnumConstants();
        var collect = Stream.of(values)
                .filter(item -> !enumValues.contains(item))
                .toList();
        if (collect.isEmpty()) {
            throw new JbstIllegalEnumException(enumClazz);
        } else {
            return randomElement(collect);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked", "SuspiciousMethodCalls"})
    public static <T extends Enum> T randomEnumExceptWildcard(Class<?> enumClazz, List<T> enumValues) {
        var values = enumClazz.getEnumConstants();
        var collect = Stream.of(values)
                .filter(item -> !enumValues.contains(item))
                .toList();
        if (collect.isEmpty()) {
            throw new JbstIllegalEnumException(enumClazz);
        } else {
            return (T) randomElement(collect);
        }
    }

    public static <T> Map<T, Boolean> getEnumMapMappedRandomBoolean(T[] values) {
        return Stream.of(values).collect(Collectors.toMap(item -> item, item -> randomBoolean()));
    }

    public static <T> boolean containsPrimitiveWrapper(Class<T> type) {
        return PRIMITIVE_WRAPPERS.containsKey(type);
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> randomListOfPrimitiveWrappers(Class<T> type, int size) {
        if (PRIMITIVE_WRAPPERS.containsKey(type)) {
            var supplier = (Supplier<T>) PRIMITIVE_WRAPPERS.get(type);
            return IntStream.range(0, size)
                    .mapToObj(i -> supplier.get())
                    .toList();
        } else {
            return Collections.emptyList();
        }
    }

    public static Method randomMethod() {
        return Object.class.getDeclaredMethods()[0];
    }

    public static ZoneId randomZoneId() {
        return ZoneId.of(randomElement(ZoneId.getAvailableZoneIds()));
    }

    public static TimeZone randomTimeZone() {
        return TimeZone.getTimeZone(randomZoneId());
    }

    public static TimeUnit randomTimeUnit() {
        return randomEnum(TimeUnit.class);
    }

    public static ChronoUnit randomChronoUnit() {
        return randomEnum(ChronoUnit.class);
    }

    @SuppressWarnings("deprecation")
    public static FeignException randomFeignException() {
        return new FeignException.InternalServerError(
                randomString(),
                Request.create(
                        Request.HttpMethod.GET,
                        "/endpoint",
                        Map.of(),
                        new byte[] {},
                        Charset.defaultCharset()
                ),
                new byte[] {},
                new HashMap<>()
        );
    }

    public static Claims validClaims() {
        var claims = Jwts.claims();
        claims.subject(Username.hardcoded().value());
        var timeAmount = new JbstPropertyTimeAmount(1, ChronoUnit.HOURS);
        var expiration = convertLocalDateTime(LocalDateTime.now(UTC).plus(timeAmount.getAmount(), timeAmount.getUnit()), UTC);
        claims.issuedAt(new Date());
        claims.expiration(expiration);
        claims.add("authorities", getSimpleGrantedAuthorities("admin", "user"));
        return claims.build();
    }

    public static Claims expiredClaims() {
        var claims = Jwts.claims();
        claims.subject(Username.hardcoded().value());
        var currentTimestamp = getCurrentTimestamp();
        var issuedAt = new Date(currentTimestamp);
        var expiration = new Date(currentTimestamp - 1000);
        claims.issuedAt(issuedAt);
        claims.expiration(expiration);
        claims.add("authorities", getSimpleGrantedAuthorities("admin", "user"));
        return claims.build();
    }
}
