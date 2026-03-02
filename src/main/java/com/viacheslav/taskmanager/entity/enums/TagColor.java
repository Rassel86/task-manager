package com.viacheslav.taskmanager.entity.enums;

import lombok.Getter;

@Getter
public enum TagColor {
    GRAY("#6B7280"),
    RED("#EF4444"),
    ORANGE("#F97316"),
    YELLOW("#EAB308"),
    GREEN("#22C55E"),
    BLUE("#3B82F6"),
    PURPLE("#A855F7"),
    PINK("#EC4899");

    private final String hex;

    TagColor(String hex) {
        this.hex = hex;
    }
}
