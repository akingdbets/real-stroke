public class Parameter {
    // 1. 가중치 (Weights) - 단위 당 위험도 증가량
    private double smokingScore;        // 흡연 시 고정 벌점
    private double bloodSugarWeight;    // 혈당 1mg/dL 초과당 벌점
    private double bloodPressureWeight; // 혈압 1mmHg 초과당 벌점
    private double inactivityScore;     // 운동 부족 시 고정 벌점

    // 2. 기준치 (Standards) - 이 값을 넘어야 위험 계산 시작
    private float stdBloodSugar;    // 예: 126
    private int stdMaxBloodPressure;// 예: 140

    // 3. 위험 판단 기준
    private double totalRiskThreshold; // 총점 몇 점 이상이면 위험?

    public Parameter(double smokingScore, double bsWeight, double bpWeight, double inactScore,
                     float stdBS, int stdBP, double totalThreshold) {
        this.smokingScore = smokingScore;
        this.bloodSugarWeight = bsWeight;
        this.bloodPressureWeight = bpWeight;
        this.inactivityScore = inactScore;
        this.stdBloodSugar = stdBS;
        this.stdMaxBloodPressure = stdBP;
        this.totalRiskThreshold = totalThreshold;
    }

    // Getters
    public double getSmokingScore() { return smokingScore; }
    public double getBloodSugarWeight() { return bloodSugarWeight; }
    public double getBloodPressureWeight() { return bloodPressureWeight; }
    public double getInactivityScore() { return inactivityScore; }
    public float getStdBloodSugar() { return stdBloodSugar; }
    public int getStdMaxBloodPressure() { return stdMaxBloodPressure; }
    public double getTotalRiskThreshold() { return totalRiskThreshold; }
    public double getRiskThreshold() { return totalRiskThreshold; }
}