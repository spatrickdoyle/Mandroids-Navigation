#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc.hpp"
#include <iostream>
#include <math.h>
#include <eigen3/Eigen/Dense>
#include <sys/time.h>

using namespace cv;
using namespace std;


vector<Rect> findBiggestBlob(Mat&,int,int);


int main(int argc, char *argv[]){
	VideoCapture camera(0);

	//namedWindow("screen_cap",WINDOW_AUTOSIZE);
	//namedWindow("path",WINDOW_AUTOSIZE);

	int rl = 136;
	int gl = 5;
	int bl = 0;
	int rh = 255;
	int gh = 255;
	int bh = 170;

	float dpp_h = 0.0766;//DEGREES PER PIXEL HORIZONTALLY. Total angle of view: 61.284 degrees
	float dpp_v = 0.09185;//DEGREES PER PIXEL VERTICALLY//Total angle of view: 41.148 degrees
	float height = 290.5;//CEILING HEIGHT IN INCHES (314.5 from ground)
	//Relative size of blobs to detect
	int threshold_area_min = 50;
	int threshold_area_max = 120;
	//Relative maximum movement of the blobs each tick
	int delta = 20;

	const float PI = 3.1415926535;

	//Uncomment to calibrate color threshold
	/*namedWindow("ctrl",WINDOW_AUTOSIZE);
	createTrackbar("rl","ctrl",&rl,255);
	createTrackbar("rh","ctrl",&rh,255);
	createTrackbar("gl","ctrl",&gl,255);
	createTrackbar("gh","ctrl",&gh,255);
	createTrackbar("bl","ctrl",&bl,255);
	createTrackbar("bh","ctrl",&bh,255);
	createTrackbar("size_min","ctrl",&threshold_area_min,255);
	createTrackbar("size_max","ctrl",&threshold_area_max,255);*/

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

	struct timeval start, end;

	long mtime, seconds, useconds;  

	gettimeofday(&start, NULL);
	while(true) {
		gettimeofday(&end, NULL);

		seconds  = end.tv_sec  - start.tv_sec;
		useconds = end.tv_usec - start.tv_usec;

		mtime = ((seconds) * 1000 + useconds/1000.0) + 0.5;

		cout << mtime << '\n';

		gettimeofday(&start, NULL);

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
					line(drawing,Point(points_abs[i][0]*5 + 320,points_abs[i][1]*5 + 240),Point(prev[j][0]*5 + 320,prev[j][1]*5 + 240),Scalar(255,255,255));

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
			cout << X << '\n';
			cout << Y << '\n';
			cout << theta << "\n\n";
			cout << "theta = 	" << asin(theta[0]) << ' ' << acos(theta[1]) << '\n';
			cout << "dx =	" << theta[2] << '\n';
			cout << "dy =	" << theta[3] << "\n\n\n";
		}

		//imshow("screen_cap",screen_cap);
		//imshow("path",drawing);
		//imshow("path",thresholded);
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
