package com.SwitchBoard.AiService.Util;


import com.SwitchBoard.AiService.Domain.PriorityColor;

public final class PriorityColorUtil {
    private PriorityColorUtil() {}

    // Accept AI text like "red"/"RED" and normalize; default to YELLOW
    public static PriorityColor normalize(String color) {
        if (color == null) return PriorityColor.YELLOW;
        return switch (color.trim().toUpperCase()) {
            case "RED" -> PriorityColor.RED;
            case "ORANGE" -> PriorityColor.ORANGE;
            case "YELLOW" -> PriorityColor.YELLOW;
            case "BLUE" -> PriorityColor.BLUE;
            case "GREEN" -> PriorityColor.GREEN;
            default -> PriorityColor.YELLOW;
        };
    }
}
