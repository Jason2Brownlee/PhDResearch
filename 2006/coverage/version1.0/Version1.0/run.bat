@echo off

SET LIB=coverage-bin1.0.jar
SET CP=%LIB%;lib/commons-lang-2.0.jar;lib/commons-math-1.0.jar
SET MAIN=swsom.gui.MainFrame

java -Xmx64M -classpath .;%CP% %MAIN%

pause
