package com.stella.ehealthtest.vitalSigns;

public class vitalSigns {
    private int user_id;
    private int EWS,upperBP,lowerBP,spO2, pulse,blood_sugar_level,HDL,LDL,heart_rate;
    private String level_of_consciousness,mealStatus;
    private double temperature;
    private String last_menstruation_date;
    private String collected_datetime;
    private int status;

    public vitalSigns(int user_id, int EWS,double temperature, int upperBP, int lowerBP, int SPO2, int pulse, int blood_sugar_level,String mealStatus, int HDL,
                      int LDL, int heart_rate, String level_of_consciousness,  String last_menstruation_date, String createdAt, int status) {
        this.user_id=user_id;
       // this.vital_id=vital_id;
        this.EWS = EWS;
        this.temperature=temperature;
        this.upperBP = upperBP;
        this.lowerBP = lowerBP;
        this.spO2 = SPO2;
        this.pulse = pulse;
        this.blood_sugar_level = blood_sugar_level;
        this.mealStatus=mealStatus;
        this.HDL=HDL;
        this.LDL=LDL;
        this.heart_rate=heart_rate;
        this.level_of_consciousness=level_of_consciousness;
        this.last_menstruation_date=last_menstruation_date;
        this.collected_datetime=createdAt;
        this.status = status;
    }

    public int getUser_id() {
        return user_id;
    }
  //  public int getvital_id() {
  //    return vital_id;
   //  }
    public int getEWS() {
        return EWS;
    }
    public int getupperBP() {
        return upperBP;
    }
    public int getLowerBPBP() {
        return lowerBP;
    }
    public int getSpO2() {
        return spO2;
    }
    public int getPulse() {
        return pulse;
    }
    public int getBlood_sugar_level() {
        return blood_sugar_level;
    }
    public String getMealStatus() {
        return mealStatus;
    }
    public int getHDL() {
        return HDL;
    }
    public int getLDL() {
        return LDL;
    }
    public int getHeart_rate() {
        return heart_rate;
    }
    public String getLevel_of_consciousness() {
        return level_of_consciousness;
    }
    public double getTemperature() {
        return temperature;
    }
    public String getLast_menstruation_date() {
        return last_menstruation_date;
    }
    public String getCreatedDateTime() {
        return collected_datetime;
    }
    public int getStatus() {
        return status;
    }
}
