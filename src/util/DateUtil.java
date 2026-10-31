package util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateUtil {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter DATE_DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("dd MMMM yyyy");
    
    /**
     * Formate une LocalDate en chaîne de caractères
     */
    public static String formatDate(LocalDate date) {
        if (date == null) return "";
        return date.format(DATE_FORMATTER);
    }
    
    /**
     * Formate une LocalDateTime en chaîne de caractères
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.format(DATETIME_FORMATTER);
    }
    
    /**
     * Formate une date pour l'affichage (ex: "15 janvier 2024")
     */
    public static String formatDateForDisplay(LocalDate date) {
        if (date == null) return "";
        return date.format(DATE_DISPLAY_FORMATTER);
    }
    
    /**
     * Parse une chaîne de caractères en LocalDate
     */
    public static LocalDate parseDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dateString.trim(), DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
    
    /**
     * Parse une chaîne de caractères en LocalDateTime
     */
    public static LocalDateTime parseDateTime(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTimeString.trim(), DATETIME_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
    
    /**
     * Vérifie si une date est valide
     */
    public static boolean isValidDate(String dateString) {
        return parseDate(dateString) != null;
    }
    
    /**
     * Retourne la date actuelle
     */
    public static LocalDate today() {
        return LocalDate.now();
    }
    
    /**
     * Retourne la date et heure actuelles
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }
    
    /**
     * Calcule le nombre de jours entre deux dates
     */
    public static long daysBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) return 0;
        return java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
    }
    
    /**
     * Vérifie si une date est dans le passé
     */
    public static boolean isPastDate(LocalDate date) {
        if (date == null) return false;
        return date.isBefore(LocalDate.now());
    }
    
    /**
     * Vérifie si une date est dans le futur
     */
    public static boolean isFutureDate(LocalDate date) {
        if (date == null) return false;
        return date.isAfter(LocalDate.now());
    }
}
