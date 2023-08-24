package org.skleipzig.losverfahrencli.domain;

public enum AssignmentType {
    Erstwunsch, Zweitwunsch, Drittwunsch, Zugelost, Nichts;

    static AssignmentType byOrdinal(int i) {
        return values()[i];
    }
}
