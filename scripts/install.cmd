@ECHO off
ECHO "Removing old version of subitarius"
rd /s %HOMEPATH%\subitarius

ECHO "Moving current versoin to home directory"
xcopy ..\subitarius*  %HOMEPATH%\

echo "Renaming the current subitarius folder to just subitarius"
move %HOMEPATH%\subitarius* %HOMEPATH%\subitarius

@ECHO on