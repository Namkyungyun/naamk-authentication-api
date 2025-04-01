package kr.co.naamk.naamkauthenticationapi.utils;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class DateTimeUtil {
    
    private static final SimpleDateFormat SDFDTM = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    
    
    private static final String DATEFORMAT = "yyyyMMdd";
    private static final String DATETIMEFORMAT = "yyyyMMddHHmmss";
    private static final String DATETIMEMILSFORMAT = "yyyyMMddHHmmssSSS";
    private static final String DATETIMEMILSFORMATNANO = "yyyy-MM-dd HH:mm:ss.SSSSSS";
    
    
    public static String getLocalDate() {
        return getLocalNow(DATEFORMAT);
    }
    
    public static String getLocalDateTime() {
        return getLocalNow(DATETIMEFORMAT);
    }
    
    
    public static String getLocalDateTimeMils() {
        return getLocalNow(DATETIMEMILSFORMAT);
    }
    
    
    private static String getLocalNow(String format) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format);
        ZoneId zoneId = ZoneId.of("Asia/Seoul");  // Or ZoneOffset.UTC or ZoneId.systemDefault()
        LocalDateTime now = LocalDateTime.now(zoneId);
        return dtf.format(now);
    }
    
    
    public static String getAddDate(int days) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DATEFORMAT);
        ZoneId zoneId = ZoneId.of("Asia/Seoul");  // Or ZoneOffset.UTC or ZoneId.systemDefault()
        LocalDateTime now = LocalDateTime.now(zoneId).plusDays(days);
        return dtf.format(now);
    }
    
    public static String getSubDate(int days) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DATEFORMAT);
        ZoneId zoneId = ZoneId.of("Asia/Seoul");  // Or ZoneOffset.UTC or ZoneId.systemDefault()
        LocalDateTime now = LocalDateTime.now(zoneId).minusDays(days);
        return dtf.format(now);
    }
    
    
    public static String getAddDate(int days, String foramt) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(foramt);
        ZoneId zoneId = ZoneId.of("Asia/Seoul");  // Or ZoneOffset.UTC or ZoneId.systemDefault()
        LocalDateTime now = LocalDateTime.now(zoneId).plusDays(days);
        return dtf.format(now);
    }
    
    public static String getSubDate(int days, String foramt) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(foramt);
        ZoneId zoneId = ZoneId.of("Asia/Seoul");  // Or ZoneOffset.UTC or ZoneId.systemDefault()
        LocalDateTime now = LocalDateTime.now(zoneId).minusDays(days);
        return dtf.format(now);
    }
    
    
    public static long currentTimeMillis() {
        return System.currentTimeMillis();
    }
    
    public static String getDateToString() {
        return getTime("yyyyMMddHHmmss");
    }
    
    public static String getTime(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(currentTimeMillis());
    }
    
    public static String getTimestampToString() {
        return getTime("yyyyMMddHHmmssSSS");
    }
    
    public static String keyDTM() {
        return getTimestampToString();
    }
    
    public static long getNanoSeconds() {
        return TimeUnit.SECONDS.convert(System.currentTimeMillis(), TimeUnit.NANOSECONDS);
    }
    
    public static Date toDate(String date) {
        return toDate(date, DATETIMEFORMAT);
    }
    
    public static Date toDate(String date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            Date d = sdf.parse(date);
            return d;
        } catch (ParseException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        
        return null;
        
    }
    
    public static String toString(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String d = sdf.format(date);
        return d;
    }
    
    public static String toString(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(currentTimeMillis());
    }
    
    public static Timestamp getTimestamp(String date_at) throws ParseException {
        //Date d = SDFDTM.parse(date_at);
        Timestamp timestamp = Timestamp.valueOf(date_at);
        log.info("{} \t {}", date_at, timestamp);
        return timestamp;
    }
    
    /*
     * 특별하게 사용합시다
     */
    public static Timestamp dateToTimestamp(String date_at) throws ParseException {
        Date d = toDate(date_at, DATEFORMAT);
        String rtnDtm = toString(d, DATETIMEMILSFORMATNANO);
        Timestamp timestamp = Timestamp.valueOf(rtnDtm);
        log.info("{} \t {}", date_at, timestamp);
        return timestamp;
    }
    
    
    public static @NotNull short getWeekNumber() {
        // Get the current date
        LocalDateTime today = LocalDateTime.now();
        // Get the week number for the current date
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int weekNumber = today.get(weekFields.weekOfWeekBasedYear());
        
        return (short) weekNumber;
    }
}
