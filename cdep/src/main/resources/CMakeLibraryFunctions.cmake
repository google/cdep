
###
### Begin CMakeLibraryFunctions.cmake
###

# Set the flags necessary to reach the given C/C++ compiler level
function (cdepRequireMinimumCxxCompilerStandard target level)
    target_compile_options(${target} PRIVATE $<$<COMPILE_LANGUAGE:CXX>:-std=c++${level}>)
endfunction()

###
### End CMakeLibraryFunctions.cmake
###
