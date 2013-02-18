#-------------------------------------------------
#
# Project created by QtCreator 2013-02-18T10:13:39
#
#-------------------------------------------------

QT       += core gui

greaterThan(QT_MAJOR_VERSION, 4): QT += widgets

TARGET 			= qtapp
TEMPLATE 		= app
DESTDIR  		= bin
MOC_DIR  		= generated/moc
OBJECTS_DIR 	= generated/obj
UI_DIR			= generated/src
INCLUDEPATH 	= include

SOURCES += 	src/main.cpp\
			src/mainwindow.cpp

HEADERS  += include/mainwindow.h
