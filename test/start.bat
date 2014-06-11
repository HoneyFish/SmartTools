@echo off

set ddmlib=%CD%/libs/ddmlib-prebuilt.jar
set SmartTools=%CD%/libs/SmartTools.jar
set rsyntaxtextarea=%CD%/libs/rsyntaxtextarea.jar
set libs=%ddmlib%;%SmartTools%;%rsyntaxtextarea%

java -cp  %libs% com.nj.simba.app.SmartToolsApp