package io.reed.dripr.Models;

/**
 * Created by reed on 1/29/16.
 * Model Class for calculations
 */
public class CoffeeModel {

    public double computeYield(double dose, double output, double tds) {
        return tds*output/dose;
    }

    public double computeDose(double output, double tds, double yield, double absorption) {
        return output/(yield/tds + absorption);
    }

    public double computeOutput(double dose, double tds, double yield, double absorption) {
        return  yield*dose/tds + absorption*dose;
    }


    public double convertBrixToTDS(double b) {
        return b/1.18;
    }

    public double convertGramsToOunces(double g) {
        return 0.035274*g;
    }

    public double convertOuncesToGrams(double oz) {
        return oz*28.3495;
    }
}
