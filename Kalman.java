import java.util.*;

public class Kalman {
	private List<Double> X;
	private List<Double> P;

	double P_ = 0.0;
	double K = 0.0;
	double X_ = 0.0;

	Kalman() {
		Double tmp[] = {0.0,0.0,0.0,0.0};
	    X = new ArrayList<Double>(Arrays.asList(tmp));
		Double tmp2[] = {0.0};
		P = new ArrayList<Double>(Arrays.asList(tmp2));
	}

	public double tick(double x, double u, double Q, double R) {
		X_ = X.get(X.size()-1) + u;// + (X.get(X.size()-1) - X.get(X.size()-2)) + ((X.get(X.size()-1)-X.get(X.size()-2)) - (X.get(X.size()-2)-X.get(X.size()-3)))/2.0;
		P_ = P.get(P.size()-1) + Q;

		K = P_/(P_+R);
		X.add(X_ + K*(x - (X.get(X.size()-1)-X.get(X.size()-2))));
		P.add((1-K)*P_);

		return X.get(X.size()-1);
	}
}
