package com.SwitchBoard.AiService.Service;



import com.SwitchBoard.AiService.Domain.Assignment;
import com.SwitchBoard.AiService.Domain.Task;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

public class DeadlinePlanner {

    private static final ZoneId ZONE = ZoneId.of("Asia/Kolkata");
    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    public static void chainDeadlines(Assignment assignment, LocalDate startDate) {
        LocalDate anchor = (startDate == null) ? LocalDate.now(ZONE) : startDate;
        ZonedDateTime cursorEnd = anchor.atTime(23, 59, 59).atZone(ZONE);

        // Order tasks by "order"
        List<Task> ordered = assignment.getTasks().stream()
                .sorted(Comparator.comparingInt(Task::getOrder))
                .toList();

        for (Task t : ordered) {
            cursorEnd = cursorEnd.plusDays(t.getDeadline());
            t.setDeadlineDateIso(cursorEnd.format(ISO));
        }

        // Assignment deadline = total duration across tasks or provided deadline (whichever is larger)
        int totalTaskDays = ordered.stream().mapToInt(Task::getDeadline).sum();
        int finalDays = Math.max(totalTaskDays, assignment.getDeadline());
        ZonedDateTime assignmentDeadline = anchor.atTime(23, 59, 59).atZone(ZONE).plusDays(finalDays);
        assignment.setDeadlineDateIso(assignmentDeadline.format(ISO));
    }
}
