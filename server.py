import socket

serversocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

serversocket.bind(('127.0.0.1', 2007))

serversocket.listen(1024)

while True:
	(clientSocket, address) = serversocket.accept()

	while True:
		clientSocket.send('sync')
		req = clientSocket.recv(100)
		if not req: break

		print req
	clientSocket.close()