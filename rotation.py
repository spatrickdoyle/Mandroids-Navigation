import math
import numpy as np
import matplotlib.pyplot as plt
import matplotlib as mpl

'''coords = [[-100,200],[100,100],[0,-200]]

theta0 = 0.0
theta1 = 0.0
dx = 0
dy = 0

def rotate(c,th):
	mat = np.matrix([
	[math.cos(math.radians(th)),-math.sin(math.radians(th)),0],
	[math.sin(math.radians(th)),math.cos(math.radians(th)),0],
	[0,0,1]])
	return mat*c

def general(c,th0,th1,dx,dy):
	mat = np.matrix([
	[math.cos(math.radians(th0+th1)), -math.sin(math.radians(th0+th1)), dx*math.cos(math.radians(th1))-dy*math.sin(math.radians(th1))],
	[math.sin(math.radians(th0+th1)), math.cos(math.radians(th0+th1)), dx*math.sin(math.radians(th1))+dy*math.cos(math.radians(th1))],
	[0,0,1]])
	return mat*c

moved = coords
moved = [rotate(np.matrix([moved[i][0],moved[i][1],1]).transpose(),theta0).transpose().tolist()[0][:2] for i in range(len(coords))]
moved = [[moved[i][0]+dx,moved[i][1]+dy] for i in range(len(coords))]
moved = [rotate(np.matrix([moved[i][0],moved[i][1],1]).transpose(),theta1).transpose().tolist()[0][:2] for i in range(len(coords))]

moved2 = [general(np.matrix([coords[i][0],coords[i][1],1]).transpose(),theta0,theta1,dx,dy).transpose().tolist()[0][:2] for i in range(len(coords))]

X = []
for i in range(len(coords)):
	X.append([coords[i][0],-coords[i][1],1,0])
	X.append([coords[i][1],coords[i][0],0,1])

Y = []
for i in moved:
	for j in i:
		Y.append(j)

X = np.matrix(X)
Y = np.matrix(Y).transpose()
#print X
#print Y

theta = np.linalg.inv(X.transpose()*X)*(X.transpose()*Y)

#print theta

print "theta: "+str(((math.acos(theta[0])/(2*math.pi))*360 + (math.asin(theta[1])/(2*math.pi))*360)/2.0)
print "dx: "+str(theta.tolist()[2][0])
print "dy: "+str(theta.tolist()[3][0])'''

import regression

def kalman():
        data_file = open("data1.txt","r")
        lines = data_file.read().split('\n')
        data_file.close()
        print lines[0]
        lines = lines[1:-1]

        measurement = 2

        data = []
        total = [0.0]

        for line in range(len(lines)):
	        data.append(float(lines[line].split()[measurement]))
	        total.append(total[-1]+data[-1])

                variance = sum([(i - sum(data)/len(data))**2 for i in data])/len(data)

                X = [0.0,0.0,0.0,0.0]
                P = [0.0]

        #Q = ~0.0001 seems to work pretty well
        Q = 0.0001#process variance
        R = 0.00104272358489#measurement variance

        P_ = 0.0
        K = 0.0
        X_ = 0.0

        for point in range(len(data)):
	        X_ = X[-1] + (X[-1]-X[-2]) + (1.0/2.0)*((X[-1]-X[-2])-(X[-2]-X[-3]))
	        P_ = P[-1] + Q

	        K = P_/(P_+R)
	        X.append(X_ + K*(data[point] - (X[-1]-X[-2])))
	        P.append((1-K)*P_)

        print X[-1]

        X = X[4:]
        P = P[1:]
        total = total[1:]

        #reg = regression.calc_exact(len(data)-1,range(len(data)),data)
        #reg = [reg[i]*i for i in range(len(reg))][1:]
        #data_reg = [(np.asarray([i**j for j in range(len(reg))])*reg).tolist()[0][0] for i in range(len(data))]

        plt.plot(range(len(data)),data,'b-')
        plt.plot(range(len(data)),total,'g-')
        plt.plot(range(len(data)),X,'r-')

        plt.show()

def encoders():
        colors = ['r','g','b','c','m','y','k']

        raw_data = file("encoderdata.txt","r").read().split('\n\n')
        X = []
        Y = []
        for i in raw_data[0].split('\n'):
                if i[-1] == ':':
                        X.append(i[:-1])
                        X.append([])
                        Y.append(i[:-1])
                        Y.append([])
                else:
                        a = i.split()
                        X[-1].append(int(a[0]))
                        Y[-1].append(float(a[1]))

        meta_X = []
        B = []
        M = []

        #plt.subplot(311)
        for i in range(len(X)/2):
                mb = regression.calc(1,X[i*2 + 1],Y[i*2 + 1],10)
                meta_X.append(int(X[i*2]))
                B.append(mb[0])
                M.append(mb[1])
                plt.plot(range(301),[j*mb[1] + mb[0] for j in range(301)],colors[i])
                plt.plot(X[i*2 + 1],Y[i*2 + 1],colors[i]+'o')

        mmb = regression.calc(1,[j for i in X[1::2] for j in i],[l for k in Y[1::2] for l in k])
        print mmb
        plt.plot(range(301),[j*mmb[1] + mmb[0] for j in range(301)],colors[-1])

        '''plt.subplot(312)
        plt.plot(meta_X,M,'yo')
        ms = regression.calc(2,meta_X,M,10)
        print ms
        plt.plot(range(0,501),[ms[0] + ms[1]*i + ms[2]*(i**2) for i in range(0,501)],'y-')

        plt.subplot(313)
        plt.plot(meta_X,B,'bo')
        bs = regression.calc(2,meta_X,B,10)
        print bs
        plt.plot(range(0,501),[bs[0] + bs[1]*i + bs[2]*(i**2) for i in range(0,501)],'b-')'''

        plt.show()

encoders()
