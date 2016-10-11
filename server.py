import socket

serversocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

serversocket.bind(('127.0.0.1', 2007))

serversocket.listen(1024)

while True:
	(clientSocket, address) = serversocket.accept()

	while True:
		clientSocket.send('sync')
		left = clientSocket.recv(100)
		if not left: break

		clientSocket.send('sync2')
		right = clientSocket.recv(100)
		if not right: break

		print left
		print right
	clientSocket.close()