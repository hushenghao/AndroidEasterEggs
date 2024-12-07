CONFIGURATION Config1
    VAR_GLOBAL
        test : BOOL := TRUE;
    END_VAR
    TASK tick1(INTERVAL := TIME#1ms, PRIORITY := 16);
    PROGRAM prog1 WITH tick1 : Program;
    PROGRAM prog2 WITH tick1 : ladder;
END_CONFIGURATION