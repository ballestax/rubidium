

!define PRODUCT_VERSION "1.1"
!define PRODUCT_PUBLISHER "DERO"
!define OUT_FILE "DCElectoral_"
!define OUT_FILE_VERSION "_BETA.exe"

SetCompressor lzma

!include "MUI2.nsh"

;Variables
Var StartMenuFolder

!define MUI_ABORTWARNINGS 
;;!define MUI_ICON 
;;!define MUI_WELCOMEPAGE_TEXT 
!insertmacro MUI_PAGE_LICENSE "${NSISDIR}\Docs\Modern UI\License.txt"
!insertmacro MUI_PAGE_COMPONENTS
!insertmacro MUI_PAGE_DIRECTORY
  
;Start Menu Folder Page Configuration
!define MUI_STARTMENUPAGE_REGISTRY_ROOT "HKCU" 
!define MUI_STARTMENUPAGE_REGISTRY_KEY "Software\DeroCElectoral" 
!define MUI_STARTMENUPAGE_REGISTRY_VALUENAME "Start Menu Folder"
  
!insertmacro MUI_PAGE_STARTMENU Application $StartMenuFolder
  
!insertmacro MUI_PAGE_INSTFILES
  
!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES

!insertmacro MUI_LANGUAGE "Spanish"
!define PRODUCT_NAME "DERO Campaña Electoral"

Name "${PRODUCT_NAME} ${PRODUCT_VERSION}" 
OutFile "${OUT_FILE}${PRODUCT_VERSION}${OUT_FILE_VERSION}" 
InstallDir "$PROGRAMFILES\DeroCELectoral" 
ShowInstDetails show
;Request application privileges for Windows Vista
RequestExecutionLevel admin


Section "${PRODUCT_NAME}" SEC_01

	SetOutPath "$INSTDIR"
  
	;ADD YOUR OWN FILES HERE...
    File "Dero_CElectoral.jar"
    File "Dero_CElectoral.exe"
	CreateDirectory $INSTDIR\config
	SetOutPath $INSTDIR\config
	File "config\dao.properties"
	File "config\jdbc-dao.properties"
	File "config\jdbc-sql.xml"
	File "config\logging.properties"
	File "config\municipios.db"
	File "config\divipol.db"		
	   
    CreateDirectory $INSTDIR\lib
	SetOutPath $INSTDIR\lib
	File "lib\antlr-2.7.6.jar"
	File "lib\bxLibrary_2.0.jar"
	File "lib\BxLibrary.jar"
	File "lib\commons-beanutils.jar"
	File "lib\commons-codec-1.6.jar"
	File "lib\commons-collections-3.2.1.jar"
	File "lib\commons-dbcp-1.4.jar"
	File "lib\commons-digester3-3.0.jar"
	File "lib\commons-io-2.4.jar"
	File "lib\commons-lang3-3.3.2.jar"
	File "lib\commons-logging-1.1.3.jar"
	File "lib\commons-pool-1.5.6.jar"
	File "lib\cssparser-0.9.14.jar"
	File "lib\dom4j-1.6.1.jar"
	File "lib\fluent-hc-4.3.3.jar"
	File "lib\gson-2.2.4.jar"
	File "lib\hibernate-core-4.3.4.Final.jar"
	File "lib\hsqldb.jar"
	File "lib\htmlunit-2.14.jar"
	File "lib\htmlunit-2.15.jar"
	File "lib\htmlunit-core-js-2.14.jar"
	File "lib\htmlunit-core-js-2.15.jar"
	File "lib\httpclient-4.3.2.jar"
	File "lib\httpclient-4.3.3.jar"
	File "lib\httpclient-cache-4.3.3.jar"
	File "lib\httpcore-4.3.2.jar"
	File "lib\httpmime-4.3.2.jar"
	File "lib\httpmime-4.3.3.jar"
	File "lib\itext-4.2.1.jar"
	File "lib\javax.persistence.jar"
	File "lib\jetty-http-8.1.15.v20140411.jar"
	File "lib\jetty-io-8.1.15.v20140411.jar"
	File "lib\jetty-util-8.1.15.v20140411.jar"
	File "lib\jetty-websocket-8.1.15.v20140411.jar"
	File "lib\jl1.0.1.jar"
	File "lib\JMapViewer.jar"
	File "lib\jsoup-1.7.2.jar"
	File "lib\jsoup-1.7.3.jar"
	File "lib\ksoap2-j2me-core-2.1.2.jar"
	File "lib\ksoap2-j2se-full-2.1.2.jar"
	File "lib\log4j-1.2.13.jar"
	File "lib\MapsJava.jar"
	File "lib\mariadb-java-client-1.1.8.jar"
	File "lib\nekohtml-1.9.21.jar"
	File "lib\poi-3.9-20121203.jar"
	File "lib\poi-examples-3.9-20121203.jar"
	File "lib\poi-excelant-3.9-20121203.jar"
	File "lib\poi-ooxml-3.9-20121203.jar"
	File "lib\poi-ooxml-schemas-3.9-20121203.jar"
	File "lib\poi-scratchpad-3.9-20121203.jar"
	File "lib\sac-1.3.jar"
	File "lib\serializer-2.7.1.jar"
	File "lib\sqlite-jdbc-3.7.2.jar"
	File "lib\stax-api-1.0.1.jar"
	File "lib\swingx-all-1.6.3.jar"
	File "lib\xalan.jar"
	File "lib\xalan-2.7.1.jar"
	File "lib\xercesImpl-2.11.0.jar"
	File "lib\xml-apis-1.4.01.jar"
	File "lib\xmlbeans-2.3.0.jar"
	File "lib\Zql.jar"
 
	;Store installation folder
	WriteRegStr HKLM SOFTWARE\DeroCElectoral "Install_Dir" "$INSTDIR"
       
    ; Escribimos las claves de desinstalación de Windows
     
    WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\DeroCElectoral" "DisplayName" "DeroCElectoral"
	WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\DeroCElectoral" "UninstallString" '"$INSTDIR\uninstall.exe"'
    WriteRegStr HKCU "Software\JavaSoft\Prefs" "\celectoral\soft\ahbabcbgcd" "1821232734"
	WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\DeroCElectoral" "NoModify" 1
    WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\DeroCElectoral" "NoRepair" 1
       
  
	SetOutPath "$INSTDIR"
	;Create uninstaller
	WriteUninstaller Uninstall.exe"
	
	!insertmacro MUI_STARTMENU_WRITE_BEGIN Application
    
    ;Create shortcuts
    CreateDirectory "$SMPROGRAMS\$StartMenuFolder"	
    CreateShortCut "$SMPROGRAMS\$StartMenuFolder\Uninstall.lnk" "$INSTDIR\uninstall.exe" "" "$INSTDIR\uninstall.exe" 0
    CreateShortCut "$SMPROGRAMS\$StartMenuFolder\Dero Campaña Electoral.lnk" "$INSTDIR\Dero_CElectoral.jar" "" "$INSTDIR\Dero_CElectoral.jar" 0
  
	!insertmacro MUI_STARTMENU_WRITE_END

SectionEnd


Section "MySQL" SEC_02
	SetOutPath "$INSTDIR"
	File mariadb-10.0.16-win32.msi
	ExecWait "msiexec /i mariadb-10.0.16-win32.msi SERVICENAME=MySQL PORT=3306 PASSWORD=DRce14 /qn /l log_mdb.log"
	SetOutPath "$PROGRAMFILES\MariaDB 10.0"
	;File my.ini
	;File mysql-init.txt
	;ExecWait '"$PROGRAMFILES\MySQL\MySQL Server 5.1\bin\mysqld"  --defaults-file = "$PROGRAMFILES\MySQL\MySQL Server 5.1\my.ini" '  
	;ExecWait '"$PROGRAMFILES\MySQL\MySQL Server 5.1\bin\mysqld"  --init-file = "$PROGRAMFILES\MySQL\MySQL Server 5.1\mysql-init.txt" '
	ExecWait "Net Start MySQL"
	
SectionEnd



Section "Uninstall"
	; Remover las claves del Registro
    DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\DeroCElectoral"
    DeleteRegKey HKLM SOFTWARE\DeroCElectoral
	DeleteRegKey HKCU "Software\JavaSoft\Prefs\ahbabcbgcd"
	
	; Eliminados los archivos y los desinstalamos
	Delete $INSTDIR\lib\antlr-2.7.6.jar
	Delete $INSTDIR\lib\bxLibrary_2.0.jar
	Delete $INSTDIR\lib\BxLibrary.jar
	Delete $INSTDIR\lib\commons-beanutils.jar
	Delete $INSTDIR\lib\commons-codec-1.6.jar
	Delete $INSTDIR\lib\commons-collections-3.2.1.jar
	Delete $INSTDIR\lib\commons-dbcp-1.4.jar
	Delete $INSTDIR\lib\commons-digester3-3.0.jar
	Delete $INSTDIR\lib\commons-io-2.4.jar
	Delete $INSTDIR\lib\commons-lang3-3.3.2.jar
	Delete $INSTDIR\lib\commons-logging-1.1.3.jar
	Delete $INSTDIR\lib\commons-pool-1.5.6.jar
	Delete $INSTDIR\lib\cssparser-0.9.14.jar
	Delete $INSTDIR\lib\dom4j-1.6.1.jar
	Delete $INSTDIR\lib\fluent-hc-4.3.3.jar
	Delete $INSTDIR\lib\gson-2.2.4.jar
	Delete $INSTDIR\lib\hibernate-core-4.3.4.Final.jar
	Delete $INSTDIR\lib\hsqldb.jar
	Delete $INSTDIR\lib\htmlunit-2.14.jar
	Delete $INSTDIR\lib\htmlunit-2.15.jar
	Delete $INSTDIR\lib\htmlunit-core-js-2.14.jar
	Delete $INSTDIR\lib\htmlunit-core-js-2.15.jar
	Delete $INSTDIR\lib\httpclient-4.3.2.jar
	Delete $INSTDIR\lib\httpclient-4.3.3.jar
	Delete $INSTDIR\lib\httpclient-cache-4.3.3.jar
	Delete $INSTDIR\lib\httpcore-4.3.2.jar
	Delete $INSTDIR\lib\httpmime-4.3.2.jar
	Delete $INSTDIR\lib\httpmime-4.3.3.jar
	Delete $INSTDIR\lib\itext-4.2.1.jar
	Delete $INSTDIR\lib\javax.persistence.jar
	Delete $INSTDIR\lib\jetty-http-8.1.15.v20140411.jar
	Delete $INSTDIR\lib\jetty-io-8.1.15.v20140411.jar
	Delete $INSTDIR\lib\jetty-util-8.1.15.v20140411.jar
	Delete $INSTDIR\lib\jetty-websocket-8.1.15.v20140411.jar
	Delete $INSTDIR\lib\jl1.0.1.jar
	Delete $INSTDIR\lib\JMapViewer.jar
	Delete $INSTDIR\lib\jsoup-1.7.2.jar
	Delete $INSTDIR\lib\jsoup-1.7.3.jar
	Delete $INSTDIR\lib\ksoap2-j2me-core-2.1.2.jar
	Delete $INSTDIR\lib\ksoap2-j2se-full-2.1.2.jar
	Delete $INSTDIR\lib\log4j-1.2.13.jar
	Delete $INSTDIR\lib\MapsJava.jar
	Delete $INSTDIR\lib\mariadb-java-client-1.1.8.jar
	Delete $INSTDIR\lib\nekohtml-1.9.21.jar
	Delete $INSTDIR\lib\poi-3.9-20121203.jar
	Delete $INSTDIR\lib\poi-examples-3.9-20121203.jar
	Delete $INSTDIR\lib\poi-excelant-3.9-20121203.jar
	Delete $INSTDIR\lib\poi-ooxml-3.9-20121203.jar
	Delete $INSTDIR\lib\poi-ooxml-schemas-3.9-20121203.jar
	Delete $INSTDIR\lib\poi-scratchpad-3.9-20121203.jar
	Delete $INSTDIR\lib\sac-1.3.jar
	Delete $INSTDIR\lib\serializer-2.7.1.jar
	Delete $INSTDIR\lib\sqlite-jdbc-3.7.2.jar
	Delete $INSTDIR\lib\stax-api-1.0.1.jar
	Delete $INSTDIR\lib\swingx-all-1.6.3.jar
	Delete $INSTDIR\lib\xalan.jar
	Delete $INSTDIR\lib\xalan-2.7.1.jar
	Delete $INSTDIR\lib\xercesImpl-2.11.0.jar
	Delete $INSTDIR\lib\xml-apis-1.4.01.jar
	Delete $INSTDIR\lib\xmlbeans-2.3.0.jar
	Delete $INSTDIR\lib\Zql.jar
	Delete $INSTDIR\config\dao.properties
	Delete $INSTDIR\config\jdbc-dao.properties
	Delete $INSTDIR\config\jdbc-sql.xml
	Delete $INSTDIR\config\logging.properties   
	Delete $INSTDIR\config\municipios.db
	Delete $INSTDIR\config\divipol.db
	Delete $INSTDIR\Dero_CElectoral.exe
	Delete $INSTDIR\Dero_CElectoral.jar
	Delete $INSTDIR\mariadb-10.0.16-win32.msi
	Delete $INSTDIR\uninstall.exe
	RMDir $INSTDIR\config	
	RMDir $INSTDIR\lib  
 
	; Eliminamos las carpetas creadas
	RMDir "$SMPROGRAMS\DeroCElectoral"
	RMDir "$INSTDIR"
	
	; Eliminamos todos los accesos directos del menú de inicio
	!insertmacro MUI_STARTMENU_GETFOLDER Application $StartMenuFolder
    
	Delete "$SMPROGRAMS\$StartMenuFolder\*.*"  
	RMDir "$SMPROGRAMS\$StartMenuFolder"
 
SectionEnd



