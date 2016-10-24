cmake_minimum_required(VERSION 2.8)
project( Mandroids-Navigation )
SET(CMAKE_CXX_FLAGS "-std=c++0x")

find_package( OpenCV REQUIRED )
find_package( Threads )

add_executable( Mandroids-Navigation main.cpp )

target_link_libraries( Mandroids-Navigation ${OpenCV_LIBS} )
target_link_libraries( Mandroids-Navigation ${CMAKE_THREAD_LIBS_INIT} )
