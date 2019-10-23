FIND_PATH( GD_INCLUDE_DIR gd.h 
	DOC "The directory where glib.h resides")
MESSAGE(STATUS "Looking for gd - found:${GD_INCLUDE_DIR}")


FIND_LIBRARY( GD_LIBRARY
	NAMES gd 
	PATHS
	/usr/lib/
	DOC "The xml2 library")

IF (GD_INCLUDE_DIR )
	SET( GD_FOUND 1 CACHE STRING "Set to 1 if Foo is found, 0 otherwise")
ELSE (GD_INCLUDE_DIR )
	SET( GD_FOUND 0 CACHE STRING "Set to 1 if Foo is found, 0 otherwise")
ENDIF (GD_INCLUDE_DIR )

MARK_AS_ADVANCED( GD_FOUND )

IF(GD_FOUND)
	MESSAGE(STATUS "Looking for gd - found")
ELSE(GD_FOUND)
	MESSAGE(FATAL_ERROR "Looking for  gd - not found :运行  apt-get install libgd2-xpm-dev")
ENDIF(GD_FOUND)


