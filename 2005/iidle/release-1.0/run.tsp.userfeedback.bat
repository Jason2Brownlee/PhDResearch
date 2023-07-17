@echo off

SET IIDLE=iidle-bin1.0.jar
SET CP=%IIDLE%;lib/bouncycastle.jar;lib/colt.jar;lib/commons-collections-3.1.jar;lib/commons-lang-2.0.jar;lib/commons-logging.jar;lib/commons-logging-api.jar;lib/commons-math-1.0.jar;lib/commons-primitives-1.0.jar;lib/FreePastry-1.4.1.jar;lib/gnujaxp.jar;lib/jcommon-0.9.6.jar;lib/jfreechart-0.9.21.jar;lib/jung-1.6.0.jar;lib/junit.jar;lib/osp.jar;lib/servlet.jar;lib/xmlpull_1_1_3_4a.jar;lib/xpp3-1.1.3.4d_b2.jar
SET MAIN=jb.selfregulation.application.runner.Runner

SET CFG=config.tsp.userfeedback.properties

java -Xmx256M -classpath .;%CP% %MAIN% %CFG%

pause
