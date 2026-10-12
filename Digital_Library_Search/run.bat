@echo off
if not exist out mkdir out
if exist sources.txt del sources.txt
for /R src %%f in (*.java) do echo %%f>>sources.txt
javac -encoding UTF-8 -d out @sources.txt
if errorlevel 1 pause & exit /b 1
java -cp out library.Main
