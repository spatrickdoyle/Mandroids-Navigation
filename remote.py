import pygame
import socket,sys
import numpy as np

pygame.init()

s = socket.socket(socket.AF_INET,socket.SOCK_STREAM)
s.connect((sys.argv[1],8000))

screen = pygame.display.set_mode((200,200))

theta = np.asarray([0,0,0,0])

left = np.array([2.0/3.0,-2.0/3.0,-1.0/3.0,1.0/3.0])
right = np.array([2.0/3.0,-2.0/3.0,1.0/3.0,-1.0/3.0])

#controller = pygame.joystick.Joystick(0)
#controller.init()

while True:
	screen.fill((0,0,0))

	for event in pygame.event.get():
		if event.type == pygame.KEYDOWN:
			if event.key == pygame.K_UP:
				theta[0] = 1
			elif event.key == pygame.K_DOWN:
				theta[1] = 1
			elif event.key == pygame.K_LEFT:
				theta[2] = 1
			elif event.key == pygame.K_RIGHT:
				theta[3] = 1
		elif event.type == pygame.KEYUP:
			if event.key == pygame.K_UP:
				theta[0] = 0
			elif event.key == pygame.K_DOWN:
				theta[1] = 0
			elif event.key == pygame.K_LEFT:
				theta[2] = 0
			elif event.key == pygame.K_RIGHT:
				theta[3] = 0

	#print int(255*controller.get_axis(2)),int(255*controller.get_axis(5))

	s.send(str(int(left.dot(theta)*255))+'\n')
	s.send(str(int(right.dot(theta)*255))+'\n')

	pygame.display.update()