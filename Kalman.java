import java.util.*;

public class Kalman {
	private List<Double> X;
	private List<Double> Px;

	double Px_ = 0.0;
	double Kx = 0.0;
	double X_ = 0.0;

	Kalman() {
		Double tmp[] = {0.0,0.0,0.0};
	    X = new ArrayList<Double>(Arrays.asList(tmp));
		Double tmp2[] = {0.0};
		Px = new ArrayList<Double>(Arrays.asList(tmp2));
	}

	public double tick(double x, double u, double Q, double R) {
		X_ = X.get(X.size()-1) + u;// + (X.get(X.size()-1) - X.get(X.size()-2)) + ((X.get(X.size()-1)-X.get(X.size()-2)) - (X.get(X.size()-2)-X.get(X.size()-3)))/2.0;
		Px_ = Px.get(Px.size()-1) + Q;

		Kx = Px_/(Px_+R);
		X.add(X_ + Kx*(x - (X.get(X.size()-1)-X.get(X.size()-2))));
		Px.add((1-Kx)*Px_);

		return X.get(X.size()-1);
	}
}
