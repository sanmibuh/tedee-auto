package org.sanmibuh.tedee.lock.domain.vo;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import org.sanmibuh.tedee.lock.domain.exception.InvalidCronExpressionException;

public record CronExpression(String value) {

  private static final Map<String, String> MACROS =
      Map.of(
          "@yearly", "0 0 0 1 1 *",
          "@annually", "0 0 0 1 1 *",
          "@monthly", "0 0 0 1 * *",
          "@weekly", "0 0 0 * * 0",
          "@daily", "0 0 0 * * *",
          "@midnight", "0 0 0 * * *",
          "@hourly", "0 0 * * * *");
  private static final List<String> MONTH_NAMES =
      List.of("JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC");
  private static final List<String> DAY_NAMES =
      List.of("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN");
  private static final List<String> NO_NAMES = List.of();
  private static final Pattern SPACES = Pattern.compile(" +");
  private static final Pattern LAST_DAY_OFFSET = Pattern.compile("L-(\\d+)");
  private static final Pattern NEAREST_WEEKDAY = Pattern.compile("(\\d+)W");
  private static final Pattern LAST_WEEKDAY = Pattern.compile("([A-Z]+|\\d)L");
  private static final Pattern NTH_WEEKDAY = Pattern.compile("([A-Z]+|\\d)#(\\d+)");

  public CronExpression {
    if (!isValid(value)) {
      throw new InvalidCronExpressionException(value);
    }
  }

  private static boolean isValid(final String value) {
    final var trimmed = value.trim();
    final var normalized = MACROS.getOrDefault(trimmed.toLowerCase(Locale.ROOT), trimmed);
    final var fields = SPACES.split(normalized, -1);
    if (fields.length != 6) {
      return false;
    }
    return validField(fields[0], 0, 59, NO_NAMES, false)
        && validField(fields[1], 0, 59, NO_NAMES, false)
        && validField(fields[2], 0, 23, NO_NAMES, false)
        && validField(fields[3], 1, 31, NO_NAMES, true)
        && validField(fields[4], 1, 12, MONTH_NAMES, false)
        && validField(fields[5], 0, 7, DAY_NAMES, true);
  }

  private static boolean validField(
      final String expression,
      final int minimum,
      final int maximum,
      final List<String> names,
      final boolean allowQuestionMark) {
    if (expression.equals("*") || (allowQuestionMark && expression.equals("?"))) {
      return true;
    }
    for (final var part : expression.split(",", -1)) {
      final var normalizedPart = part.toUpperCase(Locale.ROOT);
      if (part.isEmpty()) {
        return false;
      }
      if (minimum == 1
          && maximum == 31
          && (normalizedPart.contains("L") || normalizedPart.contains("W"))) {
        if (!validDayOfMonthSpecial(part)) {
          return false;
        }
      } else if (minimum == 0
          && maximum == 7
          && (normalizedPart.contains("L") || normalizedPart.contains("#"))) {
        if (!validDayOfWeekSpecial(normalizedPart, names)) {
          return false;
        }
      } else if (!validPart(normalizedPart, minimum, maximum, names)) {
        return false;
      }
    }
    return true;
  }

  private static boolean validDayOfMonthSpecial(final String expression) {
    if (expression.equals("L") || expression.equals("LW")) {
      return true;
    }
    final var lastDayOffset = LAST_DAY_OFFSET.matcher(expression);
    if (lastDayOffset.matches()) {
      return inRange(lastDayOffset.group(1), 1, Integer.MAX_VALUE);
    }
    final var nearestWeekday = NEAREST_WEEKDAY.matcher(expression);
    return nearestWeekday.matches() && inRange(nearestWeekday.group(1), 1, 31);
  }

  private static boolean validDayOfWeekSpecial(final String expression, final List<String> names) {
    final var lastWeekday = LAST_WEEKDAY.matcher(expression);
    if (lastWeekday.matches()) {
      return fieldValue(lastWeekday.group(1), 0, 7, names) >= 0;
    }
    final var nthWeekday = NTH_WEEKDAY.matcher(expression);
    return nthWeekday.matches()
        && fieldValue(nthWeekday.group(1), 0, 7, names) >= 0
        && inRange(nthWeekday.group(2), 1, Integer.MAX_VALUE);
  }

  private static boolean validPart(
      final String part, final int minimum, final int maximum, final List<String> names) {
    final var stepParts = part.split("/", -1);
    if (stepParts.length > 2
        || (stepParts.length == 2 && !inRange(stepParts[1], 1, Integer.MAX_VALUE))) {
      return false;
    }
    final var rangeParts = stepParts[0].split("-", -1);
    if (rangeParts.length > 2) {
      return false;
    }
    if (rangeParts.length == 1) {
      return rangeParts[0].equals("*") || fieldValue(rangeParts[0], minimum, maximum, names) >= 0;
    }
    var start = fieldValue(rangeParts[0], minimum, maximum, names);
    final var end = fieldValue(rangeParts[1], minimum, maximum, names);
    if (minimum == 0 && maximum == 7 && start == 7) {
      start = 0;
    }
    return start >= 0 && end >= start;
  }

  private static int fieldValue(
      final String value, final int minimum, final int maximum, final List<String> names) {
    for (var index = 0; index < names.size(); index++) {
      if (names.get(index).equals(value)) {
        return index + 1;
      }
    }
    try {
      final var parsed = Integer.parseInt(value);
      return parsed >= minimum && parsed <= maximum ? parsed : -1;
    } catch (final NumberFormatException _) {
      return -1;
    }
  }

  private static boolean inRange(final String value, final int minimum, final int maximum) {
    try {
      final var parsed = Integer.parseInt(value);
      return parsed >= minimum && parsed <= maximum;
    } catch (final NumberFormatException _) {
      return false;
    }
  }
}
