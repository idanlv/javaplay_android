@echo off
echo ----------------------------------------------------------------------------------------------------
echo Authenticating your client using Java keytool,
echo Please refer to the flowing link for forther instructions
echo https://developers.google.com/games/services/console/enabling#step_3_generate_an_oauth_20_client_id
echo ----------------------------------------------------------------------------------------------------
echo Please enter java install path:
echo (usually found in "C:\Program Files\Java\jre<version>"
set /P Java_Path=""
echo ==================================
echo Generating hash for debug.keystore
echo ==================================
"%Java_Path%\bin\keytool.exe" -list -v -keystore "%USERPROFILE%\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android
echo -------------------------------
echo !!! Please use the SHA1 key !!!
echo -------------------------------
pause
