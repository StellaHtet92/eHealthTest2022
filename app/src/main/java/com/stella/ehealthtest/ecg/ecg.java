package com.stella.ehealthtest.ecg;

public class ecg {
    private int user_id;
   // private int vital_id;
    private String ecgData;
    private String createdAt;
    private int status;

    public ecg(int user_id, String ecgData, String createdAt, int status) {
        this.user_id=user_id;
        //this.vital_id=vital_id;
        this.ecgData = ecgData;
        this.createdAt=createdAt;
        this.status = status;
    }

    public int getUser_id() {
        return user_id;
    }
  //  public int getvital_id() {
     //   return vital_id;
   // }
    public String getEcgData() {
        return ecgData;
    }
    public String getCreatedDateTime() {
        return createdAt;
    }
    public int getStatus() {
        return status;
    }
}
