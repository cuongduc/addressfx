/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hola.address.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 *
 * @author duccuong
 */
public class DateUtil {
    
    private static final String DATE_PATTERN = "dd.MM.yyyy";
    
    private static final DateTimeFormatter DATE_FORMATTER = 
            DateTimeFormatter.ofPattern(DATE_PATTERN);
    
    /**
     * Returns the given date as a well-formatted string. 
     * @param date
     * @return 
     */
    public static String format(LocalDate date) {
        if (date == null) {
            return null;
        }
        return DATE_FORMATTER.format(date);
    }
    
    /**
     * Converts a String in the format of the defined {@link DateUtil#DATE_PATTERN}
     * to a {@link LocalDate} object.
     * 
     * Returns null if the String could not be converted.
     * 
     * @param dateString
     * @return 
     */
    public static LocalDate parse(String dateString) {
        try {
            LocalDate date = DATE_FORMATTER.parse(dateString, LocalDate::from);
            return date;
        } catch (DateTimeParseException e) {
            return null;
        }
    }
    
    /**
     * Checks the String if it is a valid date.
     * 
     * @param dateString
     * @return 
     */
    public static boolean validDate(String dateString) {
        // Try to parse the String.
        return DateUtil.parse(dateString) != null;
    }
}
