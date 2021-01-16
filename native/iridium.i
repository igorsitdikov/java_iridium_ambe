%module iridiumAmbe

%include <typemaps.i>
%include <std_string.i>
%include <arrays_java.i>

%{
#include <iostream>
#include "native/iridium.h"
%}

%apply signed char[] { const char *  };
%apply signed char[] { char *  };

%pragma(java) jniclasscode=%{
    static {
        boolean embeddedLibrary = EmbeddedLibraryTools.LOADED_EMBEDDED_LIBRARY;
    }
%}

%include "native/iridium.h"