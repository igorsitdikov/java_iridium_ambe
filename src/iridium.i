%module iridiumAmbe

%include <typemaps.i>
%include <std_string.i>
%include <arrays_java.i>

%{
#include <iostream>
#include "iridium.h"
%}

%apply signed char[] { const char *  };
%apply signed char[] { char *  };

%pragma(java) jniclasscode=%{
    static {
        System.loadLibrary("iridium");
    }
%}

%include "iridium.h"