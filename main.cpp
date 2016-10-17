#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc.hpp"
#include <iostream>
#include <math.h>
#include <eigen3/Eigen/Dense>
#include <stdio.h>
#include <unistd.h>

using namespace cv;
using namespace std;


vector<Rect> findBiggestBlob(Mat&,int,int);


int main(int argc, char *argv[]){
	VideoCapture camera(0);
	camera.set(CV_CAP_PROP_FRAME_WIDTH,800);
	camera.set(CV_CAP_PROP_FRAME_HEIGHT,448);

	if (argc == 2) {
		if ((argv[1] == "view")||(argv[1] == "calibrate")) {
			namedWindow("screen_cap",WINDOW_AUTOSIZE);
			namedWindow("path",WINDOW_AUTOSIZE);
		}
	}

	int rl = 181;
	int gl = 176;
	int bl = 81;
	int rh = 255;
	int gh = 255;
	int bh = 255;

	float dpp_h = 0.0766;//DEGREES PER PIXEL HORIZONTALLY. Total angle of view: 61.284 degrees
	float dpp_v = 0.09185;//DEGREES PER PIXEL VERTICALLY//Total angle of view: 41.148 degrees
	float height = 300.5;//CEILING HEIGHT IN INCHES (314.5 from ground)
	//Relative size of blobs to detect
	int threshold_area_min = 90;
	int threshold_area_max = 350;
	//Relative maximum movement of the blobs each tick
	int delta = 20;

	const float PI = 3.1415926535;

	//Uncomment to calibrate color threshold
	if (argc == 2) {
		if (argv[1] == "calibrate") {
			namedWindow("ctrl",WINDOW_AUTOSIZE);
			createTrackbar("rl","ctrl",&rl,255);
			createTrackbar("rh","ctrl",&rh,255);
			createTrackbar("gl","ctrl",&gl,255);
			createTrackbar("gh","ctrl",&gh,255);
			createTrackbar("bl","ctrl",&bl,255);
			createTrackbar("bh","ctrl",&bh,255);
			createTrackbar("size_min","ctrl",&threshold_area_min,255);
			createTrackbar("size_max","ctrl",&threshold_area_max,255);
		}
	}

	Mat screen_cap;
	Mat thresholded;
	Mat drawing;
	vector<Rect> points;
	vector<vector<float>> points_abs;
	vector<vector<float>> prev;

	Eigen::MatrixXf X;
	Eigen::VectorXf Y;
	Eigen::Vector4f theta;

	int i,j;
	float dist = delta+1;
	camera.read(drawing);
	drawing.setTo(Scalar(0,0,0));

	float TOTAL_THETA = 0;
	float TOTAL_X = 0;
	float TOTAL_Y = 0;
	setbuf(stdout, (char *)NULL);

	while(true) {
		camera.read(screen_cap);
		inRange(screen_cap,Scalar(bl,gl,rl),Scalar(bh,gh,rh),thresholded);

		prev = points_abs;
		points = findBiggestBlob(thresholded,threshold_area_min,threshold_area_max);
		points_abs.resize(points.size());
		if (points.size() > 0) {
			for (i = 0; i < points.size(); i++) {
				circle(screen_cap,Point((points[i].x+(points[i].width/2)),(points[i].y+(points[i].height/2))),10,Scalar(255,0,0),-1);
				if (points_abs[i].size() != 2)
					points_abs[i].resize(2);
				points_abs[i][0] = tan(((points[i].x+(points[i].width/2))-320)*dpp_h*PI/180.0)*height;
				points_abs[i][1] = tan(((points[i].y+(points[i].height/2))-240)*dpp_v*PI/180.0)*height;
			}
		}

		if (points_abs.size() > 0) {
			X = Eigen::MatrixXf(points_abs.size()*2,4);
			Y = Eigen::VectorXf(points_abs.size()*2);
		}

		for (i = 0; i < points_abs.size(); i++) {
			for (j = 0; j < prev.size(); j++) {
				dist = sqrt(pow(points_abs[i][0]-prev[j][0],2) + pow(points_abs[i][1]-prev[j][1],2));
				//uncomment to calibrate delta
				//cout << i << ' ' << j << ' ' << dist << '\n';
				if (dist < delta) {
					X(i*2,0) = prev[j][0];
					X(i*2,1) = -prev[j][1];
					X(i*2,2) = 1;
					X(i*2,3) = 0;

					X(i*2 + 1,0) = prev[j][1];
					X(i*2 + 1,1) = prev[j][0];
					X(i*2 + 1,2) = 0;
					X(i*2 + 1,3) = 1;

					Y[i*2] = points_abs[i][0];
					Y[i*2 + 1] = points_abs[i][1];
				}
			}
		}

		if (points_abs.size() > 0) {
			theta = ((X.transpose()*X).inverse())*(X.transpose())*Y;

			if ((!isnan(theta[0]))&&(!isnan(theta[1])))
				if (pow((acos(theta[0])+asin(theta[1]))/2.0,2) < 400)
					TOTAL_THETA = (acos(theta[0])+asin(theta[1]))/2.0;//+= (acos(theta[0])+asin(theta[1]))/2.0;
			if (!isnan(theta[2]))
				if (pow(theta[2],2) < 900)
					TOTAL_X = theta[2];//+= theta[2];
			if (!isnan(theta[3]))
				if (pow(theta[3],2) < 900)
					TOTAL_Y = theta[3];//+= theta[3];

			printf("%f %f %f\n",TOTAL_THETA,TOTAL_X,TOTAL_Y);
		}

		if (argc == 2) {
			if ((argv[1] == "view")||(argv[1] == "calibrate")) {
				imshow("screen_cap",screen_cap);
				imshow("path",thresholded);
			}
		}
		waitKey(10);
	}
	return 0;
}


vector<Rect> findBiggestBlob(Mat &matImage, int threshold_area_min, int threshold_area_max) {
	Mat img_clone = matImage.clone();
	vector<Rect> output;

	vector<vector<Point> > contours;
	vector<Vec4i> hierarchy;

	findContours(img_clone, contours, hierarchy, CV_RETR_CCOMP, CV_CHAIN_APPROX_SIMPLE );

	for(int i = 0; i < contours.size(); i++){
		double a = contourArea(contours[i], false);
		//Uncomment to calibrate size of blobs
		//cout << a << '\n';
		if ((a > threshold_area_min)&&(a < threshold_area_max)) {
			output.push_back(boundingRect(contours[i]));
		}
	}

	return output;
}
