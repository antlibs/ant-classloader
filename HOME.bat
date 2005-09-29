set jdk=%1%
set antversion=%2%
if exist %~dp0..\HOME.bat goto HOME1
if exist %~dp0..\..\HOME.bat goto HOME2
if exist %~dp0..\..\..\HOME.bat goto HOME3
if exist %~dp0..\..\..\..\HOME.bat goto HOME4
if exist %~dp0..\..\..\..\..\HOME.bat goto HOME5
if exist %~dp0..\..\..\..\..\..\HOME.bat goto HOME6
if exist %~dp0..\..\..\..\..\..\..\HOME.bat goto HOME7
if exist %~dp0..\..\..\..\..\..\..\..\HOME.bat goto HOME8
if exist %~dp0..\..\..\..\..\..\..\..\..\HOME.bat goto HOME9

:HOME1
call %~dp0..\HOME.bat %jdk% %antversion%
goto EOF

:HOME2
call %~dp0..\..\HOME.bat %jdk% %antversion%
goto EOF

:HOME3
call %~dp0..\..\..\HOME.bat %jdk% %antversion%
goto EOF

:HOME4
call %~dp0..\..\..\..\HOME.bat %jdk% %antversion%
goto EOF

:HOME5
call %~dp0..\..\..\..\..\HOME.bat %jdk% %antversion%
goto EOF

:HOME6
call %~dp0..\..\..\..\..\..\HOME.bat %jdk% %antversion%
goto EOF

:HOME7
call %~dp0..\..\..\..\..\..\..\HOME.bat %jdk% %antversion%
goto EOF

:HOME8
call %~dp0..\..\..\..\..\..\..\..\HOME.bat %jdk% %antversion%
goto EOF

:HOME9
call %~dp0..\..\..\..\..\..\..\..\..\HOME.bat %jdk% %antversion%
goto EOF

:EOF
