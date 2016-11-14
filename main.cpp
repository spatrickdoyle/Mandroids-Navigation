#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc.hpp"
#include <iostream>
#include <math.h>
#include <eigen3/Eigen/Dense>
#include <stdio.h>
#include <unistd.h>
#include <cstring>
#include <thread>

using namespace cv;
using namespace std;


vector<Rect> findBiggestBlob(Mat&,int,int);
void loop_frames(Mat&);

int main(int argc, char *argv[]){
	if (argc == 2) {
		if ((strcmp(argv[1],"view") == 0)||(strcmp(argv[1],"calibrate") == 0)) {
			namedWindow("screen_cap",WINDOW_AUTOSIZE);
			namedWindow("path",WINDOW_AUTOSIZE);
		}
	}

	int rl = 120;
	int gl = 120;
	int bl = 120;
	int rh = 121;
	int gh = 121;
	int bh = 121;

	float dpp_h = 0.069;//DEGREES PER PIXEL HORIZONTALLY. Total angle of view: 44 degrees
	float dpp_v = 0.071;//DEGREES PER PIXEL VERTICALLY. Total angle of view: 34 degrees
	float height = 300.0;//CEILING HEIGHT IN INCHES (314.5 from ground)
	float camera_angle = 22;//ANGLE THE CAMERA LINE OF SIGHT MAKES WITH THE VERTICAL in degrees
	//Relative size of blobs to detect
	int threshold_area_min = 130;
	int threshold_area_max = 255;
	//Relative maximum movement of the blobs each tick
	int delta = 20;

	const float PI = 3.1415926535;

	//Uncomment to calibrate color threshold
	if (argc == 2) {
		if (strcmp(argv[1],"calibrate") == 0) {
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
	//camera.read(drawing);
	//drawing.setTo(Scalar(0,0,0));

	float TOTAL_THETA = 0;
	float TOTAL_X = 0;
	float TOTAL_Y = 0;
	setbuf(stdout, (char *)NULL);

	thread capture_thread(loop_frames,ref(screen_cap));
	//cin.ignore();
	system("sleep 10");

	while(true) {
		inRange(screen_cap,Scalar(bl,gl,rl),Scalar(bh,gh,rh),thresholded);

		 if (argc == 2)
            if ((strcmp(argv[1],"view") == 0)||(strcmp(argv[1],"calibrate") == 0))
				screen_cap.copyTo(drawing);

		prev = points_abs;
		points = findBiggestBlob(thresholded,threshold_area_min,threshold_area_max);
		points_abs.resize(points.size());
		if (points.size() > 0) {
			for (i = 0; i < points.size(); i++) {
				circle(drawing,Point((points[i].x+(points[i].width/2)),(points[i].y+(points[i].height/2))),10,Scalar(255,0,0),-1);
				if (points_abs[i].size() != 2)
					points_abs[i].resize(2);
				points_abs[i][0] = tan(((points[i].x+(points[i].width/2))-320)*dpp_h*PI/180.0)*height;
				points_abs[i][1] = tan((camera_angle-(((points[i].y+(points[i].height/2))-240)*dpp_v))*PI/180.0)*height;
			}
		}

		/*for (int i = 0; i < points_abs.size(); i++)
			cout << points_abs[i][0] << ' ' << points_abs[i][1] << '\n';
			cout << '\n';*/

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

		if (points_abs.size() > 1) {
			theta = ((X.transpose()*X).inverse())*(X.transpose())*Y;

			if ((!isnan(theta[0]))&&(!isnan(theta[1]))) {
				//cout << acos(theta[0]) << ' ' << asin(theta[1]) << '\n';
				if (pow((acos(theta[0])+asin(theta[1]))/2.0,2) < 400)
					TOTAL_THETA = (acos(theta[0])+asin(theta[1]))/2.0;//+= (acos(theta[0])+asin(theta[1]))/2.0;
			}
			if (!isnan(theta[2]))
				if (pow(theta[2],2) < 900)
					TOTAL_X = theta[2];//+= theta[2];
			if (!isnan(theta[3]))
				if (pow(theta[3],2) < 900)
					TOTAL_Y = theta[3];//+= theta[3];

			printf("%f %f %f\n",TOTAL_THETA,TOTAL_X,-TOTAL_Y);
		}

		if (argc == 2) {
			if ((strcmp(argv[1],"view") == 0)||(strcmp(argv[1],"calibrate") == 0)) {
				imshow("screen_cap",drawing);
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
	Rect ret;

	vector<vector<Point> > contours;
	vector<Vec4i> hierarchy;

	findContours(img_clone, contours, hierarchy, CV_RETR_CCOMP, CV_CHAIN_APPROX_SIMPLE );

	for(int i = 0; i < contours.size(); i++){
		double a = contourArea(contours[i], false);
		ret = boundingRect(contours[i]);
		//Uncomment to calibrate size of blobs
		//cout << a << '\n';
		if ((a > threshold_area_min)&&(a < threshold_area_max)) {
			//cout << ((double)ret.width)/ret.height << '\n';
			if (abs(1-(((double)ret.width)/ret.height)) < 0.4) {
				//cout << abs(a - pow(ret.width/2.0,2)*3.14159) << '\n';
				if ((abs(a - pow(ret.width/2.0,2)*3.14159) < 100)&&(abs(a - pow(ret.width/2.0,2)*3.14159) > 10)) {
					output.push_back(ret);
				}
			}
		}
	}

	return output;
}

void loop_frames(Mat& img) {
	VideoCapture camera(0);
	camera.set(CV_CAP_PROP_FRAME_WIDTH,640);//800);
	camera.set(CV_CAP_PROP_FRAME_HEIGHT,480);//448);
	camera.read(img);
	while (true) {
		camera.read(img);
	}
}
