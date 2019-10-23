cmake_minimum_required(VERSION 2.6)
set(CMAKE_MODULE_PATH ${CMAKE_MODULE_PATH} "${CMAKE_SOURCE_DIR}/cmake/")
set(CMAKE_INSTALL_PREFIX "../")
set(CMAKE_CXX_COMPILER "g++")
find_package(glib REQUIRED)
#find_package(libxml2 REQUIRED)
#find_package(gd REQUIRED)
set(CMAKE_USE_RELATIVE_PATHS  true )

set (SRC_LIST 
	./event/event_mgr.cpp
	./event/eventable_obj.cpp
	./inet/inet_utils.cpp
	)

#INCLUDE_DIRECTORIES( "./" "../" ${GLIB_INCLUDE_DIR}   ${LIBXML2_INCLUDE_DIR}  ${GD_INCLUDE_DIR}	)
INCLUDE_DIRECTORIES( ${GLIB_INCLUDE_DIR} )
ADD_LIBRARY(ant ${SRC_LIST}	)
ADD_DEFINITIONS( " -g -DDEBUG -Wall  -MMD -funroll-loops -fPIC " )
INSTALL (TARGETS ant DESTINATION ../)
