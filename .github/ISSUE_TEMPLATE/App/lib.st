PROGRAM Program
    VAR
        out :INT;
        out1 :INT;
        c : Counter;
    END_VAR

    c.Increase(methodin1:=1,methodin2:=2,methodout1=>out,methodout2=>out1);
END_PROGRAM

FUNCTION_BLOCK Counter
    VAR_INPUT
        FBin1 : BOOL;
        FBin2 : BOOL;
        FBin3 : INT;
    END_VAR

    VAR_IN_OUT 
        FBinout1: INT; 
    END_VAR

    VAR_OUTPUT
        FBout1 : BOOL;
        FBout2 :INT;
    END_VAR

    VAR
        Counter : DINT := 0;
    END_VAR


    ACTION ACTIONT_4:
         Counter = Counter + 1;
    END_ACTION

    METHOD Increase
        VAR_INPUT
            methodin1 : INT;
            methodin2 : INT;
        END_VAR

        VAR_OUTPUT
            methodout1 : INT;
            methodout2 : INT;
        END_VAR

        Counter := Counter + 1;
    END_METHOD

    METHOD Increase1
        VAR_INPUT
            methodin1 : INT;
            methodin2 : INT;
        END_VAR

        VAR_OUTPUT
            methodout1 : INT;
            methodout2 : INT;
        END_VAR

        Counter := Counter + 1;
    END_METHOD

END_FUNCTION_BLOCK


CLASS CounterCLS
    VAR
        Counter : DINT := 0;
    END_VAR

    METHOD IncreaseCLS
        VAR_INPUT
            methodin1 : INT;
            methodin2 : BOOL;
        END_VAR

        VAR_OUTPUT
            methodout1 : INT;
            methodout2 : BOOL;
        END_VAR

        Counter := Counter + 1;
    END_METHOD

    METHOD IncreaseCLS1
        VAR_INPUT
            methodin1 : INT;
            methodin2 : BOOL;
        END_VAR

        VAR_OUTPUT
            methodout1 : INT;
            methodout2 : BOOL;
        END_VAR

        Counter := Counter + 1;
    END_METHOD

   
END_CLASS

FUNCTION CounterFUNC
    VAR_INPUT
        Funcin1 : BOOL;
        Funcin2 : BOOL;
    END_VAR

    VAR_IN_OUT 
        Funcinout1: INT; 
    END_VAR

    VAR_OUTPUT
        Funcout1 : BOOL;
        Funcout2 :INT;
    END_VAR

    VAR
        Counter : DINT := 0;
    END_VAR
   
END_FUNCTION