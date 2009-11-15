@echo off
rem Batch file to run Platypus, version 1.1. (c) Pacific Data Works LLC

rem Replace install_directory below with the complete name of the Platypus installation directory. Do not add a slash at end.
rem If the directory contains spaces, it should be enclosed in "quotes."

set PLATYPUS_HOME=install_directory 
java -Xmx256M -jar "%PLATYPUS_HOME%\platypus.jar" %1 %2 %3 %4 %5 %6 %7 %8